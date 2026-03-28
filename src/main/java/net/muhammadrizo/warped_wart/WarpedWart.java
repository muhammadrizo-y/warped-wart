package net.muhammadrizo.warped_wart;

import net.fabricmc.api.ModInitializer;

import net.muhammadrizo.warped_wart.block.ModBlocks;
import net.muhammadrizo.warped_wart.item.ModItems;
import net.muhammadrizo.warped_wart.potion.ModPotions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarpedWart implements ModInitializer {
	public static final String MOD_ID = "warped_wart";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.init();
		ModItems.init();
		ModPotions.init();
	}
}