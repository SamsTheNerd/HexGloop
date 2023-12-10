package com.samsthenerd.hexgloop.items;

import com.samsthenerd.hexgloop.casting.orchard.IOrchard;
import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;
import com.samsthenerd.hexgloop.misc.wnboi.LabelProvider;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.PatternLabel;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.PatternLabel.PatternOptions;
import com.samsthenerd.hexgloop.screens.FidgetWheelScreen;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;

import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemFidget extends Item implements LabelyItem, KeyboundItem{
    public FidgetSettings fidgetSettings;

    public static Identifier INDEX_PREDICATE = new Identifier("hexgloop", "fidget_index");

    public ItemFidget(Settings settings, FidgetSettings fidgetSettings){
        super(settings);
        this.fidgetSettings = fidgetSettings;
    }

    
    public static class FidgetSettings{
        public int slots;
        public double startAngle;
        public int defFillColor;
        public int selFillColor;
        public int defStrokeColor;
        public int selStrokeColor;

        public static HexPattern[] NUMBER_LITERALS = {HexPattern.fromAngles("aqaa", HexDir.SOUTH_EAST), 
            HexPattern.fromAngles("aqaaw", HexDir.SOUTH_EAST),
            HexPattern.fromAngles("aqaawa", HexDir.SOUTH_EAST),
            HexPattern.fromAngles("aqaawaw", HexDir.SOUTH_EAST),
            HexPattern.fromAngles("aqaawaa", HexDir.SOUTH_EAST),
            HexPattern.fromAngles("aqaaed", HexDir.SOUTH_EAST),
            HexPattern.fromAngles("aqaaqw", HexDir.SOUTH_EAST),
            HexPattern.fromAngles("aqaawaq", HexDir.SOUTH_EAST),
            HexPattern.fromAngles("aqaawwaa", HexDir.SOUTH_EAST),
        };

        public FidgetSettings(int slots, double startAngle, int defFillColor, int selFillColor, int defStrokeColor, int selStrokeColor){
            this.slots = slots;
            this.startAngle = startAngle;
            this.defFillColor = defFillColor;
            this.selFillColor = selFillColor;
            this.defStrokeColor = defStrokeColor;
            this.selStrokeColor = selStrokeColor;
        }

        public FidgetSettings(int slots){
            this(slots, Math.PI / 2 + Math.PI / slots, 0xC0_E8E8E8, 0xC0_FFFFFF, 0xFF_BFBFBF, 0xFF_E8E8E8);
        }

        public Label getDefaultLabel(int i, boolean isCurrent, boolean isSelected){
            if(i >= 0 && i < NUMBER_LITERALS.length){
                return new PatternLabel(NUMBER_LITERALS[i+1], new PatternOptions((isCurrent ? selStrokeColor : defStrokeColor), 
                    (isCurrent ? selFillColor : defFillColor) & 0x00_FFFFFF | 0x40_000000, 
                    (isCurrent ? selFillColor : defFillColor) & 0x00_FFFFFF | 0x60_000000, (isCurrent ? selFillColor : defFillColor), 0f, 0f, false));
            }
            return null;
        }

        public Pair<Integer, Integer> getCurveOptions(int index, boolean isCurrent, boolean isSelected){
            return new Pair<Integer, Integer>(0, 5);
        }

        // expose this out here for full control?
        public int getColorFill(int index, int vI, int numOuter, int numInner, boolean isInner, boolean isCurrent, boolean isSelected){
            return isCurrent ? selFillColor : defFillColor;
        }

        public int getColorOutline(int index, int vI, boolean isCurrent, boolean isSelected){
            return isCurrent ? selStrokeColor : defStrokeColor;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public KeyBinding getKeyBinding(){
        return HexGloopKeybinds.IOTA_WHEEL_KEYBIND;
    }

    @Environment(EnvType.CLIENT)
    public static FidgetWheelScreen screen;

    @Environment(EnvType.CLIENT)
    public AbstractContextWheelScreen getScreen(){
        Pair<ItemStack, Boolean> handItemResult = getHandItem();
        if(screen == null){
            ItemStack fidgetStack = handItemResult.getLeft();
            if(fidgetStack == null) return null;
            screen = new FidgetWheelScreen(new FidgetLabelProvider(fidgetStack), MinecraftClient.getInstance().currentScreen, fidgetSettings);
        }
        ((FidgetLabelProvider) screen.labelProvider).mainHand = handItemResult.getRight();
        return screen;
    }

    public boolean putLabel(ItemStack stack, NbtCompound labelNbt){
        return LabelMaker.putLabel(stack, labelNbt, ItemFidget.getPage(stack));
    }

    public static int getPage(ItemStack stack) {
         if (NBTHelper.hasNumber(stack, ItemSpellbook.TAG_SELECTED_PAGE)) {
            int index = NBTHelper.getInt(stack, ItemSpellbook.TAG_SELECTED_PAGE);
            if (index == 0) {
                index = 1;
            }
            return index;
        } else {
            return 1;
        }
    }

    public static int rotatePageIdx(ItemStack stack, boolean increase) {
        int idx = getPage(stack);
        int numSlots = 1;
        if(stack.getItem() instanceof ItemFidget fidgetItem){
            numSlots = fidgetItem.fidgetSettings.slots;
        }
        if (idx != 0) {
            idx += increase ? 1 : -1;
            if(idx <= 0) idx = numSlots;
            if(idx > numSlots) idx = 1;
            idx = Math.max(1, idx);
        }
        idx = MathHelper.clamp(idx, 0, numSlots);
        NBTHelper.putInt(stack, ItemSpellbook.TAG_SELECTED_PAGE, idx);

        NbtCompound names = NBTHelper.getCompound(stack, ItemSpellbook.TAG_PAGE_NAMES);
        int shiftedIdx = Math.max(1, idx);
        String nameKey = String.valueOf(shiftedIdx);
        String name = NBTHelper.getString(names, nameKey);
        if (name != null) {
            stack.setCustomName(Text.Serializer.fromJson(name));
        } else {
            stack.removeCustomName();
        }

        return idx;
    }

    @Environment(EnvType.CLIENT)
    public Pair<ItemStack, Boolean> getHandItem(){
        ItemStack mainStack = MinecraftClient.getInstance().player.getMainHandStack();
        if(mainStack.getItem() instanceof ItemFidget){
            return new Pair<ItemStack, Boolean>(mainStack, true);
        }
        ItemStack offStack = MinecraftClient.getInstance().player.getOffHandStack();
        if(offStack.getItem() instanceof ItemFidget){
            return new Pair<ItemStack, Boolean>(offStack, false);
        }
        return null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient() && user instanceof ServerPlayerEntity serverPlayer){
            ItemStack fidgetStack = user.getStackInHand(hand);
            ((IOrchard)serverPlayer).setOrchardValue(getPage(fidgetStack));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    
    public static class FidgetLabelProvider implements LabelProvider{
        public ItemStack fidgetStack = null;
        protected LabelMaker labelMaker = null;
        public boolean mainHand = true;

        // be careful to only pass this a spellbook
        public FidgetLabelProvider(ItemStack fidgetStack){
            super();
            if(fidgetStack != null && fidgetStack.getItem() instanceof ItemFidget){
                this.fidgetStack = fidgetStack;
                labelMaker = new LabelMaker(fidgetStack);
            }
        }

        public LabelMaker getLabelMaker(){
            return labelMaker;
        }

        public int currentSlot(){
            return ItemFidget.getPage(fidgetStack);
        }

        public int perPage(){
            int numSlots = -1; // idk, not really a great way to handle an error, it should never happen
            if(fidgetStack.getItem() instanceof ItemFidget fidgetItem){
                numSlots = fidgetItem.fidgetSettings.slots;
            }
            return numSlots;
        }

        public int getCount(){
            return perPage(); 
        }

        @Override
        public void toSlot(int index){
            int current = currentSlot()-1;
            // HexGloop.logPrint("going to slot " + index + " from slot " + current);
            int dist = Math.abs(index - current);
            int invert = (dist == (index - current)) ? -1 : 1;
            for(int i = 0; i < dist; i++){
                IClientXplatAbstractions.INSTANCE.sendPacketToServer(
                        new MsgShiftScrollSyn(mainHand ? invert*dist : 0, !mainHand ? invert*dist : 0, true,
                            false, false));
            }
        }
    }
}
