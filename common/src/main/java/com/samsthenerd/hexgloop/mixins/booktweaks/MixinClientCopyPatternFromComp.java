package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.misc.clientgreatbook.PatternCompGetter;

import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.client.MinecraftClient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;

@Mixin(ICustomComponent.class)
public interface MixinClientCopyPatternFromComp {
    @Inject(
        method="mouseClicked(Lvazkii/patchouli/api/IComponentRenderContext;DDI)Z",
        at=@At("HEAD"),
        remap=false
    )
    public default void mouseClickedOnPattern(IComponentRenderContext context, double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        ICustomComponent self = (ICustomComponent) (Object) this;
        if(self instanceof PatternCompGetter patternComp){
            String copyText = "";
            for(HexPattern pattern : patternComp.getCopyablePatterns()){
                PatternIota patternIota = new PatternIota(pattern);
                copyText += (patternIota.display().getString() + " ");
            }
            if(!copyText.isEmpty()){
                MinecraftClient.getInstance().keyboard.setClipboard(copyText);
            }
        }
    }
}
