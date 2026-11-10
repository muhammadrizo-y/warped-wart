# tools/migrate.ps1
# Mechanical Yarn (1.21.x) -> Mojang/deobfuscated MC 26.3 replacements for src/main/java/**/*.java
# Run:  pwsh -File tools/migrate.ps1
#       (from the repo root, with JAVA_HOME not required for this script)

$ErrorActionPreference = 'Stop'
$srcRoot = Join-Path $PSScriptRoot "..\src\main\java"
$files = Get-ChildItem -Path $srcRoot -Filter *.java -Recurse

if (-not $files) {
    Write-Warning "No .java files found under $srcRoot"
    exit 0
}

# ---------------------------------------------------------------------------
# Phase A: Qualified imports (order matters)
# ---------------------------------------------------------------------------
$phaseA = @(
    @{ old = 'net.minecraft.advancement.Advancement';                               new = 'net.minecraft.advancements.Advancement' },
    @{ old = 'net.minecraft.advancement.AdvancementEntry';                          new = 'net.minecraft.advancements.AdvancementHolder' },
    @{ old = 'net.minecraft.advancement.AdvancementFrame';                          new = 'net.minecraft.advancements.AdvancementType' },
    @{ old = 'net.minecraft.advancement.criterion.InventoryChangedCriterion';       new = 'net.minecraft.advancements.criterion.InventoryChangedCriterion' },
    @{ old = 'net.minecraft.text.Text';                                             new = 'net.minecraft.network.chat.Component' },
    @{ old = 'net.minecraft.sound.BlockSoundGroup';                                 new = 'net.minecraft.world.level.block.SoundType' },
    @{ old = 'net.minecraft.loot.entry.ItemEntry';                                  new = 'net.minecraft.world.level.storage.loot.entries.LootItem' },
    @{ old = 'net.minecraft.util.Identifier';                                       new = 'net.minecraft.resources.Identifier' },
    @{ old = 'net.minecraft.registry.Registries';                                   new = 'net.minecraft.core.Registries' },
    @{ old = 'net.minecraft.registry.RegistryKey';                                  new = 'net.minecraft.core.ResourceKey' },
    @{ old = 'net.minecraft.registry.RegistryWrapper';                              new = 'net.minecraft.core.HolderLookup' },
    @{ old = 'net.minecraft.registry.Registry';                                     new = 'net.minecraft.core.Registry' },
    @{ old = 'net.minecraft.registry.tag.BlockTags';                                new = 'net.minecraft.tags.BlockTags' },
    @{ old = 'net.minecraft.registry.tag.ItemTags';                                 new = 'net.minecraft.tags.ItemTags' },
    @{ old = 'net.minecraft.registry.tag.TagKey';                                   new = 'net.minecraft.tags.TagKey' },
    @{ old = 'net.minecraft.block.*';                                               new = 'net.minecraft.world.level.block.*' },
    @{ old = 'net.minecraft.item.*';                                                new = 'net.minecraft.world.item.*' },
    @{ old = 'net.minecraft.recipe.book.RecipeCategory';                            new = 'net.minecraft.data.recipes.RecipeCategory' },
    @{ old = 'net.minecraft.recipe.Ingredient';                                     new = 'net.minecraft.world.item.crafting.Ingredient' },
    @{ old = 'net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider'; new = 'net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider' },
    @{ old = 'net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider';       new = 'net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider' },
    @{ old = 'net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup';                new = 'net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab' },
    @{ old = 'net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries';         new = 'net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput' },
    @{ old = 'net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents';                new = 'net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents' },
    @{ old = 'net.minecraft.client.data.*';                                         new = 'net.minecraft.client.data.models.*' }
)

