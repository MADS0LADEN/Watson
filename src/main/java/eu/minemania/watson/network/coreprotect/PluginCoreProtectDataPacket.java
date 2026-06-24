package eu.minemania.watson.network.coreprotect;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PluginCoreProtectDataPacket
{
    private final byte[] data;

    public PluginCoreProtectDataPacket(byte[] data)
    {
        this.data = data;
    }

    public byte[] getData()
    {
        return this.data;
    }

    public record Payload(PluginCoreProtectDataPacket content) implements CustomPacketPayload
    {
        public static final Type<Payload> TYPE = new Type<>(PluginCoreProtectDataPacketHandler.CHANNEL);
        public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(
                (payload, buf) -> buf.writeByteArray(payload.content().getData()),
                buf -> new Payload(new PluginCoreProtectDataPacket(buf.readByteArray()))
        );

        @Override
        public Type<Payload> type()
        {
            return TYPE;
        }
    }
}
