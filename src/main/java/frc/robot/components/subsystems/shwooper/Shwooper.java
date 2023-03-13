package frc.robot.components.subsystems.shwooper;

public interface Shwooper 
{
    public void suck();

    public void spit();

    public void stop();

    public void extendShwooper();

    public void retractShwooper();

    public void toggleShwooper();

    public boolean isShwooperOut();

    public String currentMode();
}
