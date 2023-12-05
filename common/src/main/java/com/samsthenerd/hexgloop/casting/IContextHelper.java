package com.samsthenerd.hexgloop.casting;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

public interface IContextHelper {
    // stuff for kitty casting:

    public default boolean isKitty(){
        return getKitty() != null;
    }

    public void setKittyContext(KittyContext kCtx);

    public KittyContext getKittyContext();

    public ItemStack getKitty();

    public void setKitty(ItemStack kitty);

    public void setCursorRef(StackReference cursorRef);

    public StackReference getCursorRef();

    // for frog casting:

    public void setFrog(ItemStack frog);

    public ItemStack getFrog();

    @Nullable
    public default FrogVariant getFrogType(){
        ItemStack frog = getFrog();
        if(frog == null){
            return null;
        }
        return HexGloopItems.CASTING_FROG_ITEM.get().getFrogVariant(frog);
    }

    public default boolean isFrogCasting(){
        return getFrog() != null;
    }
}
