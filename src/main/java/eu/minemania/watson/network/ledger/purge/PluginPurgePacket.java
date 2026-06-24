package eu.minemania.watson.network.ledger.purge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PluginPurgePacket(String searchData) implements CustomPacketPayload
{
    public static final Type<PluginPurgePacket> TYPE = new Type<>(PluginPurgePacketHandler.CHANNEL);
    public static final StreamCodec<FriendlyByteBuf, PluginPurgePacket> CODEC = CustomPacketPayload.codec(
            (packet, buf) -> packet.write(buf),
            buf -> new PluginPurgePacket(buf.readUtf())
    );

    public void write(FriendlyByteBuf output)
    {
        output.writeUtf(searchData);
    }

    @Override
    public Type<PluginPurgePacket> type()
    {
        return TYPE;
    }
}
