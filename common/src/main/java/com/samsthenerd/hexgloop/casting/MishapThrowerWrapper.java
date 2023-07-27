package com.samsthenerd.hexgloop.casting;

import com.samsthenerd.hexgloop.casting.JavaMishapThrower;

import at.petrak.hexcasting.api.spell.mishaps.Mishap;

/*
 * this way all the error squiggles are confined to just this file !
 */
public class MishapThrowerWrapper {
    public static void throwMishap(Mishap mishap){
        JavaMishapThrower.throwMishap(mishap);
    }
}
