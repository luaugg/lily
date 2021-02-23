package io.github.samophis.lily.startup;

import com.mewna.catnip.Catnip;
import com.mewna.catnip.CatnipOptions;
import com.mewna.catnip.shard.GatewayIntent;

public class Main {
    public static void main(String... args) {
        final var token = System.getenv("LILY_BOT_TOKEN");
        final var catnipOptions = new CatnipOptions(token);
        catnipOptions.intents(GatewayIntent.UNPRIVILEGED_INTENTS);

        final var catnip = Catnip.catnip(catnipOptions);
        catnip.connect();
    }
}
