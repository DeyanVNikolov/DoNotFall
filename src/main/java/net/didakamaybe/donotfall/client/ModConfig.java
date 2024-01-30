package net.didakamaybe.donotfall.client;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModConfig {
    private static final Path CONFIG_PATH = Paths.get("config/donotfallconfig.json");
    private static ModConfig instance;
    private boolean showBlockName;
    private boolean isWorkInCreative;

    private ModConfig() {
        showBlockName = false;
        isWorkInCreative = false;
    }

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }

    public boolean isShowBlockName() { return showBlockName; }
    public boolean isWorkInCreative() { return isWorkInCreative; }
    public void setShowBlockName(boolean showBlockName) { this.showBlockName = showBlockName; saveConfig();}
    public void setWorkInCreative(boolean workInCreative) {this.isWorkInCreative = workInCreative; saveConfig(); }

    private static ModConfig loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                    instance = new Gson().fromJson(reader, ModConfig.class);
                }
            } else {
                instance = new ModConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    private static void saveConfig() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(getInstance());

            if (!Files.exists(CONFIG_PATH.getParent())) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }

            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                writer.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
