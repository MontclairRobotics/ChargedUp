package frc.robot.animation;

@FunctionalInterface
public interface TransitionConstructor 
{
    public Transition construct(double len);
}
