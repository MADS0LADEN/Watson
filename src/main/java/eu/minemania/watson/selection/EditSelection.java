package eu.minemania.watson.selection;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.config.Plugins;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.BlockEditComparator;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.render.RenderUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.multiplayer.ClientPacketListener;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

public class EditSelection
{
    protected boolean _selectionChanged;
    protected BlockEdit _selection;
    protected HashMap<String, Object> _variables = new HashMap<>();
    protected static HashMap<String, BlockEditSet> _edits = new HashMap<>();
    protected Calendar _calendar = Calendar.getInstance();
    private static ReplayThread thread;

    public HashMap<String, Object> getVariables()
    {
        return _variables;
    }

    public BlockEdit getSelection()
    {
        return _selection;
    }

    public void selectBlockEdit(BlockEdit edit)
    {
        if (edit != null)
        {
            _selection = edit;

            _variables.put("time", edit.time);
            _variables.put("player", edit.player);
            _variables.put("block", edit.block.getName());
            _variables.put("action", edit.action);
            _variables.put("world", edit.world);

            // Will also dispatch the onWatsonSelection Macro/Keybind event:
            selectPosition(edit.x, edit.y, edit.z, edit.world, edit.amount);
        }
    }

    public void clearBlockEditSet()
    {
        getBlockEditSet().clear();
        _variables.clear();
        _selectionChanged = true;
        _selection = null;
        ChatMessage.localOutputT("watson.message.filters.edits_clear");
        DataManager.getFilters().clear();
    }

    public void clearSelection()
    {
        _variables.clear();
        _selectionChanged = true;
        _selection = null;
    }

    public void selectPosition(int x, int y, int z, String world, int amount)
    {
        if (_selection == null || _selection.x != x || _selection.y != y || _selection.z != z || !_selection.world.equals(world) || _selection.amount != amount)
        {
            _selection = new BlockEdit(0, "", "selection", x, y, z, null, world, amount);
        }

        _variables.put("x", x);
        _variables.put("y", y);
        _variables.put("z", z);
        _variables.put("world", world);
        _selectionChanged = true;
    }

