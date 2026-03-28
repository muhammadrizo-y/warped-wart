package net.muhammadrizo.warped_wart.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.Direction;
import net.muhammadrizo.warped_wart.block.ModBlocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(NetherFortressGenerator.CorridorNetherWartsRoom.class)
public class CorridorNetherWartsRoomMixin {
    @Inject(
            method = "generate",
            at = @At("TAIL")
    )
    private void replaceWithWarpedWart(
            StructureWorldAccess world,
            StructureAccessor structureAccessor,
            ChunkGenerator chunkGenerator,
            Random random,
            BlockBox chunkBox,
            ChunkPos chunkPos,
            BlockPos pivot,
            CallbackInfo ci
    ) {
        StructurePiece self = (StructurePiece)(Object) this;
        BlockBox bb = self.getBoundingBox();
        Direction facing = self.getFacing();

        // Collect all 20 wart positions
        List<BlockPos> positions = new ArrayList<>();
        int[][] xRanges = {{3, 4}, {8, 9}};
        for (int[] xRange : xRanges) {
            for (int x = xRange[0]; x <= xRange[1]; x++) {
                for (int z = 4; z <= 8; z++) {
                    int wx = applyX(facing, bb, x, z);
                    int wy = bb.getMinY() + 5;
                    int wz = applyZ(facing, bb, x, z);
                    positions.add(new BlockPos(wx, wy, wz));
                }
            }
        }

        // Shuffle and take first 10 to replace with warped wart
        Collections.shuffle(positions, new java.util.Random(
                BlockPos.asLong(bb.getMinX(), bb.getMinY(), bb.getMinZ())
        ));

        for (int i = 0; i < 10; i++) {
            BlockPos pos = positions.get(i);
            if (!chunkBox.contains(pos)) continue;
            if (world.getBlockState(pos).isOf(Blocks.NETHER_WART)) {
                world.setBlockState(pos,
                        ModBlocks.WARPED_WART.getDefaultState()
                                .with(NetherWartBlock.AGE, 0),
                        3
                );
            }
        }
    }

    @Unique
    private static int applyX(Direction facing, BlockBox bb, int x, int z) {
        if (facing == null) return x;
        return switch (facing) {
            case NORTH, SOUTH -> bb.getMinX() + x;
            case WEST -> bb.getMaxX() - z;
            case EAST -> bb.getMinX() + z;
            default -> x;
        };
    }

    @Unique
    private static int applyZ(Direction facing, BlockBox bb, int x, int z) {
        if (facing == null) return z;
        return switch (facing) {
            case NORTH -> bb.getMaxZ() - z;
            case SOUTH -> bb.getMinZ() + z;
            case WEST, EAST -> bb.getMinZ() + x;
            default -> z;
        };
    }
}