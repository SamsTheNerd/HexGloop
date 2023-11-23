package com.samsthenerd.hexgloop.mixins.lociathome;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(CastingContext.class)
public class MixinContextHelper implements IContextHelper{
    @Shadow
    @Final
    private ServerPlayerEntity caster;

    private ItemStack kittyStack = null;

    public void setKitty(ItemStack kitty){
        kittyStack = kitty;
    }

    public ItemStack getKitty(){
        return kittyStack;
    }

    private StackReference cursorRef = null;

    public void setCursorRef(StackReference cursorRef){
        this.cursorRef = cursorRef;
    }

    public StackReference getCursorRef(){
        return cursorRef;
    }

    private KittyContext kittyContext = null;

    public void setKittyContext(KittyContext kCtx){
        kittyContext = kCtx;
    }

    public KittyContext getKittyContext(){
        return kittyContext;
    }
}
