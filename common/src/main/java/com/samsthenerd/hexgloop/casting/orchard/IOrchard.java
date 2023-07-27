package com.samsthenerd.hexgloop.casting.orchard;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface IOrchard {
    public default double getOrchardValue(){
        return getOrchardList().get(0);
    }

    // returns atleast one element
    @NotNull
    public List<Double> getOrchardList();

    public default void setOrchardValue(double value){
        setOrchardList(List.of(value));
    }

    public void setOrchardList(List<Double> list);
}
