package onelemonyboi.lemonlib.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class InvisibleButton extends ExtendedButton {
    public int xPos;
    public int yPos;
    protected final OnTooltip onTooltip;

    public InvisibleButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = (button, matrixStack, mouseX, mouseY) -> {};
    }

    public InvisibleButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, OnTooltip onTooltip) {
        super(xPos, yPos, width, height, displayString, handler);
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = onTooltip;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
        this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
    }
}
