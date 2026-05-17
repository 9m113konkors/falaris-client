package com.falaris.client.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public final class Render3D {
    private Render3D() {}

    public static void drawBox(PoseStack ps, AABB box,
                               float red, float green, float blue, float alpha, float lineWidth) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || ps == null) return;

        LevelRenderer lr = mc.levelRenderer;
        if (lr == null) return;
        renderWireBox((com.mojang.blaze3d.vertex.VertexConsumer) lr.getBuffer(RenderType.lines()), ps.last().pose(),
                box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawLine(PoseStack ps,
                                float sx, float sy, float sz,
                                float ex, float ey, float ez,
                                float red, float green, float blue, float alpha, float lineWidth) {
        drawBox(ps,
                new AABB(sx, sy, sz, ex, ey, ez), red, green, blue, alpha, lineWidth);
    }

    private static void renderWireBox(com.mojang.blaze3d.vertex.VertexConsumer vc, Matrix4f m,
                                      double x0, double y0, double z0, double x1, double y1, double z1,
                                      float r, float g, float b, float a) {
        final int C = ((int)(a*255)<<24)|((int)(r*255)<<16)|((int)(g*255)<<8)|(int)(b*255);
        float fx0=(float)x0,fy0=(float)y0,fz0=(float)z0,
              fx1=(float)x1,fy1=(float)y1,fz1=(float)z1;
        // bottom
        vc.vertex(m,fx0,fy0,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy0,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy0,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy0,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy0,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy0,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy0,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy0,fz0).color(C).normal(0,1,0);
        // top
        vc.vertex(m,fx0,fy1,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy1,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy1,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy1,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy1,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy1,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy1,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy1,fz0).color(C).normal(0,1,0);
        // verticals
        vc.vertex(m,fx0,fy0,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy1,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy0,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy1,fz0).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy0,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx1,fy1,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy0,fz1).color(C).normal(0,1,0);
        vc.vertex(m,fx0,fy1,fz1).color(C).normal(0,1,0);
    }
}
