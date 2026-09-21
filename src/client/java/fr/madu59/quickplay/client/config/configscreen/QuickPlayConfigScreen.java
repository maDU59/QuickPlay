package fr.madu59.quickplay.client.config.configscreen;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

import fr.madu59.quickplay.client.config.SettingsManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class QuickPlayConfigScreen extends Screen {
    
    private MyConfigListWidget list;
    private final Screen parent;

    protected QuickPlayConfigScreen(Screen parent) {
        super(Component.literal("Quick Play configuration screen"));
        this.parent = parent;
    }

    public static void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                literal("quickPlayConfig")
                    .executes(context -> {
                        Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.setScreen(new QuickPlayConfigScreen(null)));
                        return 1;
                    })
            );
        });
    }

    @Override
    protected void init() {
        super.init();
        this.list = new MyConfigListWidget(this.minecraft, this.width, this.height - 80, 40, 26);

        this.list.category("quickplay.config.category.continue_button").build();
        this.list.button(SettingsManager.CONTINUE_BUTTON).build();

        this.list.category("quickplay.config.category.quickplay_buttons").build();
        this.list.button(SettingsManager.QUICKPLAY_BUTTONS).build();
        this.list.slider(SettingsManager.QUICKPLAY_BUTTONS_COUNT).range(1, 10).step(1).isEnabled(() -> SettingsManager.QUICKPLAY_BUTTONS.getValue()).build();

        Button doneButton = Button.builder(Component.translatable("quickplay.config.done"), b -> {
            this.minecraft.gui.setScreen(this.parent);
            SettingsManager.saveSettings();
        }).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build();

        this.addRenderableWidget(this.list);
        this.addRenderableWidget(doneButton);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
        SettingsManager.saveSettings();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.list.extractRenderState(context, mouseX, mouseY, delta);
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }
}
