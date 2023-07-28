package com.xpmodder.poweredgear.trait;

import com.xpmodder.poweredgear.PoweredGear;
import com.xpmodder.poweredgear.capability.PoweredItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.gear.trait.SimpleTrait;

import java.util.Collection;

public class PoweredTrait extends SimpleTrait {

    public static final Serializer<PoweredTrait> SERIALIZER = new Serializer<PoweredTrait>(PoweredGear.getId("powered"), PoweredTrait::new);

    public PoweredTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onRecalculatePost(TraitActionContext context) {
        context.getGear().getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> ((PoweredItem)energyStorage).updateLore());
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        Player player = context.getPlayer();
        Level world = player.getLevel();

        ItemStack thisGear = context.getGear();

        if (!world.isClientSide() && thisGear.getDamageValue() > 0) {

            thisGear.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                //Make sure to update energy capacity if incorrect
                if(energyStorage.getMaxEnergyStored() != (thisGear.getMaxDamage() * 10)){
                    ((PoweredItem)energyStorage).updatedMaxEnergy(thisGear.getMaxDamage() * 10);
                }
                //Try to completely repair the item with the energy stored
                int amount = energyStorage.extractEnergy((thisGear.getDamageValue() * 10), false);
                thisGear.setDamageValue(thisGear.getDamageValue() - (amount / 10));
            });

        }
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        ret.add("Items have a small internal energy storage equal to their durability and get repaired using the energy. Items will loose durability when the energy is empty.");
        return ret;
    }

}
