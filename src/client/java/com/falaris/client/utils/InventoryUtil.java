package com.falaris.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public final class InventoryUtil {
    private InventoryUtil() {}

    public static int findInventorySlot(LocalPlayer player, Predicate<ItemStack> predicate, boolean hotbarOnly) {
        Inventory inventory = player.getInventory();
        int limit = hotbarOnly ? 9 : 36;
        for (int slot = 0; slot < limit; slot++) {
            if (predicate.test(inventory.getItem(slot))) {
                return slot;
            }
        }
        return -1;
    }

    public static int toMenuSlot(int inventorySlot) {
        if (inventorySlot >= 0 && inventorySlot < 9) {
            return 36 + inventorySlot;
        }
        return inventorySlot;
    }

    public static boolean swapSlotToOffhand(Minecraft mc, int inventorySlot) {
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null || inventorySlot < 0) {
            return false;
        }

        int menuSlot = toMenuSlot(inventorySlot);
        gameMode.handleInventoryMouseClick(player.containerMenu.containerId, menuSlot, 40, ClickType.SWAP, player);
        return true;
    }

    public static boolean swapMenuSlotToOffhand(Minecraft mc, int menuSlot) {
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || gameMode == null || menuSlot < 0) {
            return false;
        }

        gameMode.handleInventoryMouseClick(player.containerMenu.containerId, menuSlot, 40, ClickType.SWAP, player);
        return true;
    }

    public static int offhandMenuSlot() {
        return InventoryMenu.SHIELD_SLOT;
    }
}
