package eu.minemania.watson.chat.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

public class FakeCommandSource extends CommandSourceStack
{
    private static final List<String> colors = Arrays.asList("black", "darkblue", "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "grey", "gray", "darkgrey", "darkgray", "blue", "green", "aqua", "red", "lightpurple", "yellow", "white");
    private static final List<String> styles = Arrays.asList("+", "/", "_", "-", "?");

    private static final CommandSource CLIENT_COMMAND_SOURCE = new CommandSource()
    {
        @Override
        public void sendSystemMessage(Component message)
        {
            ClientCommandManager.sendFeedback(message);
        }

        @Override
        public boolean acceptsSuccess()
        {
            return true;
        }

        @Override
        public boolean acceptsFailure()
        {
            return true;
        }

        @Override
        public boolean shouldInformAdmins()
        {
            return false;
        }
    };

    public FakeCommandSource(LocalPlayer player)
    {
        super(
                CLIENT_COMMAND_SOURCE,
                player.position(),
                new Vec2(player.getXRot(), player.getYRot()),
                resolveServerLevel(player),
                PermissionSet.ALL_PERMISSIONS,
                player.getName().getString(),
                player.getDisplayName(),
                resolveMinecraftServer(),
                player
        );
    }

    @SuppressWarnings("unchecked")
    private static ServerLevel resolveServerLevel(LocalPlayer player)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() != null)
        {
            return mc.getSingleplayerServer().getLevel(Level.OVERWORLD);
        }

        return (ServerLevel) (Object) player.level();
    }

    @SuppressWarnings("unchecked")
    private static MinecraftServer resolveMinecraftServer()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() != null)
        {
            return mc.getSingleplayerServer();
        }

        return (MinecraftServer) (Object) mc;
    }

    @Override
    public Collection<String> getOnlinePlayerNames()
    {
        return Minecraft.getInstance().getConnection().getSuggestionsProvider().getOnlinePlayerNames();
    }

    public static Collection<String> getColor()
    {
        return colors;
    }

    public static Collection<String> getStyle()
    {
        return styles;
    }
}
