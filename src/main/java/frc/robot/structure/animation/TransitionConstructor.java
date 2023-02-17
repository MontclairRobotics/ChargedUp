package frc.robot.structure.animation;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

@FunctionalInterface
public interface TransitionConstructor 
{
    public Transition construct(double len, AddressableLEDBuffer old, AddressableLEDBuffer newb);
}
