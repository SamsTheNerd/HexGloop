package com.samsthenerd.hexgloop.forge.misc;

import java.io.IOException;
import java.nio.file.Path;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.platform.Platform;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.PathPackResources;

public class BundleResourcePackForge {
    // yoinked from https://github.com/TelepathicGrunt/Bumblezone/blob/3b3c1527bdc8681450f3687a0a0833faafa3047d/src/main/java/com/telepathicgrunt/the_bumblezone/Bumblezone.java#L201
    public static void setupBuiltInResourcePack(final AddPackFindersEvent event) {
        if(Platform.isDevelopmentEnvironment())
            return; // it'll crash in dev
        try {
            if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
                Path resourcePath = ModList.get().getModFileById(HexGloop.MOD_ID).getFile().findResource("resourcepacks/dyeablestaffs");
                PathPackResources pack = new PathPackResources(ModList.get().getModFileById(HexGloop.MOD_ID).getFile().getFileName() + ":" + resourcePath, resourcePath);
                var metadataSection = pack.parseMetadata(PackResourceMetadata.READER);
                if (metadataSection != null) {
                    event.addRepositorySource((packConsumer, packConstructor) ->
                            packConsumer.accept(packConstructor.create(
                                    "builtin/"+HexGloop.MOD_ID, Text.of("Dyeable Staffs"), false,
                                    () -> pack, metadataSection, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN, false)));
                }
            }
        }
        catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
