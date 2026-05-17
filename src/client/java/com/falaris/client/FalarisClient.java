package com.falaris.client;

import com.falaris.client.ui.clickgui.ClickGUI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.lwjgl.glfw.GLFW;

public class FalarisClient implements ClientModInitializer {
    private boolean wasClickGuiKeyDown;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> onTick(client));
    }

    private void onTick(Minecraft client) {
        if (client.player == null) return;
        // Handle module keybinds and per-tick module logic
        com.falaris.client.modules.ModuleManager.INSTANCE.handleKeybinds();
        com.falaris.client.modules.ModuleManager.INSTANCE.onTick();

        boolean clickGuiKeyDown = InputConstants.isKeyDown(client.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
        if (clickGuiKeyDown && !wasClickGuiKeyDown) {
            if (!(client.screen instanceof ChatScreen)) {
                if (client.screen instanceof ClickGUI) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new ClickGUI());
                }
            }
        }

        wasClickGuiKeyDown = clickGuiKeyDown;
    }
}
