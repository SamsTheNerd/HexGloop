package com.samsthenerd.hexgloop.forge;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.items.ItemSimpleMediaProvider;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.forge.cap.ForgeCapabilityHandler;
import at.petrak.hexcasting.forge.cap.HexCapabilities;
import at.petrak.hexcasting.forge.cap.adimpl.CapStaticMediaHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class HexGloopCaps {
    public static void attachItemCaps(AttachCapabilitiesEvent<ItemStack> evt){
        ItemStack itemStack = evt.getObject();
        if(itemStack.getItem() instanceof ItemSimpleMediaProvider simpleMediaItem){
            evt.addCapability(ForgeCapabilityHandler.MEDIA_STATIC_CAP, provide(itemStack, HexCapabilities.MEDIA, 
                () -> new CapStaticMediaHolder(HexConfig.common()::dustMediaAmount, ADMediaHolder.AMETHYST_DUST_PRIORITY, itemStack))
            );
        }
    }

    private static <CAP> SimpleProvider<CAP> provide(ItemStack stack, Capability<CAP> capability,
        NonNullSupplier<CAP> supplier) {
        return provide(stack::isEmpty, capability, supplier);
    }

    private static <CAP> SimpleProvider<CAP> provide(BooleanSupplier invalidated, Capability<CAP> capability,
        NonNullSupplier<CAP> supplier) {
        return new SimpleProvider<>(invalidated, capability, LazyOptional.of(supplier));
    }

    private record SimpleProvider<CAP>(BooleanSupplier invalidated,
                                       Capability<CAP> capability,
                                       LazyOptional<CAP> instance) implements ICapabilityProvider {

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (invalidated.getAsBoolean()) {
                return LazyOptional.empty();
            }

            return cap == capability ? instance.cast() : LazyOptional.empty();
        }
    }
}
