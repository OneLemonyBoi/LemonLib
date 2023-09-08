package onelemonyboi.lemonlib.handlers;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {
    Boolean canReceive = true;
    Boolean canExtract = true;

    public CustomEnergyStorage(int capacity) {
        super(capacity, capacity, capacity, 0);
    }

    public CustomEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer, maxTransfer, 0);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, Boolean canReceive, Boolean canExtract) {
        super(capacity, maxReceive, maxExtract, 0);
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    public CompoundTag write(CompoundTag nbt) {
        nbt.putInt("energy", energy);
        return nbt;
    }

    public void read(CompoundTag nbt) {
        setEnergy(nbt.getInt("energy"));
    }

    @Override
    public boolean canExtract() {
        return this.canExtract;
    }

    @Override
    public boolean canReceive() {
        return this.canReceive;
    }

    public void setCanExtract(Boolean bool) {
        this.canExtract = bool;
    }

    public void setCanReceive(Boolean bool) {
        this.canReceive = bool;
    }

    public void setMaxExtract(Integer num) {
        this.maxExtract = num;
    }

    public void setMaxRecieve(Integer num) {
        this.maxReceive = num;
    }

    // REWORKING

    public int produceEnergy(int produce) {
        int energyRecieved = Math.min(capacity - energy, produce);
        energy += energyRecieved;
        return energyRecieved;
    }

    public int consumeEnergy(int consume) {
        int energyOutput = Math.min(energy, consume);
        energy -= energyOutput;
        return energyOutput;
    }

    public int simulateProduceEnergy(int produce) {
        return Math.min(capacity - energy, produce);
    }

    public int simulateConsumeEnergy(int consume) {
        return Math.min(energy, consume);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void outputToSide(Level world, BlockPos pos, Direction side, int max) {
        BlockEntity te = world.getBlockEntity(pos.relative(side));
        if(te == null) {return;}
        LazyOptional<IEnergyStorage> opt = te.getCapability(ForgeCapabilities.ENERGY, side.getOpposite());
        IEnergyStorage ies = opt.orElse(null);
        if(ies == null) {return;}

        int ext = this.consumeEnergy(max);
        int putBack = ext - ies.receiveEnergy(ext, false);
        this.produceEnergy(putBack);
    }

    public void inputFromSide(Level world, BlockPos pos, Direction side, int max) {
        BlockEntity te = world.getBlockEntity(pos.relative(side));
        if(te == null) {return;}
        LazyOptional<IEnergyStorage> opt = te.getCapability(ForgeCapabilities.ENERGY, side.getOpposite());
        IEnergyStorage ies = opt.orElse(null);
        if(ies == null) {return;}

        int ext = ies.extractEnergy(max, false);
        int putBack = ext - this.produceEnergy(ext);
        ies.receiveEnergy(putBack, false);
    }

    @Deprecated
    @Override
    public int receiveEnergy(int recieve, boolean simulate)
    {
        if (simulate) {return Math.min(capacity - energy, Math.min(maxReceive, recieve));}
        int energyRecieved = Math.min(capacity - energy, Math.min(maxReceive, recieve));
        energy += energyRecieved;
        return energyRecieved;
    }

    @Deprecated
    @Override
    public int extractEnergy(int extract, boolean simulate)
    {
        if (simulate) {return Math.min(maxExtract, Math.min(energy, extract));}
        int energyExtracted = Math.min(maxExtract, Math.min(energy, extract));
        energy -= energyExtracted;
        return energyExtracted;
    }

    @Override
    public String toString() {
        return getEnergyStored() + "/" + getMaxEnergyStored();
    }

    public int machineConsume(int consume) {
        int reset = this.maxExtract;
        this.setMaxExtract(Integer.MAX_VALUE);
        int returnVal = this.consumeEnergy(consume);
        this.setMaxExtract(reset);
        return returnVal;
    }

    public int machineProduce(int recieve) {
        int reset = this.maxReceive;
        this.setMaxRecieve(Integer.MAX_VALUE);
        int returnVal = this.produceEnergy(recieve);
        this.setMaxRecieve(reset);
        return returnVal;
    }

    public Boolean checkedMachineConsume(int consume) {
        if (this.simulateConsumeEnergy(consume) == consume) {
            machineConsume(consume);
            return true;
        }
        return false;
    }

    public Boolean checkedMachineProduce(int consume) {
        if (this.simulateProduceEnergy(consume) == consume) {
            machineProduce(consume);
            return true;
        }
        return false;
    }

    public CustomEnergyStorage copy() {
        return new CustomEnergyStorage(capacity, maxReceive, maxExtract, energy);
    }
}