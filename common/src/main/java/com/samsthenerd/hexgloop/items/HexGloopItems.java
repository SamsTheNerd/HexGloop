package com.samsthenerd.hexgloop.items;

import java.util.function.Supplier;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.ItemFidget.FidgetSettings;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.PatternLabel;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.PatternLabel.PatternOptions;
import com.samsthenerd.wnboi.utils.RenderUtils;

import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.items.ItemFocus;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class HexGloopItems {
    public static DeferredRegister<Item> items = DeferredRegister.create(HexGloop.MOD_ID, Registry.ITEM_KEY);

    public static final RegistrySupplier<Item> GLOOP_ITEM = item("gloop", 
        () -> new Item(defaultSettings()));
    public static final RegistrySupplier<ItemMultiFocus> MULTI_FOCUS_ITEM = item("multi_focus", 
        () -> new ItemMultiFocus(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemCastingRing> CASTING_RING_ITEM = item("casting_ring", 
        () -> new ItemCastingRing(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemCastingPotion> CASTING_POTION_ITEM = item("casting_potion", 
        () -> new ItemCastingPotion(defaultSettings().maxCount(16)));
    public static final RegistrySupplier<ItemGloopDye> GLOOP_DYE_ITEM = item("gloop_dye", 
        () -> new ItemGloopDye(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemFocus> FOCAL_PENDANT = item("focal_pendant",
        () -> new ItemFocus(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemFocus> FOCAL_RING = item("focal_ring",
        () -> new ItemFocus(defaultSettings().maxCount(1).fireproof()));

    // fidgets

    public static final RegistrySupplier<ItemFidget> COPPER_PEN_FIDGET = item("copper_pen_fidget", 
        () -> new ItemFidget(defaultSettings().maxCount(1), 
        new FidgetSettings(2, Math.PI*0.75, 0xC0_C15A36, 0xC0_CFA0F2, 0xFF_6D3422, 0xFF_54398A){
            @Override
            public Pair<Integer, Integer> getCurveOptions(int index, boolean isCurrent, boolean isSelected){
                if(isCurrent || isSelected)
                    return new Pair<Integer, Integer>(RenderUtils.buildCurveOptions(1, 1, false, false), 2);
                return new Pair<Integer, Integer>(RenderUtils.buildCurveOptions(0, 0, false, false), 10);
            }
        }));
    public static final RegistrySupplier<ItemFidget> RAINBOW_AMOGUS_FIDGET = item("rainbow_amogus_fidget", 
        () -> new ItemFidget(defaultSettings().maxCount(1), new FidgetSettings(6){
            @Override
            public Label getDefaultLabel(int i, boolean isCurrent, boolean isSelected){
                if(i == 0){
                    return new PatternLabel(HexPattern.fromAngles("dewdeqawwqwwedwewdweqaqedaqw", HexDir.EAST), 
                        new PatternOptions(0xFF_FFFFFF,//(isCurrent ? DARK_AMOGUS[0] : MID_AMOGUS[0]), 
                        (isCurrent ? MID_AMOGUS[0] : LIGHT_AMOGUS[0]) & 0x00_FFFFFF | 0x40_000000, 
                        (isCurrent ? MID_AMOGUS[0] : LIGHT_AMOGUS[0]) & 0x00_FFFFFF | 0x60_000000, 
                        // , 0x40_BFBFBF, 0x60_BFBFBF,
                        (isCurrent ? MID_AMOGUS[0] : LIGHT_AMOGUS[0]), 0f, 0f, false));
                }
                return super.getDefaultLabel(i, isCurrent, isSelected);
            }

            // picked from the amogus texture
            public static int[] DARK_AMOGUS = {0xFF_990001, 0xFF_8C3D00, 0xFF_8A8C00, 0xFF_218C00, 0xFF_00678B, 0xFF_4D008C};
            public static int[] MID_AMOGUS = {0xFF_CC1413, 0xFF_E56F17, 0xFF_E2E518, 0xFF_47E519, 0xFF_15AEE5, 0xFF_8916E6};
            public static int[] LIGHT_AMOGUS = {0xFF_FF6666, 0xFF_FFA865, 0xFF_FCFF65, 0xFF_8AFF66, 0xFF_66D6FF, 0xFF_BA66FF};

            @Override
            public int getColorFill(int index, int vI, int numOuter, int numInner, boolean isInner, boolean isCurrent, boolean isSelected){
                return (isCurrent ? MID_AMOGUS[index] : LIGHT_AMOGUS[index]) & 0x00_FFFFFF | 0xC0_000000;
            }
    
            @Override
            public int getColorOutline(int index, int vI, boolean isCurrent, boolean isSelected){
                return isCurrent ? DARK_AMOGUS[index] : MID_AMOGUS[index];
            }
        }));
    

    public static <T extends Item> RegistrySupplier<T> item(String name, Supplier<T> item) {
		return items.register(new Identifier(HexGloop.MOD_ID, name), item);
	}

    public static Item.Settings defaultSettings(){
        return new Item.Settings().group(HEX_GLOOP_GROUP);
    }

    public static final ItemGroup HEX_GLOOP_GROUP = CreativeTabRegistry.create(
		new Identifier(HexGloop.MOD_ID, "general"),
		() -> GLOOP_ITEM.get().getDefaultStack());

    public static void register(){
        items.register();
    }
}
