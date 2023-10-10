package com.samsthenerd.hexgloop;

import com.samsthenerd.hexgloop.items.ItemSimpleMediaProvider;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.cc.adimpl.CCMediaHolder;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;

public class HexGloopCC implements ItemComponentInitializer{

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        for(ItemSimpleMediaProvider simpleMediaProvider : ItemSimpleMediaProvider.allSimpleMediaItems){
            registry.register(simpleMediaProvider, HexCardinalComponents.MEDIA_HOLDER, stack -> new CCMediaHolder.Static(
                simpleMediaProvider::getMediaAmount, simpleMediaProvider.getPriority(), stack
            ){
                @Override
                public int withdrawMedia(int cost, boolean simulate) {
                    ItemSimpleMediaProvider thisItem = (ItemSimpleMediaProvider)(stack.getItem());
                    if(thisItem.shouldUseOwnWithdrawLogic(stack)){
                        return thisItem.withdrawMedia(stack, cost, simulate);
                    }
                    return super.withdrawMedia(cost, simulate);
                }
            });
        }
    }

    
}