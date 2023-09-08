package onelemonyboi.lemonlib.rewards;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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

        TextColor[] rainbowArray = {color("E40300"), color("FF8D00"), color("FFEE00"), color("008121"), color("004BFF"), color("750088")};
        TextColor[] transArray = {color("59D0FA"), color("F5ABBA"), color("FFFFFF"), color("F5ABBA"), color("59D0FA")};
        TextColor[] biArray = {color("D70071"), color("9C4E98"), color("0035AA")};
        TextColor[] lesbianArray = {color("D62A00"), color("FF9B56"), color("FFFFFF"), color("D461A6"), color("A40062")};
        TextColor[] asexualArray = {color("000000"), color("A4A4A4"), color("FFFFFF"), color("810081")};
        TextColor[] panArray = {color("FF1C8D"), color("FFD900"), color("1CB2FF")};
        TextColor[] queerArray = {color("B77EDD"), color("FFFFFF"), color("48821D")};
        TextColor[] nonBinaryArray = {color("FFF530"), color("FFFFFF"), color("9E58D2"), color("282828")};

        String name = event.player.getGameProfile().getName();
        MutableComponent iFormattableTextComponent = Component.literal(name);
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
                case "AsexualFlag":
                    iFormattableTextComponent = formattingSetter(name, asexualArray);
                    break;
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
                    iFormattableTextComponent.setStyle(iFormattableTextComponent.getStyle().withColor(color(type)));
                    break;
            }
            java.util.Collection<MutableComponent> suffixes = new java.util.LinkedList<>();
            iFormattableTextComponent = suffixes.stream().reduce(iFormattableTextComponent, MutableComponent::append);
            event.player.setCustomName(iFormattableTextComponent);
            ObfuscationReflectionHelper.setPrivateValue(Player.class, event.player, iFormattableTextComponent, "displayname");
        }
    }

    public static MutableComponent formattingSetter(String name, TextColor[] colors) {
        MutableComponent iFormattableTextComponent = Component.literal("");
        int count = 0;
        for (Character c : name.toCharArray()) {
            MutableComponent tempFTC = Component.literal(c.toString()).setStyle(iFormattableTextComponent.getStyle().withColor(colors[count]));
            iFormattableTextComponent.append(tempFTC);
            count = count == colors.length - 1 ? 0 : count + 1;
        }
        return iFormattableTextComponent;
    }

    public static TextColor color(String string) {
        return TextColor.parseColor("#".concat(string));
    }
}