package com.samsthenerd.hexgloop.mixins.orchard;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.casting.orchard.IOrchardKeybind;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerOrchardKeybind implements IOrchardKeybind{
    protected Set<String> activatedPatterns = new HashSet<String>();

    public void setAssociation(HexPattern pattern, boolean isActivated){
        if(pattern == null) return;
        if(isActivated){
            activatedPatterns.add(pattern.anglesSignature());
        }else{
            activatedPatterns.remove(pattern.anglesSignature());
        }

    }

    public boolean getAssociation(HexPattern pattern){
        return activatedPatterns.contains(pattern.anglesSignature());
    }
}
