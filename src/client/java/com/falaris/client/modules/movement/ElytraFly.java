package com.falaris.client.modules.movement;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.world.entity.EquipmentSlot;

public class ElytraFly extends Module {
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Horizontal flight speed", 1.5, 0.5, 5.0, 0.1));
    public final NumberSetting verticalSpeed = addSetting(new NumberSetting("Vertical Speed", "Up/down fly speed", 0.5, 0.1, 3.0, 0.1));
    public final BooleanSetting autoDeploy = addSetting(new BooleanSetting("Auto Deploy", "Automatically deploy elytra when falling", true));
    public final BooleanSetting infiniteDurability = addSetting(new BooleanSetting("Infinite Elytra", "Elytra never takes damage", true));

    public ElytraFly() {
        super("ElytraFly", "Enhanced elytra flight with hover control", Category.MOVEMENT, "elytrafly");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        if (autoDeploy.getValue() && !mc.player.isFallFlying() && !mc.player.isOnGround()
                && !mc.player.isInWater() && mc.player.fallDistance > 2.0f
                && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(net.minecraft.world.item.Items.ELYTRA)) {
            mc.player.startFallFlying();
        }

        if (!mc.player.isFallFlying()) return;

        if (infiniteDurability.getValue()) {
            mc.player.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(0, mc.player, (p) -> {});
        }

        double speedMult = speed.getValue();
        double vertMult  = verticalSpeed.getValue();

        float forward = mc.player.input.forward();
        float strafe  = mc.player.input.strafe();
        float yaw     = mc.player.getYRot() * ((float) Math.PI / 180.0f);

        if (forward != 0.0f || strafe != 0.0f) {
            double dx = forward * speedMult * Math.cos(yaw) - strafe * speedMult * Math.sin(yaw);
            double dz = forward * speedMult * Math.sin(yaw) + strafe * speedMult * Math.cos(yaw);
            mc.player.setDeltaMovement(new net.minecraft.world.phys.Vec3(dx, mc.player.getDeltaMovement().y, dz));
        } else {
            Vec3 cur = mc.player.getDeltaMovement();
            mc.player.setDeltaMovement(cur.x * 0.7, cur.y, cur.z * 0.7);
        }

        if (mc.player.input.jumping()) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(0.0, vertMult * 0.5, 0.0));
        } else if (mc.player.input.shiftKeyDown()) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(0.0, -vertMult * 0.5, 0.0));
        } else {
            Vec3 cur = mc.player.getDeltaMovement();
            mc.player.setDeltaMovement(new Vec3(cur.x, cur.y * 0.99, cur.z));
        }
    }
}
