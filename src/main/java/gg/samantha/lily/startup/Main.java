package gg.samantha.lily.startup;

import io.sentry.Sentry;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws LoginException, InterruptedException {
        final var token = System.getenv("LILY_BOT_TOKEN");
        final var dsn = System.getenv("LILY_SENTRY_DSN");
        final var jda = JDABuilder.createDefault(token)
                .addEventListeners(new Main())
                .build();

        Sentry.init(dsn);
        jda.awaitReady();
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("Ready!");
    }
}
