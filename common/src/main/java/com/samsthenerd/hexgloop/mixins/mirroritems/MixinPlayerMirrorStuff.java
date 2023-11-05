package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.mirror.IShallowMirrorBinder;
import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

// there's gonna be a few things in here
// 1) wrapping canHarvest
// 2) storing data for rendering the mirror stack
@Mixin(value = PlayerEntity.class, priority = 1000000)
public abstract class MixinPlayerMirrorStuff extends Entity implements IShallowMirrorBinder{
    @WrapOperation(method="canHarvest(Lnet/minecraft/block/BlockState;)Z",
    at=@At(value="INVOKE", target="net/minecraft/item/ItemStack.isSuitableFor (Lnet/minecraft/block/BlockState;)Z"))
    public boolean wrapCanHarvest(ItemStack stack, BlockState state, Operation<Boolean> original){
        if(stack.getItem() instanceof ItemAbstractPassThrough passItem && passItem.shouldPassTools(stack)){
            ItemStack storedItem = passItem.getStoredItemCopy(stack);
            if(storedItem != null){
                return original.call(storedItem, state);
            }
        }
        return original.call(stack, state);
    }

    @Inject(method="initDataTracker()V", at=@At("TAIL"))
    public void injectDataTracker(CallbackInfo ci){
        this.dataTracker.startTracking(HELD_STACK, ItemStack.EMPTY);
    }

    public ItemStack getTrackedStack(){
        return this.dataTracker.get(HELD_STACK);
    }

    // don't call this,,
    public MixinPlayerMirrorStuff(){
        super(null, null);
    }
}
