package com.samsthenerd.hexgloop.mixins.lociathome;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;

@Mixin(ChestBlock.class)
public interface MixinWeirdChestStatics {
    @Accessor("INVENTORY_RETRIEVER")
    public static DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> getInventoryRetriever(){
        throw new AssertionError();
    }

    @Accessor("NAME_RETRIEVER")
    public static DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> getNameRetriever(){
        throw new AssertionError();
    }


}
