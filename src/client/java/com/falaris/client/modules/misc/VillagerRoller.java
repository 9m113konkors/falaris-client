package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;

public class VillagerRoller extends Module {
    public VillagerRoller() {
        super("VillagerRoller", "Auto-cycle villager trades", Category.MISC, "roller");
    }

    @Override
    public void onTick() {
        // Implementation for cycling villager trades logic
    }
}
