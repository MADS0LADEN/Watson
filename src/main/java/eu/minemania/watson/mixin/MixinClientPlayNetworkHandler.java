package eu.minemania.watson.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import eu.minemania.watson.chat.command.ClientCommandManager;
import eu.minemania.watson.chat.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Minecraft client, Connection clientConnection, CommonListenerCookie clientConnectionState, CallbackInfo ci)
    {
        Command.registerCommands(new CommandDispatcher<CommandSourceStack>());
    }

    @Inject(method = "handleCommands", at = @At("TAIL"))
    public void onHandleCommands(ClientboundCommandsPacket packet, CallbackInfo ci)
    {
        if (Command.commandDispatcher == null)
        {
            Command.registerCommands(new CommandDispatcher<CommandSourceStack>());
        }
        else
        {
            Command.registerCommands(Command.commandDispatcher);
        }
    }

    @ModifyVariable(method = "sendCommand", at = @At("HEAD"), argsOnly = true)
    private String onSendCommand(String message)
    {
        if ((message.startsWith("pr l") || message.startsWith("pr i")) && !message.contains("-extended"))
        {
            return message + " -extended";
        }

        return message;
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void sendCommand(String message, CallbackInfo ci)
    {
        StringReader reader = new StringReader(message);
        int cursor = reader.getCursor();
        String commandName = reader.canRead() ? reader.readUnquotedString() : "";
        reader.setCursor(cursor);
        if (ClientCommandManager.isClientSideCommand(commandName))
        {
            ClientCommandManager.executeCommand(reader, message);
            ci.cancel();
        }
    }
}
