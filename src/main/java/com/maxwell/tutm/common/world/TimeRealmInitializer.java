package com.maxwell.tutm.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class TimeRealmInitializer {
    public static void placeStructure(ServerLevel level, BlockPos pos, String structurePath) {
        StructureTemplateManager manager = level.getStructureManager();
        Optional<StructureTemplate> templateOpt = manager.get(new ResourceLocation("tutm", structurePath));
        templateOpt.ifPresent(template -> {
            StructurePlaceSettings settings = new StructurePlaceSettings();
            template.placeInWorld(level, pos, pos, settings, level.random, 2);
            System.out.println("Placed structure " + structurePath + " at " + pos);
        });
    }
}