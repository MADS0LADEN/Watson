package eu.minemania.watson.network.ledger.handshake;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public class PluginHandshakePacket
{
    private Integer protocolVersion;
    private String modId;
    private String version;
    private List<String> actions;

    public PluginHandshakePacket(Integer protocolVersion, String ledgerVersion, List<String> actions)
    {
        this.protocolVersion = protocolVersion;
        this.version = ledgerVersion;
        this.actions = actions;
        this.modId = "";
    }

    public PluginHandshakePacket(Integer protocolVersion, String version, String modId)
    {
        this.protocolVersion = protocolVersion;
        this.version = version;
        this.modId = modId;
        this.actions = List.of();
    }

    public Integer getProtocolVersion()
    {
        return this.protocolVersion;
    }

    public String getVersion()
    {
        return this.version;
    }

    public List<String> getActions()
    {
        return this.actions;
    }

    public CompoundTag toNbt()
    {
        CompoundTag result = new CompoundTag();

        result.putString("modid", this.modId);
        result.putString("version", this.version);
        result.putInt("protocol_version", this.protocolVersion);

        return result;
    }

    public record Payload(PluginHandshakePacket content) implements CustomPacketPayload
    {
        public static final Type<Payload> TYPE = new Type<>(PluginHandshakePacketHandler.CHANNEL);
        public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(Payload::write, Payload::new);

        public Payload(FriendlyByteBuf input)
        {
            this(new PluginHandshakePacket(input.readInt(), input.readUtf(), actionsList(input)));
        }

        public void write(FriendlyByteBuf output)
        {
            output.writeNbt(content.toNbt());
        }

        @Override
        public Type<Payload> type()
        {
            return TYPE;
        }
    }

    private static List<String> actionsList(FriendlyByteBuf buf)
    {
        int totalActions = buf.readInt();
        List<String> actionsList = new ArrayList<>();
        if (totalActions > 0)
        {
            for (int i = 0; i < totalActions; i++)
            {
                String action = buf.readUtf();
                actionsList.add(action);
            }
        }
        return actionsList;
    }
}
