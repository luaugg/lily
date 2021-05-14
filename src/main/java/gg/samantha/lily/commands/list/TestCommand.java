package gg.samantha.lily.commands.list;

import gg.samantha.lily.commands.AbstractCommandBase;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestCommand extends AbstractCommandBase {
    @NotNull
    @Override
    public List<String> prefixes() {
        return List.of("lily, test");
    }

    @Override
    public void execute(@NotNull Message message) {
        final var currentTime = System.currentTimeMillis();
        message.getChannel().sendMessage("Pong!").queue(msg -> {
            final var differenceInTime = System.currentTimeMillis() - currentTime / 1000f;
            final var formattedTime = String.format("Latency: %.2fms", differenceInTime);
            msg.editMessage(formattedTime).queue();
        });
    }
}
