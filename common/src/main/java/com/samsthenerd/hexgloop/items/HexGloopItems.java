package com.samsthenerd.hexgloop.items;

import java.util.function.Supplier;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HexGloopItems {
    public static DeferredRegister<Item> items = DeferredRegister.create(HexGloop.MOD_ID, Registry.ITEM_KEY);

    public static final RegistrySupplier<Item> GLOOP_ITEM = item("gloop", 
        () -> new Item(defaultSettings()));
    public static final RegistrySupplier<ItemMultiFocus> MULTI_FOCUS_ITEM = item("multi_focus", 
        () -> new ItemMultiFocus(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemCastingRing> CASTING_RING_ITEM = item("casting_ring", 
        () -> new ItemCastingRing(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemCastingPotion> CASTING_POTION_ITEM = item("casting_potion", 
        () -> new ItemCastingPotion(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemGloopDye> GLOOP_DYE_ITEM = item("gloop_dye", 
        () -> new ItemGloopDye(defaultSettings().maxCount(1)));
    

    public static <T extends Item> RegistrySupplier<T> item(String name, Supplier<T> item) {
		return items.register(new Identifier(HexGloop.MOD_ID, name), item);
	}

    public static Item.Settings defaultSettings(){
        return new Item.Settings().group(HEX_GLOOP_GROUP);
    }

    public static final ItemGroup HEX_GLOOP_GROUP = CreativeTabRegistry.create(
		new Identifier(HexGloop.MOD_ID, "general"),
		() -> GLOOP_ITEM.get().getDefaultStack());

    public static void register(){
        items.register();
    }
    
}
