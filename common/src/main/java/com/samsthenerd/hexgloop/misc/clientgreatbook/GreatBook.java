package com.samsthenerd.hexgloop.misc.clientgreatbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.utils.patternmatching.PatternMatching;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

public class GreatBook {

    public static GreatBook INSTANCE = new GreatBook();

    private Map<Identifier, HexPattern> storedPatterns = new HashMap<>();

    @Nullable
    public HexPattern getPattern(Identifier id){
        return storedPatterns.get(id);
    }

    public void savePattern(HexPattern pattern){
        Identifier patternId = PatternMatching.matchGreatSpell(pattern);
        if(patternId != null){
            HexPattern prev = storedPatterns.put(patternId, pattern);
            if(!pattern.equals(prev)){
                Text patternName = PatternMatching.getName(pattern);
                MinecraftClient.getInstance().player.sendMessage(Text.translatable("hexgloop.greatbook.saved", patternName, pattern.toString()));
                save();
            }
        }
    }

    public void clearPattern(Identifier id){
        if(storedPatterns.remove(id) != null){
            Text patternName = PatternRegistry.lookupPattern(id).action().getDisplayName();
            MinecraftClient.getInstance().player.sendMessage(Text.translatable("hexgloop.greatbook.removed", patternName));
            save();
        }
    }

    public void clearAll(){
        if(!storedPatterns.isEmpty()){
            storedPatterns.clear();
            MinecraftClient.getInstance().player.sendMessage(Text.translatable("hexgloop.greatbook.cleared"));
            save();
        }
    }

    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();
        for(Identifier id : storedPatterns.keySet()){
            nbt.put(id.toString(), storedPatterns.get(id).serializeToNBT());
        }
        return nbt;
    }

    public static GreatBook fromNbt(NbtCompound nbt){
        GreatBook book = new GreatBook();
        for(String id : nbt.getKeys()){
            book.storedPatterns.put(new Identifier(id), HexPattern.fromNBT(nbt.getCompound(id)));
        }
        return book;
    }

    private static String asFileName(String original){
        return original.replaceAll("[^a-zA-Z0-9\\-_\\.]", "_");
    }

    @Nullable // null if it's not in a world
    public static File getFile(){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.isIntegratedServerRunning()){ // saving it to the world       
            IntegratedServer server = client.getServer();
            Path worldPath = server.getSavePath(WorldSavePath.ROOT);
            Path greatbookPath = worldPath.resolve("data/greatbook.dat");
            return greatbookPath.toFile();
        }
        ServerInfo sInfo = client.getCurrentServerEntry();
        if(sInfo != null){
            String cleanAddress = asFileName(sInfo.address);
            Path greatbookPath = client.runDirectory.toPath().resolve("greatbook/greatbook-" + cleanAddress + ".dat");
            return greatbookPath.toFile();
        }
        return null;
    }

    public static GreatBook load(){
        HexGloop.logPrint("Loading greatbook");
        File file = getFile();
        if(file == null || !file.canRead()){
            return new GreatBook();
        }
        NbtCompound greatBookNbt = null;
        try{
            greatBookNbt = NbtIo.read(file);
        } catch(IOException e){
            HexGloop.LOGGER.error("Failed to load greatbook");
        }
        if(greatBookNbt == null){
            return new GreatBook();
        }
        return GreatBook.fromNbt(greatBookNbt);
    }

    public void save(){
        HexGloop.logPrint("Saving greatbook");
        File file = getFile();
        if(file == null){
            return;
        }
        file.getAbsoluteFile().getParentFile().mkdirs();
        try{
            file.createNewFile();
            NbtCompound greatBookNbt = this.toNbt();
            NbtIo.write(greatBookNbt, file);
        } catch(IOException e){
            HexGloop.LOGGER.error("Failed to save greatbook");
        }
    }

    @Environment(EnvType.CLIENT)
    public static void registerLoadEvent(){
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(world -> {
            INSTANCE = load();
        });
    }
}
