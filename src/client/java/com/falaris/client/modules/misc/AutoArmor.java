package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.utils.InventoryUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class AutoArmor extends Module {
    public final BooleanSetting keepSilkTouch = addSetting(new BooleanSetting("Keep Silk Touch", "Keep silk touch equipped items", false));

    public AutoArmor() {
        super("AutoArmor", "Automatically equips best armor pieces", Category.MISC, "autoarmor");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        EquipmentSlot[] armorSlots = {
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
        };

        for (EquipmentSlot slot : armorSlots) {
            ItemStack current = mc.player.getItemBySlot(slot);
            double currentValue = getArmorScore(current);

            int bestSlot = -1;
            double bestValue = currentValue;

            for (int i = 9; i < 36; i++) {
                ItemStack candidate = mc.player.getInventory().getItem(i);
                if (candidate.isEmpty()) continue;
                if (!(candidate.getItem() instanceof ArmorItem armorItem)) continue;
                if (armorItem.getEquipmentSlot() != slot) continue;
                if (keepSilkTouch.getValue() && hasSilkTouch(candidate)) continue;

                double val = getArmorScore(candidate);
                if (val > bestValue) {
                    bestValue = val;
                    bestSlot = i;
                }
            }

            if (bestSlot >= 0) {
                mc.player.getInventory().setSelectedSlot(bestSlot - 9);
                mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, InventoryUtil.toMenuSlot(bestSlot),
                        slot == EquipmentSlot.CHEST ? 6 : slot == EquipmentSlot.LEGS ? 7 : slot == EquipmentSlot.FEET ? 8 : 5,
                        net.minecraft.world.inventory.ClickType.SWAP, mc.player);
            }
        }
    }

    private double getArmorScore(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem)) return 0.0;

        double score = 0.0;
        if (stack.getTag() != null) {
            score += stack.getTag().getInt("Unbreaking") * 2;
            score += stack.getTag().getInt("Protection") * 4;
            score += stack.getTag().getInt("BlastProtection") * 4;
            score += stack.getTag().getInt("ProjectileProtection") * 4;
            score += stack.getTag().getInt("FireProtection") * 4;
        }

        var attr = stack.getAttributeModifiers(EquipmentSlot.CHEST);
        score += 1.0;
        return score;
    }

    private boolean hasSilkTouch(ItemStack stack) {
        if (stack.getTag() == null) return false;
        for (String key : stack.getTag().getAllKeys()) {
            if (key.contains("SilkTouch") || key.toLowerCase().contains("enchantments")) return true;
        }
        return false;
    }
}
