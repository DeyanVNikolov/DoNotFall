package net.didakamaybe.donotfall.client;

import net.didakamaybe.donotfall.client.configs.WorkInCreative;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

public class DoNotFallClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean isFeatureEnabled = false;

    @Override
    public void onInitializeClient() {
        // Initialize and register the keybinding
        toggleKey = new KeyBinding(
                "key.donotfall.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.donotfall.general"
        );
        KeyBindingHelper.registerKeyBinding(toggleKey);


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (toggleKey.wasPressed()) {
                isFeatureEnabled = !isFeatureEnabled;
                if (isFeatureEnabled) {
                    client.player.sendMessage(Text.of("§aDoNotFall engaged!"), true);
                } else {
                    client.player.sendMessage(Text.of("§6DoNotFall disengaged!"), true);
                }
            }

            if (isFeatureEnabled) {
                if (client.player.isCreative() && !WorkInCreative.getWorkInCreative()) {
                    WorkInCreative.sendWarning(client);
                    return;
                }
                if (!client.player.isOnGround()) {
                    BlockPos pos = client.player.getBlockPos();
                    while (pos.getY() > -64) {
                        pos = pos.down();
                        BlockState state = client.world.getBlockState(pos);
                        if (!state.isAir()) {
                            int y = pos.getY();
                            int currentY = client.player.getBlockPos().getY();
                            double diff = currentY - y;
                            if (diff > 2) {
                                client.player.sendMessage(Text.of("§6DoNotFall disengaged while you are in the air!"), true);
                                return;
                            }
                            break;
                        }
                    }


                }

                checkBlockBelow(client);
            }
        });
    }

    private String LastBlock(MinecraftClient client) {
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

    private void playSoundToPlayer(MinecraftClient client) {
        if (client.player != null && client.world != null) {
            client.player.playSound(SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.PLAYERS, 0.3F, 1.0F);
        }
    }

    private void checkBlockBelow(MinecraftClient client) {
        assert client.player != null;
        String lastBlock = LastBlock(client);
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
