package net.muhammadrizo.warped_wart;

import net.fabricmc.api.ClientModInitializer;
import net.muhammadrizo.warped_wart.block.ModBlocks;

public class WarpedWartClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Client render layer registration API changed in MC 26.3.
        // Warped Wart render layer can be configured via datagen/client setup.
    }
}
