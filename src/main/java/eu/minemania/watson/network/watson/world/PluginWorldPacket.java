package eu.minemania.watson.network.watson.world;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PluginWorldPacket
{
    private final String world;

    public PluginWorldPacket(String world)
    {
        this.world = world;
    }

    public String getWorld()
    {
        return this.world;
    }

    public record Payload(PluginWorldPacket content) implements CustomPacketPayload
    {
        public static final Type<Payload> TYPE = new Type<>(PluginWorldPacketHandler.CHANNEL);
        public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(
                (payload, buf) -> buf.writeUtf(payload.content().getWorld()),
                buf -> new Payload(new PluginWorldPacket(buf.readUtf()))
        );

        @Override
        public Type<Payload> type()
        {
            return TYPE;
        }
    }
}
