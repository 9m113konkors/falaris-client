package com.falaris.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public final class CombatUtil {
    private CombatUtil() {}

    public static boolean isAttackReady(LocalPlayer player, double minimumStrength) {
        return player != null && player.getAttackStrengthScale(0.0f) >= minimumStrength;
    }

    public static Player getCrosshairPlayer(Minecraft mc) {
        if (!(mc.hitResult instanceof EntityHitResult entityHitResult)) {
            return null;
        }

        Entity entity = entityHitResult.getEntity();
        if (entity instanceof Player player && entity != mc.player && player.isAlive()) {
            return player;
        }
        return null;
    }

    public static boolean isValidCombatTarget(LocalPlayer self, Player target, double range, double maxFov) {
        if (self == null || target == null || target == self || !target.isAlive()) {
            return false;
        }
        if (self.distanceTo(target) > range) {
            return false;
        }
        if (self.isAlliedTo(target)) {
            return false;
        }
        return maxFov >= 180.0 || getYawDifference(self, target) <= maxFov;
    }

    public static double getYawDifference(LocalPlayer self, Entity target) {
        double dx = target.getX() - self.getX();
        double dz = target.getZ() - self.getZ();
        double targetYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        double delta = wrapDegrees(targetYaw - self.getYRot());
        return Math.abs(delta);
    }

    public static float getPitchTo(LocalPlayer self, Entity target) {
        double dx = target.getX() - self.getX();
        double dy = target.getEyeY() - self.getEyeY();
        double dz = target.getZ() - self.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        return (float) -Math.toDegrees(Math.atan2(dy, horizontal));
    }

    public static float getYawTo(LocalPlayer self, Entity target) {
        double dx = target.getX() - self.getX();
        double dz = target.getZ() - self.getZ();
        return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
    }

    public static double wrapDegrees(double degrees) {
        double wrapped = degrees % 360.0;
        if (wrapped >= 180.0) {
            wrapped -= 360.0;
        }
        if (wrapped < -180.0) {
            wrapped += 360.0;
        }
        return wrapped;
    }

    public static boolean isMace(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(Items.MACE);
    }

    public static int getEnchantmentLevel(Minecraft mc, ItemStack stack, net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey) {
        if (mc.level == null) {
            return 0;
        }

        Holder<Enchantment> enchantment = mc.level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(enchantmentKey);
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
    }

    public static int getDensityLevel(Minecraft mc, ItemStack stack) {
        return getEnchantmentLevel(mc, stack, Enchantments.DENSITY);
    }

    public static int getBreachLevel(Minecraft mc, ItemStack stack) {
        return getEnchantmentLevel(mc, stack, Enchantments.BREACH);
    }

    public static boolean isCrosshairEntity(Minecraft mc) {
        return mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY;
    }
}
