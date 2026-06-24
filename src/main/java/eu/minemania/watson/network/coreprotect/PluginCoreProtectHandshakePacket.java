package eu.minemania.watson.network.coreprotect;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PluginCoreProtectHandshakePacket
{
    private final String modVersion;
    private final String modId;
    private final int protocolVersion;
    private final boolean registered;

    public PluginCoreProtectHandshakePacket(String modVersion, String modId, int protocolVersion)
    {
        this(modVersion, modId, protocolVersion, false);
    }

    public PluginCoreProtectHandshakePacket(boolean registered)
    {
        this("", "", 0, registered);
    }

    private PluginCoreProtectHandshakePacket(String modVersion, String modId, int protocolVersion, boolean registered)
    {
        this.modVersion = modVersion;
        this.modId = modId;
        this.protocolVersion = protocolVersion;
        this.registered = registered;
    }

    public String getModVersion()
    {
        return this.modVersion;
    }

    public String getModId()
    {
        return this.modId;
    }

    public int getProtocolVersion()
    {
        return this.protocolVersion;
    }

    public boolean isRegistered()
    {
        return this.registered;
    }

    public record Payload(PluginCoreProtectHandshakePacket content) implements CustomPacketPayload
    {
        public static final Type<Payload> TYPE = new Type<>(PluginCoreProtectHandshakePacketHandler.CHANNEL);
        public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(
                (payload, buf) ->
                {
                    buf.writeUtf(payload.content().getModVersion());
                    buf.writeUtf(payload.content().getModId());
                    buf.writeInt(payload.content().getProtocolVersion());
                },
                buf -> new Payload(new PluginCoreProtectHandshakePacket(buf.readBoolean()))
        );

        @Override
        public Type<Payload> type()
        {
            return TYPE;
        }
    }
}
