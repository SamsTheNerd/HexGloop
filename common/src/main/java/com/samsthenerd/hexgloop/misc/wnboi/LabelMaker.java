package com.samsthenerd.hexgloop.misc.wnboi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

// used for the labels on the wnboi screens
// each label type needs a way to go from iota/other input -> nbt -> chilling as object -> rendered
public class LabelMaker {
    public static final String LABELS_TAG = "HEXGLOOP:LABELS";
    public static final String LABEL_SIZE_TAG = "HEXGLOOP:LABEL_SIZE";
    private static final Map<Identifier, LabelType<?> > LABEL_TYPE_MAP = new HashMap<Identifier, LabelType<?> >();
    private static final Map<IotaType<?>, Function<Iota, Label>> IOTA_LABEL_MAP = new HashMap<IotaType<?>, Function<Iota, Label>>();

    private NbtCompound nbtLabels;
    private Map<Integer, Label> labelMap;

    public static void registerIotaLabelFunction(IotaType<?> type, Function<Iota, Label> function){
        IOTA_LABEL_MAP.put(type, function);
    }

    public static <L extends Label> LabelType<L> registerLabelType(LabelType<L> type){
        LABEL_TYPE_MAP.put(type.id, type);
        return type;
    }
    
    @Nullable
    public static Label fromNbt(NbtCompound nbt){
        if(!nbt.contains("type", NbtElement.STRING_TYPE)) return null;
        Identifier id = new Identifier(nbt.getString("type"));
        LabelType<?> type = LABEL_TYPE_MAP.get(id);
        if(type == null) {
            return null;
        }
        return type.fromNbt(nbt);
    }

    @Nullable
    public static Label fromIota(Iota iota){
        Function<Iota, Label> function = IOTA_LABEL_MAP.get(iota.getType());
        if(function == null) return null;
        return function.apply(iota);
    }

    // returns whether or not it successfully put the label or not
    public static boolean putLabel(ItemStack stack, NbtCompound labelNbt, int index){
        // found issue: we never give the stack the LABEL_SIZE_TAG
        // if(!stack.hasNbt() || !stack.getNbt().contains(LABEL_SIZE_TAG, NbtElement.INT_TYPE)) return false;
        if(!stack.hasNbt()) return false;
        NbtCompound nbt = stack.getNbt();
        if(!nbt.contains(LABELS_TAG, NbtElement.COMPOUND_TYPE)){
            nbt.put(LABELS_TAG, new NbtCompound());
        }
        NbtCompound labels = nbt.getCompound(LABELS_TAG);
        labels.put(Integer.toString(index), labelNbt);
        nbt.put(LABELS_TAG, labels);
        stack.setNbt(nbt);
        return true;
    }

    public static boolean putLabel(ItemStack stack, Iota iota, int index){
        Label label = fromIota(iota);
        if(label == null) return putLabel(stack, new NbtCompound(), index);
        return putLabel(stack, label.toNbt(), index);
    }

    public static boolean putLabel(ItemStack stack, Label label, int index){
        if(label == null) return putLabel(stack, new NbtCompound(), index);
        return putLabel(stack, label.toNbt(), index);
    }

    // given an nbt from a stack
    public LabelMaker(NbtCompound nbt){
        if(nbt != null && nbt.contains(LABELS_TAG, NbtElement.COMPOUND_TYPE)){
            nbtLabels = nbt.getCompound(LABELS_TAG);
        } else {
            nbtLabels = null;
        }
        labelMap = new HashMap<Integer, Label>();
    }

    public LabelMaker(ItemStack stack){
        this(stack.getNbt());
    }

    @Nullable
    public Label getLabel(int index){
        if(nbtLabels == null || labelMap == null) return null;
        Label label = labelMap.get(index);
        if(label == null){ // no label in the map
            if(!nbtLabels.contains(Integer.toString(index), NbtElement.COMPOUND_TYPE)) return null;
            NbtCompound labelNbt = nbtLabels.getCompound(Integer.toString(index));
            label = fromNbt(labelNbt);
            if(label == null) return null;
            labelMap.put(index, label);
        }
        return label;
    }
    
    // an instance of this is made for each label 
    public static interface Label{
        // render the label centered at x,y with the given width and height
        public void render(MatrixStack matrixStack, int x, int y, int width, int height);

        public LabelType<?> getType();

        public default NbtCompound toNbt(){
            NbtCompound nbt = new NbtCompound();
            nbt.putString("type", getType().id.toString());
            return nbt;
        }
    }

    // handling nbt deserialization
    public static class LabelType<T extends Label>{
        public final Identifier id;
        private final Function<NbtCompound, T> fromNbtFunction;

        public LabelType(Identifier id, Function<NbtCompound, T> fromNbtFunction){
            this.id = id;
            this.fromNbtFunction = fromNbtFunction;
        }

        public T fromNbt(NbtCompound nbt){
            return fromNbtFunction.apply(nbt);
        }
    }
}
