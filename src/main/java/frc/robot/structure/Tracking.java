package frc.robot.structure;

/**
 * An enum containing the thing the robot is currently tracking
 */
public enum Tracking 
{
    NONE(-1),
    CUBE(1),
    CONE(2),
    SERENA(3);

    private double pipelineNum;

    private Tracking(double pipelineNum) {
        this.pipelineNum = pipelineNum;
    }

    public double getPipelineNum() {
        return pipelineNum;
    }
}