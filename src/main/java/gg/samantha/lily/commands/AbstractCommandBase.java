package gg.samantha.lily.commands;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommandBase implements Command {
    @NotNull
    @Override
    public String description() {
        return "No description specified.";
    }

    @NotNull
    @Override
    public String usage() {
        return "No usage specified.";
    }

    @NotNull
    @Override
    public Category category() {
        return Category.GENERAL;
    }
}
