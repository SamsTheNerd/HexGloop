package com.samsthenerd.hexgloop;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import com.samsthenerd.hexgloop.blockentities.BERConjuredRedstone;
import com.samsthenerd.hexgloop.blockentities.BERHexChest;
import com.samsthenerd.hexgloop.blockentities.BlockEntityGloopEnergizer;
import com.samsthenerd.hexgloop.blockentities.BlockEntityPedestal;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemCastersCoin;
import com.samsthenerd.hexgloop.items.ItemGloopDye;
import com.samsthenerd.hexgloop.items.ItemGloopifact;
import com.samsthenerd.hexgloop.items.ItemHandMirror;
import com.samsthenerd.hexgloop.items.ItemHexSword;
import com.samsthenerd.hexgloop.items.ItemSlateLoader;
import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;
import com.samsthenerd.hexgloop.utils.GloopUtils;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.event.events.client.ClientTextureStitchEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HexGloopClient {
    public static Random random = new Random();

    public static Tessellator newTess;

    public static void onInitializeClient() {
        HexGloop.logPrint("Initializing HexGloopClient");
        addToTextureAtlas();
        registerModelPredicates();
        registerColorProviders();
        registerScryingDisplayers();
        HexGloopKeybinds.registerKeybinds();

        registerRenderers();

        newTess = new Tessellator();
    }

    private static void addToTextureAtlas(){
        ClientTextureStitchEvent.PRE.register((SpriteAtlasTexture atlas, Consumer<Identifier> spriteAdder) -> {
            if(atlas.getId().equals(TexturedRenderLayers.CHEST_ATLAS_TEXTURE)){
                spriteAdder.accept(new Identifier(HexGloop.MOD_ID, "entity/chest/gloopy_slate_chest"));
                spriteAdder.accept(new Identifier(HexGloop.MOD_ID, "entity/chest/slate_chest"));
            }
        });
    }

    private static void registerRenderers(){
        BlockEntityRendererRegistry.register(HexGloopBEs.CONJURED_REDSTONE_BE.get(), BERConjuredRedstone::new);
        BlockEntityRendererRegistry.register(HexGloopBEs.SLATE_CHEST_BE.get(), BERHexChest::new);
        RenderTypeRegistry.register(RenderLayer.getTranslucent(), HexGloopBlocks.CONJURED_REDSTONE_BLOCK.get());
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
			if(tintIndex == 1){
                return ((DyeableItem)HexItems.STAFF_OAK).getColor(stack);
            }
            return 0xFFFFFF;
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

        ItemConvertible[] NEW_FOCII = {HexGloopItems.FOCAL_PENDANT.get(), HexGloopItems.FOCAL_RING.get(), HexGloopItems.CASTERS_COIN.get(), 
            HexGloopItems.GLOOPIFACT_ITEM.get()};
        
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1){
                return GloopUtils.getIotaColor(stack);
            }
            return 0xFF_FFFFFF;
        }, NEW_FOCII);

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1){
                return HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get().getColor(stack);
            }
            if(tintIndex == 2){
                return GloopUtils.getIotaColor(stack);
            }
            return 0xFF_FFFFFF;
        }, HexGloopItems.DYEABLE_SPELLBOOK_ITEM);
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

        UnclampedModelPredicateProvider focusModelProvider = (stack, level, holder, holderID) -> {
            if(!(stack.getItem() instanceof IotaHolderItem iotaHolder)) return 0;
            if (iotaHolder.readIotaTag(stack) == null && !NBTHelper.hasString(stack, IotaHolderItem.TAG_OVERRIDE_VISUALLY)) {
                return 0;
            }
            // so it works for dyebook too
            if(stack.getItem() instanceof ItemSpellbook && ItemSpellbook.isSealed(stack)){
                return 1;
            }
            if (stack.getNbt() != null && stack.getNbt().contains(ItemFocus.TAG_SEALED) && stack.getNbt().getBoolean(ItemFocus.TAG_SEALED)) {
                return 1;
            }
            return 0.5f;
        };

        ItemPropertiesRegistry.register(HexGloopItems.FOCAL_PENDANT.get(), ItemFocus.OVERLAY_PRED, focusModelProvider);
        ItemPropertiesRegistry.register(HexGloopItems.FOCAL_RING.get(), ItemFocus.OVERLAY_PRED, focusModelProvider);
        ItemPropertiesRegistry.register(HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get(), ItemFocus.OVERLAY_PRED, focusModelProvider);

        ItemPropertiesRegistry.register(HexGloopItems.CASTERS_COIN.get(), ItemCastersCoin.OVERLAY_PRED,
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
                return (HexGloopItems.CASTERS_COIN.get().isBound(itemStack)) ? 1.0F : 0.0F;
            }
        );

        ItemPropertiesRegistry.register(HexGloopItems.GLOOPIFACT_ITEM.get(), ItemFocus.OVERLAY_PRED, (stack, level, holder, holderID) -> {
            ItemGloopifact gloopifactItem = HexGloopItems.GLOOPIFACT_ITEM.get();
            if(!gloopifactItem.hasHex(stack)){
                return 0;
            }
            if(gloopifactItem.readIotaTag(stack) == null){
                return 0.5f;
            }
            return 1f;
        });

        ItemPropertiesRegistry.register(HexGloopItems.HAND_MIRROR_ITEM.get(), ItemHandMirror.MIRROR_ACTIVATED_PRED, (stack, level, holder, holderID) -> {
            return HexGloopItems.HAND_MIRROR_ITEM.get().isMirrorActivated(stack) ? 1 : 0;
        });

        ItemPropertiesRegistry.register(HexGloopItems.SLATE_LOADER_ITEM.get(), ItemSlateLoader.ACTIVATED_PRED, (stack, level, holder, holderID) -> {
            return HexGloopItems.SLATE_LOADER_ITEM.get().hasPatterns(stack) ? 1 : 0;
        });

        ItemPropertiesRegistry.register(HexGloopItems.HEX_BLADE_ITEM.get(), ItemHexSword.TOOL_STATUS_PREDICATE, (stack, level, holder, holderID) -> {
            if(!HexGloopItems.HEX_BLADE_ITEM.get().hasHex(stack)){
                return 0;
            } else {
                return HexGloopItems.HEX_BLADE_ITEM.get().hasMediaToUse(stack) ? 1 : 0.5f;
            }
        });
    }

    
    public static DecimalFormat DUST_FORMAT = new DecimalFormat("###,###.##");

    private static void pedestalDisplay(List<Pair<ItemStack, Text>> lines,
        BlockState state, BlockPos pos, PlayerEntity observer,
        World world,
        Direction hitFace){
            
        BlockEntityPedestal be = world.getBlockEntity(pos, HexGloopBEs.PEDESTAL_BE.get()).orElse(null);
        if(be == null) return;
        ItemStack stack = be.getStack(0);
        if(stack.isEmpty()){
            MutableText text = Text.literal("Empty");
            Style stlye = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
            text.setStyle(stlye);
            lines.add(new Pair<>(ItemStack.EMPTY, text));
        } else {
            lines.add(new Pair<>(stack, stack.getName()));
            ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack);
            if(iotaHolder == null) return;
            NbtCompound nbt = iotaHolder.readIotaTag();
            if(nbt == null) return;
            if(HexIotaTypes.getTypeFromTag(nbt) != HexIotaTypes.PATTERN && state.getBlock() != HexGloopBlocks.MIRROR_PEDESTAL_BLOCK.get()){
                return;
            }
            if(nbt != null){
                Text iotaDesc = HexIotaTypes.getDisplay(iotaHolder.readIotaTag());
                ItemStack slateIcon = new ItemStack(HexItems.SLATE);
                HexItems.SLATE.writeDatum(slateIcon, new PatternIota(HexPattern.fromAngles("", HexDir.EAST)));
                lines.add(new Pair<>(slateIcon, iotaDesc));
            }
        }
    }

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

        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.PEDESTAL_BLOCK.get(), HexGloopClient::pedestalDisplay);
        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.MIRROR_PEDESTAL_BLOCK.get(), HexGloopClient::pedestalDisplay);
    }
}
