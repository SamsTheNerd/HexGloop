package com.samsthenerd.hexgloop.mixins.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.blocks.ICatPost;

import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

@Mixin(CatSitOnBlockGoal.class)
public class MixinCatSit {
    @Inject(method="isTargetPos(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", at=@At("RETURN"), cancellable = true)
    public void iSits(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()) return; // if it already returns true, don't bother checking
        // make sure we don't suffocate the kitty
        if (!world.isAir(pos.up())) {
			cir.setReturnValue(false);
		}
        if(world.getBlockState(pos).getBlock() instanceof ICatPost catPost){
            if(catPost.ifItFits(world, pos)){
                cir.setReturnValue(true);
            }
        }
    }
}
