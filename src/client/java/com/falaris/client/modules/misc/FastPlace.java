package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", "Remove block placement delay", Category.MISC, "fastplace");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        mc.player.fallDistance = 0.0f;
    }
}
