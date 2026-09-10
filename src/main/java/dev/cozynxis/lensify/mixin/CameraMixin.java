package dev.cozynxis.lensify.mixin;

import dev.cozynxis.lensify.zoom.ZoomController;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "calculateFov(F)F", at = @At("RETURN"), cancellable = true)
    private void lensify$modifyFov(float partialTicks, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ZoomController.modifyFov(cir.getReturnValue()));
    }
}
