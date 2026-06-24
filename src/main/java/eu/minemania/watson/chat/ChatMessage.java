package eu.minemania.watson.chat;

import java.util.concurrent.ConcurrentLinkedQueue;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class ChatMessage
{
    private static final ChatMessage INSTANCE = new ChatMessage();
    protected ConcurrentLinkedQueue<String> _serverChatQueue = new ConcurrentLinkedQueue<>();
    protected long _lastServerChatTime;

    public static ChatMessage getInstance()
    {
        return INSTANCE;
    }

    public static void localOutput(String message, boolean watsonMessage)
    {
        sendToLocalChat(ChatFormatting.AQUA, null, message, watsonMessage);
    }

    public static void localOutputT(String translationKey, Object... args)
    {
        sendToLocalChat(ChatFormatting.AQUA, Component.translatable(translationKey, args), true);
    }

    public static void localError(String message, boolean watsonMessage)
    {
        sendToLocalChat(ChatFormatting.DARK_RED, null, message, watsonMessage);
    }

    public static void localErrorT(String translationKey, Object... args)
    {
        sendToLocalChat(ChatFormatting.DARK_RED, Component.translatable(translationKey, args), true);
    }

    public void serverChat(String message, boolean firstMessage)
    {
        _serverChatQueue.add(message);
        if (firstMessage)
        {
            _lastServerChatTime = System.currentTimeMillis();
        }
    }

    public void immediateServerChat(String message)
    {
        if (message != null)
        {
            sendToServerChat(message);
        }
    }

    public static void sendToLocalChat(String message, boolean watsonMessage)
    {
        sendToLocalChat(Component.translatable(message), watsonMessage);
    }

    public static void sendToLocalChat(Component message, boolean watsonMessage)
    {
        sendToLocalChat((MutableComponent) message, watsonMessage);
    }

    public static void sendToLocalChat(MutableComponent inputmessage, boolean watsonMessage)
    {
        MutableComponent message = Configs.Highlights.USE_CHAT_HIGHLIGHTS.getBooleanValue() ? Highlight.setHighlightChatMessage("chat.type.text", inputmessage, watsonMessage) : inputmessage;
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(message);
    }

    public static void sendToLocalChat(ChatFormatting color, ChatFormatting style, String message, boolean watsonMessage)
    {
        MutableComponent chat = Component.literal(message);
        if (color != null && style == null)
        {
            chat = chat.withStyle(color);
        }
        else if (color != null)
        {
            chat = chat.withStyle(color, style);
        }
        sendToLocalChat(chat, watsonMessage);
    }

    public static void sendToLocalChat(ChatFormatting color, MutableComponent message, boolean watsonMessage)
    {
        message = message.withStyle(color);
        sendToLocalChat(message, watsonMessage);
    }

    public static void sendToServerChat(String message)
    {
        try
        {
            Minecraft mc = Minecraft.getInstance();
            mc.player.connection.sendCommand(message);
        }
        catch (Exception e)
        {
            Watson.logger.error("Sending chat to the server.", e);
        }
    }

    public void processServerChatQueue()
    {
        if (!_serverChatQueue.isEmpty())
        {
            long now = System.currentTimeMillis();
            if (now - _lastServerChatTime >= (long) (1000 * Configs.Generic.CHAT_TIMEOUT.getDoubleValue()))
            {
                _lastServerChatTime = now;
                String message = _serverChatQueue.poll();
                immediateServerChat(message);
            }
        }
    }
}