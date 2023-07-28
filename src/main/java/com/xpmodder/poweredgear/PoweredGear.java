package com.xpmodder.poweredgear;

import com.mojang.logging.LogUtils;
import com.xpmodder.poweredgear.capability.PoweredCapabilityProvider;
import com.xpmodder.poweredgear.trait.PoweredTrait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.api.GearApi;
import net.silentchaos512.gear.api.item.ICoreItem;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PoweredGear.MODID)
public class PoweredGear
{
    public static final String MODID = "poweredgear";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> POWERED_UPGRADE = ITEMS.register("powered_upgrade", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS)));

    public PoweredGear()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        registerPoweredGearTraits();
    }

    public void registerPoweredGearTraits()
    {
        GearApi.registerTraitSerializer(PoweredTrait.SERIALIZER);
    }

    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MODID, path);
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event){
        //Add energy storage to item when it has our trait
        if(event.getObject().getItem() instanceof ICoreItem){
            if(GearApi.getTraitLevel(event.getObject(), getId("powered")) > 0) {
                event.addCapability(new ResourceLocation("energy_capability"), new PoweredCapabilityProvider(event.getObject(), event.getObject().getMaxDamage() * 10));
            }
        }

    }

}
