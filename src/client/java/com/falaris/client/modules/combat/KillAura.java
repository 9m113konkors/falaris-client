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

        Player target = mc.level.players().stream()
                .filter(player -> CombatUtil.isValidCombatTarget(mc.player, player, range.getValue(), fov.getValue()))
                .min((left, right) -> Double.compare(mc.player.distanceTo(left), mc.player.distanceTo(right)))
                .orElse(null);

        if (target == null || !CombatUtil.isAttackReady(mc.player, attackStrength.getValue())) {
            return;
        }

        mc.gameMode.attack(mc.player, target);
        if (swingHand.getValue()) {
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
