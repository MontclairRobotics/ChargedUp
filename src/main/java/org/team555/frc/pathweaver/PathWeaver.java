package org.team555.frc.pathweaver;

import java.io.IOException;
import java.nio.file.Path;

import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;

public class PathWeaver 
{
    private PathWeaver() {}

    private static String folderPath;
    public static void setFolderPath(String folderPath) {PathWeaver.folderPath = folderPath;}

    public static Trajectory get(String name) throws IOException
    {
        try 
        {
            var trajectoryPath = Filesystem.getDeployDirectory().toPath()
                .resolve(folderPath)
                .resolve(name);
            return TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } 
        catch (IOException ex) 
        {
            DriverStation.reportError("Unable to open trajectory '" + name + "'. Stopping robot program.", ex.getStackTrace());
            throw ex;
        }
    }
    public static Trajectory forceGet(String name)
    {
        try 
        {
            return get(name);
        } 
        catch (IOException ex) 
        {
            DriverStation.reportError("Unable to open trajectory '" + name + "'. Stopping robot program.", ex.getStackTrace());
            return null;
        }
    }
}
