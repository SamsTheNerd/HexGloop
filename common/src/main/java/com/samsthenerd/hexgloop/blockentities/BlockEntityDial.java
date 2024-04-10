package com.samsthenerd.hexgloop.blockentities;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blocks.BlockIoticDial;
import com.samsthenerd.hexgloop.blocks.iotic.IIoticProvider;
import com.samsthenerd.hexgloop.items.ItemMultiFocus;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
        selection = MathHelper.clamp(selection, 0, 6);
        BlockState old = world.getBlockState(pos);
        world.setBlockState(pos, old.with(BlockIoticDial.SELECTED, selection));
        if(selection != 0){
            ItemMultiFocus.setPageIdx(innerMultiFocus, selection);
        }
        markDirty();
    }

    public int getSelection(){
        return world.getBlockState(pos).get(BlockIoticDial.SELECTED);
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

    @Override
    public void markDirty() {
        if (world instanceof ServerWorld sWorld) {
            sWorld.getChunkManager().markForUpdate(pos);
        }
        super.markDirty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = new NbtCompound();
        this.writeNbt(tag);
        return tag;
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
