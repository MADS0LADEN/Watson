package eu.minemania.watson.network.ledger.search;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PluginSearchPacket(String searchData, int pages) implements CustomPacketPayload
{
    public static final Type<PluginSearchPacket> TYPE = new Type<>(PluginSearchPacketHandler.CHANNEL);
    public static final StreamCodec<FriendlyByteBuf, PluginSearchPacket> CODEC = CustomPacketPayload.codec(
            (packet, buf) -> packet.write(buf),
            buf -> new PluginSearchPacket(buf.readUtf(), buf.readInt())
    );

    public void write(FriendlyByteBuf output)
    {
        output.writeUtf(searchData);
        output.writeInt(pages);
    }

    @Override
    public Type<PluginSearchPacket> type()
    {
        return TYPE;
    }
}
