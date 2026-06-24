package eu.minemania.watson.mixin;

import eu.minemania.watson.analysis.Analysis;
import eu.minemania.watson.analysis.CoreProtectAnalysis;
import eu.minemania.watson.config.Plugins;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import eu.minemania.watson.chat.ChatProcessor;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;

@Mixin(ChatComponent.class)
public abstract class MixinChatHud
{
    private boolean delete;

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true)
    private Component chatHighlighter(Component componentln)
    {
        delete = false;
        Analysis.colorBlock = 0;
        if (Highlight.getReturnBoolean())
        {
            Highlight.toggleReturnBoolean();
            return componentln;
        }
        boolean allowChat = ChatProcessor.getInstance().onChat((MutableComponent) componentln);
        if (allowChat)
        {
            if (Configs.Plugin.PLUGIN.getOptionListValue() != Plugins.NULL &&
                Configs.Highlights.USE_CUSTOM_ROLLED_BACK_TEXT_COLOR.getBooleanValue() &&
                CoreProtectAnalysis.isCpMessage
            ) {
                if (componentln instanceof MutableComponent && componentln.getString().contains("§m"))
                {
                    MutableComponent newComponentln = MutableComponent.create(componentln.getContents());
                    newComponentln.setStyle(componentln.getStyle());
                    for (Component sibling : componentln.getSiblings())
                    {
                        if (sibling instanceof MutableComponent && ((MutableComponent) sibling).getString().contains("§m"))
                        {
                            if (sibling.getString().contains("§f§m") || sibling.getString().contains("§m§f"))
                            {
                                sibling = Component.literal(sibling.getString().replaceAll("§f", ""));
                            }
                            ((MutableComponent) sibling).setStyle(sibling.getStyle().withColor(Configs.Highlights.ROLLED_BACK_TEXT_COLOR.getIntegerValue()));
                        }
                        newComponentln.append(sibling);
                    }
                    componentln = newComponentln;
                }
            }
            if (Configs.Highlights.COLOR_BLOCK_CHAT.getBooleanValue() && componentln instanceof MutableComponent && Analysis.colorBlock != 0)
            {
                MutableComponent newComponentln = MutableComponent.create(componentln.getContents());
                newComponentln.setStyle(componentln.getStyle());
                for (Component sibling : componentln.getSiblings())
                {
                    newComponentln.append(sibling);
                }
                MutableComponent colorBlockText = Component.literal(" ⬤").withColor(Analysis.colorBlock);
                newComponentln.append(colorBlockText);
                componentln = newComponentln;
            }
            if (Configs.Highlights.USE_CHAT_HIGHLIGHTS.getBooleanValue())
            {
                if (componentln.getContents() instanceof TranslatableContents)
                {
                    if (((TranslatableContents)componentln.getContents()).getKey().contains("chat.type.text"))
                    {
                        return Highlight.setHighlightChatMessage(((TranslatableContents) componentln.getContents()).getKey(), (MutableComponent) componentln, false);
                    }
                }
                else
                {
                    return Highlight.setHighlightChatMessage((MutableComponent) componentln);
                }
            }
        }
        else
        {
            delete = true;
        }
        return componentln;
    }

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    public void onDelete(CallbackInfo ci)
    {
        if (delete)
        {
            ci.cancel();
        }
    }
}
