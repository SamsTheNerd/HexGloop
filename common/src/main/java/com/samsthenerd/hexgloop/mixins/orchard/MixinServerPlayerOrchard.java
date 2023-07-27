package com.samsthenerd.hexgloop.mixins.orchard;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.samsthenerd.hexgloop.casting.orchard.IOrchard;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerOrchard implements IOrchard {
    protected List<Double> orchardList = List.of(0.0);

    @Override
    @NotNull
    public List<Double> getOrchardList(){
        if(orchardList == null || orchardList.size() < 1) return List.of(0.0);
        return List.copyOf(orchardList);
    }

    @Override
    public void setOrchardList(List<Double> list){
        orchardList = List.copyOf(list);
    }

    @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V",
    at=@At(value="TAIL"))
    public void deserOrchard(NbtCompound nbt, CallbackInfo ci){
        if(nbt.contains("mindsorchard", NbtElement.LIST_TYPE)){
            NbtList orchardNbtList = nbt.getList("mindsorchard", NbtElement.DOUBLE_TYPE);
            List<Double> newOrchard = new ArrayList<Double>();
            for(int i = 0; i < orchardNbtList.size(); i++){
                newOrchard.add(orchardNbtList.getDouble(i));
            }
            orchardList = newOrchard;
        }
    }

    @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",
    at=@At(value="TAIL"))
    public void serOrchard(NbtCompound nbt, CallbackInfo ci){
        if(orchardList != null){
            NbtList orchardNbtList = new NbtList();
            for(int i = 0; i < orchardList.size(); i++){
                orchardNbtList.add(orchardNbtList.size(), NbtDouble.of(orchardList.get(i)));
            }
            nbt.put("mindsorchard", orchardNbtList);
        }
    }
}
