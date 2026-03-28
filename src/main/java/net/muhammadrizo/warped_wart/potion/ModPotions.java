package net.muhammadrizo.warped_wart.potion;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.potion.Potions;
import net.muhammadrizo.warped_wart.WarpedWart;
import net.muhammadrizo.warped_wart.item.ModItems;

public class ModPotions {
    public static void init() {
        WarpedWart.LOGGER.info("Registering potions for " + WarpedWart.MOD_ID);

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    Potions.WATER,
                    ModItems.WARPED_WART,
                    Potions.AWKWARD
            );
        });
    }
}
