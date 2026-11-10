$cp = Get-Content cp.txt -Raw

# Check all fortress piece classes for NETHER_WART references
$jarPath = ($cp -split ';' | Where-Object { $_ -like '*minecraft-merged*' })[0]
$entries = & jar tf $jarPath 2>$null | Where-Object { $_ -like 'net/minecraft/world/level/levelgen/structure/structures/NetherFortress*' -and $_ -like '*.class' }

foreach ($entry in $entries) {
    $cls = $entry -replace '/', '.' -replace '\.class$'
    $out = & javap -p -classpath $cp $cls 2>$null
    if ($out -match 'NETHER_WART|nether_wart') {
        Write-Host "=== $cls ==="
        $out | Select-String -Pattern 'NETHER_WART|nether_wart' | ForEach-Object { Write-Host "  $($_.Line)" }
    }
}

# Also check CastleStalkRoom.postProcess bytecode for what blocks it places
Write-Host ""
Write-Host "=== CastleStalkRoom.postProcess bytecode (first placeBlock calls) ==="
$out = & javap -p -c -classpath $cp 'net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces$CastleStalkRoom' 2>$null
$out | Select-String -Pattern 'placeBlock|Blocks\.|NETHER_WART|WART' | ForEach-Object { Write-Host "  $($_.Line)" }
