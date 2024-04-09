package com.samsthenerd.hexgloop.blockentities;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blocks.BlockIoticDial;
import com.samsthenerd.hexgloop.blocks.iotic.IIoticProvider;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityDial extends BlockEntity implements IIoticProvider {

    private ItemStack innerMultiFocus = ItemStack.EMPTY;

    public BlockEntityDial(BlockPos pos, BlockState state) {
        super(HexGloopBEs.DIAL_BE.get(), pos, state);
    }

    public ItemStack getInnerMultiFocus() {
        return innerMultiFocus;
    }

    // so that we can handle changing the blockstate
    public void setInnerMultiFocus(ItemStack innerMultiFocus) {
        this.innerMultiFocus = innerMultiFocus;
        BlockState old = world.getBlockState(pos);
        int sel = 0;
        if(!innerMultiFocus.isEmpty()){
            sel = ItemSpellbook.getPage(innerMultiFocus, 1);
        }
        world.setBlockState(pos, old.with(BlockIoticDial.SELECTED, sel));
        HexGloop.logPrint("new inner multi focus !");
        markDirty();
    }

    public void setSelection(int selection){
        BlockState old = world.getBlockState(pos);
        world.setBlockState(pos, old.with(BlockIoticDial.SELECTED, selection));
        // TODO: update the inner multi focus too
        markDirty();
    }

    // TODO: wrap this so we can get a hook when the iota is changed
    public ADIotaHolder getIotaHolder(World world, BlockPos pos){
        return IXplatAbstractions.INSTANCE.findDataHolder(innerMultiFocus);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        innerMultiFocus = ItemStack.fromNbt(nbt.getCompound("innerMultiFocus"));
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("innerMultiFocus", innerMultiFocus.writeNbt(new NbtCompound()));
    }
}
