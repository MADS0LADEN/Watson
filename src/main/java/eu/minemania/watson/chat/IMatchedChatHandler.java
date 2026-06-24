package eu.minemania.watson.chat;

import java.util.regex.Matcher;

import net.minecraft.network.chat.MutableComponent;

public interface IMatchedChatHandler
{
    boolean onMatchedChat(MutableComponent chat, Matcher m);
}
