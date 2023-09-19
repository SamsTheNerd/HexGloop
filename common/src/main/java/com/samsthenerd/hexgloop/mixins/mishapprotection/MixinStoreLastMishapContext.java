package com.samsthenerd.hexgloop.mixins.mishapprotection;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.casting.mishapprotection.IMishapStorage;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.text.Text;

@Mixin(CastingContext.class)
public class MixinStoreLastMishapContext implements IMishapStorage{
    private Text lastMishap = null;

    public Text getLastMishap(){
        return lastMishap;
    }

    public void setLastMishap(Text mishap){
        lastMishap = mishap;
    }
}
