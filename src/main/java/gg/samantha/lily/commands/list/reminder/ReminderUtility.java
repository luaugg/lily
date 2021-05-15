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
                    final var split = value.split(" ");
                    final var channel = jda.getTextChannelById(split[0]);
                    final var offset = Long.parseLong(split[2]) - System.currentTimeMillis() / 1000;
                    final var reminder = split[3];
                    if (channel == null || offset < 0)
                        return;

                    scheduleReminder(channel, reminder, key, offset);
                });
    }

    public void addReminderEntry(@NotNull Message message, @NotNull User user, long timestamp, @Nullable String reminder) {
        final var remToString = reminder == null ? "..." : reminder;
        jedis.hset(".reminders", message.getId(), String.format("%s %s %s %s", message.getChannel().getId(),
                user.getId(), timestamp, remToString));
    }

    public void removeReminderEntry(@NotNull String messageId) {
        jedis.hdel(".reminders", messageId);
    }

    public void scheduleReminder(@NotNull MessageChannel channel, @Nullable String reminder,
                             @NotNull String messageId, long delay) {
        final var formatted = "Heyo. You asked to be reminded" + (reminder == null ? "." : " to: `%s`.");
        channel.sendMessageFormat(formatted, reminder)
                .referenceById(messageId)
                .queueAfter(delay, TimeUnit.SECONDS, ignored -> removeReminderEntry(messageId));
    }
}
