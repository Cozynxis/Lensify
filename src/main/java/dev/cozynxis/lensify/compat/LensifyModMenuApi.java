package dev.cozynxis.lensify.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.cozynxis.lensify.screen.LensifyScreen;

public final class LensifyModMenuApi implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return LensifyScreen::new;
    }
}
