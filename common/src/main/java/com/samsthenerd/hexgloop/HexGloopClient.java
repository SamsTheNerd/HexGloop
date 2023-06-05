package com.samsthenerd.hexgloop;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class HexGloopClient {
    public static void onInitializeClient() {
        HexGloop.logPrint("Initializing HexGloopClient");
        registerModelPredicates();
        registerColorProviders();
    }

    private static void registerColorProviders(){
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1){
                NbtCompound iotaNbt = HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(stack);
                if(iotaNbt == null){
                    return 0xFFFFFF; //white
                }
                return HexIotaTypes.getColor(iotaNbt);
            }
            if(tintIndex > 2 && tintIndex < 9){
                NbtCompound iotaNbt = HexGloopItems.MULTI_FOCUS_ITEM.get().readSlotIotaTag(stack, tintIndex - 2);
                if(iotaNbt == null){
                    return 0xFFFFFF; //white
                }
                return HexIotaTypes.getColor(iotaNbt);
            }
            return 0xFFFFFF; //white
		}, HexGloopItems.MULTI_FOCUS_ITEM.get());
    }

    private static void registerModelPredicates(){
        ModelPredicateProviderRegistry.register(HexGloopItems.MULTI_FOCUS_ITEM.get(), new Identifier("selected"), 
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
            int slot = ItemSpellbook.getPage(itemStack, -1);
            if(slot < 1){
                return 0.0F;
            }
            return (float)((1/6.0)*(slot-1))+0.0001F;
        });

        ModelPredicateProviderRegistry.register(HexGloopItems.MULTI_FOCUS_ITEM.get(), new Identifier("sealed"), 
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
            if(HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(itemStack) == null){
                return 0.0F;
            }
            if(ItemSpellbook.isSealed(itemStack)){
                return 1.0F;
            }
            return 0.5F;
        });
    }
}
