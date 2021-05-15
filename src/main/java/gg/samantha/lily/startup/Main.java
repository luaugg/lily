package gg.samantha.lily.startup;

import gg.samantha.lily.commands.Manager;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String... args) throws LoginException, InterruptedException {
        final var token = System.getenv("LILY_BOT_TOKEN");
        final var jda = JDABuilder.createDefault(token).build();
        jda.addEventListener(new Manager(jda));
        jda.awaitReady();
    }
}
