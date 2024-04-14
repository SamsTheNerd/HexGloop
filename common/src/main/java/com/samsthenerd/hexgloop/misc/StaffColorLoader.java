package com.samsthenerd.hexgloop.misc;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class StaffColorLoader extends JsonDataLoader{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final Map<Identifier, Integer> STAFF_COLORS = new HashMap<>();

    public StaffColorLoader() {
        super(GSON, "staffcolors");
    }

    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        STAFF_COLORS.clear(); // maybe ?
        for(Map.Entry<Identifier, JsonElement> entry : map.entrySet()){
            Identifier id = entry.getKey();
            JsonObject obj = entry.getValue().getAsJsonObject();
            for(Map.Entry<String, JsonElement> staff_entry : obj.entrySet()){
                JsonPrimitive colorVal = staff_entry.getValue().getAsJsonPrimitive();
                int color = 0;
                if(colorVal.isNumber()){
                    color = colorVal.getAsInt();
                } else if(colorVal.isString()) {
                    String colorStr = colorVal.getAsString();
                    colorStr = colorStr.replace("#", "");
                    try {
                        color = Integer.parseInt(colorStr, 16);
                    } catch (NumberFormatException e) {
                        HexGloop.LOGGER.error("Invalid color value for staff color: " + staff_entry.getKey() + " in " + id.toString());
                        continue;
                    }
                } else {
                    HexGloop.LOGGER.error("Invalid color value for staff color: " + staff_entry.getKey() + " in " + id.toString());
                    continue;
                }
                
                STAFF_COLORS.put(new Identifier(staff_entry.getKey()), color);
            }
        }
    }

    // register the event(s)
    public static void init(){
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, new StaffColorLoader());
    }
}