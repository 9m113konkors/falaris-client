package com.falaris.client.modules.movement;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;

public class Sprint extends Module {
    public final BooleanSetting omni = addSetting(new BooleanSetting("Omni", "Allow sprinting while moving in any forward direction", false));

    public Sprint() {
        super("Sprint", "Automatically sprints while moving", Category.MOVEMENT, "autosprint");
    }

    @Override
    public void onTick() {
        if (mc.player == null) {
            return;
        }

        boolean movingForward = mc.player.input.hasForwardImpulse();
        boolean shouldSprint = movingForward || (omni.getValue() && mc.player.getDeltaMovement().horizontalDistanceSqr() > 0.001);
        if (shouldSprint && !mc.player.isCrouching() && !mc.player.horizontalCollision) {
            mc.player.setSprinting(true);
        }
    }
}
