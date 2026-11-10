$cp = Get-Content cp.txt -Raw
Write-Host "=== NetherBridgePiece ==="
& javap -p -classpath $cp 'net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces$NetherBridgePiece' 2>&1 | Select-Object -First 40

Write-Host ""
Write-Host "=== BoundingBox methods ==="
& javap -p -classpath $cp 'net.minecraft.world.level.levelgen.structure.BoundingBox' 2>&1 | Select-String -Pattern 'minX|minY|minZ|maxX|maxY|maxZ|isInside'

Write-Host ""
Write-Host "=== StructurePiece methods ==="
& javap -p -classpath $cp 'net.minecraft.world.level.levelgen.structure.StructurePiece' 2>&1 | Select-String -Pattern 'getBoundingBox|getFacing|getOrientation'
