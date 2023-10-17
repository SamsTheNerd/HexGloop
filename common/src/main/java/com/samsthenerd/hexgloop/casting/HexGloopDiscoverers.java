package com.samsthenerd.hexgloop.casting;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.blockentities.BlockEntitySlateChest;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.casting.mirror.SyncedItemHandling;
import com.samsthenerd.hexgloop.casting.wehavelociathome.IContextHelper;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.common.items.ItemLens;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class HexGloopDiscoverers {
    public static void init(){
        DiscoveryHandlers.addItemSlotDiscoverer((ctx)->{
            return getChestStacks(ctx);
        });
        DiscoveryHandlers.addMediaHolderDiscoverer((harness)->{
            CastingContext ctx = harness.getCtx();
            List<ADMediaHolder> holders = new ArrayList<ADMediaHolder>();
            for(ItemStack item : getChestStacks(ctx)){
                ADMediaHolder holder = IXplatAbstractions.INSTANCE.findMediaHolder(item);
                if(holder != null) holders.add(holder);
            }
            return holders;
        });

        DiscoveryHandlers.addLensPredicate(HexGloopDiscoverers::hasReflectedLens);
        DiscoveryHandlers.addMediaHolderDiscoverer(HexGloopDiscoverers::getReflectedMediaHolder);
        DiscoveryHandlers.addGridScaleModifier(HexGloopDiscoverers::getReflectedGridScaleModifier);
    }

    public static List<ItemStack> getChestStacks(CastingContext ctx){
        List<ItemStack> items = new ArrayList<ItemStack>();
        if(((Object)ctx) instanceof IContextHelper ctxHelper){
            List<BlockPos> chestRefs = ctxHelper.getChestRefs();
            for(BlockPos pos : chestRefs){
                BlockEntitySlateChest slateChest = ctx.getWorld().getBlockEntity(pos, HexGloopBEs.SLATE_CHEST_BE.get()).orElse(null);
                if(slateChest == null) continue;
                for(ItemStack item : slateChest.getInvStackListPublic()){
                    if(item.isEmpty()) continue;
                    items.add(item);
                }
            }
        }
        return items;
    }

    public static boolean hasReflectedLens(PlayerEntity player){
        return getReflectedGridScaleModifier(player) < 1;
    }

    public static float getReflectedGridScaleModifier(PlayerEntity player){
        ItemStack mainAlt = ItemStack.EMPTY;
        ItemStack offAlt = ItemStack.EMPTY;
        if(player instanceof ServerPlayerEntity sPlayer){
            mainAlt = SyncedItemHandling.getAlternateHandStack(sPlayer, Hand.MAIN_HAND, null);
            offAlt = SyncedItemHandling.getAlternateHandStack(sPlayer, Hand.OFF_HAND, null);
        } else {
            Pair<ItemStack, Boolean> mainRes = SyncedItemHandling.getAlternateStackClient(player, Hand.MAIN_HAND);
            mainAlt = mainRes == null ? ItemStack.EMPTY : mainRes.getLeft();
            Pair<ItemStack, Boolean> offRes = SyncedItemHandling.getAlternateStackClient(player, Hand.OFF_HAND);
            offAlt = offRes == null ? ItemStack.EMPTY : offRes.getLeft();
        }
        mainAlt = mainAlt == null ? ItemStack.EMPTY : mainAlt;
        offAlt = offAlt == null ? ItemStack.EMPTY : offAlt;
        float scale = 1;
        if(mainAlt.getItem() instanceof ItemLens){
            scale *= 0.75;
        }
        if(mainAlt != offAlt && offAlt.getItem() instanceof ItemLens){
            scale *= 0.75;
        }
        return scale;
    }

    
    // I guess it'll get bound reflected holder no matter what
    public static List<ADMediaHolder> getReflectedMediaHolder(CastingHarness harness){
        CastingContext ctx = harness.getCtx();
        ItemStack mainAlt = SyncedItemHandling.getAlternateHandStack(ctx.getCaster(), Hand.MAIN_HAND, ctx);
        mainAlt = mainAlt == null ? ItemStack.EMPTY : mainAlt;
        ItemStack offAlt = SyncedItemHandling.getAlternateHandStack(ctx.getCaster(), Hand.OFF_HAND, ctx);
        offAlt = offAlt == null ? ItemStack.EMPTY : offAlt;
        List<ADMediaHolder> holders = new ArrayList<ADMediaHolder>();
        ADMediaHolder mainHolder = IXplatAbstractions.INSTANCE.findMediaHolder(mainAlt);
        ADMediaHolder offHolder = IXplatAbstractions.INSTANCE.findMediaHolder(offAlt);
        if(mainHolder != null) holders.add(mainHolder);
        if(offHolder != null && mainAlt != offAlt) holders.add(offHolder); // make sure we don't add both if they're both reflecting the same thing
        return holders;
    }
}
