package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.CombatUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class TriggerBot extends Module {
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Maximum range for trigger attacks", 3.2, 2.0, 6.0, 0.1));
    public final NumberSetting attackStrength = addSetting(new NumberSetting("Cooldown", "Minimum attack strength before attacking", 0.92, 0.1, 1.0, 0.02));
    public final BooleanSetting weaponOnly = addSetting(new BooleanSetting("Weapon Only", "Only trigger when holding a weapon", true));
    public final BooleanSetting swingHand = addSetting(new BooleanSetting("Swing Hand", "Swing after each hit", true));

    public TriggerBot() {
        super("TriggerBot", "Attacks when your crosshair is over a target", Category.COMBAT, "trigger");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gameMode == null) {
            return;
        }

        Player target = CombatUtil.getCrosshairPlayer(mc);
        if (target == null || mc.player.distanceTo(target) > range.getValue()) {
            return;
        }

        if (weaponOnly.getValue() && mc.player.getMainHandItem().isEmpty()) {
            return;
        }

        if (!CombatUtil.isAttackReady(mc.player, attackStrength.getValue())) {
            return;
        }

        mc.gameMode.attack(mc.player, target);
        if (swingHand.getValue()) {
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
