package com.samsthenerd.hexgloop.casting;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.blockentities.BlockEntitySlateChest;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.casting.wehavelociathome.IContextHelper;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.item.ItemStack;
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
    }

    public static List<ItemStack> getChestStacks(CastingContext ctx){
        List<ItemStack> items = new ArrayList<ItemStack>();
        if(((Object)ctx) instanceof IContextHelper ctxHelper){
            List<BlockPos> chestRefs = ctxHelper.getChestRefs();
            for(BlockPos pos : chestRefs){
                BlockEntitySlateChest slateChest = ctx.getWorld().getBlockEntity(pos, HexGloopBEs.SLATE_CHEST_BE.get()).orElse(null);
                for(ItemStack item : slateChest.getInvStackListPublic()){
                    if(item.isEmpty()) continue;
                    items.add(item);
                }
            }
        }
        return items;
    }
}
