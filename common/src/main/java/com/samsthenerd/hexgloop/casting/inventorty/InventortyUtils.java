package com.samsthenerd.hexgloop.casting.inventorty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class InventortyUtils {
    public static void assertKittyCasting(CastingContext ctx){
        if(!((IContextHelper)(Object)ctx).isKitty()){
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(List.of("inventorty")));
        }
    }

    public static final int CURSOR_REF_SLOT = -2;

    // idx and argc just so we can throw proper mishaps
    @Nullable
    public static GrabbableStack getStackFromGrabbable(Iota grabbable, CastingContext ctx, int idx, int argc){
        if(grabbable instanceof DoubleIota dIota){
            int grabSlot = (int)Math.floor(dIota.getDouble());
            if(grabSlot == CURSOR_REF_SLOT){
                StackReference cursorRef = ((IContextHelper)(Object)ctx).getCursorRef();
                if(cursorRef != null)
                    return new GrabbableGeneric(cursorRef::get, cursorRef::set);
            }
            Slot slot = ((IContextHelper)(Object)ctx).getKittyContext().getSlot(grabSlot);
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
            if(invIndex >= getInventoryCount())
                return null;
            List<Slot> theseSlots = getSlots(invIndex);
            if(slotIndex >= theseSlots.size())
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
}
