package gg.samantha.lily.commands.list.reminder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// I know I could use a relational database for this (which would make my life a lot easier in the long run)
// but can't be bothered setting up a database for a bot that will almost certainly remain small.
// May migrate to a relational one if it grows.

public class ReminderUtility {
    final JDA jda;
    final Jedis jedis;
    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
            .withZone(ZoneId.from(ZoneOffset.UTC));

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, ScheduledFuture<?>> futureMap = new HashMap<>();

    public ReminderUtility(@NotNull JDA jda, @NotNull Jedis jedis) {
        this.jda = jda;
        this.jedis = jedis;
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
    }

    public void scheduleReminders() {
        jedis.hgetAll(".reminders")
                .forEach((key, value) -> {
                    final var split = value.split(" ", 4);
                    final var channel = split[1];
                    final var offset = Long.parseLong(split[2]) - System.currentTimeMillis() / 1000;
                    final var reminder = split[3];

                    if (offset < 0) {
                        removeReminderEntry(key);
                        return;
                    }

                    scheduleReminder(channel, reminder, key, offset);
                });
    }

    public void addReminderEntry(@NotNull Message message, long timestamp, @Nullable String reminder) {
        jedis.hset(".reminders", message.getId(), String.format("%s %s %s %s", message.getAuthor().getId(),
                message.getChannel().getId(), timestamp, reminder));
    }

    public void removeReminderEntry(@NotNull String messageId) {
        jedis.hdel(".reminders", messageId);
    }

    public void unscheduleReminder(@NotNull String messageId) {
        final var future = futureMap.get(messageId);
        if (future == null)
            return;

        if (!future.isCancelled())
            future.cancel(false);

        futureMap.remove(messageId);
    }

    public void scheduleReminder(@NotNull String channelId, @NotNull String reminder,
                             @NotNull String messageId, long delay) {
        final var future = scheduler.schedule(() -> {
            final var channel = jda.getTextChannelById(channelId);

            if (channel != null) {
                final var formatted = "Heyo. You asked to be reminded" + (reminder.equals("...") ? "." : " to: `%s`.");
                channel.sendMessageFormat(formatted, reminder)
                        .referenceById(messageId)
                        .queue();
            }

            removeReminderEntry(messageId);
            unscheduleReminder(messageId);
        }, delay, TimeUnit.SECONDS);

        futureMap.put(messageId, future);
    }
}
