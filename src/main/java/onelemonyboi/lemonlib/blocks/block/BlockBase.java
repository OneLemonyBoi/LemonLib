package onelemonyboi.lemonlib.blocks.block;

import lombok.SneakyThrows;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import onelemonyboi.lemonlib.trait.IHasProperty;
import onelemonyboi.lemonlib.trait.behaviour.Behaviour;
import onelemonyboi.lemonlib.trait.behaviour.IHasBehaviour;
import onelemonyboi.lemonlib.trait.block.BlockBehavior;
import onelemonyboi.lemonlib.trait.block.BlockTraits.*;
import org.apache.logging.log4j.core.util.ReflectionUtil;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public class BlockBase extends Block implements IHasBehaviour, EntityBlock {
    BlockBehavior behaviour;

    @SneakyThrows
    public BlockBase(BlockBehaviour.Properties properties, BlockBehavior behaviour) {
        super(tweak(behaviour, properties));
        this.behaviour = behaviour;

        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createBlockStateDefinition(builder);
        ObfuscationReflectionHelper.findField(Block.class, "f_49792_").set(this, builder.create(Block::defaultBlockState, BlockState::new));
        this.registerDefaultState(defineDefaultState());

        behaviour.tweak(this);
    }

    public static Properties tweak(BlockBehavior behaviour, Properties properties) {
        behaviour.tweak(properties);
        return properties;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        if (behaviour == null) return super.getRenderShape(state);

        if (behaviour.has(BlockRenderTypeTrait.class))
            return behaviour.getRequired(BlockRenderTypeTrait.class).getBlockRenderType();
        return super.getRenderShape(state);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {
            @Override
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                return behaviour.has(ParticlesTrait.class) && !behaviour.getRequired(ParticlesTrait.class).isShowBreakParticles();
            }

            @Override
            public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
                return behaviour.has(ParticlesTrait.class) && !behaviour.getRequired(ParticlesTrait.class).isShowBreakParticles();
            }
        });
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return behaviour.has(TileEntityTrait.class) ? behaviour.getRequired(TileEntityTrait.class).createTileEntity(this, pos, state) : null;
    }

    @Override
    @Deprecated
    public BlockState rotate(BlockState state, Rotation rotation) {
        if (behaviour == null) return super.rotate(state, rotation);

        if (behaviour.has(BlockRotationTrait.class)) {
            BlockRotationTrait trait = behaviour.getRequired(BlockRotationTrait.class);
            return state.setValue(trait.getDirectionProperty(), rotation.rotate(state.getValue(trait.getDirectionProperty())));
        }
        return super.rotate(state, rotation);
    }

    @Override
    @Deprecated
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        if (behaviour == null) return super.mirror(state, mirrorIn);

        if (behaviour.has(BlockRotationTrait.class)) {
            BlockRotationTrait trait = behaviour.getRequired(BlockRotationTrait.class);
            return state.rotate(mirrorIn.getRotation(state.getValue(trait.getDirectionProperty())));
        }
        return super.mirror(state, mirrorIn);
    }

    public BlockState defineDefaultState() {
        final BlockState[] def = {this.stateDefinition.any()};
        behaviour.getRelated(IHasProperty.class).forEach(t -> def[0] = t.modifyDefaultState(def[0]));
        return def[0];
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (behaviour == null) return;

        behaviour.getRelated(IHasProperty.class).forEach(t -> t.createBlockStateDefinition(builder));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (behaviour == null) return super.getStateForPlacement(context);

        if (behaviour.has(BlockRotationTrait.class)) {
            BlockRotationTrait trait = behaviour.getRequired(BlockRotationTrait.class);
            return trait.getStateForPlacement(this, context);
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(Level level, BlockEntityType<A> in, BlockEntityType<E> desired, BlockEntityTicker<? super E> serverTick, BlockEntityTicker<? super E> clientTick) {
        if (in != desired) return null;
        return (BlockEntityTicker<A>) (level.isClientSide() ? clientTick : serverTick);
    }
}
