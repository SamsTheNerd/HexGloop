package com.samsthenerd.hexgloop.items;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Nameable;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemInventorty extends ItemPackagedHex{
    public static final List<SoundEvent> POSSIBLE_CAT_NOISES = List.of(SoundEvents.ENTITY_CAT_PURR, SoundEvents.ENTITY_CAT_PURREOW);
    
    public ItemInventorty(Settings settings){
        super(settings);
    }

    public boolean canDrawMediaFromInventory(ItemStack stack){
        return false;
    }

    public boolean breakAfterDepletion(){
        return false;
    }

    // for hexxycraft backport
    public int cooldown(){
        return 0;
    }

    // don't hand cast
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand usedHand) {
        return TypedActionResult.pass(player.getStackInHand(usedHand));
    }

    // inventorty is in the slot
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        HexGloop.logPrint("onClicked: " + stack + 
            "\n\tCoolingDown: " + player.getItemCooldownManager().isCoolingDown(this) +
            "\n\tClickType: " + clickType.toString() +
            "\n\tHasHex: " + hasHex(stack));
        if(player.getItemCooldownManager().isCoolingDown(this)|| clickType != ClickType.RIGHT || !hasHex(stack)){
            return false;
        }

        kittyCast(stack, otherStack, slot, clickType, player, cursorStackReference, false);

        return true;
    }

    // inventorty is in the hand
    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        HexGloop.logPrint("onStackClicked: " + stack + 
            "\n\tCoolingDown: " + player.getItemCooldownManager().isCoolingDown(this) +
            "\n\tClickType: " + clickType.toString() +
            "\n\tHasHex: " + hasHex(stack));
        if(player.getItemCooldownManager().isCoolingDown(this) || clickType != ClickType.RIGHT || !hasHex(stack)){
            return false;
        }

        kittyCast(stack, slot.getStack(), slot, clickType, player, null, true);
        return true;
    }

    

    // mostly yoinked from ItemPackagedHex#use
    public void kittyCast(ItemStack tortyStack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference stackRef, boolean tortyInHand){
        // HexGloop.logPrint("in kittyCast");
        // HexGloop.logPrint("isClient: " + player.getWorld().isClient() + " isServer: " + (player.getWorld() instanceof ServerWorld));
        if(!(player.getWorld() instanceof ServerWorld sWorld)) return;
        // HexGloop.logPrint("on server");

        List<Iota> instrs = getHex(tortyStack, sWorld);
        if (instrs == null) {
            return;
        }
        var sPlayer = (ServerPlayerEntity) player;
        var ctx = new CastingContext(sPlayer, Hand.MAIN_HAND, CastingContext.CastSource.PACKAGED_HEX);
        ((IContextHelper)(Object)ctx).setCursorRef(stackRef);
        ((IContextHelper)(Object)ctx).setKitty(tortyStack);
        KittyContext kCtx = new KittyContext(player.getInventory());
        for(Slot slotToTrack : player.currentScreenHandler.slots){
            kCtx.addSlot(slotToTrack);
        }
        // HexGloop.logPrint("added a total of " + kCtx.getInventoryCount() + " inventories with: ");
        for(int i = 0; i < kCtx.getInventoryCount(); i++){
            HexGloop.logPrint("\t" + (kCtx.getInventory(i) instanceof Nameable nameableInv ? nameableInv.getName() : kCtx.getInventory(i) + ": " + kCtx.getSlots(i).size() + " slots"));
        }
        ((IContextHelper)(Object)ctx).setKittyContext(kCtx);
        var harness = new CastingHarness(ctx);
        harness.setStack(new ArrayList<Iota>(List.of(new BooleanIota(tortyInHand), new DoubleIota(kCtx.getFullSlotIndex(slot)))));

        sWorld.playSound(sPlayer, sPlayer.getBlockPos(), 
            POSSIBLE_CAT_NOISES.get((int)(sPlayer.getRandom().nextBetween(0, POSSIBLE_CAT_NOISES.size()-1))), 
            SoundCategory.AMBIENT, 0.2F + 0.3F * (sPlayer.getRandom().nextFloat() - sPlayer.getRandom().nextFloat()), 1.0F);

        var info = harness.executeIotas(instrs, sWorld);

        player.incrementStat(Stats.USED.getOrCreateStat(this));

        sPlayer.getItemCooldownManager().set(this, 5);
    }
}
