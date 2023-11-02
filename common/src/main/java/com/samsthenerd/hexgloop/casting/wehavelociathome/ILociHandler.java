package com.samsthenerd.hexgloop.casting.wehavelociathome;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.IItemProviderLocus;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.ILocusModule;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.IMediaProviderLocus;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.IRedirectLocus;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.ISpeedLocus;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

// to be injected onto BlockEntityAbstractImpetus
public interface ILociHandler {
    // all the known module types here - good for looping over
    public static final List<Class<? extends ILocusModule>> MODULE_TYPES = new ArrayList<>(List.of(
        IItemProviderLocus.class,
        IRedirectLocus.class,
        IMediaProviderLocus.class,
        ISpeedLocus.class
    ));

    // just to cast easily
    public static ILociHandler get(BlockEntityAbstractImpetus impetus){
        if(impetus instanceof ILociHandler handler){
            return handler;
        }
        return null;
    }

    // get all blocks that implement the given module - should preserve order
    public <T extends ILocusModule> List<Pair<BlockPos, T>> getTrackedModuleBlocks(Class<T> module);

    public List<BlockPos> getTrackedBlocks();
    
    // blocks that the circle has energized over
    public Set<BlockPos> getKnownBlocks();
    
    // blocks that the circle has actually gone back and casted over - good for not accessing things it hasn't "gotten to" yet
    public Set<BlockPos> getCastedBlocks();
}
