package com.samsthenerd.hexgloop.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemSlateLoader extends ItemAbstractPassThrough implements IotaHolderItem {
    public static final String TAG_PATTERN_LIST = "pattern_list";
    public static final String TAG_SLATE_COUNT = "slate_count";
    public static final Identifier ACTIVATED_PRED = new Identifier(HexGloop.MOD_ID, "is_activated");

    public ItemSlateLoader(Settings pProperties) {
        super(pProperties);
    }

    public ItemStack getStoredItem(ItemStack stack, LivingEntity ent, World world, Hand hand){
        if(getSlateCount(stack) > 0){
            return getCurrentSlate(stack);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack setStoredItem(ItemStack stack, LivingEntity ent, World world, Hand hand, ItemStack storedItem){
        // means it got used
        int slateCount = getSlateCount(stack);
        if(slateCount > 0 && storedItem.isEmpty()){
            setSlateCount(stack, --slateCount);
        }
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = super.use(world, user, hand);
        if(result.getResult().isAccepted()){
            dequeuePattern(result.getValue(), world);
        }
        return result;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        HexGloop.logPrint("slate loader count in use on block: " + getSlateCount(context.getStack()));
        ActionResult result = super.useOnBlock(context);
        if(result.isAccepted()){
            dequeuePattern(context.getStack(), context.getWorld());
        }
        HexGloop.logPrint("slate loader updated count in use on block (result): " + getSlateCount(context.getStack()));
        HexGloop.logPrint("slate loader updated count in use on block (hand): " + getSlateCount(context.getPlayer().getStackInHand(context.getHand())));
        return result;
    }

    public boolean shouldPassTools(ItemStack stack){
        return false;
    }

    public ItemStack getStoredItemCopy(ItemStack stack){
        if(getSlateCount(stack) > 0){
            return getCurrentSlate(stack);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NbtCompound readIotaTag(ItemStack stack){
        return NBTHelper.getCompound(stack, TAG_PATTERN_LIST);
    }

    @Override
    public boolean canWrite(ItemStack stack, Iota datum) {
        return datum instanceof ListIota || datum == null;
    }

    @Override
    public void writeDatum(ItemStack stack, Iota datum) {
        if (this.canWrite(stack, datum)) {
            if (datum instanceof ListIota list) {
                List<Iota> iotaList = new ArrayList<>();
                // strip out non pattern elements
                list.getList().forEach(iElem -> {
                    iotaList.add(iElem instanceof PatternIota ? iElem : new NullIota());
                });
                if(iotaList.size() > 0){
                    NBTHelper.putCompound(stack, TAG_PATTERN_LIST, HexIotaTypes.serialize(new ListIota(iotaList)));
                    setCurrentPattern(stack, iotaList.get(0) instanceof PatternIota ? ((PatternIota) iotaList.get(0)).getPattern() : null);
                    return;
                }
            }
            NBTHelper.remove(stack, TAG_PATTERN_LIST);
            setCurrentPattern(stack, null);
        }
    }

    public boolean hasPatterns(ItemStack stack){
        return NBTHelper.hasCompound(stack, TAG_PATTERN_LIST);
    }

    public int getSlateCount(ItemStack stack){
        return NBTHelper.getInt(stack, TAG_SLATE_COUNT, 0);
    }

    public void setSlateCount(ItemStack stack, int count){
        NBTHelper.putInt(stack, TAG_SLATE_COUNT, count);
    }

    public void dequeuePattern(ItemStack stack, World world){
        if(world instanceof ServerWorld sWorld && hasPatterns(stack)){
            Iota storedIota = HexIotaTypes.deserialize(readIotaTag(stack), sWorld);
            if(storedIota instanceof ListIota list){
                List<Iota> newList = new ArrayList<>();
                boolean notFirst = false;
                for(Iota iota : list.getList()){
                    if(notFirst){
                        newList.add(iota);
                    } else {
                        notFirst = true;
                    }
                }
                writeDatum(stack, new ListIota(newList));
            }
        }
    }

    private void setCurrentPattern(ItemStack stack, @Nullable HexPattern pattern){
        if(pattern == null){
            var beTag = NBTHelper.getOrCreateCompound(stack, "BlockEntityTag");
            beTag.remove(BlockEntitySlate.TAG_PATTERN);
            if (beTag.isEmpty()) {
                NBTHelper.remove(stack, "BlockEntityTag");
            }
            NBTHelper.remove(stack, TAG_PATTERN_LIST);
        } else {
            var beTag = NBTHelper.getOrCreateCompound(stack, "BlockEntityTag");
            beTag.put(BlockEntitySlate.TAG_PATTERN, pattern.serializeToNBT());
            return;
        }
    }

    public HexPattern getCurrentPattern(ItemStack stack){
        var beTag = NBTHelper.getCompound(stack, "BlockEntityTag");
        if(beTag != null){
            return HexPattern.fromNBT(beTag.getCompound(BlockEntitySlate.TAG_PATTERN));
        }
        return null;
    }

    public ItemStack getCurrentSlate(ItemStack stack){
        ItemStack slateStack = new ItemStack(HexItems.SLATE);
        HexPattern pattern = getCurrentPattern(stack);
        if(pattern != null){
            HexItems.SLATE.writeDatum(slateStack, new PatternIota(pattern));
        }
        return slateStack;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack slotStack = slot.getStack();
        if(clickType != ClickType.RIGHT){
            return false;
        }
        if(slotStack.getItem() == HexItems.SLATE){
            int amtToTake = Math.min(slotStack.getCount(), DEFAULT_MAX_COUNT - getSlateCount(stack));
            if(amtToTake > 0){
                setSlateCount(stack, getSlateCount(stack) + amtToTake);
                slotStack.decrement(amtToTake);
                return true;
            }
        } else if(slotStack.isEmpty()){
            ItemStack newSlateStack = new ItemStack(HexItems.SLATE);
            newSlateStack.setCount(getSlateCount(stack));
            slot.setStack(newSlateStack);
            setSlateCount(stack, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if(clickType != ClickType.RIGHT){
            return false;
        }
        if(otherStack.getItem() == HexItems.SLATE){
            int amtToTake = Math.min(otherStack.getCount(), DEFAULT_MAX_COUNT - getSlateCount(stack));
            if(amtToTake > 0){
                setSlateCount(stack, getSlateCount(stack) + amtToTake);
                otherStack.decrement(amtToTake);
                return true;
            }
        } else if(otherStack.isEmpty()){
            ItemStack newSlateStack = new ItemStack(HexItems.SLATE);
            newSlateStack.setCount(getSlateCount(stack));
            cursorStackReference.set(newSlateStack);
            setSlateCount(stack, 0);
            return true;
        }
        return false;
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return getSlateCount(stack) > 0 || hasPatterns(stack);
    }

    public int getItemBarStep(ItemStack stack) {
        return 13*getSlateCount(stack) / DEFAULT_MAX_COUNT;
    }

    public int getItemBarColor(ItemStack stack) {
        float p = getSlateCount(stack) / (float) DEFAULT_MAX_COUNT;
        int emptycolor = 0x5a391c;
        int fullcolor = 0xffbc5e;
        int newColor = 0;
        for(int i = 0; i < 3; i++){
            int empty = (int) (((emptycolor >> (8*i)) & 0xFF) * (1-p));
            int full = (int) (((fullcolor >> (8*i)) & 0xFF) * (p));
            newColor |= (Math.min(empty + full, 0xFF) << (8*i));
        }
        return newColor;
        // return 0x564661; // light-ish slate color ?
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(Integer.toString(getSlateCount(stack))).append("/64"));
        if(hasPatterns(stack)){
            tooltip.add(HexIotaTypes.getDisplay(readIotaTag(stack)));
        }
    }

    public Text getName(ItemStack stack) {
        if(hasPatterns(stack)){
            return Text.translatable(this.getTranslationKey(stack) + ".written", 
                HexIotaTypes.getDisplay(HexItems.SLATE.readIotaTag(getCurrentSlate(stack)))
            );
        } else {
            return Text.translatable(this.getTranslationKey(stack));
        }
    }
}
