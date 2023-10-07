package com.samsthenerd.hexgloop.forge.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

@Mixin(LivingEntity.class)
public class MixinMaintainUsingCorrectItemForge {

    @Shadow
    protected ItemStack activeItemStack;

    // idk why forge doesn't have mixinextras rn,, just ues redirect for now and switch it later if possible
    @Redirect(method="tickActiveItemStack()V",
    at=@At(value="INVOKE", target="net/minecraftforge/common/ForgeHooks.canContinueUsing (Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean makeItemsEqualInTickActive(ItemStack left, ItemStack right){
        // left is hand item, right is active item
        boolean originalRes = ForgeHooks.canContinueUsing(left, right);
        if(originalRes) return true;
        if(left.getItem() instanceof ItemAbstractPassThrough passItem){
            LivingEntity thisEnt = (LivingEntity)(Object)this;
            ItemStack storedItem = passItem.getStoredItem(left, thisEnt, thisEnt.getWorld(), thisEnt.getActiveHand());
            // HexGloop.logPrint("in tick active mixin: stored item: [" + (storedItem == null ? "null" : storedItem.toString()) + "] vs activeItemStack: [" + right.toString() + "]");
            if(storedItem == null) return originalRes;
            return ForgeHooks.canContinueUsing(storedItem, right);
        }
        return originalRes;
    }
}
