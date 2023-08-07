package com.samsthenerd.hexgloop.compat.moreIotas;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.TextLabel;

import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.text.Text;
import ram.talia.moreiotas.api.spell.iota.StringIota;

public class MoreIotasLabels {
    public static void register(){
        LabelMaker.registerIotaLabelFunction(StringIota.TYPE, MoreIotasLabels::stringToLabel);
    }

    @Nullable
    public static Label stringToLabel(Iota iota){
        if(!(iota instanceof StringIota)) return null;
        StringIota stringIota = (StringIota) iota;
        return new TextLabel(Text.literal(stringIota.getString()));
    }
}
