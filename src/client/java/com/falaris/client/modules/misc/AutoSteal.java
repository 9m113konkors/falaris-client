package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import net.minecraft.world.inventory.ChestMenu;

public class AutoSteal extends Module {
    public AutoSteal() { super("AutoSteal", "Automatically steals from chests", Category.MISC, "cheststealer"); }

    @Override
    public void onTick() {
        if (mc.player != null && mc.player.containerMenu instanceof ChestMenu chest) {
            // Logic to move items from chest to inventory
        }
    }
}
