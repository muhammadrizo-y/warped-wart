package net.muhammadrizo.warped_wart.block;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.muhammadrizo.warped_wart.WarpedWart;
import net.muhammadrizo.warped_wart.item.ModItems;

import java.util.function.Function;

public class ModBlocks {
    public static final Block WARPED_WART = registerWithoutItem(
            "warped_wart",
            settings -> new NetherWartBlock(settings) {
                @Override
                protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
                    return new ItemStack(ModItems.WARPED_WART);
                }
            },
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .noCollision()
                    .randomTicks()
                    .sound(SoundType.NETHER_WART)
                    .pushReaction(PushReaction.IMMOVEABLE)
    );

    private static Block registerWithoutItem(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockKey));

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(WarpedWart.MOD_ID, name));
    }

    public static void init() {
        WarpedWart.LOGGER.info("Registering blocks for " + WarpedWart.MOD_ID);
    }
}
