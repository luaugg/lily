package gg.samantha.lily.commands.list.reminder;

import gg.samantha.lily.commands.AbstractCommandBase;
import gg.samantha.lily.util.EmbedUtility;
import gg.samantha.lily.util.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShowReminderCommand extends AbstractCommandBase {
    private final ReminderUtility reminderUtility;

    public ShowReminderCommand(@NotNull ReminderUtility reminderUtility) {
        this.reminderUtility = reminderUtility;
    }

    @NotNull
    @Override
    public List<String> prefixes() {
        return List.of("show my reminders", "delete my reminder", "delete a reminder");
    }

    @Override
    public void execute(@NotNull Message message, @NotNull String trimmedContent) {
        final var reminders = new ArrayList<String>();
        final var author = message.getAuthor();
        final var embedBuilder = new EmbedBuilder()
                .setAuthor("Your Reminders", null, author.getEffectiveAvatarUrl())
                .setDescription("Here are a list of your reminders. You may delete one of these by replying to this " +
                        "message with a list of space-separated numbers.\n")
                .setColor(EmbedUtility.highestRoleColor(message.getGuild().getSelfMember()));

        final var reminderEntrySet = reminderUtility.jedis.hgetAll(".reminders")
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().startsWith(author.getId()))
                .sorted(Comparator.comparingLong(entry -> Long.parseLong(entry.getValue().split(" ", 4)[2])))
                .collect(Collectors.toList());

        reminderEntrySet.forEach(entry -> {
            if (entry.getValue().startsWith(author.getId())) {
                final var split = entry.getValue().split(" ", 4);
                final var reminder = split[3];
                reminders.add(entry.getKey());
                final var timestamp = Instant.ofEpochSecond(Long.parseLong(split[2]));
                embedBuilder.appendDescription(String.format("\n**#%d:** %s | %s UTC", reminders.size(), reminder,
                        ReminderUtility.TIME_FORMATTER.format(timestamp)));
            }
        });

        if (reminders.isEmpty()) {
            message.reply("You don't seem to have any reminders. That's strange.")
                    .mentionRepliedUser(false)
                    .queue();

            return;
        }

        message.reply(embedBuilder.build())
                .mentionRepliedUser(false)
                .queue(msg ->
                    EventWaiter.listenForReply(msg, response -> {
                        final var split = response.getContentRaw().split("\\s+");
                        final var toBeRemoved = new ArrayList<Integer>();

                        for (var index : split) {
                            try {
                                final var indexAsInteger = Integer.parseUnsignedInt(index);
                                toBeRemoved.add(indexAsInteger - 1);
                            } catch (NumberFormatException exception) {
                                message.getChannel().sendMessage("Try giving me valid integers next time.")
                                        .reference(response)
                                        .mentionRepliedUser(false)
                                        .queue();

                                return;
                            }
                        }

                        toBeRemoved.stream()
                                .map(reminders::get)
                                .forEach(messageId -> {
                                    reminderUtility.removeReminderEntry(messageId);
                                    reminderUtility.unscheduleReminder(messageId);
                                });

                        response.reply("Awesome. I deleted those reminders.")
                                .mentionRepliedUser(false)
                                .queue();
                    })
                );

    }
}
