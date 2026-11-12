package net.muhammadrizo.warped_wart;

import net.fabricmc.api.ClientModInitializer;
import net.muhammadrizo.warped_wart.block.ModBlocks;

public class WarpedWartClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // No code needed: in MC 26.3 the chunk section layer (solid/cutout/translucent)
        // is derived automatically from each texture's pixel alpha (SpriteContents
        // transparency), and Fabric API 27.x removed BlockRenderLayerMap as a result.
        // The warped wart textures contain transparent pixels, so they render as
        // cutout like vanilla crops without any registration.
    }
}
