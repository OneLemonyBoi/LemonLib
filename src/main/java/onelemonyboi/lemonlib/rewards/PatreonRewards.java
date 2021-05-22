package onelemonyboi.lemonlib.rewards;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PatreonRewards {
    /**
     BLACK
     DARK_BLUE
     DARK_GREEN
     DARK_AQUA
     DARK_RED
     DARK_PURPLE
     GOLD
     GRAY
     DARK_GRAY
     BLUE
     GREEN
     AQUA
     RED
     LIGHT_PURPLE
     YELLOW
     WHITE
     OBFUSCATED
     BOLD
     STRIKETHROUGH
     UNDERLINE
     ITALIC

     RAINBOW:
     RED
     GOLD
     YELLOW
     GREEN
     BLUE
     LIGHT_PURPLE
     DARK_PURPLE
     */

    public static void PatreonRewardsHandling(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            return;
        }

        Color[] rainbowArray = {color("E40300"), color("FF8D00"), color("FFEE00"), color("008121"), color("004BFF"), color("750088")};
        Color[] transArray = {color("59D0FA"), color("F5ABBA"), color("FFFFFF"), color("F5ABBA"), color("59D0FA")};
        Color[] biArray = {color("D70071"), color("9C4E98"), color("0035AA")};
        Color[] lesbianArray = {color("D62A00"), color("FF9B56"), color("FFFFFF"), color("D461A6"), color("A40062")};
        //Color[] asexualArray = {color("000000"), color("A4A4A4"), color("FFFFFF"), color("810081")};
        Color[] panArray = {color("FF1C8D"), color("FFD900"), color("1CB2FF")};
        Color[] queerArray = {color("B77EDD"), color("FFFFFF"), color("48821D")};
        Color[] nonBinaryArray = {color("FFF530"), color("FFFFFF"), color("9E58D2"), color("282828")};

        String name = event.player.getGameProfile().getName();
        IFormattableTextComponent iFormattableTextComponent = new StringTextComponent(name);
        String type = PatreonJSON.REWARD_MAP.getOrDefault(name, "No Value");
        if (!type.equals("No Value")) {
            switch (type) {
                case "Rainbow":
                    iFormattableTextComponent = formattingSetter(name, rainbowArray);
                    break;
                case "TransFlag":
                    iFormattableTextComponent = formattingSetter(name, transArray);
                    break;
                case "BiFlag":
                    iFormattableTextComponent = formattingSetter(name, biArray);
                    break;
                case "LesbianFlag":
                    iFormattableTextComponent = formattingSetter(name, lesbianArray);
                    break;
                //case "AsexualFlag":
                //    iFormattableTextComponent = formattingSetter(name, asexualArray);
                //    break;
                case "PanFlag":
                    iFormattableTextComponent = formattingSetter(name, panArray);
                    break;
                case "QueerFlag":
                    iFormattableTextComponent = formattingSetter(name, queerArray);
                    break;
                case "NonBinaryFlag":
                    iFormattableTextComponent = formattingSetter(name, nonBinaryArray);
                    break;
                default:
                    iFormattableTextComponent.setStyle(iFormattableTextComponent.getStyle().setColor(color(type)));
                    break;
            }
            java.util.Collection<IFormattableTextComponent> suffixes = new java.util.LinkedList<>();
            iFormattableTextComponent = suffixes.stream().reduce(iFormattableTextComponent, IFormattableTextComponent::appendSibling);
            event.player.setCustomName(iFormattableTextComponent);
            ObfuscationReflectionHelper.setPrivateValue(PlayerEntity.class, event.player, iFormattableTextComponent, "displayname");
        }
    }

    public static IFormattableTextComponent formattingSetter(String name, Color[] colors) {
        IFormattableTextComponent iFormattableTextComponent = new StringTextComponent("");
        int count = 0;
        for (Character c : name.toCharArray()) {
            IFormattableTextComponent tempFTC = new StringTextComponent(c.toString()).setStyle(iFormattableTextComponent.getStyle().setColor(colors[count]));
            iFormattableTextComponent.appendSibling(tempFTC);
            count = count == colors.length - 1 ? 0 : count + 1;
        }
        return iFormattableTextComponent;
    }

    public static Color color(String string) {
        return Color.fromHex("#".concat(string));
    }
}