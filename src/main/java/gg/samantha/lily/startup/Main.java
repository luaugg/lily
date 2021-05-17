package gg.samantha.lily.startup;

import gg.samantha.lily.commands.Manager;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String... args) throws LoginException, InterruptedException {
        var sentryDsn = System.getenv("LILY_SENTRY_DSN");
        sentryDsn = sentryDsn == null ? "" : sentryDsn;
        Sentry.init(sentryDsn);
        final var token = System.getenv("LILY_BOT_TOKEN");
        final var jda = JDABuilder.createDefault(token).build();
        jda.addEventListener(new Manager(jda));
        jda.awaitReady();
    }
}
