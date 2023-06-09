package com.samsthenerd.hexgloop.blockentities;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HexGloopBEs {
    public static DeferredRegister<BlockEntityType<?>> blockEntities = DeferredRegister.create(HexGloop.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);

    public static RegistrySupplier<BlockEntityType<BlockEntityGloopEnergizer>> GLOOP_ENERGIZER_BE 
        = blockEntities.register(new Identifier(HexGloop.MOD_ID, "gloop_energizer_tile"), 
        () -> BlockEntityType.Builder.create(BlockEntityGloopEnergizer::new, HexGloopBlocks.GLOOP_ENERGIZER_BLOCK.get()).build(null));

    public static void register(){
        blockEntities.register();
    }
}