    public BlockEditSet getBlockEditSet()
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null)
        {
            return null;
        }
        StringBuilder idBuilder = new StringBuilder();
        String serverIP = DataManager.getServerIP();
        if (serverIP != null)
        {
            idBuilder.append(serverIP);
        }
        idBuilder.append('/');
        idBuilder.append(WorldUtils.getDimensionId(player.level()));
        String id = idBuilder.toString();

        BlockEditSet edits = _edits.get(id);
        if (edits == null)
        {
            edits = new BlockEditSet();
            _edits.put(id, edits);
        }
        return edits;
    }

    public void drawSelection()
    {
        if (_selection != null && Configs.Edits.SELECTION_SHOWN.getBooleanValue() && (DataManager.getWorldPlugin().isEmpty() || DataManager.getWorldPlugin().equals(_selection.world)))
        {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = RenderUtils.startDrawingLines(tesselator);
            MeshData builtBuffer;

            final float halfSize = 0.3f;
            float x = _selection.x + 0.5f;
            float y = _selection.y + 0.5f;
            float z = _selection.z + 0.5f;
            buffer.addVertex(x - halfSize, y, z).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
            buffer.addVertex(x + halfSize, y, z).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
            buffer.addVertex(x, y - halfSize, z).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
            buffer.addVertex(x, y + halfSize, z).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
            buffer.addVertex(x, y, z - halfSize).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
            buffer.addVertex(x, y, z + halfSize).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
            try {
                builtBuffer = buffer.build();
                RenderUtils.drawMesh(builtBuffer);
                builtBuffer.close();
            } catch (Exception e) {
                // Ignored
            }

            if (_selection.playereditSet != null)
            {
                BlockEdit previous = _selection.playereditSet.getEditBefore(_selection);
                if (previous != null)
                {
                    buffer = RenderUtils.startDrawingLines(tesselator);
                    buffer.addVertex(previous.x + 0.5f, previous.y + 0.5f, previous.z + 0.5f).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
                    buffer.addVertex(x, y, z).setColor(255 / 255f, 0 / 255f, 255 / 255f, 128).setNormal(0, 0, 0);
                    try {
                        builtBuffer = buffer.build();
                        RenderUtils.drawMesh(builtBuffer);
                        builtBuffer.close();
                    } catch (Exception e) {
                        // Ignored
                    }
                }
            }
        }
    }

    public void queryPreEdits(int count)
    {
        if (_variables.containsKey("player") && _variables.containsKey("time"))
        {
            if (Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.LOGBLOCK)
            {
                _calendar.setTimeInMillis((Long) _variables.get("time"));
                int day = _calendar.get(Calendar.DAY_OF_MONTH);
                int month = _calendar.get(Calendar.MONTH) + 1;
                int year = _calendar.get(Calendar.YEAR);
                int hour = _calendar.get(Calendar.HOUR_OF_DAY);
                int minute = _calendar.get(Calendar.MINUTE);
                int second = _calendar.get(Calendar.SECOND);
                String player = (String) _variables.get("player");

                String query = String.format("lb before %d.%d.%d %02d:%02d:%02d player %s coords limit %d", day, month, year, hour, minute, second, player, count);
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info(query);
                }
                ChatMessage.sendToServerChat(query);
            }
            else
            {
                InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_logblock");
            }
        }
        else
        {
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_player_time");
        }
    }

    public void queryPostEdits(int count)
    {
        if (_variables.containsKey("player") && _variables.containsKey("time"))
        {
            if (Configs.Plugin.PLUGIN.getOptionListValue() == Plugins.LOGBLOCK)
            {
                _calendar.setTimeInMillis((Long) _variables.get("time"));
                int day = _calendar.get(Calendar.DAY_OF_MONTH);
                int month = _calendar.get(Calendar.MONTH) + 1;
                int year = _calendar.get(Calendar.YEAR);
                int hour = _calendar.get(Calendar.HOUR_OF_DAY);
                int minute = _calendar.get(Calendar.MINUTE);
                int second = _calendar.get(Calendar.SECOND);
                String player = (String) _variables.get("player");

                String query = String.format("lb since %d.%d.%d %02d:%02d:%02d player %s coords limit %d asc", day, month, year, hour, minute, second, player, count);
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info(query);
                }
                ChatMessage.sendToServerChat(query);
            }
            else
            {
                InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_logblock");
            }
        }
        else
        {
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.info.no_player_time");
        }
    }

    public void replay(String since, double speed, int radius, CommandSourceStack source)
    {
        TreeSet<BlockEdit> edits = new TreeSet<>(new BlockEditComparator());
        long timing = DataManager.getTimeDiff(since);
        Entity entity = source.getEntity();

        if (entity == null)
        {
            return;
        }
        if (timing == -1)
        {
            InfoUtils.showInGameMessage(MessageType.ERROR, "watson.message.edits.none_edits", "here");
            return;
        }

        for (PlayereditSet playereditSet : DataManager.getEditSelection().getBlockEditSet().getPlayereditSet().values())
        {
            for (BlockEdit edit : playereditSet.getBlockEdits())
            {
                if (timing <= edit.time)
                {
                    Vec3 editPos = new Vec3(edit.x, edit.y, edit.z);
                    if (entity.position().closerThan(editPos, radius))
                    {
                        edits.add(edit);
                    }
                }
            }
        }

        if (!edits.isEmpty())
        {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            ClientPacketListener networkHandler = mc.getConnection();
            if (player == null || networkHandler == null)
            {
                return;
            }
            ReplayThread replayThread = new ReplayThread(edits, mc, this, speed);
            Thread t = new Thread(replayThread);
            thread = replayThread;
            t.start();
        }
        else
        {
            InfoUtils.showInGameMessage(MessageType.ERROR, "watson.message.edits.none_world");
        }
    }

    public void cancelReplay()
    {
        thread.cancelReplay();
    }
}

class ReplayThread implements Runnable {
    private volatile boolean exit = false;
    private final TreeSet<BlockEdit> edits;
    private final Minecraft mc;
    private final EditSelection editSelection;
    private final double speed;
    public ReplayThread(TreeSet<BlockEdit> edits, Minecraft mc, EditSelection editSelection, double speed)
    {
        this.edits = edits;
        this.mc = mc;
        this.editSelection = editSelection;
        this.speed = speed;
    }
    public void run() {
        for (BlockEdit edit : edits)
        {
            if (exit)
            {
                return;
            }
            try
            {
                Player player = mc.player;
                ClientPacketListener networkHandler = mc.getConnection();
                double randX = Mth.clamp(edit.x + player.level().getRandom().nextDouble() * 16.0D, edit.x - 3, edit.x + 3);
                double randY = Mth.clamp(edit.y + (double) (player.level().getRandom().nextInt(16)), edit.y - 3, edit.y + 3);
                double randZ = Mth.clamp(edit.z + player.level().getRandom().nextDouble() * 16.0D, edit.z - 3, edit.z + 3);
                player.startFallFlying();
                networkHandler.getConnection().send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                Teleport.teleport(randX, randY, randZ, edit.world);
                Thread.sleep(50L);
                player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(edit.x, edit.y, edit.z));
                networkHandler.getConnection().send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), false, false));
                editSelection.selectPosition(edit.x, edit.y, edit.z, edit.world, edit.amount);
                player.stopFallFlying();
                networkHandler.getConnection().send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                Thread.sleep((long) (10000L / speed) - 50L);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void cancelReplay()
    {
        exit = true;
    }
}