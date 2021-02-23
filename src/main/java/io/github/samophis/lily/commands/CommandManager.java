package io.github.samophis.lily.commands;

import com.mewna.catnip.entity.message.Message;
import io.github.classgraph.ClassGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class CommandManager {
    private static final Map<String, MethodAnnotationPair> PAIRS = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void registerCommands() {
        try (var scanResult = new ClassGraph().acceptPackages("io.github.samophis.lily.commands.list").scan()) {
            scanResult.getAllStandardClasses()
                    .stream()
                    .filter(classInfo -> !Modifier.isAbstract(classInfo.getModifiers()))
                    .forEach(classInfo -> {
                        var cls = classInfo.loadClass();
                        var methods = cls.getDeclaredMethods();
                        for (var method : methods) {
                            final var annotation = method.getDeclaredAnnotation(Command.class);
                            if (annotation == null)
                                continue;

                            final var map = new MethodAnnotationPair();
                            map.command = annotation;
                            map.method = method;
                            for (var name : annotation.value())
                                PAIRS.put(name, map);
                        }
                    });
        }
    }

    public static void handleMessage(Message message) {
        if (message.guildId() == null)
            return;

        final var content = message.content();
        if (!content.startsWith("<@777228923356053544>") && !content.startsWith("<@!777228923356053544>"))
            return;

        final var split = content.split("//s+");
        if (split.length < 2)
            return;

        final var pair = PAIRS.get(split[1]);
        if (pair == null)
            return;

        final var args = new ArrayList<>(Arrays.asList(split));
        args.remove(0);
        args.remove(0);

        final var botPermissions = pair.command.botPermissions();
        final var hasBotPerms = message.guild()
                .flatMap(guild -> guild.member(777228923356053544L))
                .map(member -> member.hasPermissions(botPermissions))
                .blockingGet(); // todo: REMOVE THIS, DO RXJAVA PROPERLY

        if (!hasBotPerms) // NPE maybe?
            return;

        final var userPermissions = pair.command.userPermissions();
        final var hasUserPerms = message.member().hasPermissions(userPermissions);
        if (!hasUserPerms)
            return;

        EXECUTOR.execute(() -> {
            try {
                pair.method.invoke(pair.method.getDeclaringClass(), message, args);
            } catch (Exception exception) {
                LOGGER.error("Error when invoking command " + split[1], exception);
            }
        });
    }

    private static class MethodAnnotationPair {
        private Command command;
        private Method method;
    }
}
