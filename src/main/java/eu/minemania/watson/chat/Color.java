package eu.minemania.watson.chat;

import java.util.HashMap;

import net.minecraft.ChatFormatting;

public enum Color
{
    black(ChatFormatting.BLACK),
    darkblue(ChatFormatting.DARK_BLUE),
    darkgreen(ChatFormatting.DARK_GREEN),
    darkaqua(ChatFormatting.DARK_AQUA),
    darkred(ChatFormatting.DARK_RED),
    darkpurple(ChatFormatting.DARK_PURPLE),
    gold(ChatFormatting.GOLD),
    grey(ChatFormatting.GRAY),
    gray(ChatFormatting.GRAY),
    darkgrey(ChatFormatting.DARK_GRAY),
    darkgray(ChatFormatting.DARK_GRAY),
    blue(ChatFormatting.BLUE),
    green(ChatFormatting.GREEN),
    aqua(ChatFormatting.AQUA),
    red(ChatFormatting.RED),
    lightpurple(ChatFormatting.LIGHT_PURPLE),
    yellow(ChatFormatting.YELLOW),
    white(ChatFormatting.WHITE);

    private final ChatFormatting _color;
    private static final HashMap<ChatFormatting, Color> _byTextFormatColor = new HashMap<>();

    static
    {
        _byTextFormatColor.put(ChatFormatting.BLACK, Color.black);
        _byTextFormatColor.put(ChatFormatting.DARK_BLUE, Color.darkblue);
        _byTextFormatColor.put(ChatFormatting.DARK_GREEN, Color.darkgreen);
        _byTextFormatColor.put(ChatFormatting.DARK_AQUA, Color.darkaqua);
        _byTextFormatColor.put(ChatFormatting.DARK_RED, Color.darkred);
        _byTextFormatColor.put(ChatFormatting.DARK_PURPLE, Color.darkpurple);
        _byTextFormatColor.put(ChatFormatting.GOLD, Color.gold);
        _byTextFormatColor.put(ChatFormatting.GRAY, Color.gray);
        _byTextFormatColor.put(ChatFormatting.DARK_GRAY, Color.darkgray);
        _byTextFormatColor.put(ChatFormatting.BLUE, Color.blue);
        _byTextFormatColor.put(ChatFormatting.GREEN, Color.green);
        _byTextFormatColor.put(ChatFormatting.AQUA, Color.aqua);
        _byTextFormatColor.put(ChatFormatting.RED, Color.red);
        _byTextFormatColor.put(ChatFormatting.LIGHT_PURPLE, Color.lightpurple);
        _byTextFormatColor.put(ChatFormatting.YELLOW, Color.yellow);
        _byTextFormatColor.put(ChatFormatting.WHITE, Color.white);
    }

    public ChatFormatting getColor()
    {
        return _color;
    }

    public static Color getByTextFormatColor(ChatFormatting colorTextFormat)
    {
        Color color = _byTextFormatColor.get(colorTextFormat);
        if (color == null)
        {
            throw new IllegalArgumentException("invalid color code: " + colorTextFormat.toString());
        }
        return color;
    }

    public static Color getByColorOrName(String colorOrName)
    {
        if (colorOrName.contains("ChatFormatting."))
        {
            return getByTextFormatColor((ChatFormatting) (Object) colorOrName);
        }
        else
        {
            return Color.valueOf(colorOrName.toLowerCase());
        }
    }

    Color(ChatFormatting formattingName)
    {
        _color = formattingName;
    }
}