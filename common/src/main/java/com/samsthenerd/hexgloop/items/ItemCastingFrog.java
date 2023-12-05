package com.samsthenerd.hexgloop.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.casting.IContextHelper;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ItemCastingFrog extends ItemPackagedHex {


    public static final String FROG_VARIANT_KEY = "frog_variant";

    public static final Identifier FROG_VARIANT_PREDICATE = new Identifier(HexGloop.MOD_ID, "frog_variant");

    public ItemCastingFrog(Settings settings){
        super(settings);
    }
    
    public boolean canDrawMediaFromInventory(ItemStack stack){
        return true;
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

    public List<ItemStack> getEquippedFrogs(LivingEntity entity){
        List<ItemStack> frogs = new ArrayList<>();
        for(List<ItemStack> stacksInSlot : HexGloop.TRINKETY_INSTANCE.getTrinkets(entity).values()){
            for(ItemStack stack : stacksInSlot){
                if(stack.getItem().equals(this)){
                    frogs.add(stack);
                }
            }
        }
        return frogs;
    }

    @Override
    public void setMedia(ItemStack stack, int media) {
        HexGloop.logPrint("setting media on frog: " + stack + " to " + media);
        super.setMedia(stack, media);
    }

    @Nullable
    public FrogVariant getFrogVariant(ItemStack stack){
        if(stack.hasNbt() && stack.getNbt().contains(FROG_VARIANT_KEY, NbtElement.STRING_TYPE)){
            Identifier frogId = new Identifier(stack.getNbt().getString(FROG_VARIANT_KEY));
            return Registry.FROG_VARIANT.get(frogId);
        }
        return null;
    }

    public void setFrogVariant(ItemStack stack, FrogVariant frogVariant){
        Identifier frogId = Registry.FROG_VARIANT.getId(frogVariant);
        stack.getOrCreateNbt().putString(FROG_VARIANT_KEY, frogId.toString());
    }

    public void attachVariantIfNeeded(ItemStack stack, World world, Entity entity){
        if(stack.hasNbt() && stack.getNbt().contains(FROG_VARIANT_KEY, NbtElement.STRING_TYPE)){
            return;
        }
        FrogVariant frogVariant = getFrogVariantFromWorld(world, entity.getBlockPos());
        setFrogVariant(stack, frogVariant);
    }

    public static FrogVariant getFrogVariantFromWorld(World world, BlockPos pos){
        RegistryEntry<Biome> registryEntry = world.getBiome(pos);
        if (registryEntry.isIn(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
            return FrogVariant.COLD;
        } else if (registryEntry.isIn(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
            return FrogVariant.WARM;
        } else {
            return FrogVariant.TEMPERATE;
        }
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        attachVariantIfNeeded(stack, world, entity);
    }

    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        attachVariantIfNeeded(stack, world, player);
    }

    public void cast(ItemStack stack, LivingEntity entity){
        HexGloop.logPrint("in frog cast");
        if(!(entity instanceof ServerPlayerEntity sPlayer)) return;
        HexGloop.logPrint("is server player");
        ServerWorld sWorld = sPlayer.getWorld();

        List<Iota> instrs = getHex(stack, sWorld);
        if (instrs == null) {
            return;
        }
        HexGloop.logPrint("has instrs");
        var ctx = new CastingContext(sPlayer, Hand.MAIN_HAND, CastingContext.CastSource.PACKAGED_HEX);
        if((Object)ctx instanceof IContextHelper ctxHelper){
            ctxHelper.setFrog(stack);
        }
        var harness = new CastingHarness(ctx);

        // sWorld.playSound(sPlayer, sPlayer.getBlockPos(), 
        //     POSSIBLE_CAT_NOISES.get((int)(sPlayer.getRandom().nextBetween(0, POSSIBLE_CAT_NOISES.size()-1))), 
        //     SoundCategory.AMBIENT, 0.2F + 0.3F * (sPlayer.getRandom().nextFloat() - sPlayer.getRandom().nextFloat()), 1.0F);

        var info = harness.executeIotas(instrs, sWorld);

        sPlayer.incrementStat(Stats.USED.getOrCreateStat(this));

        sPlayer.getItemCooldownManager().set(this, 5);
    }
}
