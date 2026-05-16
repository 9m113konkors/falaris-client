package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.EnumSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.CombatUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class SilentAura extends Module {
    public enum RotationMode {
        NONE,
        SNAP
    }

    public enum PriorityMode {
        DISTANCE,
        LOW_HEALTH
    }

    public final NumberSetting range = addSetting(new NumberSetting("Range", "Maximum target range", 3.4, 2.0, 6.0, 0.1));
    public final NumberSetting fov = addSetting(new NumberSetting("FOV", "Targeting field of view", 100.0, 35.0, 180.0, 5.0));
    public final NumberSetting attackStrength = addSetting(new NumberSetting("Cooldown", "Minimum attack strength before attacking", 0.94, 0.1, 1.0, 0.02));
    public final EnumSetting<RotationMode> rotationMode = addSetting(new EnumSetting<>("Rotation", "How the aura handles aim", RotationMode.NONE, RotationMode.values()));
    public final EnumSetting<PriorityMode> priority = addSetting(new EnumSetting<>("Priority", "Target sort order", PriorityMode.DISTANCE, PriorityMode.values()));
    public final BooleanSetting swingHand = addSetting(new BooleanSetting("Swing Hand", "Swing after each attack", true));

    public SilentAura() {
        super("SilentAura", "Balanced aura with optional rotation snaps", Category.COMBAT, "saura", "assist aura");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) {
            return;
        }

        Player target = mc.level.players().stream()
                .filter(player -> CombatUtil.isValidCombatTarget(mc.player, player, range.getValue(), fov.getValue()))
                .min((left, right) -> {
                    if (priority.getValue() == PriorityMode.LOW_HEALTH) {
                        return Float.compare(left.getHealth(), right.getHealth());
                    }
                    return Double.compare(mc.player.distanceTo(left), mc.player.distanceTo(right));
                })
                .orElse(null);

        if (target == null || !CombatUtil.isAttackReady(mc.player, attackStrength.getValue())) {
            return;
        }

        if (rotationMode.getValue() == RotationMode.SNAP) {
            mc.player.setYRot(CombatUtil.getYawTo(mc.player, target));
            mc.player.setXRot(CombatUtil.getPitchTo(mc.player, target));
        }

        mc.gameMode.attack(mc.player, target);
        if (swingHand.getValue()) {
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
