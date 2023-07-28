package com.xpmodder.poweredgear.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class PoweredItem extends EnergyStorage {

    private ItemStack stack;

    public PoweredItem(ItemStack itemStack, int maxEnergy){
        super(getMaxCapacity(itemStack, maxEnergy), Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.stack = itemStack;
        this.energy = stack.hasTag() && stack.getTag().contains("energy") ? stack.getTag().getInt("energy") : 0;
    }

    private static int getMaxCapacity(ItemStack itemStack, int capacity) {
        if( !itemStack.hasTag() || !itemStack.getTag().contains("max_energy") )
            return capacity;

        return itemStack.getTag().getInt("max_energy");
    }

    public void updatedMaxEnergy(int max) {
        this.stack.getOrCreateTag().putInt("max_energy", max);
        this.capacity = max;

        // Ensure the current stored energy is up to date with the new max.
        this.receiveEnergy(1, false);
    }

    public void updateLore(){
        if(this.capacity == 0){
            return;
        }
        CompoundTag tag = this.stack.getOrCreateTagElement("display");
        ListTag list = new ListTag();
        String color = "#00A300";
        if(this.energy < (this.stack.getMaxDamage() / 2)){
            color = "#FFD800";
        }
        if(this.energy < (this.stack.getMaxDamage() / 4)){
            color = "#FF6A00";
        }
        if(this.energy < (this.stack.getMaxDamage() / 10)){
            color = "#FF0000";
        }
        list.add(StringTag.valueOf("{\"text\":\"" + this.energy + "FE / " + (this.stack.getMaxDamage() * 10) + "FE\",\"color\":\"" + color + "\",\"italic\":false}"));
        tag.put("Lore", list);
        this.stack.removeTagKey("display");
        this.stack.addTagElement("display", tag);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int amount = super.extractEnergy(maxExtract, simulate);
        updateLore();
        if( !simulate ) {
            //IMPOTANT: save new energy amount to item
            this.stack.getOrCreateTag().putInt("energy", this.energy);
        }
        return amount;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int stored = this.getEnergyStored() + maxReceive;
        if (stored < 0) {
            return 0;
        }

        int amount = super.receiveEnergy(maxReceive, simulate);
        if( !simulate ) {
            this.stack.getOrCreateTag().putInt("energy", this.energy);

            //Repair item while charging if damaged
            if(this.stack.getDamageValue() > 0){
                int energy = this.extractEnergy((this.stack.getDamageValue() * 10), false);
                this.stack.setDamageValue(this.stack.getDamageValue() - (energy / 10));
            }
        }

        updateLore();
        return amount;
    }

}
