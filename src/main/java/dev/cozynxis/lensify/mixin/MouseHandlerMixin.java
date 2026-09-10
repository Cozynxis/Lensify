package dev.cozynxis.lensify.mixin;

import dev.cozynxis.lensify.config.ConfigManager;
import dev.cozynxis.lensify.zoom.ZoomController;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "onScroll(JDD)V", at = @At("HEAD"), cancellable = true)
    private void lensify$scrollZoom(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        if (ZoomController.isActive() && ConfigManager.get().scrollZoom && yOffset != 0.0) {
            ZoomController.scroll(yOffset);
            ci.cancel();
        }
    }
}
