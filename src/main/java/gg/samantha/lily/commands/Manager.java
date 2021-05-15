package gg.samantha.lily.commands;

import gg.samantha.lily.commands.list.reminder.ReminderUtility;
import gg.samantha.lily.commands.list.reminder.SetReminderCommand;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Manager extends ListenerAdapter {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final long OWNER_ID = Long.parseLong(System.getenv("LILY_BOT_OWNER"));
    private static final Logger LOGGER = LoggerFactory.getLogger(Manager.class);
    private final List<Command> commands = new ArrayList<>();

    public Manager(@NotNull JDA jda) {
        final var jedis = new Jedis("localhost", 6379);
        final var reminderUtility = new ReminderUtility(jda, jedis);
        reminderUtility.scheduleReminders();
        commands.add(new SetReminderCommand(reminderUtility));

        // don't want to leak resources
        Runtime.getRuntime().addShutdownHook(new Thread(THREAD_POOL::shutdown));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        final var message = event.getMessage();
        final var authorId = message.getAuthor().getIdLong();
        final var content = message.getContentRaw();

        for (final var command : commands) {
            for (final var prefix : command.prefixes()) {
                final int trimPosition;

                if (authorId == OWNER_ID && content.startsWith(prefix)) {
                    trimPosition = prefix.length() + 1;
                } else if (content.startsWith("lily, " + prefix)) {
                    trimPosition = prefix.length() + 7;
                } else {
                    continue;
                }

                final var trimmedContent = content.substring(trimPosition);
                THREAD_POOL.execute(() -> {
                    try {
                        command.execute(message, trimmedContent);
                    } catch (Exception exception) {
                        LOGGER.error("An error occurred while executing a command!", exception);
                        message.getChannel().sendMessage("an error occurred, go scream at sam").queue();
                    }
                });

                return;
            }

        }
    }
}
