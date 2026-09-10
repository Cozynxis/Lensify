package dev.cozynxis.lensify.screen;

import dev.cozynxis.lensify.config.ConfigManager;
import dev.cozynxis.lensify.config.LensifyConfig;
import dev.cozynxis.lensify.zoom.ZoomController;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class LensifyScreen extends Screen {
    private final Screen parent;

    public LensifyScreen(Screen parent) {
        super(Component.literal("Lensify Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - 155;
        int right = this.width / 2 + 5;
        int y = this.height / 2 - 100;
        int w = 150;
        int h = 20;

        LensifyConfig cfg = ConfigManager.get();

        addRenderableWidget(Button.builder(label("Enabled", cfg.enabled), b -> {
            cfg.enabled = !cfg.enabled;
            b.setMessage(label("Enabled", cfg.enabled));
            if (!cfg.enabled) ZoomController.stop();
        }).bounds(left, y, w, h).build());

        addRenderableWidget(Button.builder(label("Mode", cfg.toggleMode ? "Toggle" : "Hold"), b -> {
            cfg.toggleMode = !cfg.toggleMode;
            ZoomController.stop();
            b.setMessage(label("Mode", cfg.toggleMode ? "Toggle" : "Hold"));
        }).bounds(right, y, w, h).build());

        y += 24;

        addRenderableWidget(Button.builder(label("Smooth", cfg.smoothZoom), b -> {
            cfg.smoothZoom = !cfg.smoothZoom;
            b.setMessage(label("Smooth", cfg.smoothZoom));
        }).bounds(left, y, w, h).build());

        addRenderableWidget(Button.builder(label("Scroll zoom", cfg.scrollZoom), b -> {
            cfg.scrollZoom = !cfg.scrollZoom;
            b.setMessage(label("Scroll zoom", cfg.scrollZoom));
        }).bounds(right, y, w, h).build());

        y += 24;

        addRenderableWidget(Button.builder(zoomText(cfg), b -> {
            cfg.zoomFactor += 1.0;
            if (cfg.zoomFactor > 10.0) cfg.zoomFactor = 2.0;
            ZoomController.syncConfiguredFactor();
            b.setMessage(zoomText(cfg));
        }).bounds(left, y, w, h).build());

        addRenderableWidget(Button.builder(speedText(cfg), b -> {
            cfg.smoothSpeed += 2.0;
            if (cfg.smoothSpeed > 18.0) cfg.smoothSpeed = 4.0;
            b.setMessage(speedText(cfg));
        }).bounds(right, y, w, h).build());

        y += 24;

        addRenderableWidget(Button.builder(label("Sensitivity scaling", cfg.scaleSensitivity), b -> {
            cfg.scaleSensitivity = !cfg.scaleSensitivity;
            b.setMessage(label("Sensitivity scaling", cfg.scaleSensitivity));
        }).bounds(left, y, w, h).build());

        addRenderableWidget(Button.builder(Component.literal("Preset: Soft"), b -> {
            ConfigManager.applyPreset("soft");
            rebuildWidgets();
        }).bounds(right, y, w, h).build());

        y += 24;

        addRenderableWidget(Button.builder(Component.literal("Preset: Balanced"), b -> {
            ConfigManager.applyPreset("balanced");
            rebuildWidgets();
        }).bounds(left, y, w, h).build());

        addRenderableWidget(Button.builder(Component.literal("Preset: Deep"), b -> {
            ConfigManager.applyPreset("deep");
            rebuildWidgets();
        }).bounds(right, y, w, h).build());

        y += 34;

        addRenderableWidget(Button.builder(Component.literal("Reset"), b -> {
            ConfigManager.resetBalanced();
            ZoomController.stop();
            rebuildWidgets();
        }).bounds(left, y, w, h).build());

        addRenderableWidget(Button.builder(Component.literal("Save & Done"), b -> onClose())
                .bounds(right, y, w, h).build());
    }

    private void rebuildWidgets() {
        clearWidgets();
        init();
    }

    private static Component label(String name, boolean value) {
        return Component.literal(name + ": " + (value ? "ON" : "OFF"));
    }

    private static Component label(String name, String value) {
        return Component.literal(name + ": " + value);
    }

    private static Component zoomText(LensifyConfig cfg) {
        return Component.literal("Zoom: " + String.format("%.1fx", cfg.zoomFactor));
    }

    private static Component speedText(LensifyConfig cfg) {
        return Component.literal("Smooth speed: " + String.format("%.0f", cfg.smoothSpeed));
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }
}
