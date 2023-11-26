package com.samsthenerd.hexgloop.mixins.lociathome;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
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
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociHandler;
import com.samsthenerd.hexgloop.casting.wehavelociathome.LociRegistration;
import com.samsthenerd.hexgloop.casting.wehavelociathome.LociUtils;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.IIotaProviderLocus;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.ILocusModule;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.IRedirectLocus;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.ISpeedLocus;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(BlockEntityAbstractImpetus.class)
public class MixinLociAtHome implements ILociHandler{
    @Shadow
    private List<BlockPos> trackedBlocks;
    @Shadow
    private transient Set<BlockPos> knownBlocks;
    
    private boolean shouldExit = false;
    private Set<BlockPos> castedBlocks = new HashSet<BlockPos>();
    // don't break the contract, only put modules in lists corresponding to the correct key
    
    @Shadow
    void stepCircle() {
        throw new AssertionError();
    }
    
    // ILociHandler accessors
    
    private Map<Class<? extends ILocusModule>, List<Pair<BlockPos, ? extends ILocusModule>>> trackedModuleBlocks = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends ILocusModule> List<Pair<BlockPos, T>> getTrackedModuleBlocks(Class<T> module){
        if(trackedModuleBlocks.containsKey(module)){
            // cast to list to force it to return the correct generic
            return new ArrayList<Pair<BlockPos, T>>((List)(trackedModuleBlocks.get(module)));
        }
        return new ArrayList<>();
    }

    public List<BlockPos> getTrackedBlocks(){
        return new ArrayList<BlockPos>(trackedBlocks);
    }
    
    // blocks that the circle has energized over
    public Set<BlockPos> getKnownBlocks(){
        return new HashSet<BlockPos>(knownBlocks);
    }
    
    // blocks that the circle has actually gone back and casted over - good for not accessing things it hasn't "gotten to" yet
    public Set<BlockPos> getCastedBlocks(){
        return new HashSet<BlockPos>(castedBlocks);
    }

    // primary hooks

    // track blocks by module
    @WrapOperation(method="stepCircle()V", at=@At(value="INVOKE", target="java/util/List.add (Ljava/lang/Object;)Z"), remap=false)
    private boolean trackNewBlock(List probablyTrackedBlocks, Object probablyBlockPos, Operation<Boolean> original){
        if(probablyBlockPos instanceof BlockPos pos){
            BlockEntityAbstractImpetus impetus = ((BlockEntityAbstractImpetus)(Object)this);
            World world = impetus.getWorld();
            BlockState state = world.getBlockState(pos);
            ILociAtHome locusBlock = LociRegistration.getLocus(state, pos, world);
            if(locusBlock != null){
                // enter new one
                locusBlock.waveEnter(pos, state, world, impetus);
                
                for(Class<? extends ILocusModule> module : ILociHandler.MODULE_TYPES){
                    if(!module.isInstance(locusBlock)) continue; // verify that it has this module
                    // track it !
                    if(!trackedModuleBlocks.containsKey(module)){
                        trackedModuleBlocks.put(module, new ArrayList<>());
                    }
                    trackedModuleBlocks.get(module).add(new Pair<BlockPos, ILocusModule>(pos, module.cast(locusBlock)));
                }
            }
            // exit old one
            if(trackedBlocks.size() > 0){
                BlockPos lastBlock = trackedBlocks.get(trackedBlocks.size()-1);
                BlockState lastState = world.getBlockState(lastBlock);
                ILociAtHome lastLocus = LociRegistration.getLocus(lastState, lastBlock, world);
                if(lastLocus != null){
                    lastLocus.waveExit(lastBlock, lastState, world, impetus);
                }
            }
        }
        return original.call(probablyTrackedBlocks, probablyBlockPos);
    }
    
