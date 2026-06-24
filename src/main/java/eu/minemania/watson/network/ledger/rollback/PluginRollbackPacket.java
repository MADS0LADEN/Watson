package eu.minemania.watson.network.ledger.rollback;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PluginRollbackPacket(boolean restore, String searchData) implements CustomPacketPayload
{
    public static final Type<PluginRollbackPacket> TYPE = new Type<>(PluginRollbackPacketHandler.CHANNEL);
    public static final StreamCodec<FriendlyByteBuf, PluginRollbackPacket> CODEC = CustomPacketPayload.codec(
            (packet, buf) -> packet.write(buf),
            buf -> new PluginRollbackPacket(buf.readBoolean(), buf.readUtf())
    );

    public void write(FriendlyByteBuf output)
    {
        output.writeBoolean(restore);
        output.writeUtf(searchData);
    }

    @Override
    public Type<PluginRollbackPacket> type()
    {
        return TYPE;
    }
}
