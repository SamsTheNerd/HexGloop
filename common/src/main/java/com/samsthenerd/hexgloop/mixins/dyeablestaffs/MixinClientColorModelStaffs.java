package com.samsthenerd.hexgloop.mixins.dyeablestaffs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import at.petrak.hexcasting.common.items.ItemStaff;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Mixin(ItemStaff.class)
public class MixinClientColorModelStaffs {
    @Inject(method ="<init>(Lnet/minecraft/item/Item$Settings;)V", at=@At("RETURN"))
    public void injectProviders(Item.Settings settings, CallbackInfo ci){
        ItemStaff staff = (ItemStaff)(Object)this;
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
			if(tintIndex == 1 && stack.getItem() instanceof DyeableItem dyeItem){
                return dyeItem.getColor(stack);
            }
            return 0xFFFFFF;
		}, staff);
        
        ItemPropertiesRegistry.register(staff, new Identifier("hexgloop:is_casting"), 
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
            if(MinecraftClient.getInstance().currentScreen instanceof GuiSpellcasting && livingEntity != null && livingEntity.isHolding(itemStack::equals)){
                return 1.0F;
            }
            return 0.0F;
        });
    }
}
