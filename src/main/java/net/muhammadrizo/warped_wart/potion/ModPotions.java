package net.muhammadrizo.warped_wart.potion;

import net.muhammadrizo.warped_wart.WarpedWart;

public class ModPotions {
    public static void init() {
        WarpedWart.LOGGER.info("Registering potions for " + WarpedWart.MOD_ID);
        // Brewing is data-driven in 26.3: brewing recipes are JSON under
        // resources/data/warped_wart/recipe/brewing/ (water + warped wart -> awkward,
        // for potion / splash_potion / lingering_potion), mirroring vanilla's
        // potion_water_nether_wart.json.
    }
}
