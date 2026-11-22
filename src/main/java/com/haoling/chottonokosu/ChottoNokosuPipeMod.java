package com.haoling.chottonokosu;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(ChottoNokosuPipeMod.MODID)
public class ChottoNokosuPipeMod {
    public static final String MODID = "chottonokosu";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<ChottoNokosuPipeBlock> PIPE = BLOCKS.register("chotto_nokosu_pipe",
            () -> new ChottoNokosuPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> PIPE_ITEM = ITEMS.registerSimpleBlockItem("chotto_nokosu_pipe", PIPE);

    public static final Supplier<BlockEntityType<ChottoNokosuPipeBlockEntity>> PIPE_BE = BLOCK_ENTITIES.register("chotto_nokosu_pipe",
            () -> BlockEntityType.Builder.of(ChottoNokosuPipeBlockEntity::new, PIPE.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<ChottoNokosuPipeMenu>> PIPE_MENU = MENUS.register("chotto_nokosu_pipe",
            () -> IMenuTypeExtension.create(ChottoNokosuPipeMenu::new));

    public static final Supplier<CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.chottonokosu"))
            .icon(() -> PIPE_ITEM.get().getDefaultInstance())
            .displayItems((params, output) -> output.accept(PIPE_ITEM.get()))
            .build());

    public ChottoNokosuPipeMod(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        MENUS.register(modBus);
        TABS.register(modBus);
        modBus.addListener(ChottoNokosuPipeMod::addToVanillaTabs);
    }

    private static void addToVanillaTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(PIPE_ITEM.get());
        }
    }
}
