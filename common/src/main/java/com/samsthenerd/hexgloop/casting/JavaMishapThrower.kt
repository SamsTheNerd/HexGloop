package com.samsthenerd.hexgloop.casting

import at.petrak.hexcasting.api.spell.mishaps.Mishap

/**
 * kotlin doesn't check its exceptions so code extending kotlin compiled stuff can't actually properly throw exceptions
 */
class JavaMishapThrower {
    companion object {
        @JvmStatic
        fun throwMishap(mishap: Mishap) {
            throw mishap
        }
    }
}