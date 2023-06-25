package com.samsthenerd.hexgloop.items;

import java.util.List;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.ColorHelper.Argb;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemGloopDye extends ItemMediaHolder implements IotaHolderItem{
    public ItemGloopDye(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack(){
        return ItemMediaHolder.withMedia(super.getDefaultStack(), 0xFFFFFF, 64*MediaConstants.DUST_UNIT);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if(this.isIn(group)){
            stacks.add(getDefaultStack());
        }
    }

    @Override
    public boolean canRecharge(ItemStack stack){
        return true;
    }

    @Override
    public boolean canProvideMedia(ItemStack stack){
        return false;
    }

    public boolean decrementMedia(ItemStack stack, int amt){
        if(getMedia(stack) < amt)
            return false;
        setMedia(stack, getMedia(stack) - amt);
        return true;
    }

    @Override
    public @Nullable NbtCompound readIotaTag(ItemStack stack) {
        return NBTHelper.getCompound(stack, ItemFocus.TAG_DATA);
    }

    @Override
    public boolean canWrite(ItemStack stack, Iota datum) {
        return datum.getType() == HexIotaTypes.VEC3;
    }

    @Override
    public void writeDatum(ItemStack stack, Iota datum) {
        if (datum == null) {
            stack.removeSubNbt(ItemFocus.TAG_DATA);
        } else if(datum.getType() == HexIotaTypes.VEC3) {
            NBTHelper.put(stack, ItemFocus.TAG_DATA, HexIotaTypes.serialize(datum));
        }
    }

    public static int getDyeColor(ItemStack stack){
        NbtCompound iotaNbt = NBTHelper.getCompound(stack, ItemFocus.TAG_DATA);
        if(iotaNbt == null || HexIotaTypes.getTypeFromTag(iotaNbt) != HexIotaTypes.VEC3){
            return 0;
        }
        Vec3d rgbVec = ((Vec3Iota) HexIotaTypes.deserialize(iotaNbt, null)).getVec3();
        return (((int) (rgbVec.x)) << 16) + (((int) (rgbVec.y)) << 8) + ((int) (rgbVec.z));
    }

    @Override
    public void appendTooltip(ItemStack pStack, @Nullable World pLevel, List<Text> pTooltipComponents,
        TooltipContext pIsAdvanced) {
        super.appendTooltip(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        int argbColor = getDyeColor(pStack);
        Text dyeBadge = Text.literal("◈").styled(style -> style.withColor(argbColor));
        Text dyeLabel = Text.literal("<" + Argb.getRed(argbColor) + ", " + Argb.getGreen(argbColor) + ", " + Argb.getBlue(argbColor) + ">").formatted(Formatting.GRAY);
        pTooltipComponents.add(dyeBadge.copy().append(dyeLabel).append(dyeBadge));
    }
}
