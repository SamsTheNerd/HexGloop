package com.samsthenerd.hexgloop.mixins.ring;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(CastingHarness.class)
public class MixinLetRingDrawMedia {
    @ModifyExpressionValue(
        method = "withdrawMedia(IZ)I",
        at = @At(value = "INVOKE", target="net/minecraft/item/ItemStack.isIn (Lnet/minecraft/tag/TagKey;)Z"))
    private boolean allowRingCastingMediaDraw(boolean original){
        ServerPlayerEntity player = ((CastingHarness)(Object)this).getCtx().getCaster();
        if(player == null) return original;
        return original || HexGloop.TRINKETY_INSTANCE.isCastingRingEquipped(player);
    }
}
