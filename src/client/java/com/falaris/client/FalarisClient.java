package com.falaris.client;

import com.falaris.client.config.Config;
import com.falaris.client.modules.ModuleManager;
import com.falaris.client.ui.clickgui.ClickGUI;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

public class FalarisClient implements ClientModInitializer {
    public static final String MOD_ID = "falaris";
    private boolean wasClickGuiKeyDown;

    @Override
    public void onInitializeClient() {
        Config.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            ModuleManager.INSTANCE.onTick();

            if (client.screen == null) {
                ModuleManager.INSTANCE.handleKeybinds();
            }

            boolean clickGuiKeyDown = InputConstants.isKeyDown(client.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
            if (clickGuiKeyDown && !wasClickGuiKeyDown) {
                if (client.screen instanceof ClickGUI) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new ClickGUI());
                }
            }

            wasClickGuiKeyDown = clickGuiKeyDown;
        });
    }
}
