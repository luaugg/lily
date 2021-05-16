package gg.samantha.lily.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EventWaiter {
    private EventWaiter() { }

    public static void listenForResponse(@NotNull JDA jda, long userId, @NotNull Consumer<Message> handler) {
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
                if (event.getMessage().getAuthor().getIdLong() == userId) {
                    jda.removeEventListener(this);
                    handler.accept(event.getMessage());
                }
            }
        });
    }
}
