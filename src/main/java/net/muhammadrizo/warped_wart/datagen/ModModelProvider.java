package net.muhammadrizo.warped_wart.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.level.block.NetherWartBlock;
import net.muhammadrizo.warped_wart.block.ModBlocks;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // Mirrors vanilla nether wart: creates the crop blockstate (age 0-3 ->
        // stage 0,1,1,2), the stage cross models (block/warped_wart_stageN with
        // texture block/warped_wart_stageN), and the block item's flat item model
        // (from the item texture). 26.3 rejects duplicate item model definitions,
        // so generateItemModels must stay empty.
        blockStateModelGenerator.createCropBlock(
                ModBlocks.WARPED_WART,
                NetherWartBlock.AGE,
                0, 1, 1, 2
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        // Intentionally empty: createCropBlock above registers the flat item model.
    }
}
