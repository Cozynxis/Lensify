package dev.cozynxis.lensify.zoom;

import dev.cozynxis.lensify.config.ConfigManager;
import dev.cozynxis.lensify.config.LensifyConfig;

public final class ZoomController {
    private static boolean active = false;
    private static boolean toggled = false;

    private static double desiredFactor = 1.0;
    private static double renderedFactor = 1.0;
    private static long lastFrameNanos = System.nanoTime();

    private ZoomController() {}

    public static void setHeld(boolean held) {
        LensifyConfig cfg = ConfigManager.get();
        if (!cfg.enabled || cfg.toggleMode) return;
        active = held;
        desiredFactor = held ? cfg.zoomFactor : 1.0;
    }

    public static void toggle() {
        LensifyConfig cfg = ConfigManager.get();
        if (!cfg.enabled || !cfg.toggleMode) return;
        toggled = !toggled;
        active = toggled;
        desiredFactor = active ? cfg.zoomFactor : 1.0;
    }

    public static boolean isActive() {
        return active && ConfigManager.get().enabled;
    }

    public static void stop() {
        active = false;
        toggled = false;
        desiredFactor = 1.0;
    }

    public static void scroll(double amount) {
        LensifyConfig cfg = ConfigManager.get();
        if (!isActive() || !cfg.scrollZoom || amount == 0) return;

        desiredFactor += Math.signum(amount) * cfg.scrollStep;
        desiredFactor = Math.max(cfg.minZoomFactor, Math.min(cfg.maxZoomFactor, desiredFactor));
    }

    public static float modifyFov(float vanillaFov) {
        LensifyConfig cfg = ConfigManager.get();

        double target = isActive() ? desiredFactor : 1.0;
        long now = System.nanoTime();
        double dt = Math.min((now - lastFrameNanos) / 1_000_000_000.0, 0.1);
        lastFrameNanos = now;

        if (!cfg.smoothZoom) {
            renderedFactor = target;
        } else {
            double alpha = 1.0 - Math.exp(-cfg.smoothSpeed * dt);
            renderedFactor += (target - renderedFactor) * alpha;
        }

        if (Math.abs(renderedFactor - 1.0) < 0.0005) renderedFactor = 1.0;
        renderedFactor = Math.max(1.0, Math.min(32.0, renderedFactor));

        return (float) Math.max(1.0, vanillaFov / renderedFactor);
    }

    public static double getRenderedFactor() {
        return renderedFactor;
    }

    public static double getDesiredFactor() {
        return desiredFactor;
    }

    public static void syncConfiguredFactor() {
        if (isActive()) {
            desiredFactor = ConfigManager.get().zoomFactor;
        }
    }
}
