package gg.samantha.lily.commands;

import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import java.util.List;

/*

commands in lily will have natural language prefixes for me only:
    - ex. remind me to do something in 2 hours
    - for other users has to be prefixed by "lily," or "hey lily," or "<mention>", to prevent accidental invocations

 */

@SuppressWarnings("unused")
public interface Command {
    @Nonnull
    List<String> prefixes();

    @Nonnull
    String description();

    @Nonnull
    String usage();

    @Nonnull
    Category category();

    void execute(@Nonnull Message message, @Nonnull String trimmedContent);
}
