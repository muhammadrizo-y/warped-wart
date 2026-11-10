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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Replaces exactly half of the 20 vanilla nether wart plants in the fortress
 * nether-wart corridor room (CastleStalkRoom) with warped wart.
 *
 * Vanilla 26.3 places the plot through StructurePiece.generateBox, which loops
 * over cells and calls this.placeBlock(level, state, x, y, z, box) with
 * piece-local coordinates. The two plots are fixed and verified against the
 * CastleStalkRoom.postProcess bytecode:
 *   plot 1: x in [3, 4], y = 5, z in [4, 8]   -> 10 plants, local indices 0..9
 *   plot 2: x in [8, 9], y = 5, z in [4, 8]   -> 10 plants, local indices 10..19
 *
 * The choice is deterministic per room: a Fisher-Yates shuffle of indices
 * 0..19 seeded by the piece's bounding-box origin decides which 10 indices
 * become warped wart. Because the decision depends only on (room origin,
 * local cell index), cells split across chunk clip boxes are always decided
 * the same way, and every reload/world-copy yields the same layout.
 *
 * No direct chunk/world access happens in this mixin, so there is no
 * deadlock on the world-gen thread. Vanilla's own placeBlock path is still
 * the one that performs the actual chunk-local write via Operation#call.
 */
@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {

    @Unique
    private static final int warpedwart$PLOT_SIZE = 10;

    @WrapOperation(
        method = "generateBox(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;IIIIIILnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;placeBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/block/state/BlockState;IIILnet/minecraft/world/level/levelgen/structure/BoundingBox;)V"
        )
    )
    private void warpedwart$swapHalf(
        StructurePiece instance,
        WorldGenLevel level,
        BlockState state,
        int x, int y, int z,
        BoundingBox box,
        Operation<Void> original
    ) {
        BlockState chosen = state;
        int index;
        if (
            instance instanceof CastleStalkRoom
            && state.getBlock() == Blocks.NETHER_WART
            && (index = warpedwart$plotIndex(x, y, z)) >= 0
            && warpedwart$isWarpedCell(instance, index)
        ) {
            chosen = ModBlocks.WARPED_WART.defaultBlockState();
        }
        original.call(instance, level, chosen, x, y, z, box);
    }

    /**
     * Maps a generateBox-local cell to plot index 0..19, or -1 when the cell is
     * not part of the two nether-wart plots of the 26.3 fortress stalk room.
     */
    @Unique
    private static int warpedwart$plotIndex(int x, int y, int z) {
        if (y != 5 || z < 4 || z > 8) {
            return -1;
        }
        if (x >= 3 && x <= 4) {
            return (x - 3) * 5 + (z - 4);
        }
        if (x >= 8 && x <= 9) {
            return warpedwart$PLOT_SIZE + (x - 8) * 5 + (z - 4);
        }
        return -1;
    }

    /**
     * Deterministic "random half" decision. The 20 cell indices are shuffled
     * with a seed derived from the room's bounding-box origin; the first 10
     * shuffled slots are replaced, so every room gets exactly 10 warped of 20.
     */
    @Unique
    private static boolean warpedwart$isWarpedCell(StructurePiece piece, int index) {
        BoundingBox bb = piece.getBoundingBox();
        long seed = 341873128712L * bb.minX() ^ 132897987541L * bb.minY() ^ 12627129576835L * bb.minZ();
        java.util.Random rng = new java.util.Random(seed);

        int[] order = new int[warpedwart$PLOT_SIZE * 2];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        for (int i = order.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = order[i];
            order[i] = order[j];
            order[j] = tmp;
        }
        for (int slot = 0; slot < warpedwart$PLOT_SIZE; slot++) {
            if (order[slot] == index) {
                return true;
            }
        }
        return false;
    }
}
