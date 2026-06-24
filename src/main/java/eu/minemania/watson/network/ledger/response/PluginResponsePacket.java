package eu.minemania.watson.network.ledger.response;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class PluginResponsePacket
{
    private Identifier identifier;
    private int responseCode;

    public PluginResponsePacket(Identifier identifier, int responseCode)
    {
        this.identifier = identifier;
        this.responseCode = responseCode;
    }

    public Identifier getIdentifier()
    {
        return this.identifier;
    }

    public int getResponseCode()
    {
        return this.responseCode;
    }

    public record Payload(PluginResponsePacket content) implements CustomPacketPayload
    {
        public static final Type<Payload> TYPE = new Type<>(PluginResponsePacketHandler.CHANNEL);
        public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(
                (payload, buf) -> {
                    buf.writeIdentifier(payload.content().getIdentifier());
                    buf.writeInt(payload.content().getResponseCode());
                },
                buf -> new Payload(new PluginResponsePacket(buf.readIdentifier(), buf.readInt()))
        );

        @Override
        public Type<Payload> type()
        {
            return TYPE;
        }
    }
}
