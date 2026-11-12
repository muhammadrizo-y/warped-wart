package net.muhammadrizo.warped_wart.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.item.enchantment.Enchantments;
import net.muhammadrizo.warped_wart.block.ModBlocks;
import net.muhammadrizo.warped_wart.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootSubProvider {
    public ModLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // Mirrors vanilla nether wart: age 3 drops 2-4 items (plus fortune bonus),
        // younger ages drop 1. Matched against the vanilla 26.3 loot table and
        // VanillaBlockLoot datagen bytecode.
        add(ModBlocks.WARPED_WART, this::warpedWartDrops);
    }

    private LootTable.Builder warpedWartDrops(Block block) {
        StatePropertiesPredicate.Builder fullyGrown = StatePropertiesPredicate.Builder.properties()
                .hasProperty(NetherWartBlock.AGE, 3);
        return LootTable.lootTable()
                .withPool(applyExplosionDecay(block, LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.WARPED_WART)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.between(2, 4))
                                        .when(MatchBlock.blockMatches(this.blocks, block, fullyGrown)))
                                .apply(ApplyBonusCount.addUniformBonusCount(
                                                this.enchantments.getOrThrow(Enchantments.FORTUNE))
                                        .when(MatchBlock.blockMatches(this.blocks, block, fullyGrown))))));
    }
}
