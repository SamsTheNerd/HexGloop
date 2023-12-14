package com.samsthenerd.hexgloop.forge;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.HexGloopClient;
import com.samsthenerd.hexgloop.forge.misc.BundleResourcePackForge;
import com.samsthenerd.hexgloop.forge.misc.TrinketyImplForge;
import com.samsthenerd.hexgloop.misc.TrinketyImplFake;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("hexgloop")
public class HexGloopForge {
    public HexGloopForge(){
        // so that we can register properly with architectury
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(HexGloop.MOD_ID, modBus);
        modBus.addListener(this::onClientSetup);
        modBus.addListener(EventPriority.NORMAL, BundleResourcePackForge::setupBuiltInResourcePack);

        // yoink from hex
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modBus.register(HexGloopForgeClient.class));

        // setup curios
        if(Platform.isModLoaded("curios")){
            modBus.addListener(TrinketyImplForge::onInterModEnqueue);
            HexGloop.TRINKETY_INSTANCE = new TrinketyImplForge();
        } else {
            HexGloop.TRINKETY_INSTANCE = new TrinketyImplFake();
        }

        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, HexGloopCaps::attachItemCaps);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, HexGloopCaps::attachBlockEntityCaps);

        HexGloop.onInitialize();
        HexGloop.GLOOPXPLAT = new GloopXPlatForge();
    }

    private void onClientSetup(FMLClientSetupEvent event) { 
        HexGloopClient.onInitializeClient();
        // event.enqueueWork(() -> {
        //     HexGloopClient.enqueuedInitClient();
        // });
    }


}
