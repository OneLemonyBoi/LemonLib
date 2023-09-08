package onelemonyboi.lemonlib.trait.tile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import onelemonyboi.lemonlib.handlers.CustomEnergyStorage;
import onelemonyboi.lemonlib.handlers.MUItemStackHandler;
import onelemonyboi.lemonlib.trait.Trait;

public class TileTraits {
    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class PowerTrait extends Trait {
        private CustomEnergyStorage energyStorage;
        private final LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> this.energyStorage);

        public PowerTrait(int capacity) {
            energyStorage = new CustomEnergyStorage(capacity);
        }

        public PowerTrait(int capacity, int maxTransfer) {
            energyStorage = new CustomEnergyStorage(capacity, maxTransfer);
        }

        public PowerTrait(int capacity, int maxReceive, int maxExtract) {
            energyStorage = new CustomEnergyStorage(capacity, maxReceive, maxExtract);
        }

        public PowerTrait(int capacity, int maxReceive, int maxExtract, int energy) {
            energyStorage = new CustomEnergyStorage(capacity, maxReceive, maxExtract, energy);
        }

        public PowerTrait(CustomEnergyStorage storage) {
            energyStorage = storage;
        }

        @Override
        @SneakyThrows
        public Object clone() {
            return new PowerTrait(this.getEnergyStorage().copy());
        }
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class ItemTrait extends Trait {
        private MUItemStackHandler itemStackHandler;
        private final LazyOptional<MUItemStackHandler> lazyItemStackHandler = LazyOptional.of(() -> this.itemStackHandler);

        public ItemTrait(int slots) {
            itemStackHandler = new MUItemStackHandler(slots);
        }

        public ItemTrait(MUItemStackHandler storage) {
            itemStackHandler = storage;
        }

        @Override
        @SneakyThrows
        public Object clone() {
            return new ItemTrait(this.getItemStackHandler().copy());
        }
    }
}
