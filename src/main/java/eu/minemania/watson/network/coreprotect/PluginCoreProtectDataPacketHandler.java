package eu.minemania.watson.network.coreprotect;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.malilib.util.StringUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;

public abstract class PluginCoreProtectDataPacketHandler<T extends CustomPacketPayload> implements IPluginClientPlayHandler<T>
{
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath("coreprotect", "data");
    private boolean registered;

    private static final PluginCoreProtectDataPacketHandler<PluginCoreProtectDataPacket.Payload> INSTANCE = new PluginCoreProtectDataPacketHandler<>()
    {
        @Override
        public void receive(PluginCoreProtectDataPacket.Payload payload, ClientPlayNetworking.Context context)
        {
            this.receivePlayPayload(payload, context);
        }
    };

    public static PluginCoreProtectDataPacketHandler<PluginCoreProtectDataPacket.Payload> getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Identifier getPayloadChannel()
    {
        return CHANNEL;
    }

    @Override
    public boolean isPlayRegistered(Identifier channel)
    {
        return this.registered;
    }

    @Override
    public void setPlayRegistered(Identifier channel)
    {
        if (channel.equals(CHANNEL))
        {
            this.registered = true;
        }
    }

    @Override
    public void reset(Identifier channel)
    {
        if (!channel.equals(CHANNEL))
        {
            return;
        }

        INSTANCE.unregisterPlayReceiver();
        this.registered = false;
    }

    public void decodePayload(PluginCoreProtectDataPacket content)
    {
        if (content.getData().length == 0)
        {
            return;
        }

        this.registered = true;
        ByteArrayInputStream in = new ByteArrayInputStream(content.getData());
        DataInputStream dis = new DataInputStream(in);

        try
        {
            int type = dis.readInt();
            long time;
            String resultUser;
            String action;
            String target;
            int x;
            int y;
            int z;
            int count;
            String worldName;
            boolean rolledBack = false;

            switch (type)
            {
                case 2 ->
                {
                    time = dis.readLong();
                    action = "session" + StringUtils.translate("watson.message.cp.logged", dis.readUTF());
                    resultUser = dis.readUTF();
                    target = "minecraft:player";
                    count = dis.readInt();
                    x = dis.readInt();
                    y = dis.readInt();
                    z = dis.readInt();
                    worldName = dis.readUTF();
                }
                case 3 ->
                {
                    time = dis.readLong();
                    resultUser = dis.readUTF();
                    action = dis.readUTF();
                    target = dis.readBoolean() ? "minecraft:oak_sign" : "minecraft:player";
                    x = dis.readInt();
                    y = dis.readInt();
                    z = dis.readInt();
                    worldName = dis.readUTF();
                    count = 1;
                }
                case 4 ->
                {
                    time = dis.readLong();
                    resultUser = dis.readUTF();
                    target = "minecraft:player";
                    action = dis.readUTF();
                    x = 0;
                    y = 0;
                    z = 0;
                    worldName = "unknown";
                    count = 1;
                }
                default ->
                {
                    time = dis.readLong();
                    action = dis.readUTF();
                    resultUser = dis.readUTF();
                    target = dis.readUTF();
                    count = dis.readInt();
                    x = dis.readInt();
                    y = dis.readInt();
                    z = dis.readInt();
                    worldName = dis.readUTF();
                    rolledBack = dis.readBoolean();
                }
            }

            if (count == -1)
            {
                count = 1;
            }

            if (!DataManager.getFilters().isAcceptedPlayer(resultUser))
            {
                return;
            }

            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("CoreProtect data type={} user={} action={} target={} at {}/{}/{} in {}", type, resultUser, action, target, x, y, z, worldName);
            }

            WatsonBlock watsonBlock = WatsonBlockRegistery.getInstance().getWatsonBlockByName(target);
            BlockEdit edit = new BlockEdit(time, resultUser, action, x, y, z, watsonBlock, worldName, count);
            HashMap<String, Object> additional = new HashMap<>();
            additional.put("rolledBack", rolledBack);

            if (type == 1)
            {
                boolean isContainer = dis.readBoolean();
                boolean added = dis.readBoolean();
                additional.put("isContainer", isContainer);
                additional.put("added", added);
            }

            edit.setAdditional(additional);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, false));
        }
        catch (Exception e)
        {
            Watson.logger.error("Failed to decode CoreProtect networking data", e);
        }
    }

    @Override
    public void encodeWithSplitter(FriendlyByteBuf buf, ClientPacketListener handler)
    {
        // NO-OP
    }

    @Override
    public void receivePlayPayload(T payload, ClientPlayNetworking.Context context)
    {
        INSTANCE.decodePayload(((PluginCoreProtectDataPacket.Payload) payload).content());
    }
}
