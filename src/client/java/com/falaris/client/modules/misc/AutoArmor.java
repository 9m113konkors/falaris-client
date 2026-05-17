package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class AutoArmor extends Module {
    public AutoArmor() { super("AutoArmor", "Automatically equips best armor", Category.MISC, "autoarmor"); }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor()) {
                ItemStack current = mc.player.getItemBySlot(slot);
                if (current.isEmpty()) {}
            }
        }
    }
}
