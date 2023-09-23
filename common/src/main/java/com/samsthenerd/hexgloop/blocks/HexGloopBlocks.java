package com.samsthenerd.hexgloop.blocks;

import java.util.function.Supplier;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HexGloopBlocks {
    public static DeferredRegister<Block> blocks = DeferredRegister.create(HexGloop.MOD_ID, Registry.BLOCK_KEY);

    public static final RegistrySupplier<BlockGloopEnergizer> GLOOP_ENERGIZER_BLOCK = block("gloop_energizer", 
        () -> new BlockGloopEnergizer(AbstractBlock.Settings.of(Material.METAL, MapColor.TERRACOTTA_PURPLE).requiresTool().strength(25.0f, 1200.0f).sounds(BlockSoundGroup.NETHERITE)));

    public static final RegistrySupplier<BlockPedestal> PEDESTAL_BLOCK = block("pedestal", 
        () -> new BlockPedestal(AbstractBlock.Settings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).requiresTool().strength(4f, 4f).sounds(BlockSoundGroup.DEEPSLATE_TILES), false));
    public static final RegistrySupplier<BlockPedestal> MIRROR_PEDESTAL_BLOCK = block("mirror_pedestal", 
        () -> new BlockPedestal(AbstractBlock.Settings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).requiresTool().strength(4f, 4f).sounds(BlockSoundGroup.DEEPSLATE_TILES), true));

    public static final RegistrySupplier<BlockConjuredRedstone> CONJURED_REDSTONE_BLOCK = block("conjured_redstone", 
        () -> new BlockConjuredRedstone(AbstractBlock.Settings.of(Material.AMETHYST, MapColor.TERRACOTTA_PURPLE).requiresTool().strength(1.5f, 6.0f).sounds(BlockSoundGroup.AMETHYST_BLOCK)),
        new Item.Settings()); // don't put it in item group

    public static Block.Settings defaultSettings(){
        return Block.Settings.of(Material.STONE).hardness((float)1.3);
    }

    public static <T extends Block> RegistrySupplier<T> block(String name, Supplier<T> block) {
        return block(name, block, HexGloopItems.defaultSettings());
	}

    public static <T extends Block> RegistrySupplier<T> block(String name, Supplier<T> block, Item.Settings settings) {
        RegistrySupplier<T> blockRegistered = blockNoItem(name, block);
        HexGloopItems.item(name, () -> new BlockItem(blockRegistered.get(), settings));
		return blockRegistered;
	}

    public static <T extends Block> RegistrySupplier<T> blockNoItem(String name, Supplier<T> block) {
		return blocks.register(new Identifier(HexGloop.MOD_ID, name), block);
	}

    public static void register(){
        blocks.register();
    }
}
