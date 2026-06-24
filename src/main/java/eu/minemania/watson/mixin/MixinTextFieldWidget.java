package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import eu.minemania.watson.interfaces.ITextFieldWidget;
import net.minecraft.client.gui.components.EditBox;

@Mixin(EditBox.class)
public abstract class MixinTextFieldWidget implements ITextFieldWidget
{
    @Accessor("maxLength")
    @Override
    public abstract int clientcommands_getMaxLength();
}
