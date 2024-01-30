package net.didakamaybe.donotfall.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.didakamaybe.donotfall.client.configs.ShouldDoVersionCheck;
import net.didakamaybe.donotfall.client.configs.ShowBlockName;
import net.didakamaybe.donotfall.client.configs.WorkInCreative;
import net.minecraft.text.Text;


public class ConfigScreen implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(screen)
                    .setTitle(Text.literal("DoNotFall Config"));
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show block name"), ShowBlockName.getShowBlock())
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("Show the name of the block below the block you're standing on"))
                    .setSaveConsumer(ShowBlockName::saveShowBlock)
                    .build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Work in creative mode"), WorkInCreative.getWorkInCreative())
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("Work in creative mode"))
                    .setSaveConsumer(WorkInCreative::saveWorkInCreative)
                    .build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Check for updates"), ShouldDoVersionCheck.getShouldDoVersionCheck())
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Check for updates"))
                    .setSaveConsumer(ShouldDoVersionCheck::saveShouldDoVersionCheck)
                    .build());

            return builder.build();
        };
    }
}