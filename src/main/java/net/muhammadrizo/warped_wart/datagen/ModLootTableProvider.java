package net.muhammadrizo.warped_wart.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import net.muhammadrizo.warped_wart.block.ModBlocks;
import net.muhammadrizo.warped_wart.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.WARPED_WART, cropDrops(
                ModBlocks.WARPED_WART,
                ModItems.WARPED_WART,
                ModItems.WARPED_WART,
                BlockStatePropertyLootCondition.builder(ModBlocks.WARPED_WART)
                        .properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))
        ));
    }
}
