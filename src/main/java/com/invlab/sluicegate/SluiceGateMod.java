package com.invlab.sluicegate;

import com.invlab.sluicegate.block.SluiceGateBlock;
import com.invlab.sluicegate.entity.ModEntity;
import com.invlab.sluicegate.item.SluiceGateWandItem;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(SluiceGateMod.MODID)
public class SluiceGateMod {
    public static final String MODID = "sluice_gate";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> SLUICE_GATE_BLOCK = BLOCKS.register("sluice_gate_block",
            () -> new SluiceGateBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Item> SLUICE_GATE_WAND = ITEMS.register("sluice_gate_wand",
            () -> new SluiceGateWandItem(new Item.Properties().stacksTo(1)));

    public SluiceGateMod() {
        // 获取Forge提供的Mod事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册内容
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ModEntity.ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        // 注册配置（如有需要）
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("闸门模组起始阶段消息");
    }
}