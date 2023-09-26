package com.samsthenerd.hexgloop.blockentities;

import com.samsthenerd.hexgloop.blocks.BlockSlateChest;
import com.samsthenerd.hexgloop.mixins.lociathome.MixinExposeChestInventory;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class BlockEntitySlateChest extends ChestBlockEntity {
    private boolean gloopy = false;

    public BlockEntitySlateChest(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        gloopy = false;
        if(blockState.getBlock() instanceof BlockSlateChest slateBlock){
            gloopy = slateBlock.isGloopy();
        }
        ((MixinExposeChestInventory)(Object)this).setInventory(DefaultedList.ofSize(9 * (gloopy ? 6 : 4), ItemStack.EMPTY));
    }

    public BlockEntitySlateChest(BlockPos pos, BlockState state) {
        this(HexGloopBEs.SLATE_CHEST_BE.get(), pos, state);
    }

    public boolean isGloopy(){
        return gloopy;
    }

    public DefaultedList<ItemStack> getInvStackListPublic(){
        return getInvStackList();
    }

    protected Text getContainerName() {
        return Text.translatable(isGloopy() ? "block.hexgloop.gloopy_slate_chest" : "block.hexgloop.slate_chest");
     }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        if(gloopy){
            return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
        } else {
            // hopefully this works ? i figure having it be slightly larger than a normal chest is nice
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, playerInventory, this, 4);
        }
    }

    public int size() {
        return 9 * (gloopy ? 6 : 4);
    }
}
