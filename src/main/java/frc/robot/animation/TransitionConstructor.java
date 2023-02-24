package frc.robot.structure.animation;

@FunctionalInterface
public interface TransitionConstructor 
{
    public Transition construct(double len);
}
