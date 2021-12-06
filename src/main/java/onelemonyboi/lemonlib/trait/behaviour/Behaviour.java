package onelemonyboi.lemonlib.trait.behaviour;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import onelemonyboi.lemonlib.trait.Trait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Behaviour {

    protected final Int2ReferenceMap<Trait> traits = new Int2ReferenceOpenHashMap<>();
    protected final List<Class<? extends Trait>> requiredTraits = new ArrayList<>();

    protected Behaviour() {

    }

    /**
     * Returns if this behaviour has a Trait
     */
    public boolean has(Class<? extends Trait> trait) {
        return traits.containsKey(trait.hashCode());
    }

    /**
     * Returns if this behaviour has any trait using inheritence. For example, if `TraitApple` extends `TraitFruit`
     * and this behaviour has `TraitApple` then calling Behaviour#hasBaseTrait(TraitFruit.class) will return true.
     */
    public boolean hasBaseTrait(Class<? extends Trait> matchTrait) {
        return traits.values().stream().anyMatch(trait -> matchTrait.isAssignableFrom(trait.getClass()));
    }

    /**
     * Returns any trait that matches using inheritence. For example, if `TraitApple` extends `TraitFruit`
     * and this behaviour has `TraitApple` then calling Behaviour#getBaseTrait(TraitFruit.class) would return the first trait that inherits from TraitFruit.
     * @return
     */
    public <T extends Trait> Optional<T> getBaseTrait(Class<T> matchTrait) {
        return (Optional<T>) traits.values().stream().filter(trait -> matchTrait.isAssignableFrom(trait.getClass())).findFirst();
    }

    /**
     * Gets a trait in this behaviour, if it exists.
     */
    public <T extends Trait> Optional<T> get(Class<T> trait) {
        return (Optional<T>) Optional.ofNullable(traits.get(trait.hashCode()));
    }

    /**
     * Gets a trait in this behaviour, if it exists.
     */
    public <T extends Trait> T getRequired(Class<T> trait) {
        Optional<T> optionalTrait = get(trait);
        if (!optionalTrait.isPresent()) {
            if (!requiredTraits.contains(trait))
                throw new IllegalStateException("Trait: " + trait
                        .getName() + " is not required! Did you forget to mark it as required in the behaviour builder?");
            else
                throw new IllegalStateException("Missing required trait: " + trait.getName());
        }
        return optionalTrait.get();
    }

    /**
     * Only tweaks the passed in objects for a certain trait
     *
     * @param traitType the trait type
     * @param objects   the objects
     */
    @SafeVarargs
    public final <T> void tweak(Class<? extends Trait> traitType, T... objects) {
        Trait trait = traits.get(traitType.hashCode());
        if (trait != null)
            trait.tweak(objects);
    }

    /**
     * Loops through all attributes and applies their tweaks to the passed in objects.
     */
    @SafeVarargs
    public final <T> void tweak(T... objects) {
        traits.values().forEach(trait -> trait.tweak(objects));
    }

    /**
     * Gets an attribute from this behaviour, if it exists.
     *
     * @param object the object to query for a behaviour. Should implement {@link IHasBehaviour}.
     */
    public static <T extends Trait> Optional<T> get(Object object, Class<T> trait) {
        if (object instanceof IHasBehaviour) {
            return ((IHasBehaviour) object).getBehaviour().get(trait);
        }
        return Optional.empty();
    }

    /**
     * Gets required.
     *
     * @param <T>    the type parameter
     * @param object the object
     * @param trait  the trait
     * @return the required
     */
    public static <T extends Trait> T getRequired(Object object, Class<T> trait) {
        if (object instanceof IHasBehaviour) {
            return ((IHasBehaviour) object).getBehaviour().getRequired(trait);
        }

        throw new IllegalStateException("Should never get here");
    }

    /**
     * Checks if this behaviour contains an attribute.
     *
     * @param object the object to query for a behaviour. Should implement {@link IHasBehaviour}.
     */
    public static boolean has(Object object, Class<? extends Trait> trait) {
        return object instanceof IHasBehaviour && ((IHasBehaviour) object).getBehaviour().has(trait);
    }

    public static class Builder<BEHAVIOUR extends Behaviour, BUILDER extends Builder<BEHAVIOUR, BUILDER>> {
        protected BEHAVIOUR behaviour;

        public Builder(BEHAVIOUR behaviour) {
            this.behaviour = behaviour;
        }

        private BUILDER getThis() {
            return (BUILDER) this;
        }

        /**
         * Compose a new behaviour from other partial behaviour.
         */
        public BUILDER composeFrom(PartialBehaviour... partialBehaviours) {
            for (PartialBehaviour partialBehaviour : partialBehaviours) {
                partialBehaviour.getPartialBehaviour().traits.forEach((i, t) -> this.behaviour.traits.put(i, t));
                this.behaviour.requiredTraits.addAll(partialBehaviour.getPartialBehaviour().requiredTraits);
            }
            return getThis();
        }

        /**
         * Add a trait to this behaviour
         */
        public BUILDER with(Trait... traits) {
            for (Trait trait : traits) {
                this.behaviour.traits.put(trait.getClass().hashCode(), trait);
            }
            return getThis();
        }

        /**
         * Remove a trait from this behaviour
         *
         * @param trait the trait
         * @return the builder
         */
        public BUILDER without(Class<? extends Trait> trait) {
            this.behaviour.traits.remove(trait.hashCode());
            return getThis();
        }

        /**
         * Adds a required trait(s) to this builder. If a non-partial behaviour is built and is missing a required trait an exception is thrown.
         */
        @SafeVarargs
        public final BUILDER requiredTraits(Class<? extends Trait>... traits) {
            this.behaviour.requiredTraits.addAll(Arrays.asList(traits));
            return getThis();
        }

        /**
         * Marks this behaviour as partial, which indicates that it's non-final and intended to be composed with other behaviours.
         *
         * @return the builder
         */
        public PartialBehaviour partial() {
            return new PartialBehaviour() {
                @Override
                public Behaviour getPartialBehaviour() {
                    return behaviour;
                }
            };
        }

        /**
         * Creates a finished, non-composable behaviour. This method will crash if not all required traits have been applied.
         *
         * @return the behaviour
         */
        public BEHAVIOUR build() {
            for (Class<? extends Trait> requiredTrait : this.behaviour.requiredTraits) {
                if (!this.behaviour.hasBaseTrait(requiredTrait)) {
                    throw new IllegalStateException("Non-partial behaviour must have required trait of type: " + requiredTrait
                            .getName());
                }
            }

            return this.behaviour;
        }
    }
}
