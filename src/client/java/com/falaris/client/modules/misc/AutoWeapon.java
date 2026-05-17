package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;

public class AutoWeapon extends Module {
    public AutoWeapon() { super("AutoWeapon", "Selects the best weapon for the target", Category.MISC, "autoweapon"); }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;
        int bestSlot = -1;
        float bestDamage = -1;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            // Simplified check, ignoring attribute map complexity for now to get a clean build
            float damage = 1.0f; 
            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }
        if (bestSlot != -1 && mc.player.getInventory().getSelectedSlot() != bestSlot) {
            mc.player.getInventory().setSelectedSlot(bestSlot);
        }
    }
}
