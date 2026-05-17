package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class Velocity extends Module {
    public final NumberSetting horizontal = addSetting(new NumberSetting("Horizontal", "Horizontal KB multiplier (0=full cancel)", 0.0, 0.0, 1.0, 0.05));
    public final NumberSetting vertical = addSetting(new NumberSetting("Vertical", "Vertical KB multiplier (0=full cancel)", 0.0, 0.0, 1.0, 0.05));
    public final BooleanSetting onlyPlayers = addSetting(new BooleanSetting("Only Players", "Only modify KB from players", false));

    public Velocity() {
        super("Velocity", "Modify knockback velocity applied to you", Category.COMBAT, "velocity");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        double horiz = 1.0 - horizontal.getValue();
        double vert  = 1.0 - vertical.getValue();

        if (horiz == 1.0 && vert == 1.0) return;

        Vec3 motion = mc.player.getDeltaMovement();

        if (Math.abs(motion.x) > 0.003 || Math.abs(motion.z) > 0.003 || motion.y > 0.003) {
            double newX = motion.x * horiz;
            double newZ = motion.z * horiz;
            double newY = motion.y * vert;
            mc.player.setDeltaMovement(new Vec3(newX, Math.max(newY, motion.y), newZ));
        }
    }
}
