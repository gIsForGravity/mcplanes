package co.tantleffbeef.mcplanes.listeners;

import co.tantleffbeef.mcplanes.BlockManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class BlockManagerListener implements Listener {
    private final BlockManager manager;

    public BlockManagerListener(BlockManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        manager.loadChunk(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        manager.unloadChunk(event.getChunk());
    }
}
