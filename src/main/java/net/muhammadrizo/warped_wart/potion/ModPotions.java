package net.muhammadrizo.warped_wart.potion;

import net.muhammadrizo.warped_wart.WarpedWart;

public class ModPotions {
    public static void init() {
        WarpedWart.LOGGER.info("Registering potions for " + WarpedWart.MOD_ID);
        // Brewing recipes are data-generated in MC 26.3; see datagen providers.
    }
}
