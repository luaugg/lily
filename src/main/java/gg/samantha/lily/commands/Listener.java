package gg.samantha.lily.commands;

import gg.samantha.lily.commands.list.TestCommand;
import io.sentry.Sentry;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener extends ListenerAdapter {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private final List<Command> commands = new ArrayList<>();

    public Listener() {
        commands.add(new TestCommand());

        // don't want to leak resources
        Runtime.getRuntime().addShutdownHook(new Thread(THREAD_POOL::shutdown));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        final var message = event.getMessage();
        final var content = message.getContentRaw();

        for (final var command : commands) {
            for (final var prefix : command.prefixes()) {
                if (content.startsWith(prefix)) {
                    THREAD_POOL.execute(() -> {
                        try {
                            command.execute(message);
                        } catch (Exception exception) {
                            Sentry.captureException(exception);
                            message.getChannel().sendMessage("an error occurred, go scream at sam").queue();
                        }
                    });
                }
            }

        }
    }
}
