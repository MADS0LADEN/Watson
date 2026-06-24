package eu.minemania.watson.chat.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class WatsonCommandBase
{
    public static void localOutput(CommandSourceStack sender, String message)
    {
        sendColoredText(sender, ChatFormatting.AQUA, message);
    }

    public static void localOutputT(CommandSourceStack sender, String translationKey, Object... args)
    {
        sendColoredText(sender, ChatFormatting.AQUA, Component.translatable(translationKey, args));
    }

    public static void localError(CommandSourceStack sender, String message)
    {
        sendColoredText(sender, ChatFormatting.DARK_RED, message);
    }

    public static void localErrorT(CommandSourceStack sender, String translationKey, Object... args)
    {
        sendColoredText(sender, ChatFormatting.DARK_RED, Component.translatable(translationKey, args));
    }

    public static void sendColoredText(CommandSourceStack sender, ChatFormatting color, String message)
    {
        MutableComponent chat = Component.literal(message);
        chat = chat.withStyle(color);
        sender.sendSystemMessage(chat);
    }

    public static void sendColoredText(CommandSourceStack sender, ChatFormatting color, MutableComponent component)
    {
        component = component.withStyle(color);
        sender.sendSystemMessage(component);
    }
}