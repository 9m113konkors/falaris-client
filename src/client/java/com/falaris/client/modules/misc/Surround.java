package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

public class Surround extends Module {
    public final BooleanSetting useEnderChests = addSetting(new BooleanSetting("Use Ender Chests", "Ender chest blocks instead of obsidian", false));
    public final BooleanSetting autoDisable = addSetting(new BooleanSetting("Auto Disable", "Disable when surround is complete", false));
    public final NumberSetting placeSpeed = addSetting(new NumberSetting("Place Speed", "Ticks between block places", 1.0, 1.0, 10.0, 1.0));
    private int delayTicks = 0;

    public Surround() {
        super("Surround", "Surrounds player with solid blocks to block knockback", Category.MISC, "surround");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        if (mc.player.isInWater() || mc.player.isInLava()) return;

        delayTicks++;
        if (delayTicks < placeSpeed.getValue()) return;
        delayTicks = 0;

        int blockSlot = InventoryUtil.findInventorySlot(mc.player,
                s -> !s.isEmpty() && (s.is(Blocks.OBSIDIAN) || s.is(Blocks.ENDER_CHEST)), true);

        if (blockSlot < 0) return;

        if (mc.player.getInventory().getSelectedSlot() != blockSlot) {
            mc.player.getInventory().setSelectedSlot(blockSlot);
        }

        boolean placedAny = false;
        List<BlockPos> surround = getSurroundPositions();
        for (BlockPos bp : surround) {
            if (!mc.level.getBlockState(bp).isAir()) continue;
            if (tryPlace(bp, blockSlot)) {
                placedAny = true;
                break;
            }
        }

        if (autoDisable.getValue() && !placedAny) {
            setEnabled(false);
        }
    }

    private List<BlockPos> getSurroundPositions() {
        BlockPos playerFeet = BlockPos.containing(mc.player.position()).below();
        return Arrays.asList(
            playerFeet.north(), playerFeet.south(), playerFeet.east(), playerFeet.west(),
            playerFeet.north().below(), playerFeet.south().below(),
            playerFeet.east().below(), playerFeet.west().below()
        );
    }

    private boolean tryPlace(BlockPos pos, int slot) {
        try {
            return mc.gameMode.useItemOn(mc.player, mc.level,
                    mc.player.getInventory().getItem(slot), net.minecraft.world.InteractionHand.MAIN_HAND) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDisable() {
        delayTicks = 0;
    }
}
