package gg.samantha.lily.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EventWaiter {
    private EventWaiter() { }

    public static void listenForReply(@NotNull Message original, @NotNull Consumer<Message> handler) {
        original.getJDA().addEventListener(new ListenerAdapter() {
            @Override
            public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
                final var referenced = event.getMessage().getReferencedMessage();
                if (referenced == null)
                    return;

                if (original.equals(referenced) && original.getAuthor().equals(referenced.getAuthor())) {
                    original.getJDA().removeEventListener(this);
                    handler.accept(event.getMessage());
                }
            }
        });
    }
}
