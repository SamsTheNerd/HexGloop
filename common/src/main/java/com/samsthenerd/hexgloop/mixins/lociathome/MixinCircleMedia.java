package com.samsthenerd.hexgloop.mixins.lociathome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.samsthenerd.hexgloop.casting.wehavelociathome.LociUtils;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;

@Mixin(CastingHarness.class)
public class MixinCircleMedia {
    // tell it about the media we can use from any components
    @WrapOperation(method="withdrawMedia(IZ)I",
        at=@At(value="INVOKE", target="at/petrak/hexcasting/api/block/circle/BlockEntityAbstractImpetus.getMedia ()I"),
        remap=false)
    public int getAllCircleMedia(BlockEntityAbstractImpetus impetus, Operation<Integer> original, @Share("mediaAvailable") LocalIntRef mediaAvailableRef ){
        int mediaAvailable = LociUtils.getCircleMedia(impetus);
        mediaAvailableRef.set(mediaAvailable);
        return mediaAvailable;
    }

    // take from other media first
    @WrapOperation(method="withdrawMedia(IZ)I",
        at=@At(value="INVOKE", target="at/petrak/hexcasting/api/block/circle/BlockEntityAbstractImpetus.setMedia (I)V"),
        remap=false)
    public void wrapSetCircleMedia(BlockEntityAbstractImpetus impetus, int newAmount, Operation<Void> original, @Share("mediaAvailable") LocalIntRef mediaAvailableRef){
        int withdrawAmount = mediaAvailableRef.get() - newAmount;
        LociUtils.withdrawCircleMedia(impetus, withdrawAmount, true, false);
    }
}
