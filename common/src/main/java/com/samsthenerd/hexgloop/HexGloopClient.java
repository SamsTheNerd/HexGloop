package com.samsthenerd.hexgloop;

import java.text.DecimalFormat;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.samsthenerd.hexgloop.blockentities.BlockEntityGloopEnergizer;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemGloopDye;
import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class HexGloopClient {
    public static Random random = new Random();

    public static void onInitializeClient() {
        HexGloop.logPrint("Initializing HexGloopClient");
        registerModelPredicates();
        registerColorProviders();
        registerScryingDisplayers();
        HexGloopKeybinds.registerKeybinds();
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
            return 0xFFFFFF; //white
		}, HexGloopItems.MULTI_FOCUS_ITEM.get());

        ItemConvertible[] hexStaffs = {HexItems.STAFF_OAK, HexItems.STAFF_SPRUCE, 
                HexItems.STAFF_BIRCH, HexItems.STAFF_JUNGLE, HexItems.STAFF_ACACIA, 
                HexItems.STAFF_DARK_OAK, HexItems.STAFF_CRIMSON, HexItems.STAFF_WARPED};

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
			if(tintIndex != 1) {
				return 0xFFFFFF;
			}
			return ((DyeableItem) HexItems.STAFF_OAK).getColor(stack);
		}, hexStaffs);

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            FrozenColorizer colorizer = HexGloopItems.CASTING_POTION_ITEM.get().getColorizer(stack);
            if(tintIndex == 0 || tintIndex >= 5 || colorizer == null){
                return 0xFFFFFF; //white
            }
            return tintsFromColorizer(colorizer, tintIndex-1, 4);
        }, HexGloopItems.CASTING_POTION_ITEM.get());

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1 || tintIndex > 4){ // base
                return 0xFFFFFF; //white
            }
            int color = ItemGloopDye.getDyeColor(stack);
            if(tintIndex == 0){
                return color;
            }
            if(tintIndex == 2){ // red
                return color & 0xFF0000;
            }
            if(tintIndex == 3){ // green
                return color & 0x00FF00;
            }
            if(tintIndex == 4){ // blue
                return color & 0x0000FF;
            }
            return 0xFFFFFF; //white
        }, HexGloopItems.GLOOP_DYE_ITEM.get());
    }

    public static int tintsFromColorizer(FrozenColorizer colorizer, int tintIndex, int sections){
        float time = MinecraftClient.getInstance().world.getTime();
        double section = 5.0 * tintIndex;
        return colorizer.getColor(time, new Vec3d(section, 0, 0));
    }

    private static void registerModelPredicates(){
        ItemPropertiesRegistry.register(HexGloopItems.MULTI_FOCUS_ITEM.get(), new Identifier("selected"), 
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
            int slot = ItemSpellbook.getPage(itemStack, -1);
            if(slot < 1 || HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(itemStack) == null){
                return 0.0F;
            }
            return (float)((1/6.0)*(slot));
        });

        ItemPropertiesRegistry.register(HexGloopItems.MULTI_FOCUS_ITEM.get(), new Identifier("sealed"), 
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
            return ItemSpellbook.isSealed(itemStack) ? 1.0F : 0.0F;
        });

        ItemPropertiesRegistry.register(HexGloopItems.CASTING_POTION_ITEM.get(), new Identifier("colorized"),
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
                FrozenColorizer colorizer = HexGloopItems.CASTING_POTION_ITEM.get().getColorizer(itemStack);
                return (colorizer == null) ? 0.0F : 1.0F;
            }
        );

        ItemPropertiesRegistry.register(HexGloopItems.CASTING_POTION_ITEM.get(), new Identifier("hashex"),
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
                return (HexGloopItems.CASTING_POTION_ITEM.get().hasHex(itemStack)) ? 1.0F : 0.0F;
            }
        );
    }

    public static DecimalFormat DUST_FORMAT = new DecimalFormat("###,###.##");

    private static void registerScryingDisplayers(){
        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.GLOOP_ENERGIZER_BLOCK.get(), 
        (lines, state, pos, observer, world, direction) -> {
            if(world.getBlockEntity(pos) instanceof BlockEntityGloopEnergizer energizer){
                if (energizer.getMedia() < 0) {
                    lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), ItemCreativeUnlocker.infiniteMedia(world)));
                } else {
                    var dustCount = (float) energizer.getMedia() / (float) MediaConstants.DUST_UNIT;
                    var dustCmp = Text.translatable("hexcasting.tooltip.media",
                    DUST_FORMAT.format(dustCount));
                    lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), dustCmp));
                }
                lines.add(new Pair<>(new ItemStack(Items.WATER_BUCKET), Text.literal(energizer.getNumConnected() + " blocks")));
                // lines.add(new Pair<>(energizer.getLatestResult(), Text.literal("Latest result")));
            }
        });
    }
}
