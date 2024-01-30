package net.didakamaybe.donotfall.client.configs;

import net.didakamaybe.donotfall.client.ModConfig;

public class ShouldDoVersionCheck {

    public static boolean getShouldDoVersionCheck() {
        return ModConfig.getInstance().shouldDoVersionCheck();
    }

    public static void saveShouldDoVersionCheck(boolean newValue) {
        ModConfig.getInstance().setShouldDoVersionCheck(newValue);
    }
}
