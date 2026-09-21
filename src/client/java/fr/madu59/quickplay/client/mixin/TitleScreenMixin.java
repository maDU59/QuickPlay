package fr.madu59.quickplay.client.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;

import fr.madu59.quickplay.client.QuickPlayClient;
import fr.madu59.quickplay.client.config.SettingsManager;
import fr.madu59.quickplay.client.data.PlayedWorldData;
import fr.madu59.quickplay.client.gui.component.IconButton;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
        method = "createNormalMenuOptions",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/TitleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",
            ordinal = 0
        )
    )
    private <T extends GuiEventListener & Renderable & NarratableEntry> T quickplay$addRenderableWidget(TitleScreen instance, T widget, Operation<T> original, @Local(ordinal = 0) int topPos) {

        if(SettingsManager.QUICKPLAY_BUTTONS.getValue()){
            for(int i = 0; i < SettingsManager.QUICKPLAY_BUTTONS_COUNT.getValue(); i++){
                int index = i;
                PlayedWorldData data = QuickPlayClient.getLastPlayed(index);

                Identifier iconId = Identifier.withDefaultNamespace("textures/misc/unknown_server.png");
                Component name = Component.literal("Unknown");
                if(data != null){
                    if(data.isClientLevel()){
                        FaviconTexture icon = FaviconTexture.forWorld(this.minecraft.getTextureManager(), data.getId());
                        if(icon != null) {
                            if(validateIconFile(data.getIconPath())) loadIcon(icon, data.getIconPath());
                            iconId = icon.textureLocation();
                        }
                    }
                    else{
                        FaviconTexture icon = FaviconTexture.forServer(this.minecraft.getTextureManager(), data.getId());
                        if(icon != null) {
                            try {
                                icon.upload(NativeImage.read(data.getIconBytes()));
                            } catch (Throwable t) {

                            }
                            iconId = icon.textureLocation();
                        }
                    }
                    name = Component.literal(data.getServerName());

                    IconButton spriteIconButton = new IconButton(20, 20, name, 18, 18, 0, 0, new WidgetSprites(iconId), (var1) -> QuickPlayClient.resumeLastPlayed(index), name, null, true);
                    spriteIconButton.setPosition(width - 22, height - 32 - i * 22);
                    original.call(instance, spriteIconButton);
                }
            }
        }

		if(SettingsManager.CONTINUE_BUTTON.getValue() && QuickPlayClient.getLastPlayed(0) != null){
			Button singlePlayerButton = Button.builder(Component.translatable("menu.singleplayer"), (var1) -> this.minecraft.gui.setScreen(new SelectWorldScreen(this))).bounds(this.width / 2 + 2, topPos, 98, 20).build();

            Component continueButtonText = Component.translatable("quickplay.menu.continue");
            if(SettingsManager.CONTINUE_BUTTON_CUSTOM_TEXT.getValue()) continueButtonText = Component.literal(QuickPlayClient.getLastPlayed(0).getServerName());

            Button continueButton = Button.builder(continueButtonText, (var1) -> QuickPlayClient.resumeLastPlayed()).bounds(this.width / 2 - 100, topPos, 98, 20).build();
            if(SettingsManager.CONTINUE_BUTTON_TOOLTIP.getValue()) continueButton.setTooltip(Tooltip.create(Component.literal(QuickPlayClient.getLastPlayed(0).getServerName())));

            original.call(instance, continueButton);

			return original.call(instance, singlePlayerButton);
		}
		else return original.call(instance, widget);

    }

    @Unique
    private boolean validateIconFile(Path iconFile) {
        if (iconFile != null) {
            try {
                BasicFileAttributes attributes = Files.readAttributes(iconFile, BasicFileAttributes.class, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
                if (attributes.isSymbolicLink()) {
                    List<ForbiddenSymlinkInfo> issues = this.minecraft.directoryValidator().validateSymlink(iconFile);
                    if (!issues.isEmpty()) {
                        System.out.println(ContentValidationException.getMessage(iconFile, issues));
                        return false;
                    } else {
                        attributes = Files.readAttributes(iconFile, BasicFileAttributes.class, new LinkOption[0]);
                    }
                }

                if (!attributes.isRegularFile()) {
                    return false;
                }
                return true;
            } catch (NoSuchFileException e) {
                System.out.println(e);
                return false;
            } catch (IOException e) {
                System.out.println(e);
                return false;
            }
        }
        return false;
    }

    @Unique
    private void loadIcon(FaviconTexture icon, Path iconFile) {
        boolean shouldHaveIcon = iconFile != null && Files.isRegularFile(iconFile, new LinkOption[0]);
        if (shouldHaveIcon) {
            try {
                InputStream stream = Files.newInputStream(iconFile, new OpenOption[0]);

                try {
                    icon.upload(NativeImage.read(stream));
                } catch (Throwable var6) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }
                    }

                    throw var6;
                }

                if (stream != null) {
                    stream.close();
                }
            } catch (Throwable t) {
                iconFile = null;
                System.out.println(t);
            }
        } else {
            icon.clear();
            System.out.println("SHOULD NOT HAVE AN ICON");
        }
    }
	
}