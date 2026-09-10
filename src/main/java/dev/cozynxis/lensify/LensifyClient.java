package dev.cozynxis.lensify;

import com.mojang.blaze3d.platform.InputConstants;
import dev.cozynxis.lensify.config.ConfigManager;
import dev.cozynxis.lensify.screen.LensifyScreen;
import dev.cozynxis.lensify.zoom.ZoomController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class LensifyClient implements ClientModInitializer {
    private static KeyMapping zoomKey;
    private static KeyMapping settingsKey;
    private static double originalSensitivity = -1.0;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();

        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lensify.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KeyMapping.Category.MISC));
        settingsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lensify.settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var cfg = ConfigManager.get();

            if (!cfg.enabled) {
                ZoomController.stop();
            } else if (cfg.toggleMode) {
                while (zoomKey.consumeClick()) ZoomController.toggle();
            } else {
                ZoomController.setHeld(zoomKey.isDown());
            }

            while (settingsKey.consumeClick()) {
                client.gui.setScreen(new LensifyScreen(client.gui.screen()));
            }

            if (client.player == null) ZoomController.stop();

            if (cfg.scaleSensitivity && ZoomController.isActive()) {
                if (originalSensitivity < 0.0) originalSensitivity = client.options.sensitivity().get();
                double factor = Math.max(1.0, ZoomController.getRenderedFactor());
                double scaled = originalSensitivity * Math.max(0.08, cfg.sensitivityScale / Math.sqrt(factor / 2.0));
                client.options.sensitivity().set(Math.max(0.01, Math.min(1.0, scaled)));
            } else if (originalSensitivity >= 0.0) {
                client.options.sensitivity().set(originalSensitivity);
                originalSensitivity = -1.0;
            }
        });
    }
}
