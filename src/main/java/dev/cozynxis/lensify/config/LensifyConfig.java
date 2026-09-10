package dev.cozynxis.lensify.config;

public final class LensifyConfig {
    public boolean enabled = true;
    public boolean toggleMode = false;
    public boolean smoothZoom = true;
    public boolean scrollZoom = true;
    public boolean scaleSensitivity = true;

    public double zoomFactor = 4.0;
    public double minZoomFactor = 1.5;
    public double maxZoomFactor = 16.0;
    public double scrollStep = 0.75;
    public double smoothSpeed = 10.0;
    public double sensitivityScale = 0.45;

    public void validate() {
        zoomFactor = clamp(zoomFactor, 1.1, 32.0);
        minZoomFactor = clamp(minZoomFactor, 1.0, 8.0);
        maxZoomFactor = clamp(maxZoomFactor, minZoomFactor, 32.0);
        scrollStep = clamp(scrollStep, 0.1, 4.0);
        smoothSpeed = clamp(smoothSpeed, 1.0, 30.0);
        sensitivityScale = clamp(sensitivityScale, 0.05, 1.0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
