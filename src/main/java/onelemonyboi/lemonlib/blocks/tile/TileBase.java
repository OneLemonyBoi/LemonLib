package onelemonyboi.lemonlib.blocks.tile;

import lombok.SneakyThrows;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import onelemonyboi.lemonlib.annotations.SaveInNBT;
import onelemonyboi.lemonlib.trait.behaviour.Behaviour;
import onelemonyboi.lemonlib.trait.behaviour.IHasBehaviour;
import onelemonyboi.lemonlib.trait.tile.TileBehaviour;
import onelemonyboi.lemonlib.trait.tile.TileTraits;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

// Based On: TileEntityImpl by Ellpeck - https://github.com/Ellpeck/NaturesAura/blob/main/src/main/java/de/ellpeck/naturesaura/blocks/tiles/TileEntityImpl.java

public class TileBase extends BlockEntity implements IHasBehaviour {
    TileBehaviour behaviour;

    public TileBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, TileBehaviour behaviour) {
        super(tileEntityTypeIn, pos, state);
        this.behaviour = behaviour.copy();

        behaviour.tweak(this);
    }

    public TileBase(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        if (behaviour.has(TileTraits.PowerTrait.class)) {
            behaviour.getRequired(TileTraits.PowerTrait.class).getEnergyStorage().write(nbt);
        }
        if (behaviour.has(TileTraits.ItemTrait.class)) {
            nbt.put("itemSH", behaviour.getRequired(TileTraits.ItemTrait.class).getItemStackHandler().serializeNBT());
        }

        for (Field f : this.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(SaveInNBT.class)) continue;
            f.setAccessible(true);
            Object obj = null;
            try {
                obj = f.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String name = f.getAnnotation(SaveInNBT.class).key();
            String className = obj.getClass().getSimpleName();
            if (className.equals("String")) nbt.putString(name, (String) obj);
            else if (className.equals("int") || className.equals("Integer")) nbt.putInt(name, (Integer) obj);
            else if (className.equals("double") || className.equals("Double")) nbt.putDouble(name, (Double) obj);
            else if (className.equals("float") || className.equals("Float")) nbt.putFloat(name, (Float) obj);
            f.setAccessible(false);
        }

        super.saveAdditional(nbt);
    }

    @SneakyThrows
    @Override
    public void load(CompoundTag nbt) {
        if (behaviour.has(TileTraits.PowerTrait.class)) {
            behaviour.getRequired(TileTraits.PowerTrait.class).getEnergyStorage().read(nbt);
        }
        if (behaviour.has(TileTraits.ItemTrait.class)) {
            behaviour.getRequired(TileTraits.ItemTrait.class).getItemStackHandler().deserializeNBT(nbt.getCompound("itemSH"));
        }

        for (Field f : this.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(SaveInNBT.class)) continue;
            f.setAccessible(true);
            String name = f.getAnnotation(SaveInNBT.class).key();
            String className = f.get(this).getClass().getSimpleName();
            if (className.equals("String")) f.set(this, nbt.getString(name));
            else if (className.equals("int") || className.equals("Integer")) f.set(this, nbt.getInt(name));
            else if (className.equals("double") || className.equals("Double")) f.set(this, nbt.getDouble(name));
            else if (className.equals("float") || className.equals("Float")) f.set(this, nbt.getFloat(name));
            f.setAccessible(false);
        }

        super.load(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && behaviour.has(TileTraits.PowerTrait.class))
            return behaviour.getRequired(TileTraits.PowerTrait.class).getLazyEnergyStorage().cast();

        if (cap == ForgeCapabilities.ITEM_HANDLER && behaviour.has(TileTraits.ItemTrait.class)) {
            return behaviour.getRequired(TileTraits.ItemTrait.class).getLazyItemStackHandler().cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (behaviour.has(TileTraits.PowerTrait.class))
            behaviour.getRequired(TileTraits.PowerTrait.class).getLazyEnergyStorage().invalidate();
        if (behaviour.has(TileTraits.ItemTrait.class))
            behaviour.getRequired(TileTraits.ItemTrait.class).getLazyItemStackHandler().invalidate();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    public void sendToClients() {
        if (!level.isClientSide) {
            ServerLevel world = (ServerLevel) this.getLevel();
            List<ServerPlayer> entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.getBlockPos()), false);
            ClientboundBlockEntityDataPacket packet = this.getUpdatePacket();
            entities.forEach(e -> e.connection.send(packet));
        }
    }

    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }
}