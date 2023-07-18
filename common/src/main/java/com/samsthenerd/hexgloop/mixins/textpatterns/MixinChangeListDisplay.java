package com.samsthenerd.hexgloop.mixins.textpatterns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;

@Mixin(targets = "at.petrak.hexcasting.api.spell.iota.ListIota$1")
public class MixinChangeListDisplay {
    @WrapOperation(method = "display(Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/text/Text;",
    at = @At(value = "INVOKE", target = "net/minecraft/text/MutableText.append (Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    public MutableText removeListCommasBetweenPatterns(MutableText text, String comma, Operation<MutableText> originalOp, 
        NbtElement tag, @Local(ordinal = 0) int index, @Local(ordinal = 0) NbtList list ) {
        
        // we know that i < list.size() - 1 from the original code
        NbtCompound thisElem = HexUtils.downcast(list.get(index), NbtCompound.TYPE);
        NbtCompound nextElem = HexUtils.downcast(list.get(index+1), NbtCompound.TYPE);
        if(HexIotaTypes.getTypeFromTag(thisElem) == HexIotaTypes.PATTERN && HexIotaTypes.getTypeFromTag(nextElem) == HexIotaTypes.PATTERN) {
            text.append(" ");
        } else {
            originalOp.call(text, comma);
        }
        return text;
    }
}
