package onelemonyboi.lemonlib.trait.block;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import onelemonyboi.lemonlib.trait.IHasProperty;
import onelemonyboi.lemonlib.trait.Trait;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.Function;

@UtilityClass
public class BlockTraits {
    public abstract static class MaterialTrait extends Trait {
        protected MaterialTrait() {
            this.addTweaker(BlockBehaviour.Properties.class, this::tweakProperties);
        }
        protected abstract void tweakProperties(BlockBehaviour.Properties properties);
    }

    public static class TileEntityTrait<T extends BlockEntity> extends Trait {
        private final TriFunction<Block, BlockPos, BlockState, T> function;

        public TileEntityTrait(TriFunction<Block, BlockPos, BlockState, T> function) {
            this.function = function;
        }

        @SneakyThrows
        public T createTileEntity(Block block, BlockPos pos, BlockState state) {
            return function.apply(block, pos, state);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class ParticlesTrait extends Trait {
        private final boolean showBreakParticles;
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class BlockRenderTypeTrait extends Trait {
        private final RenderShape blockRenderType;
    }
    
    public static class BlockRotationTrait extends Trait implements IHasProperty {

    	protected final RotationType rotationType;

    	public BlockRotationTrait(RotationType rotationType) {
    		this.rotationType = rotationType;
    	}

        public BlockState getStateForPlacement(Block block, BlockPlaceContext context) {
        	Direction dir;
        	switch (rotationType) {
                case XZ:
                    dir = context.getHorizontalDirection().getOpposite();
                    break;
                case XYZ:
                default:
                    dir = context.getPlayer().isShiftKeyDown() ? context.getNearestLookingDirection().getOpposite() : context.getNearestLookingDirection();
            }

        	return block.defaultBlockState().setValue(rotationType.direction, dir);
        }

        public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(rotationType.direction);
        }

        public BlockState modifyDefaultState(BlockState state) {
    	    return state.setValue(rotationType.direction, rotationType.defaultDir);
        }

        public DirectionProperty getDirectionProperty() {
        	return rotationType.direction;
        }
    }

	public enum RotationType {
        XZ(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
		XYZ(BlockStateProperties.FACING, Direction.NORTH);

		DirectionProperty direction;
        Direction defaultDir;

		RotationType(DirectionProperty dir, Direction def) {
			direction = dir;
			defaultDir = def;
		}
	}
}
