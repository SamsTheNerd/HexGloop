package com.samsthenerd.hexgloop.mixins.lociathome;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

    @Shadow
    private List<BlockPos> trackedBlocks;

    @Shadow
    private transient Set<BlockPos> knownBlocks;

    @WrapOperation(method="stepCircle()V", at=@At(value="INVOKE", target="at/petrak/hexcasting/api/block/circle/BlockCircleComponent.exitDirections (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;)Ljava/util/EnumSet;"))
    public EnumSet<Direction> checkForForcedNextPos(BlockCircleComponent thisComponent, BlockPos currentPos, 
        BlockState currentState, World world, Operation<EnumSet<Direction>> original, @Share("pos") LocalRef<BlockPos> forcedPos){
        if(thisComponent instanceof ILociAtHome locus){
            BlockPos possibleNextPos = locus.forceNextPos(currentPos, currentState, world, trackedBlocks, knownBlocks);
            if(possibleNextPos != null){
                forcedPos.set(possibleNextPos);
                return EnumSet.noneOf(Direction.class);
            }
        }
        // set it back to null if it was set from last time
        forcedPos.set(null);
        return original.call(thisComponent, currentPos, currentState, world);
    }

    @ModifyVariable(method="stepCircle()V", 
    at=@At(value="STORE"),
    ordinal = 1,
    slice=@Slice(
        from=@At(value="INVOKE", target="at/petrak/hexcasting/api/block/circle/BlockCircleComponent.exitDirections (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;)Ljava/util/EnumSet;")
    ))
    public BlockPos applyForcedNextPos(BlockPos original, @Share("pos") LocalRef<BlockPos> forcedPos){
        BlockPos forced = forcedPos.get();
        if(forced != null){
            forcedPos.set(null);
            return forced;
        } else {
            return original;
        }
    }

    @Shadow
    void stepCircle() {
        throw new AssertionError();
    }

    @ModifyReturnValue(method="getTickSpeed()I", at=@At("RETURN"), remap=false)
    public int modifyTickSpeed(int original){
        double currentModifier = 1;
        Map<Block, Integer> blockCounts = new HashMap<Block, Integer>();
        int blocksIn = 0;
        int blocksAgo = trackedBlocks != null ? trackedBlocks.size()-1 : 0;
        BlockEntityAbstractImpetus impetus = (BlockEntityAbstractImpetus)(Object)this;
        if(trackedBlocks != null){
            for(BlockPos pos : trackedBlocks){
                BlockState state = impetus.getWorld().getBlockState(pos);
                Block block = state.getBlock();
                if(blockCounts.containsKey(block)){
                    blockCounts.put(block, blockCounts.get(block) + 1);
                } else {
                    blockCounts.put(block, 1);
                }
                blocksIn++;
                blocksAgo--;
                if(block instanceof ILociAtHome locus){
                    currentModifier *= locus.modifyTickDelay(blocksAgo, currentModifier, original, blockCounts.get(block), trackedBlocks, impetus.getWorld(), impetus);
                    if(currentModifier < 0){
                        return -1; // -1 to mean 
                    }
                }
            }
        }
        if(currentModifier < 0){
            return -1;
        }
        return (int)Math.round(original * currentModifier);
    }

    
    @WrapOperation(method="stepCircle()V", at=@At(value="INVOKE", target="net/minecraft/world/World.createAndScheduleBlockTick (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V"))
    public void stepImmediatelyIfNeeded(World world, BlockPos pos, Block block, int delay, Operation<Void> original){
        if(delay < 0){ // have negative imply that we should tick again immediately
            HexGloop.logPrint("immediately stepping");
            stepCircle();
        } else {
            original.call(world, pos, block, delay);
        }
    }
}
