package com.samsthenerd.hexgloop.mixins.lociathome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockEntityAbstractImpetus.class)
public class MixinLociAtHome {
    private boolean shouldExit = false;
    
    @WrapOperation(method = "castSpell()V",
    at=@At(value="INVOKE", target="at/petrak/hexcasting/api/block/circle/BlockCircleComponent.getPattern (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;)Lat/petrak/hexcasting/api/spell/math/HexPattern;"))
    private HexPattern bigFakeGloopyLociWrap(BlockCircleComponent block, BlockPos pos, BlockState bs, World world, Operation<HexPattern> original, 
        @Local(ordinal=0) CastingHarness harness, @Local(ordinal = 0) LocalRef<BlockPos> erroredPos){
        if(shouldExit) return null; // unideal but it'll just skip every new slate
        if(block instanceof ILociAtHome lociBlock){
            lociBlock.rawLociCall(pos, bs, world, harness);
            if(lociBlock.shouldStopCircle()){
                shouldExit = true;
                ((BlockEntityAbstractImpetus)(Object)this).setLastMishap(lociBlock.getStopCircleError());
                erroredPos.set(pos);
                return null;
            }
            if(lociBlock.hasBetterGetPattern()){
                return lociBlock.betterGetPattern(pos, bs, world, harness);
            }
        }
        return original.call(block, pos, bs, world);
    }

    @Inject(method = "castSpell()V", at=@At("RETURN"), remap=false)
    private void resetShouldExit(CallbackInfo ci){
        shouldExit = false;
    }
}
