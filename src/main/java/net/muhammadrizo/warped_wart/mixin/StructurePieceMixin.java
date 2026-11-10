package net.muhammadrizo.warped_wart.mixin;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.muhammadrizo.warped_wart.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {

    @ModifyArg(
        method = "placeBlock",
        index = 1,
        at = @At("HEAD")
    )
    private static BlockState onPlaceBlock(BlockState state) {
        if (state.getBlock() == Blocks.NETHER_WART) {
            return ModBlocks.WARPED_WART.defaultBlockState();
        }
        return state;
    }
}
