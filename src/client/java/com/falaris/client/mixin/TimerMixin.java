package com.falaris.client.mixin;

import com.falaris.client.modules.misc.Timer;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DeltaTracker.Timer.class)
public class TimerMixin {
    @ModifyArg(
            method = "advanceGameTime",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/floats/FloatUnaryOperator;apply(F)F"
            ),
            index = 0
    )
    private float falaris$adjustMsPerTick(float msPerTick) {
        return msPerTick / Timer.timerSpeed;
    }
}
