package com.samsthenerd.hexgloop.mixins.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemSimpleMediaProvider;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.item.ItemStack;

@Mixin(CastingHarness.class)
public class MixinDontLetHexxyEatMySnacks {
    @WrapOperation(method={"_init_$lambda$7(Lat/petrak/hexcasting/api/spell/casting/CastingHarness;)Ljava/util/List;",
        "_init_$lambda$10(Lat/petrak/hexcasting/api/spell/casting/CastingHarness;)Ljava/util/List;",
        "_init_$lambda$13(Lat/petrak/hexcasting/api/spell/casting/CastingHarness;)Ljava/util/List;"},
    at=@At(value = "INVOKE", target="at/petrak/hexcasting/api/utils/MediaHelper.isMediaItem (Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean dontEatSnacks(ItemStack stack, Operation<Boolean> original){
        if(stack.getItem() instanceof ItemSimpleMediaProvider simpleItem && !simpleItem.shouldGrabFromInventory(stack)){
            return false;
        } else {
            return original.call(stack);
        }
    }
}
