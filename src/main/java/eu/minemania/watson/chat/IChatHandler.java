package eu.minemania.watson.chat;

import net.minecraft.network.chat.MutableComponent;

public interface IChatHandler
{
    boolean onChat(MutableComponent chat);
}
