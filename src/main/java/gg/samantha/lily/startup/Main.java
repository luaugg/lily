package gg.samantha.lily.startup;

import gg.samantha.lily.commands.Listener;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String... args) throws LoginException, InterruptedException {
        final var token = System.getenv("LILY_BOT_TOKEN");
        final var jda = JDABuilder.createDefault(token)
                .addEventListeners(new Listener())
                .build();

        jda.awaitReady();
    }
}
