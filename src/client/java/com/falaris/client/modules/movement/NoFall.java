package com.falaris.client.modules.movement;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {
    public final BooleanSetting alwaysActive = addSetting(new BooleanSetting("Always Active", "Continuously cancel fall damage while falling", false));
    public final BooleanSetting packetMode = addSetting(new BooleanSetting("Packet Mode", "Use network packets (more anti-kb safe)", false));
    private boolean wasFalling = false;

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT, "nofall");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.connection == null) return;

        if (packetMode.getValue()) {
            if (mc.player.fallDistance > 2.0f) {
                mc.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, true));
                wasFalling = true;
            } else if (wasFalling) {
                wasFalling = false;
            }
        } else {
            if (mc.player.fallDistance > 2.0f) {
                if (wasFalling || alwaysActive.getValue()) {
                    mc.connection.send(new ServerboundMovePlayerPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYRot(), mc.player.getXRot(), mc.player.isOnGround(), mc.player.horizontalCollision));
                }
                wasFalling = true;
            } else {
                wasFalling = false;
            }
        }
    }
}
