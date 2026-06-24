package eu.minemania.watson.network.ledger.inspect;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.BlockPos;


public record PluginInspectPacket(BlockPos pos, int pages) implements CustomPacketPayload
{
    public static final Type<PluginInspectPacket> TYPE = new Type<>(PluginInspectPacketHandler.CHANNEL);
    public static final StreamCodec<FriendlyByteBuf, PluginInspectPacket> CODEC = CustomPacketPayload.codec(
            (packet, buf) -> packet.write(buf),
            buf -> new PluginInspectPacket(buf.readBlockPos(), buf.readInt())
    );

    public void write(FriendlyByteBuf output)
    {
        output.writeBlockPos(pos);
        output.writeInt(pages);
    }

    @Override
    public Type<PluginInspectPacket> type()
    {
        return TYPE;
    }
}
