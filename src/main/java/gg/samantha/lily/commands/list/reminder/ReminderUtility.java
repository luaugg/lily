package gg.samantha.lily.commands.list.reminder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// I know I could use a relational database for this (which would make my life a lot easier in the long run)
// but can't be bothered setting up a database for a bot that will almost certainly remain small.
// May migrate to a relational one if it grows.

public class ReminderUtility {
    final JDA jda;
    final Jedis jedis;
    final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ReminderUtility(@NotNull JDA jda, @NotNull Jedis jedis) {
        this.jda = jda;
        this.jedis = jedis;
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
    }

    public void scheduleReminders() {
        jedis.hgetAll(".reminders")
                .forEach((key, value) -> {
                    final var split = value.split(" ", 3);
                    final var channel = split[0];
                    final var offset = Long.parseLong(split[1]) - System.currentTimeMillis() / 1000;
                    final var reminder = split[2];

                    if (offset < 0) {
                        removeReminderEntry(key);
                        return;
                    }

                    scheduleReminder(channel, reminder, key, offset);
                });
    }

    public void addReminderEntry(@NotNull Message message, long timestamp, @Nullable String reminder) {
        jedis.hset(".reminders", message.getId(), String.format("%s %s %s", message.getChannel().getId(),
                timestamp, reminder));
    }

    public void removeReminderEntry(@NotNull String messageId) {
        jedis.hdel(".reminders", messageId);
    }

    public void scheduleReminder(@NotNull String channelId, @NotNull String reminder,
                             @NotNull String messageId, long delay) {
        scheduler.schedule(() -> {
            final var channel = jda.getTextChannelById(channelId);

            if (channel != null) {
                final var formatted = "Heyo. You asked to be reminded" + (reminder.equals("...") ? "." : " to: `%s`.");
                channel.sendMessageFormat(formatted, reminder)
                        .referenceById(messageId)
                        .queue();
            }

            removeReminderEntry(messageId);
        }, delay, TimeUnit.SECONDS);
    }
}
