package eu.minemania.watson.network.coreprotect;

import eu.minemania.watson.Reference;
import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Plugins;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public abstract class PluginCoreProtectHandshakePacketHandler<T extends CustomPacketPayload> implements IPluginClientPlayHandler<T>
{
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath("coreprotect", "handshake");
    private boolean registered;

    private static final PluginCoreProtectHandshakePacketHandler<PluginCoreProtectHandshakePacket.Payload> INSTANCE = new PluginCoreProtectHandshakePacketHandler<>()
    {
        @Override
        public void receive(PluginCoreProtectHandshakePacket.Payload payload, ClientPlayNetworking.Context context)
        {
            this.receivePlayPayload(payload, context);
        }
    };

    public static PluginCoreProtectHandshakePacketHandler<PluginCoreProtectHandshakePacket.Payload> getInstance()
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
        PluginCoreProtectDataPacketHandler.getInstance().reset(PluginCoreProtectDataPacketHandler.CHANNEL);
    }

    public void decodePayload(PluginCoreProtectHandshakePacket content)
    {
        if (content.isRegistered())
        {
            Configs.Plugin.PLUGIN.setOptionListValue(Plugins.COREPROTECT);

            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("CoreProtect networking handshake accepted.");
            }
        }
    }

    public void encodePayload()
    {
        INSTANCE.sendPlayPayload(new PluginCoreProtectHandshakePacket.Payload(
                new PluginCoreProtectHandshakePacket(Reference.MOD_VERSION, Reference.MOD_ID, Reference.COREPROTECT_PROTOCOL)
        ));
    }

    @Override
    public void encodeWithSplitter(FriendlyByteBuf buf, ClientPacketListener handler)
    {
        // NO-OP
    }

    @Override
    public void receivePlayPayload(T payload, ClientPlayNetworking.Context context)
    {
        INSTANCE.decodePayload(((PluginCoreProtectHandshakePacket.Payload) payload).content());
    }
}
