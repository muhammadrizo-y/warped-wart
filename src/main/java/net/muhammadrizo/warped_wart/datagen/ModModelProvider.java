package net.muhammadrizo.warped_wart.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.muhammadrizo.warped_wart.block.ModBlocks;
import net.muhammadrizo.warped_wart.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // Nether Wart uses a simple cross/crop model; register a simple item model for the block item.
        blockStateModelGenerator.registerSimpleItemModel(
                ModBlocks.WARPED_WART,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("warped_wart", "block/warped_wart"));
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.declareCustomModelItem(ModItems.WARPED_WART);
    }
}
