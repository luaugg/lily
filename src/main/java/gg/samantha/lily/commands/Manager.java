package gg.samantha.lily.commands;

import gg.samantha.lily.commands.list.reminder.ReminderUtility;
import gg.samantha.lily.commands.list.reminder.SetReminderCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Manager extends ListenerAdapter {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final long OWNER_ID = Long.parseLong(System.getenv("LILY_BOT_OWNER"));
    private static final Logger LOGGER = LoggerFactory.getLogger(Manager.class);
    private static final Pattern PATTERN = Pattern.compile("(hey lily, |lily, |\uD83C\uDF3C |\uD83D\uDC90 " +
            "|\uD83C\uDF38 |\uD83C\uDF3A |\uD83C\uDF39 |\uD83C\uDF3B |\uD83C\uDF37 )?(?:please )?(.+)\\??");

    private static final String[] THANKS_RESPONSES = new String[] { "np", "nps", "yw", "ur welcome", "\uD83D\uDE33" };
    private static final Random THANKS_RESPONSE_RANDOM = new Random();
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

        if (content.equals("thanks lily") || content.equals("thank you lily")) {
            final var index = THANKS_RESPONSE_RANDOM.nextInt(THANKS_RESPONSES.length);
            final var response = THANKS_RESPONSES[index];
            message.getChannel().sendMessage(response)
                    .reference(message)
                    .mentionRepliedUser(false)
                    .queue();

            return;
        }

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
