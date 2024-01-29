package net.didakamaybe.donotfall.client.configs;

import net.didakamaybe.donotfall.client.ModConfig;

public class ShowBlockName {
    public static boolean getShowBlock() {
        return ModConfig.getInstance().isShowBlockName();
    }

    public static void saveShowBlock(boolean newValue) {
        ModConfig.getInstance().setShowBlockName(newValue);
    }
}
