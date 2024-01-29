package net.didakamaybe.donotfall.client.configs;

import net.didakamaybe.donotfall.client.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class WorkInCreative {

    public static boolean getWorkInCreative() {
        return ModConfig.getInstance().isWorkInCreative();
    }

    public static void saveWorkInCreative(boolean newValue) {
        ModConfig.getInstance().setWorkInCreative(newValue);
    }

    public static void sendWarning(MinecraftClient client) {
        if (client.player == null) return;
        client.player.sendMessage(Text.of("§6DoNotFall disengaged while you are in creative mode!"), true);
    }
}
