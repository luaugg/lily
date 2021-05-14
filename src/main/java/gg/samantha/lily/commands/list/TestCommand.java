package gg.samantha.lily.commands.list;

import gg.samantha.lily.commands.AbstractCommandBase;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestCommand extends AbstractCommandBase {
    @NotNull
    @Override
    public List<String> prefixes() {
        return List.of("...test");
    }

    @Override
    public void execute(@NotNull Message message, @NotNull String trimmedContent) {
        final var currentTime = System.nanoTime();

        message.getChannel().sendMessage("Pong!").queue(msg -> {
            final var differenceInTime = (System.nanoTime() - currentTime) / 1000000f;
            final var formattedTime = String.format("Latency: %.2fms", differenceInTime);
            msg.editMessage(formattedTime).queue();
        });
    }
}
