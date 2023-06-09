package com.samsthenerd.hexgloop.mixins.ring;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(GuiSpellcasting.class)
public class MixinRingCastingSmall {
    @Inject(method="hexSize()F", at=@At("RETURN"), cancellable=true, remap = false)
    private void ringOverrideGridSize(CallbackInfoReturnable<Float> callbackInfo){
        PlayerEntity player = MinecraftClient.getInstance().player;
        // probably not the best way to do this, but just so that it doesn't reduce the size of the grid when you're using a staff with ring casting
        boolean hasStaff = player.isHolding((item) -> item.isIn(HexTags.Items.STAVES));
        if(HexGloop.TRINKETY_INSTANCE.isCastingRingEquipped(player) && !hasStaff){
            callbackInfo.setReturnValue(1.5F*callbackInfo.getReturnValue());
        }
    }
}
