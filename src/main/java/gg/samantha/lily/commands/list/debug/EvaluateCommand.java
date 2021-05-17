package gg.samantha.lily.commands.list.debug;

import gg.samantha.lily.commands.AbstractCommandBase;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class EvaluateCommand extends AbstractCommandBase {
    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
    private static final long OWNER_ID = Long.parseLong(System.getenv("LILY_BOT_OWNER"));

    @NotNull
    @Override
    public List<String> prefixes() {
        return List.of("can you evaluate", "evaluate", "run this code:");
    }

    @Override
    public void execute(@NotNull Message message, @NotNull String trimmedContent) {
        if (message.getAuthor().getIdLong() != OWNER_ID) {
            message.replyFormat("Sorry. This is limited to my owner.")
                    .mentionRepliedUser(false)
                    .queue();

            return;
        }

        final var code = trimmedContent.replace("`", "");
        final var engine = MANAGER.getEngineByName("groovy");
        engine.put("jda", message.getJDA());
        engine.put("message", message);

        try {
            final var currentTime = System.nanoTime();
            final var result = engine.eval(code);
            final var timeDiff = (System.nanoTime() - currentTime) / 1000000f;
            final var formatted = String.format("**%.2fms**", timeDiff);

            if (result == null) {
                message.replyFormat("Your script executed successfully in %s with no output.", formatted)
                        .mentionRepliedUser(false)
                        .queue();

                return;
            }

            final var toString = result.toString();

            if (toString.length() > 1900) {
                message.replyFormat("Your script executed successfully in %s, however the output was too large.", formatted)
                        .mentionRepliedUser(false)
                        .queue();

                return;
            }

            message.replyFormat("Your script executed successfully in %s. Here's the output:\n```\n%s```", formatted, toString)
                    .mentionRepliedUser(false)
                    .queue();
        } catch (ScriptException exception) {
            message.replyFormat("There was an error with this script:\n```%s```", exception.getMessage())
                    .mentionRepliedUser(false)
                    .queue();
        }
    }
}
