$cp = Get-Content cp.txt -Raw
Write-Host "=== CastleStalkRoom.postProcess: looking for NETHER_WART or placeBlock ==="
& javap -p -c -classpath $cp 'net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces$CastleStalkRoom' 2>&1 | Select-String -Pattern 'NETHER_WART|placeBlock|Blocks\.|WART'
