package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class AutoWeb extends Module {
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Target range to web", 3.5, 1.0, 6.0, 0.5));
    public final BooleanSetting autoSwitch = addSetting(new BooleanSetting("Auto Switch", "Switch to cobwebs in hotbar", true));
    public final BooleanSetting trapOnly = addSetting(new BooleanSetting("Trap Target", "Place at target feet instead of player's feet", true));
    private int placeTicks = 0;

    public AutoWeb() {
        super("AutoWeb", "Automatically places cobwebs near targets", Category.MISC, "autoweb");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        if (placeTicks > 0) { placeTicks--; return; }

        LivingEntity target = findTarget();
        if (target == null) return;

        BlockPos feetPos = BlockPos.containing(target.position().add(0, -1, 0));
        if (trapOnly.getValue()) {
            placeCobwebAt(feetPos);
        } else {
            boolean selfPlaced = false;
            if (placeCobwebAt(mc.player.blockPosition()) || placeCobwebAt(mc.player.blockPosition().below())) {
                selfPlaced = true;
            }
        }
    }

    private LivingEntity findTarget() {
        LivingEntity best = null;
        double bestDist = range.getValue();
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e instanceof LivingEntity le && le != mc.player && le.isAlive() && mc.player.distanceTo(le) <= bestDist) {
                best = le;
                bestDist = mc.player.distanceTo(le);
            }
        }
        return best;
    }

    private boolean placeCobwebAt(BlockPos pos) {
        int cobwebSlot = InventoryUtil.findInventorySlot(mc.player,
                s -> !s.isEmpty() && s.is(Items.COBWEB), true);
        if (cobwebSlot < 0) return false;
        if (!mc.level.getBlockState(pos).is(Blocks.AIR)) return false;

        if (autoSwitch.getValue()) {
            mc.player.getInventory().setSelectedSlot(cobwebSlot);
        }

        try {
            mc.gameMode.useItemOn(mc.player, mc.level, mc.player.getInventory().getItem(cobwebSlot),
                    net.minecraft.world.InteractionHand.MAIN_HAND);
            placeTicks = 4;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
