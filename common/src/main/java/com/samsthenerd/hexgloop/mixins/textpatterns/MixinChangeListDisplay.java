package com.samsthenerd.hexgloop.mixins.textpatterns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

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

    // add copy thingy for list
    @Inject(method = "display(Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/text/Text;", at = @At("RETURN"), cancellable = true)
    public void copyfullText(NbtElement tag, CallbackInfoReturnable<Text> cir){
        // basically just yoink inside of list creating method
        NbtList list = HexUtils.downcast(tag, NbtList.TYPE);

        String copyText = getCopyText(list);
        
        MutableText listText = cir.getReturnValue().copy();
        Style clickEventStyle = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copyText));
        listText.setStyle(clickEventStyle.withParent(listText.getStyle()));
        cir.setReturnValue(listText);
    }

    // make our recursion more visible so that we don't exponential skill issue ourselves
    public String getCopyText(NbtElement element){ 
        String copyText = "";
        if(element.getNbtType() == NbtList.TYPE){ // handle list
            NbtList list = HexUtils.downcast(element, NbtList.TYPE);
            copyText = "[";
            for (int i = 0; i < list.size(); i++) {
                NbtElement sub = list.get(i);
                copyText += getCopyText(sub);
                
                if (i < list.size() - 1) {
                    copyText += (", ");
                }
            }
            copyText += "]";
        } else { // handle not list
            NbtCompound csub = HexUtils.downcast(element, NbtCompound.TYPE);
            IotaType<?> type = HexIotaTypes.getTypeFromTag(csub);
            if(type == PatternIota.TYPE){
                // handle pattern:
                NbtCompound tagData = csub.getCompound(HexIotaTypes.KEY_DATA);
                if(tagData == null || tagData.isEmpty()) return copyText;
                HexPattern pattern = HexPattern.fromNBT(tagData);
                // HexGloop.logPrint(csub.toString() + " => " + pattern.toString() + "\n");
                copyText += ("<" + pattern.getStartDir().toString().replace("_", "").toLowerCase() + "," + pattern.anglesSignature() + ">");
            } else if(type == ListIota.TYPE){
                // kinda handle lists again ? mostly just get the data out of here and call it as if we were passing it to the display method
                var data = csub.get(HexIotaTypes.KEY_DATA);
                return getCopyText(data);
            } else {
                copyText += HexIotaTypes.getDisplay(csub).getString();
            }
        }
        
        return copyText;
    }
    
}
