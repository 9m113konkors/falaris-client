package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;

public class AutoReconnect extends Module {
    public final NumberSetting reconnectDelay = addSetting(new NumberSetting("Reconnect Delay", "Seconds to wait before rejoin", 3.0, 1.0, 30.0, 1.0));
    public final BooleanSetting keepPreviousServer = addSetting(new BooleanSetting("Keep Server", "Reconnect to last joined server", true));
    public final BooleanSetting autoReconnect = addSetting(new BooleanSetting("Auto Reconnect", "Automatically reconnect on disconnect", true));

    private String lastIp = "";
    private int countdown = -1;

    public AutoReconnect() {
        super("AutoReconnect", "Automatically reconnects to server after disconnect", Category.MISC, "autoreconnect");
    }

    @Override
    public void onTick() {
        if (mc.player != null) return;

        if (!autoReconnect.getValue()) return;

        boolean connected = isConnected();
        ServerData data = mc.getCurrentServer();

        if (data != null && !connected) {
            if (!keepPreviousServer.getValue()) {
                return;
            }
            if (lastIp.isEmpty() && data.ip != null) lastIp = data.ip;
            countdown = (int) (reconnectDelay.getValue() * 20);
        }

        if (countdown > 0) {
            countdown--;
            if (countdown <= 0 && !lastIp.isEmpty()) {
                try {
                    String ip = lastIp.endsWith("\u0000") || lastIp.contains("\u0000")
                            ? lastIp.substring(0, lastIp.indexOf('\u0000')) : lastIp;
                    ip = ip.trim();
                    if (!ip.isEmpty()) {
                        mc.setScreen(new ConnectScreen(mc.screen, mc,
                                new ServerData("Server", ip, false)));
                        countdown = -1;
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private boolean isConnected() {
        return mc.getCurrentServer() != null && mc.getCurrentServer().isChatDisabled() != null;
    }
}
