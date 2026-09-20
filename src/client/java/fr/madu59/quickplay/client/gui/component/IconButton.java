package fr.madu59.quickplay.client.gui.component;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SpriteIconButton.CenteredIcon;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class IconButton extends CenteredIcon {

    public IconButton(int width, int height, Component message, int spriteWidth, int spriteHeight, int spriteOffsetX,
            int spriteOffsetY, WidgetSprites sprite, OnPress onPress, @Nullable Component tooltip,
            @Nullable CreateNarration narration, boolean switchToLoadingAfterPress) {
        super(width, height, message, spriteWidth, spriteHeight, spriteOffsetX, spriteOffsetY, sprite, onPress, tooltip, narration, switchToLoadingAfterPress);
    }

    @Override
    protected void extractSprite(final GuiGraphicsExtractor graphics, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.sprite.get(this.isActive(), this.isHoveredOrFocused()), x, y, 0, 0, this.spriteWidth, this.spriteHeight, this.spriteWidth, this.spriteHeight);
    }
    
}
