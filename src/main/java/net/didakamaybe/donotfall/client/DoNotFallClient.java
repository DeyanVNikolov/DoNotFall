package net.didakamaybe.donotfall.client;

import net.didakamaybe.donotfall.client.configs.WorkInCreative;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

public class DoNotFallClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean isFeatureEnabled = false;

    @Override
    public void onInitializeClient() {
        System.out.println("DoNotFall is initializing!");
        toggleKey = new KeyBinding(
                "Toggle DoNotFall",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "DoNotFall"
        );
        KeyBindingHelper.registerKeyBinding(toggleKey);
        System.out.println("DoNotFall has initialized!");


        System.out.println("DoNotFall is registering events!");
        ClientTickEvents.END_CLIENT_TICK.register(this::handleClientTick);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            handleVersionCheck(MinecraftClient.getInstance());
        });
        System.out.println("DoNotFall has registered events!");


    }


    private void handleVersionCheck(MinecraftClient client) {
        if (ModConfig.getInstance().shouldDoVersionCheck()) {
            System.out.println("DoNotFall is checking for updates!");
            try {
                String ver = VersionChecker.checkVersion();
                if (!ver.equals("error") && !ver.equals(VersionChecker.VERSION)) {
                    client.player.sendMessage(Text.of("§9[DoNotFall] §6There is a new version of DoNotFall available! You are using §c" + VersionChecker.VERSION + "§6 and the latest version is §c" + ver), false);
                    client.player.sendMessage(Text.of("§9[DoNotFall] §6You can download it from §c" + VersionChecker.UPDATE_URL), false);
                } else if (ver.equals("error")) {
                    client.player.sendMessage(Text.of("§9[DoNotFall] §cThere was an error while checking for updates!"), false);
                } else {
                    client.player.sendMessage(Text.of("§9[DoNotFall] §aYou are using the latest version of DoNotFall!"), false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("DoNotFall is not checking for updates!");
        }
    }

    private void handleClientTick(MinecraftClient client) {
        if (client.player == null) return;

        if (toggleKey.wasPressed()) {
            isFeatureEnabled = !isFeatureEnabled;
            if (isFeatureEnabled) {
                client.player.sendMessage(Text.of("§9DoNotFall §aengaged!"), true);
            } else {
                client.player.sendMessage(Text.of("§9DoNotFall §cdisengaged!"), true);
            }
        }

        if (isFeatureEnabled) {
            if (client.player.isCreative() && !WorkInCreative.getWorkInCreative()) {
                WorkInCreative.sendWarning(client);
                return;
            }
            if (!client.player.isOnGround()) {
                client.player.sendMessage(Text.of("§9DoNotFall §6is §cdisenaged §6while you are in the air!"), true);
                return;
            }

            checkBlockBelow(client);

        }
    }

    private String lastBlock(MinecraftClient client) {
        assert client.player != null;
        BlockPos pos = client.player.getBlockPos().down().down();
        boolean is_air = client.world.getBlockState(pos).isAir();
        if (is_air) {
            return "AIR";
        } else {
            Block block = client.world.getBlockState(pos).getBlock();
            if (block == net.minecraft.block.Blocks.WATER) {
                return "WATER";
            } else if (block == net.minecraft.block.Blocks.LAVA) {
                return "LAVA";
            } else {
                return block.getName().getString();
            }
        }
    }

    // #TODO this method is not used (yet)
    private void playSoundToPlayer(MinecraftClient client) {
        if (client.player != null && client.world != null) {
            client.player.playSound(SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.PLAYERS, 0.3F, 1.0F);
        }
    }

    private void checkBlockBelow(MinecraftClient client) {
        assert client.player != null;
        String lastBlock = lastBlock(client);
        float diff = 0;
        float currentY = 0;
        float y = 0;
        if (lastBlock == "AIR") {
            BlockPos pos = client.player.getBlockPos().down().down();
            while (pos.getY() > -64) {
                pos = pos.down();
                BlockState state = client.world.getBlockState(pos);
                if (!state.isAir()) {
                    lastBlock = state.getBlock().getName().getString();
                    y = pos.getY();
                    currentY = client.player.getBlockPos().getY();
                    diff = currentY - y;
                    break;
                }
            }

            if ((y == 0 && currentY == 0 && diff == 0) || y <= -63) {
                client.player.sendMessage(Text.of("§cWARNING! VOID!"), true);
            } else {

                if (ModConfig.getInstance().isShowBlockName()) {
                    client.player.sendMessage(Text.of("§cLAST BLOCK! §7/ §6NEXT BLOCK: §c" + lastBlock + "§7 / §6HEIGHT: §c" + diff + " blocks"), true);
                } else {
                    client.player.sendMessage(Text.of("§cLAST BLOCK!"), true);
                }
            }
        } else {
            if (lastBlock.equals("WATER")) {
                client.player.sendMessage(Text.of("§9WATER!"), true);
            } else if (lastBlock.equals("LAVA")) {
                client.player.sendMessage(Text.of("§cLAVA!"), true);
            } else {
                if (ModConfig.getInstance().isShowBlockName()) {
                    client.player.sendMessage(Text.of("§6SOLID: §c" + lastBlock), true);
                } else {
                    client.player.sendMessage(Text.of("§6SOLID!"), true);
                }

            }
        }
    }
}
