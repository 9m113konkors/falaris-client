package com.falaris.client.modules.movement;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;

public class Speed extends Module {
    public Speed() { super("Speed", "Increases movement speed", Category.MOVEMENT, "speed"); }

    @Override
    public void onTick() {
        if (mc.player != null && (mc.player.xxa != 0 || mc.player.zza != 0)) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().multiply(1.2, 1, 1.2));
        }
    }
}
