package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.NumberSetting;

public class AimAssist extends Module {
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Assist speed", 0.5, 0.1, 2.0, 0.1));
    public AimAssist() { super("AimAssist", "Assist with aiming", Category.COMBAT, "aimassist"); }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        // find nearest valid target
        com.falaris.client.modules.ModuleManager mm = com.falaris.client.modules.ModuleManager.INSTANCE;
        com.falaris.client.modules.combat.KillAura dummy = mm.getModule(KillAura.class);
        double range = 4.5;
        if (dummy != null) range = dummy.range.getValue();
        // add reach module bonus if present
        com.falaris.client.modules.combat.Reach reach = mm.getModule(Reach.class);
        if (reach != null) range += reach.extra.getValue();

        // compute effective range including KillAura base and Reach bonus
        double effectiveRange = range;
        com.falaris.client.modules.combat.Reach reachMod = mm.getModule(Reach.class);
        if (reachMod != null) effectiveRange += reachMod.extra.getValue();

        net.minecraft.world.entity.player.Player target = null;
        double bestDist = Double.MAX_VALUE;
        for (net.minecraft.world.entity.player.Player p : mc.level.players()) {
            if (!com.falaris.client.utils.CombatUtil.isValidCombatTarget(mc.player, p, effectiveRange, 180.0)) continue;
            double d = mc.player.distanceTo(p);
            if (d < bestDist) { bestDist = d; target = p; }
        }
        if (target == null) return;

        float targetYaw = com.falaris.client.utils.CombatUtil.getYawTo(mc.player, target);
        float targetPitch = com.falaris.client.utils.CombatUtil.getPitchTo(mc.player, target);

        float currentYaw = mc.player.getYRot();
        float currentPitch = mc.player.getXRot();

        double yawDelta = com.falaris.client.utils.CombatUtil.wrapDegrees(targetYaw - currentYaw);
        double pitchDelta = com.falaris.client.utils.CombatUtil.wrapDegrees(targetPitch - currentPitch);

        double factor = Math.max(0.01, Math.min(1.0, speed.getValue()));
        float newYaw = (float) (currentYaw + yawDelta * factor * 0.2);
        float newPitch = (float) (currentPitch + pitchDelta * factor * 0.2);

        mc.player.setYRot(newYaw);
        mc.player.setXRot(newPitch);
    }
}
