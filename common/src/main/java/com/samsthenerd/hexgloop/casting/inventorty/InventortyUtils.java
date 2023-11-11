package com.samsthenerd.hexgloop.casting.inventorty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.MerchantInventory;

public class InventortyUtils {
    public static KittyContext assertKittyCasting(CastingContext ctx){
        if(!((IContextHelper)(Object)ctx).isKitty()){
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(List.of("inventorty")));
            return null;
        }
        return ((IContextHelper)(Object)ctx).getKittyContext();
    }

    public static final int CURSOR_REF_SLOT = -2;

    // idx and argc just so we can throw proper mishaps
    @Nullable
    public static GrabbableStack getStackFromGrabbable(Iota grabbable, CastingContext ctx, int idx, int argc){
        KittyContext kCtx = ((IContextHelper)(Object)ctx).getKittyContext();
        if(grabbable instanceof DoubleIota dIota){
            int grabSlot = (int)Math.floor(dIota.getDouble());
            if(grabSlot == CURSOR_REF_SLOT){
                StackReference cursorRef = ((IContextHelper)(Object)ctx).getCursorRef();
                if(cursorRef != null)
                    return new GrabbableGeneric(cursorRef::get, cursorRef::set);
            }
            if(grabSlot < 0 && -grabSlot % 1000 == 0){ // want to get an auto fill slot
                int inventoryIndex = (-grabSlot / 1000)-1;
                if(inventoryIndex >= kCtx.getInventoryCount()){
                    // mishap can't find slot here ?
                    return null;
                }
                return new AutoGrabbable(inventoryIndex);
            }
            Slot slot = kCtx.getSlot(grabSlot);
            if(slot == null){
                // mishap bad slot here too
                return null;
            }
            return new GrabbableSlot(slot);
        }
        if(grabbable instanceof EntityIota eIota){
            if(eIota.getEntity() instanceof ItemEntity itemEnt && eIota.getEntity().distanceTo(ctx.getCaster()) < 1.0f){
                return new GrabbableEnt(itemEnt);
            }
        }

        MishapThrowerWrapper.throwMishap(MishapInvalidIota.ofType(grabbable, argc == 0 ? idx : argc - (idx + 1), "kittygrabbable"));
        return null;
    }

    // a little class for us to store in the casting context
    public static class KittyContext{
        private List<Inventory> additionalInventories;
        private PlayerInventory playerInv;

        // should it index by accessible slots or by total inventory slots ? i think accessible would be safer but could mess up the order ?
        private Map<Inventory, List<Slot>> slots;

        public KittyContext(PlayerInventory playerInv){
            this.playerInv = playerInv;
            this.additionalInventories = new ArrayList<>();
            this.slots = new HashMap<>();
        }

        // always includes inventory
        public int getInventoryCount(){
            return additionalInventories.size() + 1;
        }

        // 0 is *always* player inventory
        public Inventory getInventory(int index){
            if(index == 0)
                return playerInv;
            return additionalInventories.get(index - 1);
        }

        public void addSlot(Slot slot){
            if(!slots.containsKey(slot.inventory)){
                slots.put(slot.inventory, new ArrayList<>(List.of(slot)));
                if(slot.inventory != playerInv){
                    additionalInventories.add(slot.inventory);
                    // need to see if the sort order is consistent enough for same sized inventories
                    additionalInventories.sort((a, b) -> b.size() - a.size());
                }
                // Object inv = slot.inventory instanceof Nameable nameableInv ? nameableInv.getName() : slot.inventory;
                // HexGloop.logPrint("adding: " + slot.inventory + "th inventory: " + inv.toString());
            } else {
                List<Slot> theseSlots = slots.get(slot.inventory);
                theseSlots.add(slot);
                // keep them in order i guess
                theseSlots.sort((a, b) -> a.getIndex() - b.getIndex());
            }
        }

        @Nullable
        public Slot getSlot(int fullIndex){
            int invIndex = fullIndex / 1000;
            int slotIndex = fullIndex % 1000;
            if(invIndex >= getInventoryCount() || invIndex < 0)
                return null;
            List<Slot> theseSlots = getSlots(invIndex);
            if(slotIndex >= theseSlots.size() || slotIndex < 0)
                return null;
            return theseSlots.get(slotIndex);
        }

        public List<Slot> getSlots(int invIndex){
            return slots.get(getInventory(invIndex));
        }

        public int getInventoryIndex(Inventory inv){
            if(inv == playerInv)
                return 0;
            return additionalInventories.indexOf(inv) + 1;
        }

        public int getFullSlotIndex(Slot slot){
            int invIndex = getInventoryIndex(slot.inventory);
            if(invIndex == -1)
                return -1;
            int slotIndex = getSlots(invIndex).indexOf(slot);
            if(slotIndex == -1)
                return -1;
            return invIndex * 1000 + slotIndex;
        }
    }

    public static abstract class GrabbableStack{
        // VV these are mostly from Slot, but implement them for other types as well

        public abstract ItemStack getStack();

        public abstract boolean canTake(PlayerEntity player);

        public abstract boolean canInsert(ItemStack stack);

        public abstract ItemStack takeStack(int amount, PlayerEntity player);

        public abstract ItemStack insertStack(ItemStack stack);

        public abstract int getMaxCount();
    }

    public static class GrabbableSlot extends GrabbableStack{
        public Slot slot;

        public GrabbableSlot(Slot slot){
            this.slot = slot;
        }

        public ItemStack getStack(){
            return slot.getStack();
        }

        public boolean canTake(PlayerEntity player){
            return slot.canTakeItems(player);
        }

        public boolean canInsert(ItemStack stack){
            return slot.canInsert(stack);
        }

        public ItemStack takeStack(int amount, PlayerEntity player){
            return slot.takeStackRange(amount, amount, player);
        }

        public ItemStack insertStack(ItemStack stack){
            return slot.insertStack(stack);
        }

        public int getMaxCount(){
            return slot.getMaxItemCount();
        }
    }

    public static class GrabbableGeneric extends GrabbableStack{
        public Supplier<ItemStack> getter;
        public Consumer<ItemStack> setter;
        
        public GrabbableGeneric(Supplier<ItemStack> getter, Consumer<ItemStack> setter){
            this.getter = getter;
            this.setter = setter;
        }

        public ItemStack getStack(){
            return getter.get();
        }

        public boolean canTake(PlayerEntity player){
            return true;
        }

        public boolean canInsert(ItemStack stack){
            return ItemStack.canCombine(stack, getter.get());
        }

        public ItemStack takeStack(int amount, PlayerEntity player){
            return getter.get().split(amount);
        }

        public ItemStack insertStack(ItemStack stack){
            if(getter.get().isEmpty()){
                setter.accept(stack);
                return ItemStack.EMPTY;
            }
            int transferAmt = Math.min(stack.getCount(), getter.get().getMaxCount() - getter.get().getCount());
            getter.get().increment(transferAmt);
            stack.decrement(transferAmt);
            return stack;
        }

        public int getMaxCount(){
            return getter.get().getMaxCount();
        }
    }

    public static class GrabbableEnt extends GrabbableGeneric{
        public GrabbableEnt(ItemEntity ent){
            super(ent::getStack, ent::setStack);
        }
    }

    public static class AutoGrabbable extends GrabbableSlot{
        int inventoryIndex;
        
        // for the inventory you want to auto fill into, 0 would be player inventory, 1 would be next largest, and so on
        public AutoGrabbable(int inventoryIndex){
            super((Slot)null);
            this.inventoryIndex = inventoryIndex;
        }

        // call this to set the slot to something actually useable
        // return whether or not it actually found a valid slot
        public boolean findSlot(ItemStack stackWith, CastingContext ctx){
            KittyContext kCtx = ((IContextHelper)(Object)ctx).getKittyContext();
            Slot lastSlot = null;
            for(Slot maybeSlot : kCtx.getSlots(inventoryIndex)){
                if(maybeSlot.canInsert(stackWith) 
                && (maybeSlot.getStack().isEmpty() || (ItemStack.canCombine(maybeSlot.getStack(), stackWith)
                && maybeSlot.getStack().getCount() < Math.min(maybeSlot.getMaxItemCount(), maybeSlot.getStack().getMaxCount())))){
                    this.slot = maybeSlot;
                    return true;
                }
                lastSlot = maybeSlot;
            }
            this.slot = lastSlot; // return *something* and it just won't transfer any
            return false;
        }
    }


    // give translation keys for the names of the inventories
    public static final Map<Class<? extends Inventory>, String> inventoryNames = Map.of(
        EnderChestInventory.class, "container.enderchest",
        SimpleInventory.class, "hexgloop.container.simple",
        CraftingResultInventory.class, "hexgloop.container.result",
        CraftingInventory.class, "container.crafting",
        MerchantInventory.class, "merchant.trades"
    );

    // some inventory types are just too generic
    public static final Set<Class<? extends Inventory>> needsMoreDetail = Set.of(
        SimpleInventory.class,
        CraftingResultInventory.class
    );

    public static String getInventoryName(Inventory inv, KittyContext kCtx){
        // easy if it has a name
        if(inv instanceof Nameable nameableInv){
            return nameableInv.getName().getString();
        }
        // seems to really only be for like minecarts ?
        if(inv instanceof NamedScreenHandlerFactory nameableInv){
            return nameableInv.getDisplayName().getString();
        }

        Class<?> invClass = inv.getClass();
        if(invClass.isAnonymousClass()){
            invClass = invClass.getSuperclass();
        }
        if(inventoryNames.containsKey(invClass)){
            // not sure how well this will work but oh well
            String genericName = Text.translatable(inventoryNames.get(invClass)).getString();
            if(needsMoreDetail.contains(invClass)){
                // try to get more detail
                ScreenHandler handler = kCtx.playerInv.player.currentScreenHandler;
                Identifier handlerID = null;
                try {
                    handlerID = Registry.SCREEN_HANDLER.getId(handler.getType());
                } catch(Exception e){} // ignore it, it's fine
                if(handlerID != null){
                    // try to get the name of the screen handler -- who knows if it'll actually translate ! should work fine for vanilla handlers atleast
                    String handlerTransKey = getHandlerTranslationKey(handlerID);
                    if(handlerTransKey == null){
                        return genericName;
                    }
                    return Text.translatable(handlerTransKey).getString() + ": " + genericName;
                }
            }
            return genericName;
        }

        // backup case - use the class name.
        // won't work for obfuscated classes, but we should have those handled already hopefully
        // might not be the best for modded inventories, but decent enough
        return invClass.getSimpleName();
    }

    public static final Map<Identifier, String> hardToMapIDs = Map.of(
        new Identifier("enchantment"), "block.minecraft.enchanting_table"
    );

    public static String getHandlerTranslationKey(Identifier handlerID){
        if(hardToMapIDs.containsKey(handlerID)){
            return hardToMapIDs.get(handlerID);
        }
        Block maybeBlock = Registry.BLOCK.get(handlerID);
        if(maybeBlock != null && maybeBlock != Blocks.AIR){
            return maybeBlock.getTranslationKey();
        }
        // for things like crafting_table or smithing_table - really only 2 vanilla cases but who knows, maybe catch some modded ones?
        Block maybeTableBlock = Registry.BLOCK.get(new Identifier(handlerID.getNamespace(), handlerID.getPath() + "_table"));
        if(maybeTableBlock != null && maybeTableBlock != Blocks.AIR){
            return maybeTableBlock.getTranslationKey();
        }
        EntityType<?> maybeEnt = EntityType.get(handlerID.toString()).orElse(null);
        if(maybeEnt != null){
            return maybeEnt.getTranslationKey();
        }
        return null;
    }
}
