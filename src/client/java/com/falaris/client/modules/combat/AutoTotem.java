package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.InventoryUtil;
import net.minecraft.world.item.Items;

public class AutoTotem extends Module {
    public final NumberSetting healthThreshold = addSetting(new NumberSetting("Health Threshold", "Swap to a totem below this health", 10.0, 1.0, 20.0, 0.5));
    public final BooleanSetting alwaysHold = addSetting(new BooleanSetting("Always Hold", "Always keep a totem in the offhand", false));
    public final BooleanSetting fallDanger = addSetting(new BooleanSetting("Fall Danger", "Treat high fall distance as danger", true));
    public final BooleanSetting restoreItem = addSetting(new BooleanSetting("Restore Item", "Restore the previous offhand item when safe", false));
    private int restoreMenuSlot = -1;
    private int lastSwapTick = -20;

    public AutoTotem() {
        super("AutoTotem", "Moves a totem into the offhand when needed", Category.COMBAT, "totem");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gameMode == null || mc.player.tickCount - lastSwapTick < 5) {
            return;
        }

        boolean safe = !shouldUseTotem();
        boolean hasTotemEquipped = mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);
        if (safe && hasTotemEquipped && restoreItem.getValue() && restoreMenuSlot >= 0) {
            InventoryUtil.swapMenuSlotToOffhand(mc, restoreMenuSlot);
            restoreMenuSlot = -1;
            lastSwapTick = mc.player.tickCount;
            return;
        }

        if (!shouldUseTotem() || hasTotemEquipped) {
            return;
        }

        int slot = InventoryUtil.findInventorySlot(mc.player, stack -> stack.is(Items.TOTEM_OF_UNDYING), false);
        if (slot < 0) {
            return;
        }

        restoreMenuSlot = InventoryUtil.toMenuSlot(slot);
        InventoryUtil.swapSlotToOffhand(mc, slot);
        lastSwapTick = mc.player.tickCount;
    }

    private boolean shouldUseTotem() {
        if (mc.player == null) {
            return false;
        }

        if (alwaysHold.getValue()) {
            return true;
        }

        float combinedHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (combinedHealth <= healthThreshold.getValue()) {
            return true;
        }

        return fallDanger.getValue() && mc.player.fallDistance > 8.0f;
    }

    @Override
    public void onDisable() {
        restoreMenuSlot = -1;
    }
}
