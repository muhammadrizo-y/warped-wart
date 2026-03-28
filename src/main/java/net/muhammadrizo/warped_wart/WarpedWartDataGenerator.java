package net.muhammadrizo.warped_wart;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.muhammadrizo.warped_wart.datagen.ModEnglishLangProvider;
import net.muhammadrizo.warped_wart.datagen.ModLootTableProvider;
import net.muhammadrizo.warped_wart.datagen.ModModelProvider;
import net.muhammadrizo.warped_wart.datagen.ModRecipeProvider;

public class WarpedWartDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModEnglishLangProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}
