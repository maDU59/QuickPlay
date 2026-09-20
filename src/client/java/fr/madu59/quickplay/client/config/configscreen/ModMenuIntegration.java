package fr.madu59.quickplay.client.config.configscreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
        @Override
        public ConfigScreenFactory<QuickPlayConfigScreen> getModConfigScreenFactory() {
                return QuickPlayConfigScreen::new;
        }
}