package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.CombatUtil;
import com.falaris.client.utils.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CrystalAura extends Module {
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Target range to place crystals", 5.0, 1.0, 10.0, 0.5));
    public final NumberSetting placeRange = addSetting(new NumberSetting("Place Range", "How far to place from target center", 3.0, 1.0, 6.0, 0.5));
    public final BooleanSetting explodeCrystals = addSetting(new BooleanSetting("Explode Crystals", "Break near enemies to damage them", true));
    public final BooleanSetting autoSwitch = addSetting(new BooleanSetting("Auto Switch", "Switch to crystal in hotbar", true));
    public final BooleanSetting hitOwnCrystals = addSetting(new BooleanSetting("Hit Own Crystals", "Attack own crystals too", false));

    private int placeDelay = 0;
    private int breakDelay = 0;

    public CrystalAura() {
        super("CrystalAura", "Auto places and explodes end crystals near targets", Category.COMBAT, "ca");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        if (placeDelay > 0) placeDelay--;
        if (breakDelay > 0) breakDelay--;

        LivingEntity target = findTarget();
        if (target == null) return;

        if (explodeCrystals.getValue()) {
            for (Entity e : mc.level.entitiesForRendering()) {
                if (e instanceof EndCrystal crystal) {
                    if (crystal.isRemoved()) continue;
                    double dist = crystal.distanceTo(target);
                    if (dist <= range.getValue() && (hitOwnCrystals.getValue() || crystal.getOwner() != mc.player)) {
                        if (breakDelay <= 0) {
                            mc.gameMode.attack(mc.player, crystal);
                            breakDelay = 2;
                            if (breakDelay > 0) breakDelay--;
                        }
                    }
                }
            }
        }

        if (placeDelay <= 0) {
            BlockPos targetPos = target.blockPosition();
            BlockPos[] offsets = {
                targetPos.north(), targetPos.south(), targetPos.east(), targetPos.west(),
                targetPos.north().east(), targetPos.north().west(), targetPos.south().east(), targetPos.south().west()
            };

            for (BlockPos pos : offsets) {
                if (!mc.level.getBlockState(pos).isAir() || !mc.level.getBlockState(pos.above()).isAir()) continue;
                double dist = mc.player.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                if (dist > placeRange.getValue() * placeRange.getValue()) continue;

                if (autoSwitch.getValue()) {
                    int crystalSlot = InventoryUtil.findInventorySlot(mc.player,
                            stack -> stack.is(Items.END_CRYSTAL), true);
                    if (crystalSlot >= 0) {
                        mc.player.getInventory().setSelectedSlot(crystalSlot);
                    } else {
                        continue;
                    }
                }

                net.minecraft.core.Direction dir = net.minecraft.core.Direction.UP;
                com.mojang.datafixers.util.Pair<net.minecraft.world.InteractionHand, net.minecraft.world.InteractionResult> result =
                    mc.gameMode.useItemOn(mc.player, mc.level,
                            Items.END_CRYSTAL.getDefaultInstance(), net.minecraft.world.InteractionHand.MAIN_HAND);
                if (result != null && result.getSecond().consumesAction()) {
                    placeDelay = 4;
                }
                return;
            }
        }
    }

    private LivingEntity findTarget() {
        LivingEntity best = null;
        double bestDist = range.getValue();
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e instanceof LivingEntity le && le != mc.player && le.isAlive() && CombatUtil.isValidCombatTarget(mc.player, le, bestDist, 180.0)) {
                double dist = mc.player.distanceTo(le);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = le;
                }
            }
        }
        return best;
    }
}