    @WrapOperation(method = "castSpell()V",
    at=@At(value="INVOKE", target="at/petrak/hexcasting/api/block/circle/BlockCircleComponent.getPattern (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;)Lat/petrak/hexcasting/api/spell/math/HexPattern;"))
    private HexPattern bigFakeGloopyLociWrap(BlockCircleComponent block, BlockPos pos, BlockState bs, World world, Operation<HexPattern> original, 
        @Local(ordinal=0) CastingHarness harness, @Local(ordinal = 0) LocalRef<BlockPos> erroredPos){
        if(shouldExit) return null; // unideal but it'll just skip every new slate
        ILociAtHome locusBlock = LociRegistration.getLocus(bs, pos, world);
        if(locusBlock != null){
            castedBlocks.add(pos);
            locusBlock.rawLociCall(pos, bs, world, harness);
            if(locusBlock.shouldStopCircle()){
                shouldExit = true;
                ((BlockEntityAbstractImpetus)(Object)this).setLastMishap(locusBlock.getStopCircleError());
                erroredPos.set(pos);
                return null;
            }
            if(locusBlock instanceof IIotaProviderLocus iotaLocus){
                Iota providedIota = iotaLocus.provideIota(pos, bs, world, harness);
                if(providedIota instanceof PatternIota pIota){
                    return pIota.getPattern();
                } else if(providedIota != null){
                    // embed it
                    LociUtils.addOrEmbedIota(harness, providedIota);
                }
            }
        }
        return original.call(block, pos, bs, world);
    }

    @Inject(method = "stopCasting()V", at=@At("HEAD"), remap=false)
    private void resetShouldExit(CallbackInfo ci){
        shouldExit = false;
        castedBlocks = new HashSet<BlockPos>();
        trackedModuleBlocks = new HashMap<>();
        // tell them the circle stopped
        BlockEntityAbstractImpetus impetus = (BlockEntityAbstractImpetus)(Object)this;
        for(BlockPos pos : knownBlocks){
            World world = impetus.getWorld();
            BlockState state = world.getBlockState(pos);
            ILociAtHome locus = LociRegistration.getLocus(state, pos, world);
            if(locus != null){
                locus.circleStopped(pos, state, world, impetus);
            }
        }
    }    

    // specific module hooks:

    @WrapOperation(method="stepCircle()V", at=@At(value="INVOKE", target="at/petrak/hexcasting/api/block/circle/BlockCircleComponent.exitDirections (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;)Ljava/util/EnumSet;"))
    public EnumSet<Direction> checkForForcedNextPos(BlockCircleComponent thisComponent, BlockPos currentPos, 
        BlockState currentState, World world, Operation<EnumSet<Direction>> original, @Share("pos") LocalRef<BlockPos> forcedPos){
        ILociAtHome locus = LociRegistration.getLocus(currentState, currentPos, world);
        if(locus instanceof IRedirectLocus redirectLocus){
            BlockPos possibleNextPos = redirectLocus.forceNextPos(currentPos, currentState, world, trackedBlocks, knownBlocks);
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

    @ModifyReturnValue(method="getTickSpeed()I", at=@At("RETURN"), remap=false)
    public int modifyTickSpeed(int original){
        double currentModifier = 1;
        Map<Block, Integer> blockCounts = new HashMap<Block, Integer>();
        int blocksIn = 0;
        int blocksAgo = trackedBlocks != null ? trackedBlocks.size()-1 : 0;
        BlockEntityAbstractImpetus impetus = (BlockEntityAbstractImpetus)(Object)this;
        if(trackedBlocks != null){
            // OK i know i just reworked this to not need to iterate over every block,, but it's the easiest way to get the blocks ago number
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
                ILociAtHome locus = LociRegistration.getLocus(state, pos, impetus.getWorld());
                if(locus instanceof ISpeedLocus speedLocus){
                    currentModifier *= speedLocus.modifyTickDelay(blocksAgo, currentModifier, original, blockCounts.get(block), trackedBlocks, impetus.getWorld(), impetus);
                    if(currentModifier < 0){
                        return -1; // -1 to mean instant
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
            // HexGloop.logPrint("immediately stepping");
            stepCircle();
        } else {
            original.call(world, pos, block, delay);
        }
    }
}
