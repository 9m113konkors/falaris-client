package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class Criticals extends Module {
    public final BooleanSetting onlyWhenSprinting = addSetting(new BooleanSetting("Only While Sprinting", "Only critical while sprinting", false));
    public final NumberSetting motionY = addSetting(new NumberSetting("Jump Height", "Upward motion on attack", 0.25, 0.05, 1.0, 0.05));

    private boolean wasLooking = false;

    public Criticals() {
        super("Criticals", "Always lands critical hits", Category.COMBAT, "crits");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        boolean lookingAtEntity = mc.hitResult != null
                && mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY
                && mc.hitResult instanceof net.minecraft.world.phys.EntityHitResult entityHit
                && entityHit.getEntity() instanceof LivingEntity;

        if (mc.options.keyAttack.isDown() && lookingAtEntity) {
            if (onlyWhenSprinting.getValue() && !mc.player.isSprinting()) return;

            double y = mc.player.getDeltaMovement().y;
            if (y <= 0.0125) {
                Vec3 cur = mc.player.getDeltaMovement();
                mc.player.setDeltaMovement(cur.x, motionY.getValue().floatValue(), cur.z);
            }
            wasLooking = true;
        } else if (!lookingAtEntity) {
            wasLooking = false;
        }
    }
}
