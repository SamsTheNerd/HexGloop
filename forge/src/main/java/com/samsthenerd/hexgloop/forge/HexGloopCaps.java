package com.samsthenerd.hexgloop.forge;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blockentities.IReallyHateForgeWhyWouldAnInventoryInterfaceNotBeAnInterfaceThatsWhatAnInterfaceIsFor;
import com.samsthenerd.hexgloop.items.ItemSimpleMediaProvider;

import at.petrak.hexcasting.forge.cap.ForgeCapabilityHandler;
import at.petrak.hexcasting.forge.cap.HexCapabilities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.items.wrapper.InvWrapper;

public class HexGloopCaps {

    public static final Identifier INVENTORY_HANDLER = new Identifier(HexGloop.MOD_ID, "inventory_handler");

    public static void attachItemCaps(AttachCapabilitiesEvent<ItemStack> evt){
        ItemStack itemStack = evt.getObject();
        if(itemStack.getItem() instanceof ItemSimpleMediaProvider simpleMediaItem){
            evt.addCapability(ForgeCapabilityHandler.MEDIA_STATIC_CAP, provide(itemStack, HexCapabilities.MEDIA, 
                () -> simpleMediaItem.getProvider(itemStack))
            );
        }
    }

    public static void attachBlockEntityCaps(AttachCapabilitiesEvent<BlockEntity> evt) {
        BlockEntity be = evt.getObject();
        if (be instanceof IReallyHateForgeWhyWouldAnInventoryInterfaceNotBeAnInterfaceThatsWhatAnInterfaceIsFor inv) {
            evt.addCapability(INVENTORY_HANDLER, provide(be, ForgeCapabilities.ITEM_HANDLER,
                () -> new InvWrapper(inv)));
        }
    }

    private static <CAP> SimpleProvider<CAP> provide(ItemStack stack, Capability<CAP> capability,
        NonNullSupplier<CAP> supplier) {
        return provide(stack::isEmpty, capability, supplier);
    }

    private static <CAP> SimpleProvider<CAP> provide(BlockEntity be, Capability<CAP> capability,
        NonNullSupplier<CAP> supplier) {
        return provide(be::isRemoved, capability, supplier);
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
