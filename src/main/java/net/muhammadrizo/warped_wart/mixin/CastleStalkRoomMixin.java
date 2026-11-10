package net.muhammadrizo.warped_wart.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces.CastleStalkRoom;
import net.muhammadrizo.warped_wart.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Replaces vanilla nether wart placed by the fortress nether-wart corridor room
 * with warped wart. Vanilla 26.3 passes {@code Blocks.NETHER_WART.defaultBlockState()}
 * as both primary and secondary state to {@link StructurePiece#generateBox}, so we
 * wrap that invocation and swap the state before it is written to the chunk.
 *
 * No direct chunk/world access happens here, so there is no deadlock on the
 * world-gen thread. The original call still runs through {@code Operation#call},
 * so vanilla's own block placement logic (chunk-local writes) is untouched.
 */
@Mixin(CastleStalkRoom.class)
public abstract class CastleStalkRoomMixin {

    @WrapOperation(
        method = "postProcess",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/structures/NetherFortressPieces$CastleStalkRoom;generateBox(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;IIIIIILnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Z)V"
        )
    )
    private void warpedwart$swapWart(
        CastleStalkRoom instance,
        WorldGenLevel level,
        BoundingBox box,
        int x1, int y1, int z1,
        int x2, int y2, int z2,
        BlockState primary,
        BlockState secondary,
        boolean flag,
        Operation<Void> original
    ) {
        if (primary.getBlock() == Blocks.NETHER_WART) {
            primary = ModBlocks.WARPED_WART.defaultBlockState();
        }
        if (secondary.getBlock() == Blocks.NETHER_WART) {
            secondary = ModBlocks.WARPED_WART.defaultBlockState();
        }
        original.call(instance, level, box, x1, y1, z1, x2, y2, z2, primary, secondary, flag);
    }
}
