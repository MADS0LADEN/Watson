package eu.minemania.watson.network.ledger.handshake;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Plugins;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public abstract class PluginHandshakePacketHandler<T extends CustomPacketPayload> implements IPluginClientPlayHandler<T>
{
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath("ledger","handshake");
    private boolean registered = false;

    private final static PluginHandshakePacketHandler<PluginHandshakePacket.Payload> INSTANCE = new PluginHandshakePacketHandler<>()
    {
        @Override
        public void receive(PluginHandshakePacket.Payload payload, ClientPlayNetworking.Context context)
        {
            this.receivePlayPayload(payload, context);
        }
    };

    public static PluginHandshakePacketHandler<PluginHandshakePacket.Payload> getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Identifier getPayloadChannel()
    {
        return CHANNEL;
    }

    @Override
    public boolean isPlayRegistered(Identifier channel)
    {
        return this.registered;
    }

    @Override
    public void setPlayRegistered(Identifier channel)
    {
        if (channel.equals(CHANNEL))
        {
            this.registered = true;
        }
    }

    @Override
    public void reset(Identifier channel)
    {
        if (!channel.equals(CHANNEL))
        {
            return;
        }

        INSTANCE.unregisterPlayReceiver();
        if(Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.LEDGER) {
            Configs.Plugin.PLUGIN.resetToDefault();
        }
    }

    public void decodePayload(PluginHandshakePacket content)
    {
        int protocolVersion = content.getProtocolVersion();
        String version = content.getVersion();
        List<String> actionsList = content.getActions();

        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info("protocol version: " + protocolVersion);
            Watson.logger.info("ledger version: " + version);
            Watson.logger.info("allowed actions: " + actionsList);
        }

        DataManager.setLedgerVersion(version);
        DataManager.setLedgerActions(actionsList);
        Configs.Plugin.PLUGIN.setOptionListValue(Plugins.LEDGER);
    }

    public void encodePayload(PluginHandshakePacket content)
    {
        INSTANCE.sendPlayPayload(new PluginHandshakePacket.Payload(content));
    }

    @Override
    public void encodeWithSplitter(FriendlyByteBuf buf, ClientPacketListener handler)
    {
        // NO-OP
    }

    @Override
    public void receivePlayPayload(T payload, ClientPlayNetworking.Context context)
    {
        INSTANCE.decodePayload(((PluginHandshakePacket.Payload)payload).content());
    }

}
