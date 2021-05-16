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
import java.util.regex.Pattern;

public class Manager extends ListenerAdapter {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final long OWNER_ID = Long.parseLong(System.getenv("LILY_BOT_OWNER"));
    private static final Logger LOGGER = LoggerFactory.getLogger(Manager.class);
    private static final Pattern PATTERN = Pattern.compile("(hey lily, |lily, )?(?:please )?(.+)\\??");
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
                final var matcher = PATTERN.matcher(content);
                if (!matcher.find())
                    continue;

                final var hasNoPrefix = matcher.group(1) == null;
                final var commandString = matcher.group(2);
                if ((hasNoPrefix && authorId != OWNER_ID) || !commandString.startsWith(prefix))
                    continue;

                final var trimmed = content.substring(content.indexOf(prefix) + prefix.length() + 1);
                THREAD_POOL.execute(() -> {
                    try {
                        command.execute(message, trimmed);
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
