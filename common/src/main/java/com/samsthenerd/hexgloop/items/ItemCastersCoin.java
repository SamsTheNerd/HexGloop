package com.samsthenerd.hexgloop.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

public class ItemCastersCoin extends Item implements IotaHolderItem {
    public ItemCastersCoin(Settings properties) {
        super(properties);
    }

    // 0 = empty, 1 = written
    public static final Identifier OVERLAY_PRED = new Identifier(HexAPI.MOD_ID, "overlay_layer");

    public static final String TAG_DATA = "data";
    // these may be garbage data if the data tag isn't set !
    public static final String TAG_CASTER = "caster";
    public static final String TAG_CASTER_DISPLAY = "caster_display";

    @Override
    public @Nullable NbtCompound readIotaTag(ItemStack stack) {
        return NBTHelper.getCompound(stack, TAG_DATA);
    }
    
    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack) + (isBound(stack) ? ".bound" : "");
    }

    public boolean isBound(ItemStack stack) {
        NbtCompound nbt = NBTHelper.getCompound(stack, TAG_DATA);
        return nbt != null && !nbt.isEmpty();
    }

    // item stack after it's used 
    public ItemStack useCoin(ItemStack stack){
        if(isBound(stack)){
            stack.decrement(1);
            ItemStack blankCoin = this.getDefaultStack();
            blankCoin.setCount(1);
            return blankCoin;
        }
        return ItemStack.EMPTY;
    }

    public void setBoundCaster(ItemStack stack, PlayerEntity player){
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putUuid(TAG_CASTER, player.getUuid());
        nbt.putString(TAG_CASTER_DISPLAY, player.getName().getString());
    }

    @Nullable
    public Pair<UUID, String> getBoundCaster(ItemStack stack){
        if(!isBound(stack)) return null;
        NbtCompound nbt = stack.getNbt();
        if(nbt == null) return null;
        String displayName = "";
        UUID uuid = null;
        if(nbt.containsUuid(TAG_CASTER)){
            uuid = nbt.getUuid(TAG_CASTER);
        }
        if(nbt.contains(TAG_CASTER_DISPLAY, NbtElement.STRING_TYPE)){
            displayName = nbt.getString(TAG_CASTER_DISPLAY);
        }
        return new Pair<>(uuid, displayName);
    }

    @Override
    public @Nullable Iota emptyIota(ItemStack stack) {
        return new NullIota();
    }

    @Override
    public boolean canWrite(ItemStack stack, Iota datum) {
        return true;
    }

    @Override
    public void writeDatum(ItemStack stack, Iota datum) {
        if (datum == null) {
            stack.removeSubNbt(TAG_DATA);
            stack.removeSubNbt(TAG_CASTER);
            stack.removeSubNbt(TAG_CASTER_DISPLAY);
        } else {
            NBTHelper.put(stack, TAG_DATA, HexIotaTypes.serialize(datum));
        }
    }

    @Override
    public void appendTooltip(ItemStack pStack, @Nullable World pLevel, List<Text> pTooltipComponents,
                                TooltipContext pIsAdvanced) {
        IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
        NbtCompound nbt = pStack.getNbt();
        if(nbt != null && isBound(pStack) && nbt.contains(TAG_CASTER_DISPLAY, NbtElement.STRING_TYPE)){
            MutableText actualCasterText = Text.literal(nbt.getString(TAG_CASTER_DISPLAY));
            Style casterStyle = actualCasterText.getStyle();
            casterStyle = casterStyle.withItalic(true).withColor(Formatting.AQUA);
            actualCasterText.setStyle(casterStyle);
            MutableText boundCasterMessage = Text.translatable("item.hexgloop.casters_coin.bound_caster", actualCasterText);
            Style style = boundCasterMessage.getStyle();
            style = style.withItalic(true).withColor(Formatting.GRAY);
            boundCasterMessage.setStyle(style);
            pTooltipComponents.add(boundCasterMessage);
        }
    }
}
