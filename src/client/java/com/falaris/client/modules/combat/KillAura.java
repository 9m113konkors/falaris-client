package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.CombatUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class KillAura extends Module {
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Target range", 3.3, 2.0, 6.0, 0.1));
    public final NumberSetting fov = addSetting(new NumberSetting("FOV", "Targeting field of view", 90.0, 30.0, 180.0, 5.0));
    public final NumberSetting attackStrength = addSetting(new NumberSetting("Cooldown", "Minimum attack strength before swinging", 0.92, 0.1, 1.0, 0.02));
    public final BooleanSetting swingHand = addSetting(new BooleanSetting("Swing Hand", "Swing after each attack", true));

    public KillAura() {
        super("KillAura", "Attacks the closest target in range", Category.COMBAT, "aura");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) {
            return;
        }

        // include Reach bonus if available
        double effectiveRange = range.getValue();
        com.falaris.client.modules.combat.Reach reach = com.falaris.client.modules.ModuleManager.INSTANCE.getModule(com.falaris.client.modules.combat.Reach.class);
        if (reach != null) effectiveRange += reach.extra.getValue();

        Player target = null;
        double best = Double.MAX_VALUE;
        for (Player p : mc.level.players()) {
            if (!CombatUtil.isValidCombatTarget(mc.player, p, effectiveRange, fov.getValue())) continue;
            double d = mc.player.distanceTo(p);
            if (d < best) { best = d; target = p; }
        }

        if (target == null || !CombatUtil.isAttackReady(mc.player, attackStrength.getValue())) {
            return;
        }

        mc.gameMode.attack(mc.player, target);
        if (swingHand.getValue()) {
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
