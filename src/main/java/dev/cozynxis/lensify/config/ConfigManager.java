package dev.cozynxis.lensify.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("lensify.json");

    private static LensifyConfig config = new LensifyConfig();

    private ConfigManager() {}

    public static LensifyConfig get() {
        return config;
    }

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }

        try {
            String json = Files.readString(PATH);
            LensifyConfig loaded = GSON.fromJson(json, LensifyConfig.class);
            if (loaded != null) {
                config = loaded;
                config.validate();
            }
        } catch (Exception ignored) {
            config = new LensifyConfig();
        }
    }

    public static void save() {
        config.validate();
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException("Could not save Lensify config", e);
        }
    }

    public static void resetBalanced() {
        config = new LensifyConfig();
        save();
    }

    public static void applyPreset(String preset) {
        switch (preset.toLowerCase()) {
            case "soft" -> {
                config.zoomFactor = 2.5;
                config.smoothSpeed = 7.0;
                config.scrollStep = 0.5;
                config.sensitivityScale = 0.65;
            }
            case "deep" -> {
                config.zoomFactor = 8.0;
                config.smoothSpeed = 12.0;
                config.scrollStep = 1.0;
                config.sensitivityScale = 0.30;
            }
            default -> {
                config.zoomFactor = 4.0;
                config.smoothSpeed = 10.0;
                config.scrollStep = 0.75;
                config.sensitivityScale = 0.45;
            }
        }
        config.validate();
        save();
    }
}
