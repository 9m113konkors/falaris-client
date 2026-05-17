package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

public class SilentAura extends Module {
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Aura attack range", 3.5, 1.0, 6.0, 0.1));
    public final BooleanSetting swingHand = addSetting(new BooleanSetting("Swing Hand", "Visual swing", true));
    public final BooleanSetting playersOnly = addSetting(new BooleanSetting("Players Only", "Only target players", true));
    public final BooleanSetting ignoreInvisible = addSetting(new BooleanSetting("Ignore Invisible", "Skip invisible entities", true));

    public SilentAura() {
        super("SilentAura", "Automatically attacks nearby entities", Category.COMBAT, "aura", "silent");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        for (net.minecraft.world.entity.Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player || !(entity instanceof LivingEntity le)) continue;
            if (!le.isAlive()) continue;
            if (entity.isInvisible() && ignoreInvisible.getValue()) continue;
            if (mc.player.distanceTo(entity) > range.getValue()) continue;

            if (playersOnly.getValue() && !(entity instanceof Player)) continue;

            if (mc.player.getAttackStrengthScale(0) >= 0.95) {
                mc.gameMode.attack(mc.player, entity);
                if (swingHand.getValue()) mc.player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }
}
