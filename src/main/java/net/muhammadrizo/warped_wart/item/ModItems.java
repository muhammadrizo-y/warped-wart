package net.muhammadrizo.warped_wart.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.muhammadrizo.warped_wart.WarpedWart;
import net.muhammadrizo.warped_wart.block.ModBlocks;

import java.util.function.Function;

public class ModItems {
    public static final Item WARPED_WART = register(
            "warped_wart",
            settings -> new BlockItem(ModBlocks.WARPED_WART, settings)
    );

    public static Item register(String name, Function<Item.Properties, Item> itemFactory) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(WarpedWart.MOD_ID, name));

        Item.Properties settings = new Item.Properties().setId(itemKey);

        Item item = itemFactory.apply(settings);

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void init() {
        WarpedWart.LOGGER.info("Registering items for " + WarpedWart.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((FabricCreativeModeTabOutput output) -> {
            output.insertAfter(Items.NETHER_WART,
                    WARPED_WART);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((FabricCreativeModeTabOutput output) -> {
            output.insertAfter(Items.NETHER_WART,
                    WARPED_WART);
        });
    }
}
