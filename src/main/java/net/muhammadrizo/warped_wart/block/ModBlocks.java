package net.muhammadrizo.warped_wart.block;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.muhammadrizo.warped_wart.WarpedWart;
import net.muhammadrizo.warped_wart.item.ModItems;

import java.util.function.Function;

public class ModBlocks {
    public static final Block WARPED_WART = registerWithoutItem(
            "warped_wart",
            settings -> new NetherWartBlock(settings) {
                @Override
                protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
                    return new ItemStack(ModItems.WARPED_WART);
                }
            },
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BRIGHT_TEAL)
                    .noCollision()
                    .ticksRandomly()
                    .sounds(BlockSoundGroup.NETHER_WART)
                    .pistonBehavior(PistonBehavior.DESTROY)
    );

    private static Block registerWithoutItem(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(WarpedWart.MOD_ID, name));
    }

    public static void init() {
        WarpedWart.LOGGER.info("Registering blocks for " + WarpedWart.MOD_ID);
    }
}
