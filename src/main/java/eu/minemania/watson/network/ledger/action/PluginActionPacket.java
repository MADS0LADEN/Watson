package eu.minemania.watson.network.ledger.action;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

public class PluginActionPacket
{
    private BlockPos blockPos;
    private String type;
    private Identifier dim;
    private Identifier oldObj;
    private Identifier newObj;
    private String source;
    private long time;
    private boolean rolledBack;
    private String additionalData;

    public PluginActionPacket(BlockPos blockPos, String type, Identifier dim, Identifier oldObj, Identifier newObj, String source, long time, boolean rolledBack, String additionalData)
    {
        this.blockPos = blockPos;
        this.type = type;
        this.dim = dim;
        this.oldObj = oldObj;
        this.newObj = newObj;
        this.source = source;
        this.time = time;
        this.rolledBack = rolledBack;
        this.additionalData = additionalData;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public String getType()
    {
        return this.type;
    }

    public Identifier getDimension()
    {
        return this.dim;
    }

    public Identifier getOldObject()
    {
        return this.oldObj;
    }

    public Identifier getNewObject()
    {
        return this.newObj;
    }

    public String getSource()
    {
        return this.source;
    }

    public long getTime()
    {
        return this.time;
    }

    public boolean isRolledBack()
    {
        return this.rolledBack;
    }

    public String getAdditionalData()
    {
        return this.additionalData;
    }

    public record Payload(PluginActionPacket content) implements CustomPacketPayload
    {
        public static final Type<Payload> TYPE = new Type<>(PluginActionPacketHandler.CHANNEL);
        public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(
                (payload, buf) -> {
                    PluginActionPacket content = payload.content();
                    buf.writeBlockPos(content.getBlockPos());
                    buf.writeUtf(content.getType());
                    buf.writeIdentifier(content.getDimension());
                    buf.writeIdentifier(content.getOldObject());
                    buf.writeIdentifier(content.getNewObject());
                    buf.writeUtf(content.getSource());
                    buf.writeLong(content.getTime());
                    buf.writeBoolean(content.isRolledBack());
                    buf.writeUtf(content.getAdditionalData());
                },
                buf -> new Payload(new PluginActionPacket(
                        buf.readBlockPos(),
                        buf.readUtf(),
                        buf.readIdentifier(),
                        buf.readIdentifier(),
                        buf.readIdentifier(),
                        buf.readUtf(),
                        buf.readLong(),
                        buf.readBoolean(),
                        buf.readUtf()
                ))
        );

        @Override
        public Type<Payload> type()
        {
            return TYPE;
        }
    }
}
