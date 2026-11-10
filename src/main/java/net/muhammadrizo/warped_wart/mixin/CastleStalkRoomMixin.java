package net.muhammadrizo.warped_wart.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces.CastleStalkRoom;
import net.muhammadrizo.warped_wart.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(CastleStalkRoom.class)
public class CastleStalkRoomMixin {

    @Inject(
            method = "postProcess",
            at = @At("TAIL")
    )
    private void replaceWithWarpedWart(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox box,
            ChunkPos chunkPos,
            BlockPos pivot,
            CallbackInfo ci
    ) {
        CastleStalkRoom self = (CastleStalkRoom) (Object) this;
        BoundingBox bb = self.getBoundingBox();

        // Collect all wart positions (same grid as vanilla 1.21.x)
        List<BlockPos> positions = new ArrayList<>();
        int[][] xRanges = {{3, 4}, {8, 9}};
        for (int[] xRange : xRanges) {
            for (int x = xRange[0]; x <= xRange[1]; x++) {
                for (int z = 4; z <= 8; z++) {
                    int wx = applyX(self.getOrientation(), bb, x, z);
                    int wy = bb.minY() + 5;
                    int wz = applyZ(self.getOrientation(), bb, x, z);
                    positions.add(new BlockPos(wx, wy, wz));
                }
            }
        }

        // Shuffle and take first N to replace with warped wart
        Collections.shuffle(positions, new java.util.Random(
                BlockPos.asLong(bb.minX(), bb.minY(), bb.minZ())
        ));

        Level serverLevel = level.getLevel();
        int replaced = 0;
        for (BlockPos pos : positions) {
            if (replaced >= 10) break;
            if (!box.isInside(pos)) continue;
            BlockState state = serverLevel.getBlockState(pos);
            if (state.getBlock() == Blocks.NETHER_WART) {
                serverLevel.setBlock(pos, ModBlocks.WARPED_WART.defaultBlockState(), 3, 0);
                replaced++;
            }
        }
    }

    @Unique
    private static int applyX(Direction facing, BoundingBox bb, int x, int z) {
        if (facing == null) return x;
        return switch (facing) {
            case NORTH, SOUTH -> bb.minX() + x;
            case WEST -> bb.maxX() - z;
            case EAST -> bb.minX() + z;
            default -> x;
        };
    }

    @Unique
    private static int applyZ(Direction facing, BoundingBox bb, int x, int z) {
        if (facing == null) return z;
        return switch (facing) {
            case NORTH -> bb.maxZ() - z;
            case SOUTH -> bb.minZ() + z;
            case WEST, EAST -> bb.minZ() + x;
            default -> z;
        };
    }
}
