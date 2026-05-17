package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AntiKnockback extends Module {
    public final BooleanSetting onlyFromPlayers = addSetting(new BooleanSetting("Only From Players", "Only cancel KB from players", true));
    public final NumberSetting strengthThreshold = addSetting(new NumberSetting("Threshold", "Minimum velocity to count as KB", 0.15, 0.0, 1.0, 0.05));
    private Vec3 lastVelocity = Vec3.ZERO;
    private int waitTicks = 0;

    public AntiKnockback() {
        super("AntiKnockback", "Negate knockback applied to you", Category.COMBAT, "antikb");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        if (waitTicks > 0) {
            waitTicks--;
            lastVelocity = mc.player.getDeltaMovement();
            return;
        }

        Vec3 current = mc.player.getDeltaMovement();
        double horizSpeed = Math.sqrt(current.x * current.x + current.z * current.z);
        boolean incomingKB = horizSpeed > strengthThreshold.getValue()
                          || current.y > strengthThreshold.getValue();

        if (incomingKB) {
            mc.player.setDeltaMovement(0.0, current.y, 0.0);
            if (!onlyFromPlayers.getValue()) {
                mc.player.setDeltaMovement(Vec3.ZERO);
            }
            waitTicks = 2;
        }

        lastVelocity = current;
    }
}
