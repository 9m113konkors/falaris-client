package com.falaris.client.mixin;

import com.falaris.client.modules.misc.TimerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class TimerMixin {
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getTickTargetMillis(F)F"))
    private float redirectGetTickTargetMillis(Minecraft client, float tickTime) {
        return client.getDeltaTracker().getGameTimeDeltaTicks() / TimerModule.timerSpeed;
    }
}
