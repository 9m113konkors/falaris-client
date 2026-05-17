package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.InventoryUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class AutoEat extends Module {
    public final NumberSetting healthThreshold = addSetting(new NumberSetting("Health", "Health to eat at", 10.0, 1.0, 20.0, 0.5));
    public final BooleanSetting eatToFull = addSetting(new BooleanSetting("Eat to Full", "Eat until food bar is full", false));
    public final NumberSetting saturationThreshold = addSetting(new NumberSetting("Saturation", "Saturation to stop eating at", 18.0, 1.0, 20.0, 0.5));
    public final BooleanSetting onlyWhenSafe = addSetting(new BooleanSetting("Only When Safe", "Don't eat when taking damage", false));

    private boolean wasEating = false;

    public AutoEat() {
        super("AutoEat", "Automatically eats food when hungry or hurt", Category.MISC, "autoeat");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gameMode == null) return;
        Player player = mc.player;

        boolean shouldEat;
        if (eatToFull.getValue()) {
            shouldEat = player.getFoodData().getSaturationLevel() < saturationThreshold.getValue()
                     || player.getFoodData().getFoodLevel() < 20;
        } else {
            shouldEat = player.getHealth() <= healthThreshold.getValue()
                     || player.getFoodData().getFoodLevel() < 10;
        }

        if (onlyWhenSafe.getValue() && mc.level != null) {
            boolean safe = !mc.level.getEntities(player, player.getBoundingBox().inflate(3.0))
                    .stream().anyMatch(e -> e instanceof net.minecraft.world.entity.LivingEntity l && l != player && l.isAlive());
            if (!safe) {
                shouldEat = false;
            }
        }

        if (!shouldEat && wasEating) {
            stopEating(player);
            wasEating = false;
            return;
        }

        if (!shouldEat) return;

        int foodSlot = InventoryUtil.findInventorySlot(player,
                s -> !s.isEmpty() && s.getItem() instanceof net.minecraft.world.item.FoodItem, true);

        if (foodSlot < 0) {
            stopEating(player);
            wasEating = false;
            return;
        }

        if (mc.player.getInventory().getSelectedSlot() != foodSlot) {
            mc.player.getInventory().setSelectedSlot(foodSlot);
        }

        ItemStack held = mc.player.getMainHandItem();
        if (!held.isEmpty() && held.getItem() instanceof net.minecraft.world.item.FoodItem) {
            if (player.getUseItem() != held || player.getUseItemRemainingTicks() <= 0) {
                mc.gameMode.useItem(player, mc.level, held);
                wasEating = true;
            }
        }
    }

    private void stopEating(Player player) {
        if (player.isUsingItem()) {
            player.releaseUsingItem();
        }
    }
}
