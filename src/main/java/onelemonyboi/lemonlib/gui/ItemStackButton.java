package onelemonyboi.lemonlib.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.gui.widget.button.Button.ITooltip;

public class ItemStackButton extends ExtendedButton {
    public Item item;
    public int xPos;
    public int yPos;
    protected final ITooltip onTooltip;

    public ItemStackButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.item = Items.AIR;
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = (button, matrixStack, mouseX, mouseY) -> {};
    }

    public ItemStackButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler, Item item) {
        super(xPos, yPos, width, height, displayString, handler);
        this.item = item;
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = (button, matrixStack, mouseX, mouseY) -> {};
    }

    public ItemStackButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler, Item item, ITooltip onTooltip) {
        super(xPos, yPos, width, height, displayString, handler);
        this.item = item;
        this.xPos = xPos;
        this.yPos = yPos;
        this.onTooltip = onTooltip;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {
        super.renderBg(matrixStack, minecraft, mouseX, mouseY);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(item), xPos, yPos);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
    }
}