# ---------------------------------------------------------------------------
# Phase B: Bare token replacements (order matters - do NOT reorder)
# ---------------------------------------------------------------------------
$phaseB = @(
    @{ old = 'Registries.';                                      new = 'BuiltInRegistries.' },
    @{ old = 'RegistryKeys.';                                    new = 'Registries.' },
    @{ old = 'RegistryKey.of(';                                  new = 'ResourceKey.create(' },
    @{ old = 'RegistryKey';                                      new = 'ResourceKey' },
    @{ old = 'Identifier.ofVanilla(';                            new = 'Identifier.withDefaultNamespace(' },
    @{ old = 'Identifier.of(';                                   new = 'Identifier.fromNamespaceAndPath(' },
    @{ old = 'Text.translatable';                                new = 'Component.translatable' },
    @{ old = 'PistonBehavior.';                                  new = 'PushReaction.' },
    @{ old = 'ItemConvertible';                                  new = 'ItemLike' },
    @{ old = 'AbstractBlock.Settings';                           new = 'BlockBehaviour.Properties' },
    @{ old = 'PistonBehavior';                                   new = 'PushReaction' },
    @{ old = 'StairsBlock';                                      new = 'StairBlock' },
    @{ old = 'PillarBlock';                                      new = 'RotatedPillarBlock' },
    @{ old = 'BlockSoundGroup.';                                 new = 'SoundType.' },
    @{ old = 'ItemGroup';                                        new = 'CreativeModeTab' },
    @{ old = 'ItemGroups';                                       new = 'CreativeModeTabs' },
    @{ old = 'FabricItemGroupEntries';                           new = 'FabricCreativeModeTabOutput' },
    @{ old = 'FabricItemGroup.';                                 new = 'FabricCreativeModeTab.' },
    @{ old = 'ItemGroupEvents.modifyEntriesEvent(';              new = 'CreativeModeTabEvents.modifyOutputEvent(' },
    @{ old = 'RegistryWrapper.WrapperLookup';                    new = 'HolderLookup.Provider' },
    @{ old = 'AdvancementEntry';                                 new = 'AdvancementHolder' },
    @{ old = 'AdvancementFrame';                                 new = 'AdvancementType' },
    @{ old = 'FabricBlockLootTableProvider';                     new = 'FabricBlockLootSubProvider' },
    @{ old = 'FabricTagProvider';                                new = 'FabricTagsProvider' },
    @{ old = '.addAfter(';                                       new = '.insertAfter(' },
    @{ old = '.addBefore(';                                      new = '.insertBefore(' },
    @{ old = '.sounds(';                                         new = '.sound(' },
    @{ old = '.solid()';                                         new = '.forceSolidOn()' },
    @{ old = '.copy(';                                           new = '.ofLegacyCopy(' },
    @{ old = '.registryKey(';                                    new = '.setId(' },
    @{ old = '.getHardness()';                                   new = 'defaultDestroyTime()' },
    @{ old = '.getBlastResistance()';                            new = 'getExplosionResistance()' },
    @{ old = '.getSettings()';                                   new = 'properties()' },
    @{ old = '.getDefaultState()';                               new = 'defaultBlockState()' },
    @{ old = '.getInstrument()';                                 new = 'defaultBlockState().instrument()' },
    @{ old = '.getStateManager().getStates()';                   new = 'getStateDefinition().getPossibleStates()' },
    @{ old = '.getDefaultMapColor()';                            new = 'defaultMapColor()' },
    @{ old = '.PushReaction(';                                   new = '.pushReaction(' },
    @{ old = 'PushReaction.DESTROY';                             new = 'PushReaction.IMMOVEABLE' },
    @{ old = 'Item.Settings';                                    new = 'Item.Properties' },
    @{ old = '.useBlockPrefixedTranslationKey()';                new = '.useBlockDescriptionPrefix()' },
    @{ old = 'FabricDataOutput';                                 new = 'FabricPackOutput' },
    @{ old = 'valueLookupBuilder(';                              new = 'builder(' },
    @{ old = 'TagKey.of(';                                       new = 'TagKey.create(' },
    @{ old = 'Registry.register(Registries.';                    new = 'Registry.register(BuiltInRegistries.' },
    @{ old = 'BuiltInRegistries.ITEM.getId(item).getPath()';     new = 'item.builtInRegistryHolder().key().identifier().getPath()' }
)

function Apply-Replacements {
    param([string[]] $Paths, [hashtable[]] $Replacements, [string] $PhaseName)
    $count = 0
    foreach ($path in $Paths) {
        $content = [System.IO.File]::ReadAllText($path)
        $original = $content
        foreach ($r in $Replacements) {
            # Literal replacement (not regex) to avoid PowerShell/Regex escaping pitfalls.
            $content = $content.Replace($r.old, $r.new)
        }
        if ($content -ne $original) {
            [System.IO.File]::WriteAllText($path, $content)
            $count++
        }
    }
    Write-Host "${PhaseName}: modified $count file(s)"
    return $count
}

# BlockState needs word-boundary handling so BlockStateModelGenerator etc. are not mangled.
function Apply-BlockState {
    param([string[]] $Paths)
    $count = 0
    $fqn = 'net.minecraft.world.level.block.state.BlockState'
    foreach ($path in $Paths) {
        $content = [System.IO.File]::ReadAllText($path)
        $original = $content
        $content = [System.Text.RegularExpressions.Regex]::Replace($content, '\bBlockState\b', $fqn)
        if ($content -ne $original) {
            [System.IO.File]::WriteAllText($path, $content)
            $count++
        }
    }
    Write-Host "BlockState (word-boundary): modified $count file(s)"
    return $count
}

Write-Host "Phase A: qualified imports"
$null = Apply-Replacements -Paths $files -Replacements $phaseA -PhaseName "Phase A"

Write-Host "Phase B: bare tokens"
$null = Apply-Replacements -Paths $files -Replacements $phaseB -PhaseName "Phase B"

Write-Host "BlockState: fully-qualified (word-boundary)"
$null = Apply-BlockState -Paths $files

Write-Host "Mechanical pass complete."
