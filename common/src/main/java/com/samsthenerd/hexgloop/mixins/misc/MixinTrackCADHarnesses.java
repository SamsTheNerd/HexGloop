package com.samsthenerd.hexgloop.mixins.misc;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class MixinTrackCADHarnesses implements ICADHarnessStorage{
    private Set<CastingHarness> harnesses = new HashSet<CastingHarness>();

    public void addHarness(CastingHarness harness){
        harnesses.add(harness);
    }

    // get the harness that has this context
    public CastingHarness getHarness(CastingContext ctx){
        for(CastingHarness harness : harnesses){
            if(harness.getCtx() == ctx) return harness;
        }
        return null;
    }

    public void removeHarness(CastingHarness harness){
        harnesses.remove(harness);
    }
}
