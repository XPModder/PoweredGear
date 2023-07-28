package com.xpmodder.poweredgear.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PoweredCapabilityProvider implements ICapabilityProvider {

    private ItemStack stack;
    private int capacity;
    private LazyOptional<IEnergyStorage> capability = LazyOptional.of(() -> new PoweredItem(stack, capacity));

    public PoweredCapabilityProvider(ItemStack itemStack, int energyCapacity){
        this.stack = itemStack;
        this.capacity = energyCapacity;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ENERGY ? capability.cast() : LazyOptional.empty();
    }

}
