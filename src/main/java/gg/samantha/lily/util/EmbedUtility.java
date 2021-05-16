package gg.samantha.lily.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import java.awt.*;
import java.util.Comparator;

public class EmbedUtility {
    private static final Color DEFAULT_COLOR = new Color(0xc47dff);

    private EmbedUtility() {}

    @NotNull
    @CheckReturnValue
    public static Color highestRoleColor(@NotNull Member author) {
        return author.getRoles()
                .stream()
                .filter(role -> role.getColor() != null)
                .max(Comparator.comparingInt(Role::getPosition))
                .map(Role::getColor)
                .orElse(DEFAULT_COLOR);
    }
}
