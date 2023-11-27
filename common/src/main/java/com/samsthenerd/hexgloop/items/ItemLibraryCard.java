package com.samsthenerd.hexgloop.items;

import java.util.List;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.compat.moreIotas.MoreIotasMaybeIotas;
import com.samsthenerd.hexgloop.utils.GloopUtils;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicRecord;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dev.architectury.platform.Platform;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ItemLibraryCard extends Item implements IotaHolderItem{

    public static String TAG_DIMENSION = "bound_dimension";
    public static Identifier DIMENSION_PREDICATE = new Identifier(HexGloop.MOD_ID, "dimension");

    public ItemLibraryCard(Settings settings) {
        super(settings);
    }

    public void setDimension(ItemStack stack, RegistryKey<World> dim){
        if(dim == null){
            stack.getOrCreateNbt().remove(TAG_DIMENSION);
            return;
        }
        stack.getOrCreateNbt().putString(TAG_DIMENSION, dim.getValue().toString());
    }

    @Nullable
    public RegistryKey<World> getDimension(ItemStack stack){
        NbtCompound nbt = stack.getNbt();
        if(nbt == null || !nbt.contains(TAG_DIMENSION, NbtElement.STRING_TYPE))
            return null;
        return RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString(TAG_DIMENSION)));
    }

    public static final List<Identifier> DIMENSIONS = List.of(
        World.OVERWORLD.getValue(), 
        World.NETHER.getValue(), 
        World.END.getValue()
    );

    public float getPredicateValue(RegistryKey<World> dim){
        if(dim == null)
            return 0;

        // so that overworld is 0.01, nether is 0.02, end is 0.03, and we can add 96 more dimensions ! (not that I will, but i'd rather have the expandability than not)
        int dimIndex = DIMENSIONS.indexOf(dim.getValue());
        if(dimIndex != -1)
            return (dimIndex+1) * 0.01f;
        
        // return 1 for generic bound
        return 1;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockAkashicRecord){
            ItemStack stack = context.getStack();
            setDimension(stack, world.getRegistryKey());
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    public NbtCompound readIotaTag(ItemStack stack){
        RegistryKey<World> dim = getDimension(stack);
        if(Platform.isModLoaded("moreiotas") && dim != null){
            Iota maybeString = MoreIotasMaybeIotas.makeStringIota(dim.getValue().toString());
            return HexIotaTypes.serialize(maybeString);
        }
        return null;
    }

    public boolean canWrite(ItemStack stack, @Nullable Iota iota){
        return iota == null;
    }

    /**
     * Write {@code null} to indicate erasing
     */
    public void writeDatum(ItemStack stack, @Nullable Iota iota){
        if(iota == null){
            setDimension(stack, null);
            return;
        }
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context){
        RegistryKey<World> dim = getDimension(stack);
        if(dim != null){
            tooltip.add(Text.translatable("item.hexgloop.library_card.tooltip_dimension", GloopUtils.prettyPrintID(dim.getValue())));
        }
    }
}
