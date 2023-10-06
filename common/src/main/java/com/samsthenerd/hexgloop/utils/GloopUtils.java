package com.samsthenerd.hexgloop.utils;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class GloopUtils {
    // yoink from IotaHolderItem because,, agony,, with Dyebooks getColor messing up our mappings
    public static int getIotaColor(ItemStack stack){
        if (NBTHelper.hasString(stack, IotaHolderItem.TAG_OVERRIDE_VISUALLY)) {
            var override = NBTHelper.getString(stack, IotaHolderItem.TAG_OVERRIDE_VISUALLY);

            if (override != null && Identifier.isValid(override)) {
                var key = new Identifier(override);
                if (HexIotaTypes.REGISTRY.containsId(key)) {
                    var iotaType = HexIotaTypes.REGISTRY.get(key);
                    if (iotaType != null) {
                        return iotaType.color();
                    }
                }
            }

            return 0xFF000000 | MathHelper.hsvToRgb(ClientTickCounter.getTotal() * 2 % 360 / 360F, 0.75F, 1F);
        }

        if(!(stack.getItem() instanceof IotaHolderItem iotaHolder)) return 0xFFFFFF;

        var tag = iotaHolder.readIotaTag(stack);
        if (tag == null) {
            return HexUtils.ERROR_COLOR;
        }

        return HexIotaTypes.getColor(tag);
    }

}
