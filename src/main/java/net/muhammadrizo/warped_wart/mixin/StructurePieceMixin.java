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
 * Replaces half of the vanilla nether wart plants (5 of each of the two
 * 10-plant plots) in the fortress nether-wart corridor room (CastleStalkRoom)
 * with warped wart.
 *
 * Vanilla 26.3 places the plot through StructurePiece.generateBox, which loops
 * over cells and calls this.placeBlock(level, state, x, y, z, box) with
 * piece-local coordinates. The two plots are fixed and verified against the
 * CastleStalkRoom.postProcess bytecode:
 *   plot 1: x in [3, 4], y = 5, z in [4, 8]   -> 10 plants, local indices 0..9
 *   plot 2: x in [8, 9], y = 5, z in [4, 8]   -> 10 plants, local indices 10..19
 *
 * The split is decided per plot, not across the 20 cells: a Fisher-Yates
 * shuffle of indices 0..9 seeded by the piece's bounding-box origin picks
 * exactly 5 warped cells per plot. Because the decision depends only on
 * (room origin, local cell index), cells split across chunk clip boxes are
 * always decided the same way, and every reload/world-copy yields the same
 * layout.
 *
 * Aging is left to vanilla's natural random-tick growth; both blocks extend
 * NetherWartBlock, so planted warts grow normally once their chunk ticks.
 *
 * No direct chunk/world access happens in this mixin, so there is no
 * deadlock on the world-gen thread. Vanilla's own placeBlock path is still
 * the one that performs the actual chunk-local write via Operation#call.
 */
@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {

    @Unique
    private static final int warpedwart$PLOT_SIZE = 10;

    @Unique
    private static final int warpedwart$HALF = 5;

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
        int index = warpedwart$plotIndex(x, y, z);
        if (
            instance instanceof CastleStalkRoom
            && state.getBlock() == Blocks.NETHER_WART
            && index >= 0
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
     * Deterministic "random half per plot" decision. The 10 indices of a plot
     * are shuffled with a seed derived from the room's bounding-box origin and
     * a plot-specific salt; the first 5 shuffled slots are replaced, so each
     * plot gets exactly 5 warped of 10 plants.
     */
    @Unique
    private static boolean warpedwart$isWarpedCell(StructurePiece piece, int index) {
        int plot = index < warpedwart$PLOT_SIZE ? 0 : 1;
        int local = index - plot * warpedwart$PLOT_SIZE;
        long seed = warpedwart$roomSeed(piece) ^ (plot == 0 ? 0x51EED17L : 0xA5F00DL);
        java.util.Random rng = new java.util.Random(seed);

        int[] order = new int[warpedwart$PLOT_SIZE];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        for (int i = order.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = order[i];
            order[i] = order[j];
            order[j] = tmp;
        }
        for (int slot = 0; slot < warpedwart$HALF; slot++) {
            if (order[slot] == local) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static long warpedwart$roomSeed(StructurePiece piece) {
        BoundingBox bb = piece.getBoundingBox();
        return 341873128712L * bb.minX() ^ 132897987541L * bb.minY() ^ 12627129576835L * bb.minZ();
    }
}
