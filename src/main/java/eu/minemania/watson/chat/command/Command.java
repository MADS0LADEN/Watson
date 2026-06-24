package eu.minemania.watson.chat.command;

import com.mojang.brigadier.CommandDispatcher;
import fi.dy.masa.malilib.config.options.ConfigString;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;

public class Command
{
    public static CommandDispatcher<CommandSourceStack> commandDispatcher;

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        ClientCommandManager.clearClientSideCommands();
        WatsonCommand.register(dispatcher);
        RefreshCommand.register(dispatcher);
        AnnoCommand.register(dispatcher);
        CalcCommand.register(dispatcher);
        HighlightCommand.register(dispatcher);

        if (Minecraft.getInstance().isLocalServer())
        {

        }

        commandDispatcher = dispatcher;
    }

    public static void reregisterWatsonCommand(CommandDispatcher<CommandSourceStack> dispatcher, ConfigString command)
    {
        ClientCommandManager.getClientSideCommands().remove(command.getStringValue());
        CommandRemoval.removeCommand(dispatcher.getRoot(), command.getStringValue());
        WatsonCommand.register(dispatcher);

        if (Minecraft.getInstance().isLocalServer())
        {

        }
    }
}