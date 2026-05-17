package com.falaris.client.modules.movement;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {
    public final BooleanSetting packet = addSetting(new BooleanSetting("Packet", "Send packet to negate fall damage", true));

    public NoFall() { super("NoFall", "Prevents fall damage", Category.MOVEMENT, "nofall"); }

    @Override
    public void onTick() {
        if (mc.player != null && mc.player.fallDistance > 2.0f) {
            if (packet.getValue()) {
                mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, true));
            }
        }
    }
}
