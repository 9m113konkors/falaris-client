package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.CombatUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class BowAimbot extends Module {
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Max bow attack range", 35.0, 10.0, 80.0, 1.0));
    public final BooleanSetting predictTarget = addSetting(new BooleanSetting("Predict Target", "Lead moving targets", true));
    public final BooleanSetting onlyDrawn = addSetting(new BooleanSetting("Only Drawn", "Only aim while bow is fully drawn", true));
    public final NumberSetting aimSpeed = addSetting(new NumberSetting("Aim Speed", "How fast aim snaps to target", 0.5, 0.1, 1.0, 0.05));

    public BowAimbot() {
        super("BowAimbot", "Predictive aim assist for bows", Category.COMBAT, "bow");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null) return;

        if (onlyDrawn.getValue()) {
            var held = mc.player.getUseItem();
            if (held == null || !(held.getItem() instanceof net.minecraft.world.item.BowItem)) return;
            if (!mc.player.isUsingItem()) return;
        }

        Player target = findTarget();
        if (target == null) return;

        Vec3 aimPos = predictAimPos(target);
        float targetYaw   = CombatUtil.getYawTo(mc.player, aimPos);
        float targetPitch = CombatUtil.getPitchTo(mc.player, aimPos);
        float curYaw   = mc.player.getYRot();
        float curPitch = mc.player.getXRot();
        double factor  = Math.max(0.01, Math.min(1.0, aimSpeed.getValue()));

        float newYaw   = (float) (curYaw   + CombatUtil.wrapDegrees(targetYaw   - curYaw)   * factor * 0.08f);
        float newPitch = (float) Math.max(-90.0, Math.min(90.0,
                curPitch + CombatUtil.wrapDegrees(targetPitch - curPitch) * factor * 0.08f));

        mc.player.setYRot(newYaw);
        mc.player.setXRot(newPitch);
    }

    private Player findTarget() {
        Player best = null;
        double bestDist = range.getValue();
        for (Entity e : mc.level.entitiesForRendering()) {
            if (!(e instanceof Player p) || p == mc.player || !p.isAlive()) continue;
            if (!CombatUtil.isValidCombatTarget(mc.player, p, bestDist, 180.0)) continue;
            double d = mc.player.distanceTo(p);
            if (d < bestDist) {
                bestDist = d;
                best = p;
            }
        }
        return best;
    }

    private Vec3 predictAimPos(Player target) {
        Vec3 head = target.position().add(0, target.getEyeHeight(), 0);
        if (!predictTarget.getValue()) return head;

        double speed = 3.08;
        double grav  = 0.05;
        double dx = head.x - mc.player.getX();
        double dz = head.z - mc.player.getZ();
        double distH = Math.sqrt(dx*dx + dz*dz);
        double time  = Math.max(1.0, distH / Math.max(speed, 0.01));

        Vec3 vel = target.getDeltaMovement();
        return head.add(vel.x*time, vel.y*time + 0.5*grav*time*time, vel.z*time);
    }
}
