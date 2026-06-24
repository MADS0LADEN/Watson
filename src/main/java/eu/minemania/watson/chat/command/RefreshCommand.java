package eu.minemania.watson.chat.command;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import eu.minemania.watson.config.Configs;
import net.minecraft.commands.CommandSourceStack;

public class RefreshCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("refresh");
        LiteralArgumentBuilder<CommandSourceStack> refresh = literal("refresh").executes(RefreshCommand::refresh);
        dispatcher.register(refresh);
    }

    private static int refresh(CommandContext<CommandSourceStack> context)
    {
        Command.reregisterWatsonCommand(Command.commandDispatcher, Configs.Generic.WATSON_PREFIX);
        return 1;
    }
}