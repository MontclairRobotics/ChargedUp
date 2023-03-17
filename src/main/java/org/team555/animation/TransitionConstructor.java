package org.team555.animation;

@FunctionalInterface
public interface TransitionConstructor 
{
    public Transition construct(double len);
}
