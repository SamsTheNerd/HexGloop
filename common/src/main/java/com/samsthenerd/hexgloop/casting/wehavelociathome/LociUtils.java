package com.samsthenerd.hexgloop.casting.wehavelociathome;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.IMediaProviderLocus;
import com.samsthenerd.hexgloop.mixins.lociathome.MixinExposeHarnessStuff;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType;
import at.petrak.hexcasting.api.spell.casting.eval.FunctionalData;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class LociUtils {
    public static int getCircleMedia(BlockEntityAbstractImpetus impetus){
        return withdrawCircleMedia(impetus, -1, true, true);
    }

    // returns how much media is left unfulfilled
    // so if it withdraws the full amount it returns 0
    // if amount == -1 then return how much media there is -- silly but it keeps everything in one place, note that return value of 0 means infinite media
    public static int withdrawCircleMedia(BlockEntityAbstractImpetus impetus, int amount, boolean takeFromImpetus, boolean simulate){
        if(impetus.getMedia() < 0 && !simulate && takeFromImpetus) return 0; // for infinite media
        int amountRemaining = amount;
        if(amount == -1) amountRemaining = 0;
        List<Pair<BlockPos, IMediaProviderLocus>> mediaProviders = ILociHandler.get(impetus).getTrackedModuleBlocks(IMediaProviderLocus.class);
        List<ADMediaHolder> mediaHolders = new ArrayList<>();
        // check through for infinite media
        for(Pair<BlockPos, IMediaProviderLocus> mediaProvider : mediaProviders){
            ADMediaHolder mediaHolder = mediaProvider.getRight().getMediaHolder(mediaProvider.getLeft(), impetus.getWorld(), impetus);
            if(mediaHolder == null) continue;
            if(mediaHolder.getMedia() < 0) return 0; // infinite media
            mediaHolders.add(mediaHolder);
        }
        // didn't find infinite media so go through and withdraw them
        for(ADMediaHolder mediaHolder : mediaHolders){
            if(amount == -1){
                amountRemaining += mediaHolder.getMedia();
                continue;
            }
            int amtToTake = Math.min(amountRemaining, mediaHolder.getMedia());
            amountRemaining -= amtToTake;
            if(!simulate)
                mediaHolder.setMedia(mediaHolder.getMedia() - amtToTake);
        }
        if(takeFromImpetus){
            if(amount == -1){
                amountRemaining += impetus.getMedia();
            } else {
                int amtToTake = Math.min(amountRemaining, impetus.getMedia());
                amountRemaining -= amtToTake;
                if(!simulate)
                    impetus.setMedia(impetus.getMedia() - amtToTake);
            }
        }
        return amountRemaining;
    }

    public static boolean addOrEmbedIota(CastingHarness harness, Iota iota){
        // so we want to either embed the iota in a new paren or add it to existing or add it to the stack?
        if(iota instanceof PatternIota patternIota){
            // if it's a pattern iota deal with it elsewhere maybe ?
            return true;
        }
        kotlin.Pair<FunctionalData, ResolvedPatternType> result = ((MixinExposeHarnessStuff)(Object)harness).invokehandleParentheses(iota);
        if(result != null && result.getSecond() != null && result.getSecond().getSuccess()){
            harness.applyFunctionalData(result.getFirst());
            return true;
        }
        return false; // ?
    }
}
