package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TriggerBot extends Module {
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Attack range limit", 3.0, 1.0, 6.0, 0.1));
    public final BooleanSetting weaponOnly = addSetting(new BooleanSetting("Weapon Only", "Only attack when holding a weapon", true));
    public final BooleanSetting swingHand = addSetting(new BooleanSetting("Swing Hand", "Visual swing", true));

    public TriggerBot() { super("TriggerBot", "Automatically attacks crosshair targets", Category.COMBAT, "trigger", "bot"); }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gameMode == null) return;
        
        HitResult hit = mc.hitResult;
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target) {
            // check reach bonus
            double baseDist = mc.player.distanceTo(target);
            com.falaris.client.modules.combat.Reach reach = com.falaris.client.modules.ModuleManager.INSTANCE.getModule(com.falaris.client.modules.combat.Reach.class);
            double maxRange = range.getValue();
            if (reach != null) maxRange += reach.extra.getValue();
            if (baseDist <= maxRange && mc.player.getAttackStrengthScale(0) >= 1.0) {
                mc.gameMode.attack(mc.player, target);
                if (swingHand.getValue()) mc.player.swing(mc.player.getUsedItemHand());
            }
        }
    }
}
