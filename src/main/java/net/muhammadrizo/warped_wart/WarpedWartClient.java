package net.muhammadrizo.warped_wart;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.muhammadrizo.warped_wart.block.ModBlocks;

public class WarpedWartClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.WARPED_WART, BlockRenderLayer.CUTOUT);
    }
}
