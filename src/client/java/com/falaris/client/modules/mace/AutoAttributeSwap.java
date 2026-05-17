package com.falaris.client.modules.mace;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.utils.CombatUtil;
import net.minecraft.world.item.ItemStack;

public class AutoAttributeSwap extends Module {
    public final BooleanSetting countJumping = addSetting(new BooleanSetting("Count Jumping", "Treat jumping as airborne when choosing Density", false));
    private int lastSwapTick = -20;
    private int lastManualSlot = -1;

    public AutoAttributeSwap() {
        super("AutoAttributeSwap", "Swaps between Breach and Density maces", Category.MACE, "mace swap", "density breach");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) {
            return;
        }

        int currentSlot = mc.player.getInventory().getSelectedSlot();
        if (lastManualSlot != -1 && currentSlot != lastManualSlot) {
            lastManualSlot = -1; 
        } else if (lastManualSlot != -1) {
            return; 
        }

        if (mc.player.tickCount - lastSwapTick < 2) {
            return;
        }

        int preferredSlot = findPreferredMaceSlot();
        if (preferredSlot < 0 || currentSlot == preferredSlot) {
            return;
        }

        ItemStack current = mc.player.getMainHandItem();
        if (!current.isEmpty() && !CombatUtil.isMace(current)) {
            return;
        }

        mc.player.getInventory().setSelectedSlot(preferredSlot);
        lastSwapTick = mc.player.tickCount;
        lastManualSlot = preferredSlot;
    }

    private int findPreferredMaceSlot() {
        boolean airborne = isAirborne();
        int bestSlot = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = mc.player.getInventory().getItem(slot);
            if (!CombatUtil.isMace(stack)) {
                continue;
            }

            int density = CombatUtil.getDensityLevel(mc, stack);
            int breach = CombatUtil.getBreachLevel(mc, stack);
            int score = airborne ? density * 10 + breach : breach * 10 + density;
            if (score > bestScore) {
                bestScore = score;
                bestSlot = slot;
            }
        }

        return bestSlot;
    }

    private boolean isAirborne() {
        if (mc.player == null) {
            return false;
        }

        if (!mc.player.onGround()) {
            return true;
        }

        return countJumping.getValue() && mc.options.keyJump.isDown();
    }
}
