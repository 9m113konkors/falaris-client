package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Menu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

public class AutoSteal extends Module {
    public final BooleanSetting autoExit = addSetting(new BooleanSetting("Auto Exit", "Close chest when empty", true));
    public final NumberSetting stealDelay = addSetting(new NumberSetting("Delay", "Ticks between item takes", 4, 1, 20, 1));

    private int stealTicks = 0;

    public AutoSteal() {
        super("AutoSteal", "Automatically takes all items from chests", Category.MISC, "cheststealer");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.gameMode == null) return;
        Player player = mc.player;

        if (--stealTicks > 0) return;

        Menu menu = player.containerMenu;
        if (!(menu instanceof ChestMenu chest)) return;
        if (menu.containerId == player.getInventory().containerId) return;

        int containerSlots = chest.getRowCount() * 9;
        for (int i = 0; i < containerSlots; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (stack.isEmpty()) continue;
            if (mc.gameMode != null) {
                mc.gameMode.handleInventoryMouseClick(menu.containerId, i, 0,
                        net.minecraft.world.inventory.ClickType.QUICK_MOVE, player);
            }
            stealTicks = stealDelay.getValue().intValue();
            return;
        }

        if (autoExit.getValue()) {
            player.closeContainer();
        }
    }
}
