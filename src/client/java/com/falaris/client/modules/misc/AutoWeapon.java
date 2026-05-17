package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.CombatUtil;
import com.falaris.client.utils.InventoryUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;

public class AutoWeapon extends Module {
    public final BooleanSetting swapBack = addSetting(new BooleanSetting("Swap Back", "Return to previous slot after attack", false));
    public final BooleanSetting detectAOE = addSetting(new BooleanSetting("Detect AOE", "Prefer weapons for multi-entity targets", true));
    private int lastSlot = -1;

    public AutoWeapon() {
        super("AutoWeapon", "Selects best weapon for your crosshair target", Category.MISC, "autoweapon");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        if (mc.hitResult == null || !mc.hitResult.isPick()) return;

        Entity target = null;
        if (mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY
                && mc.hitResult instanceof net.minecraft.world.phys.EntityHitResult entityHit) {
            target = entityHit.getEntity();
        }

        if (target == null || !(target instanceof LivingEntity)) return;

        if (swapBack.getValue()) {
            int currentSlot = mc.player.getInventory().getSelectedSlot();
            if (lastSlot != -1 && currentSlot != lastSlot) {
                lastSlot = -1;
            } else if (lastSlot != -1) {
                return;
            }
        }

        int targetSlot = findBestWeapon((LivingEntity) target, detectAOE.getValue());
        if (targetSlot < 0) return;

        if (targetSlot != mc.player.getInventory().getSelectedSlot()) {
            if (swapBack.getValue()) lastSlot = mc.player.getInventory().getSelectedSlot();
            mc.player.getInventory().setSelectedSlot(targetSlot);
        }
    }

    private int findBestWeapon(LivingEntity target, boolean checkAOE) {
        double bestScore = -1;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem)) continue;

            double score = 0.0;
            if (target instanceof EndCrystal) {
                score += CombatUtil.getEnchantmentLevel(mc.gameLevel, stack, net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS) * 20.0;
                score += CombatUtil.getEnchantmentLevel(mc.gameLevel, stack, net.minecraft.world.item.enchantment.Enchantments.SMITE) * 20.0;
                score += stack.getOrDefault(Attributes.ATTACK_DAMAGE, null) != null
                        ? 1.0 : 1.0;
            } else {
                score += CombatUtil.getEnchantmentLevel(mc.gameLevel, stack, net.minecraft.world.item.enchantment.Enchantments.SHARPNESS) * 2.5;
                score += stack.getOrDefault(Attributes.ATTACK_DAMAGE, null) != null
                        ? 2.0 : 1.0;
            }

            score += 1.0; // base weapon score

            if (score > bestScore) {
                bestScore = score;
                bestSlot = i;
            }
        }
        return bestSlot;
    }
}
