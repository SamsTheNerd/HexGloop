package com.samsthenerd.hexgloop.compat.moreIotas;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import ram.talia.moreiotas.api.spell.iota.StringIota;

public class MoreIotasMaybeIotas {
    public static Iota makeStringIota(String sIn){
        try{
            return new StringIota(sIn);
        } catch (Mishap e){
            // idk why it would do this but whatever
            HexGloop.LOGGER.error("MoreIotas threw a mishap when making a StringIota: ", e);
            return new NullIota();
        }
    }
}
