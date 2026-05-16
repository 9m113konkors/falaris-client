package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.ColorSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.Render3D;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ESP extends Module {
    public final ColorSetting color = addSetting(new ColorSetting("Color", "Box color for player ESP", 0xFFFFA31A));
    public final NumberSetting lineWidth = addSetting(new NumberSetting("Line Width", "ESP outline width", 2.0, 1.0, 4.0, 0.5));
    public final BooleanSetting showTeammates = addSetting(new BooleanSetting("Show Teammates", "Render teammates in ESP", true));

    public ESP() {
        super("ESP", "Renders player hitboxes", Category.VISUAL, "boxes", "player esp");
    }

    @Override
    public void onRender3D(Camera camera, float tickDelta) {
        if (mc.level == null || mc.player == null) {
            return;
        }

        PoseStack poseStack = new PoseStack();
        Vec3 cameraPos = camera.position();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Player player) || player == mc.player) {
                continue;
            }
            if (!showTeammates.getValue() && player.isAlliedTo(mc.player)) {
                continue;
            }

            double x = player.xo + (player.getX() - player.xo) * tickDelta - cameraPos.x;
            double y = player.yo + (player.getY() - player.yo) * tickDelta - cameraPos.y;
            double z = player.zo + (player.getZ() - player.zo) * tickDelta - cameraPos.z;
            AABB box = player.getBoundingBox()
                    .move(-player.getX(), -player.getY(), -player.getZ())
                    .move(x, y, z);

            Render3D.drawBox(poseStack, box, color.red(), color.green(), color.blue(), color.alpha(), lineWidth.getValue().floatValue());
        }
    }
}
