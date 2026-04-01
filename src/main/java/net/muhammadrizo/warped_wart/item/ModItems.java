package net.muhammadrizo.warped_wart.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.muhammadrizo.warped_wart.WarpedWart;
import net.muhammadrizo.warped_wart.block.ModBlocks;

import java.util.function.Function;

public class ModItems {
    public static final Item WARPED_WART = register(
            "warped_wart",
            settings -> new BlockItem(ModBlocks.WARPED_WART, settings.useItemPrefixedTranslationKey()));

    public static Item register(String name, Function<Item.Settings, Item> itemFactory) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WarpedWart.MOD_ID, name));

        Item.Settings settings = new Item.Settings().registryKey(itemKey);

        Item item = itemFactory.apply(settings);

        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void init() {
        WarpedWart.LOGGER.info("Registering items for " + WarpedWart.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.addAfter(Items.NETHER_WART,
                    WARPED_WART);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.NETHER_WART,
                    WARPED_WART);
        });
    }
}
