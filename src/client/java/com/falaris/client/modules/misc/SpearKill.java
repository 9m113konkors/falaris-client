package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;

public class SpearKill extends Module {
    public final NumberSetting minDistance = addSetting(new NumberSetting("Min Distance", "Keep targets at least this far", 2.5, 1.0, 6.0, 0.1));
    public final NumberSetting maxDistance = addSetting(new NumberSetting("Max Distance", "Stop if target is closer than this", 1.5, 0.5, 3.0, 0.1));
    public final BooleanSetting targetOnly = addSetting(new BooleanSetting("Target Only", "Only when looking at a living target", true));

    public SpearKill() {
        super("SpearKill", "Maintains optimal range from combat targets for spear combat", Category.MISC, "spearkill");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        if (targetOnly.getValue() && !mc.hitResult.isPick()) return;

        for (var e : mc.level.entitiesForRendering()) {
            if (e != mc.player && !mc.player.isPassenger() && e instanceof net.minecraft.world.entity.LivingEntity le && mc.player.distanceTo(le) <= maxDistance.getValue()) {
                net.minecraft.world.phys.Vec3 look = mc.player.getLookAngle();
                mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(
                    -look.x * minDistance.getValue() * 0.08, look.y * 0.04,
                    -look.z * minDistance.getValue() * 0.08));
            }
        }
    }
}
