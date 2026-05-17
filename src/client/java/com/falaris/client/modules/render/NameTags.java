package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.CombatUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;

public class NameTags extends Module {
    public final BooleanSetting showArmor  = addSetting(new BooleanSetting("Show Armor",   "Render equipped gear",                false));
    public final BooleanSetting showHealth = addSetting(new BooleanSetting("Show Health", "Show health under name tag",          true));
    public final BooleanSetting background  = addSetting(new BooleanSetting("Background",  "Filled panel behind text",            true));
    public final NumberSetting scale       = addSetting(new NumberSetting("Scale",        "Tag scale",                          1.0, 0.5, 2.0, 0.1));
    public final BooleanSetting targetsOnly= addSetting(new BooleanSetting("Targets Only","Only show for in-range targets",      false));

    public NameTags() {
        super("NameTags", "Enhanced name tags above player heads", Category.VISUAL, "nametags");
    }

    @Override
    public void onRender3D(Camera camera, float tickDelta) {
        if (mc.level == null || mc.player == null) return;
        MultiBufferSource source = mc.renderBuffers().bufferSource();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity le) || le == mc.player) continue;
            if (!CombatUtil.isValidCombatTarget(mc.player, le, 64.0, 180.0)) continue;

            String name = (le instanceof Player p) ? p.getName().getString() : le.getType().getName().getString();
            float hp = le.getHealth() / Math.max(le.getMaxHealth(), 0.01f);
            renderNametag(camera, tickDelta, le, name, hp, source);
        }
    }

    private void renderNametag(Camera camera, float tickDelta, LivingEntity le,
                               String name, float hp, MultiBufferSource source) {
        Vec3 cam = camera.position();
        double x  = le.xo + (le.getX() - le.xo) * tickDelta - cam.x;
        double y  = le.yo + (le.getY() - le.yo) * tickDelta - cam.y + le.getBbHeight() + 0.9;
        double z  = le.zo + (le.getZ() - le.zo) * tickDelta - cam.z;
        double d  = Math.sqrt(x * x + z * z);
        if (d < 2.5) return;

        float sc   = scale.getValue().floatValue() * 0.025f;
        Font font  = mc.font;
        float nameW = font.width(name);
        String hpStr = String.format("%.0fHP", le.getHealth());
        float hpW    = font.width(hpStr);
        float contentW = Math.max(nameW, hpW);

        PoseStack ps = new PoseStack();
        ps.pushPose();
        ps.translate((float)x, (float)y, (float)z);
        ps.mulPose(camera.rotation());
        ps.scale(sc, sc, sc);

        float cx = -contentW / 2.0f;
        float lineH = font.lineHeight;
        float th = lineH * (showHealth.getValue() ? 2.0f : 1.0f) + 4;

        ps.pushPose();
        ps.translate(cx - 2, -th / 2.0f, 0);

        if (background.getValue()) {
            source.getBuffer(RenderType.gui())
                  .vertex(0, -2, 0).color(30, 30, 30, 170).normal(0, 1, 0)
                  .vertex(contentW + 4, -2, 0).color(30, 30, 30, 170).normal(0, 1, 0)
                  .vertex(contentW + 4, th + 2, 0).color(30, 30, 30, 170).normal(0, 1, 0)
                  .vertex(0, th + 2, 0).color(30, 30, 30, 170).normal(0, 1, 0)
                  .endVertex();
        }

        font.drawInBatch(name, 0, 0, 0xFFFFFFFF, false, ps.last().pose(), source, false, 0, 1115680, false);
        if (showHealth.getValue()) {
            int g = (int)(hp * 255); int r = 255 - g;
            font.drawInBatch(hpStr, 0, lineH + 2, (r << 16) | (g << 8) | 0xFF, false, ps.last().pose(), source, false, 0, 1115680, false);
        }

        ps.popPose();
        ps.popPose();
    }
}
