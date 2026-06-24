package eu.minemania.watson.mixin;

import net.minecraft.world.scores.Team;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.mojang.authlib.GameProfile;
import eu.minemania.watson.chat.Highlight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity
{
    @Shadow
    public abstract Component getDisplayName();

    protected MixinPlayerEntity(Level level, GameProfile gameprofile)
    {
        super(EntityType.PLAYER, level);
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getTeam()Lnet/minecraft/world/scores/Team;"))
    private Team getCustomScoreboardPlayerTeam(Player player)
    {
        if (Highlight.changeUsername)
        {
            return null;
        }
        else
        {
            return player.getTeam();
        }
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getName()Lnet/minecraft/network/chat/Component;"))
    private Component getCustomUsername(Player player)
    {
        if (Highlight.changeUsername)
        {
            return Component.literal(Highlight.getUsername());
        }
        else
        {
            return player.getName();
        }
    }

    @ModifyVariable(method = "decorateDisplayNameComponent(Lnet/minecraft/network/chat/MutableComponent;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("HEAD"))
    private MutableComponent changeUsernameColor(MutableComponent componentln)
    {
        if (Highlight.changeUsername && Highlight.getStyle() != null)
        {
            componentln.setStyle(Highlight.getStyle());
        }
        return componentln;
    }

    @Redirect(method = "decorateDisplayNameComponent(Lnet/minecraft/network/chat/MutableComponent;)Lnet/minecraft/network/chat/MutableComponent;", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;name()Ljava/lang/String;", remap = false))
    private String changeCustomUsername(GameProfile gameProfile)
    {
        String username = gameProfile.name();
        if (Highlight.changeUsername)
        {
            username = Highlight.getUsername();
        }
        return username;
    }
}
