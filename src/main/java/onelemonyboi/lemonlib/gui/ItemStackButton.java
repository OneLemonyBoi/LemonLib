package onelemonyboi.lemonlib.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class ItemStackButton extends ExtendedButton {
    public Item item;
    public int xPos;
    public int yPos;
    protected final OnTooltip onTooltip;

    public ItemStackButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.item = Items.AIR;
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = (button, matrixStack, mouseX, mouseY) -> {};
    }

    public ItemStackButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, Item item) {
        super(xPos, yPos, width, height, displayString, handler);
        this.item = item;
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = (button, matrixStack, mouseX, mouseY) -> {};
    }

    public ItemStackButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, Item item, OnTooltip onTooltip) {
        super(xPos, yPos, width, height, displayString, handler);
        this.item = item;
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = onTooltip;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {
        super.renderBg(matrixStack, minecraft, mouseX, mouseY);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(item), xPos, yPos);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        if (this.isHovered) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
        this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
    }
}
