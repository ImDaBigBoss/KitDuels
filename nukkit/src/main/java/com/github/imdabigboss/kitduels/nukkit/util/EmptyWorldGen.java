package com.github.imdabigboss.kitduels.nukkit.util;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.Map;

public class EmptyWorldGen extends Generator {
    private ChunkManager chunkManager;

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
        this.chunkManager = chunkManager;
    }

    @Override
    public void generateChunk(int i, int i1) {

    }

    @Override
    public void populateChunk(int i, int i1) {

    }

    @Override
    public Map<String, Object> getSettings() {
        return null;
    }

    @Override
    public String getName() {
        return "emptyworldgen";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(128, 65, 128);
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }
}
