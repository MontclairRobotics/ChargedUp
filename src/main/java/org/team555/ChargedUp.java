// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.team555;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.net.PortForwarder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import org.team555.animation2.AllianceAnimation;
import org.team555.animation2.FadeTransition;
import org.team555.animation2.SolidAnimation;
import org.team555.animation2.WipeTransition;
import org.team555.animation2.ZoomAnimation;
import org.team555.animation2.api.Animation;
import org.team555.animation2.api.ConditionalAnimation;
import org.team555.components.managers.Auto;
import org.team555.components.managers.BackupAuto;
import org.team555.components.managers.GyroscopeNavX;
import org.team555.components.managers.LED;
import org.team555.components.managers.MiscData;
import org.team555.components.subsystems.Drivetrain;
import org.team555.components.subsystems.Elevator;
import org.team555.components.subsystems.Grabber;
import org.team555.components.subsystems.Shwooper;
import org.team555.components.subsystems.Stinger;
import org.team555.inputs.JoystickInput;
import org.team555.structure.DetectionType;
import org.team555.util.HashMaps;
import org.team555.util.LazyDouble;
import org.team555.util.frc.GameController;
import org.team555.util.frc.Logging;
import org.team555.util.frc.Trajectories;
import org.team555.util.frc.GameController.Axis;
import org.team555.util.frc.GameController.Button;
import org.team555.util.frc.GameController.DPad;
import org.team555.util.frc.can.CANSafety;
import org.team555.util.frc.commandrobot.CommandRobot;
import org.team555.util.frc.commandrobot.RobotContainer;
import org.team555.vision.DummySystem;
import org.team555.vision.LimelightSystem;
import org.team555.vision.VisionSystem;

import org.team555.constants.*;

import java.util.HashMap;
import java.util.Map;

import com.pathplanner.lib.server.PathPlannerServer;

public class ChargedUp extends RobotContainer 
{
    public static final boolean useDebugController = false;

    // SIMULATION //
    public static final Field2d field = new Field2d();
    public static Field2d getField() {return field;}
    public static final Mechanism2d mainMechanism = new Mechanism2d(5, 5);

    // CONTROLLERS //
    public static final GameController driverController = GameController.from(
            ControlScheme.DRIVER_CONTROLLER_TYPE,
            ControlScheme.DRIVER_CONTROLLER_PORT);
    public static final GameController operatorController = GameController.from(
        ControlScheme.OPERATOR_CONTROLLER_TYPE,
        ControlScheme.OPERATOR_CONTROLLER_PORT);
    public static final GameController debugController = useDebugController 
        ? GameController.from(
        ControlScheme.DEBUG_CONTROLLER_TYPE,
        ControlScheme.DEBUG_CONTROLLER_PORT)
        : null;

    // SHUFFLEBOARD //
    private static final ShuffleboardTab debugTab = Shuffleboard.getTab("Debug");

    public static ShuffleboardTab getDebugTab() 
    {
        return debugTab;
    }

    private static final ShuffleboardTab mainTab = Shuffleboard.getTab("Main Tab");

    public static ShuffleboardTab getMainTab() 
    {
        return mainTab;
    }

    private static final ShuffleboardTab PIDTab = Shuffleboard.getTab("PID");

    public static ShuffleboardTab getPIDTab() 
    {
        return PIDTab;
    }

    // ANIMATIONS //
    public static Animation getDriverAlignmentAnimation()
    {
        return new ConditionalAnimation(new SolidAnimation(Color.kRed)) //! This solid animation should not happen!
            .addCase(() -> vision.getObjectAX() > +1, new ZoomAnimation(Color.kOrange).flip())
            .addCase(() -> vision.getObjectAX() < -1, new ZoomAnimation(Color.kTurquoise));
    }

    public static Animation getDisabledAnimation()
    {
        Debouncer debouncer = new Debouncer(0.5);
        return new ConditionalAnimation(Constants.Robot.LED.DEMO_ANIMATION)
            .addCase(CANSafety::hasErrors, new ZoomAnimation(Color.kRed).mirror())
            .addCase(() -> debouncer.calculate(Math.abs(vision.getObjectAX()) > 1), getDriverAlignmentAnimation());
    }

    public static Animation getGrabberAnimation()
    {
        return new ConditionalAnimation(new ZoomAnimation(Color.kPurple).mirror())
            .addCase(grabber::getHoldingCone, new ZoomAnimation(Color.kYellow).mirror());
    }
    
    public static Animation getDefaultAnimation()
    {
        return new ConditionalAnimation(getGrabberAnimation())
            .addCase(DriverStation::isDisabled, getDisabledAnimation());
    }

    // COMPONENTS //
    public static final GyroscopeNavX gyroscope = new GyroscopeNavX();
    public static final PneumaticHub pneu = new PneumaticHub(PneuConstants.PH_PORT);
    public static final VisionSystem vision = new LimelightSystem();

    public static final Drivetrain drivetrain = new Drivetrain();
    public static final Elevator elevator = new Elevator();
    public static final Shwooper shwooper = new Shwooper();
    public static final Grabber grabber = new Grabber();
    public static final Stinger stinger = new Stinger();
    public static final MiscData misc = new MiscData();
    
    public static final LED led = new LED(
        getDefaultAnimation(),
        new FadeTransition()
    );

    // INITIALIZER //
    @Override 
    public void initialize() 
    {
        field.setRobotPose(2, 2, Rotation2d.fromDegrees(0));
        BackupAuto.getAutoTab().add(field).withSize(7, 4).withPosition(3, 0);

        pneu.enableCompressorDigital();
        CANSafety.monitor(pneu);

        // CameraServer.startAutomaticCapture();
        PathPlannerServer.startServer(5820);

        for(int port = 5800; port <= 5805; port++)
        {
            PortForwarder.add(port, "limelight.local", port);
        }

        setupDebugTab();
        setupMainTab();
        setupPIDTab();
        setupCommandsTab();

        vision.setTargetType(DetectionType.DEFAULT);

        // HANDLE DRIVING //
        drivetrain.setDefaultCommand(
            Commands.run(
                () -> 
                {
                    if (!DriverStation.isTeleop()) 
                    {
                        drivetrain.setChassisSpeeds(0, 0, 0);
                        return;
                    }

                    drivetrain.setInput(
                        JoystickInput.getRight(driverController, true, true),
                        JoystickInput.getLeft(driverController, true, true)
                    );
                },
                drivetrain
            ).finallyDo(interrupted -> drivetrain.pauseDriverInput())
        );

        // Increase/Decrease Max Speed
        driverController.getButton(Button.RIGHT_BUMPER)
                .onTrue(drivetrain.commands.increaseSpeed());
        driverController.getButton(Button.LEFT_BUMPER)
                .onTrue(drivetrain.commands.decreaseSpeed());

        // FIELD RELATIVE //
        driverController.getAxis(Axis.LEFT_TRIGGER)
            .whenGreaterThan(0.5)
            .whileTrue(drivetrain.commands.disableFieldRelative())
            .whileFalse(drivetrain.commands.enableFieldRelative());

        // ANGLES //
        driverController.getButton(Button.Y_TRIANGLE)
            .onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(0)));
        driverController.getButton(Button.X_SQUARE)
            .onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(90)));
        driverController.getButton(Button.A_CROSS)
            .onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(180)));
        driverController.getButton(Button.B_CIRCLE)
            .onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(270)));

        // AUTONS //
        driverController.getDPad(DPad.UP)
            .onTrue(Commands555.balance());
        driverController.getDPad(DPad.LEFT)
            .onTrue(Commands555.turnToObject(() -> DetectionType.CONE));
        driverController.getDPad(DPad.RIGHT)
            .onTrue(Commands555.moveToObjectSideways(() -> DetectionType.CONE));

        // Button to Zero NavX //
        driverController.getButton(Button.START_TOUCHPAD)
            .onTrue(Commands.runOnce(() -> {
                Logging.info("Diego hit the button; current rotation is " + gyroscope.getRotation2d().getDegrees() + " degrees");
                gyroscope.setNorth();
            }).ignoringDisable(true));

        // Cancel PID
        Trigger pidActive = operatorController.getButton(Button.START_TOUCHPAD).negate();

        if(useDebugController)
        {
            debugController.getButton(Button.X_SQUARE).onTrue(drivetrain.commands.goToPositionRelative(0, 2));

            debugController.getDPad(DPad.UP)   .onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(0)));
            debugController.getDPad(DPad.RIGHT).onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(90)));
            debugController.getDPad(DPad.DOWN) .onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(180)));
            debugController.getDPad(DPad.LEFT) .onTrue(drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(270)));

            debugController.getButton(Button.B_CIRCLE).onTrue(Commands.runOnce(() -> vision.setTargetType(DetectionType.TAPE)).ignoringDisable(true));
            debugController.getButton(Button.A_CROSS).onTrue(Commands.runOnce(() -> vision.setTargetType(DetectionType.APRIL_TAG)).ignoringDisable(true));
        }

        ////////////// OPERATOR CONTROLS /////////

        // D-Pad Controls
        operatorController.getDPad(DPad.UP).and(pidActive)
            .toggleOnTrue(Commands555.elevatorHumanPlayerLevel());
        operatorController.getDPad(DPad.LEFT).and(pidActive)
            .toggleOnTrue(Commands555.scoreMid(false, true));
        operatorController.getDPad(DPad.DOWN).and(pidActive)
            .toggleOnTrue(Commands555.scoreCubeLow());
        operatorController.getDPad(DPad.RIGHT).and(pidActive)
            .toggleOnTrue(Commands555.elevatorStingerReturn());

        // Grabber
        operatorController.getButton(Button.A_CROSS)
            .onTrue(Commands555.toggleGrabber());
        operatorController.getButton(Button.Y_TRIANGLE)
            .onTrue(Commands555.setGrabberHasCone());
        operatorController.getButton(Button.X_SQUARE)
            .onTrue(Commands555.setGrabberHasCube());
        operatorController.getButton(Button.B_CIRCLE)
            .onTrue(Commands555.toggleStinger());

        // Shwooper
        // suck button
        operatorController.getAxis(Axis.LEFT_TRIGGER)
            .whenGreaterThan(0.5)
            .onTrue(Commands555.shwooperSpit())
            .onFalse(Commands555.stopShwooper());
        // button to spit schwooper
        operatorController.getAxis(Axis.RIGHT_TRIGGER)
            .whenGreaterThan(0.5)
            .onTrue(Commands555.shwooperSuck())
            .onFalse(Commands555.stopShwooper());

        // Elevator
        elevator.setDefaultCommand(Commands.run(() -> {
            JoystickInput left = JoystickInput.getLeft(
                operatorController, false, true);

            ElevatorConstants.JOY_ADJUSTER.adjustY(left);

            elevator.setSpeed(left.getY());
        }, elevator));
    }

    public static boolean skipDriveAuto()
    {
        return false;
    }

    // AUTO //
    public static final BackupAuto auto = new BackupAuto(); //TODO:

    @Override
    public Command getAuto() 
    {
        return auto.get();
    }

    // SHUFFLEBOARD //
    public void setupMainTab() 
    {
        // MAIN INFO //
        final ShuffleboardLayout info = mainTab.getLayout("Info", BuiltInLayouts.kList)
            .withSize(2, 4)
            .withPosition(9, 0);

        info.addBoolean("Field Relative", drivetrain::usingFieldRelative)
            .withWidget(BuiltInWidgets.kBooleanBox);
        
        info.addNumber("Max Linear Speed (mps)",
            () -> drivetrain.getCurrentSpeedLimits()[0] * DriveConstants.MAX_SPEED_MPS)
            .withWidget(BuiltInWidgets.kTextView);

        info.addNumber("Max Angular Speed (rps)",
            () -> drivetrain.getCurrentSpeedLimits()[1] * DriveConstants.MAX_TURN_SPEED_RAD_PER_S)
            .withWidget(BuiltInWidgets.kTextView);

        // OTHER INFORMATION //
        mainTab
            .addString("Recent Log", Logging::mostRecentLog)
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2, 1)
            .withPosition(0, 3);
        
        mainTab
            .addBoolean("Intake Manipulated Object", shwooper::manipulatedObject)
            .withWidget(BuiltInWidgets.kBooleanBox)
            .withSize(2, 1)
            .withPosition(0, 3);

        info
            .addBoolean("Pressure Maxxed?", () -> !pneu.getPressureSwitch());
            // .withSize(2, 1)
            // .withPosition(7, 3);
        
        info
            .addString("Suck Mode", shwooper::currentMode);
            // .withSize(2, 1)
            // .withPosition(7, 2);
        
        // GYROSCOPE VALUE //
        mainTab
            .addNumber("Gyroscope", () -> gyroscope.getRotationInCircle().getDegrees())
            .withWidget(BuiltInWidgets.kGyro)
            .withSize(2, 2)
            .withPosition(0, 0);

        // OBJECT MANIPULATION //
        mainTab
            .addBoolean("Current Held Object", ChargedUp.grabber::getHoldingCone)
            .withWidget(BuiltInWidgets.kBooleanBox)
            .withSize(2, 1)
            .withPosition(0, 2)
            .withProperties(Map.of(
                "Color when true",  Color.kGold.toHexString(),
                "Color when false", Color.kDarkViolet.toHexString()
            ));
    }

    public void setupDebugTab() 
    {
        debugTab.addStringArray("All Logs", Logging::allLogsArr)
            .withPosition(0 + 2 + 2 + 2, 3)
            .withSize(2, 2);

        debugTab.addDouble("Elevator Extension", elevator::getHeightNormalized)
            .withPosition(0 + 2, 3)
            .withSize(2, 1);

        debugTab.addDouble("FPS", misc::fps)
            .withPosition(0 + 2 + 2, 3)
            .withSize(2, 1);

        debugTab.add("Mechanism", mainMechanism);

        debugTab.addDouble("Limelight X",vision::getObjectAX)
            .withPosition(0, 3)
            .withSize(1, 1);

        debugTab.addDouble("PIPELINE #",vision::getPipeline)
            .withPosition(0, 4)
            .withSize(1, 1);
        debugTab.addDouble("Current Draw", shwooper::getCurrent)
            .withPosition(5, 5)
            .withWidget(BuiltInWidgets.kGraph);
    }

    public void setupPIDTab() 
    {
        final ShuffleboardLayout xPID = PIDTab.getLayout("X-PID", BuiltInLayouts.kGrid)
            .withPosition(0, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        xPID.add("X-PID Controller", drivetrain.xPID.getPIDController()).withPosition(0, 0);
        xPID.addBoolean("At SetPoint?", drivetrain.xPID::free).withPosition(0, 1);
        xPID.addDouble("X-Speed", drivetrain.xPID::getSpeed).withPosition(0, 2);
        xPID.addDouble("X-Measurement", drivetrain.xPID::getMeasurement).withPosition(0, 3);

        final ShuffleboardLayout yPID = PIDTab.getLayout("Y-PID", BuiltInLayouts.kGrid)
            .withPosition(1, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        yPID.add("Y-PID Controller", drivetrain.yPID.getPIDController()).withPosition(0, 0);
        yPID.addBoolean("At SetPoint?", drivetrain.yPID::free).withPosition(0, 1);
        yPID.addDouble("Speed", drivetrain.yPID::getSpeed).withPosition(0, 2);
        yPID.addDouble("Measurement", drivetrain.yPID::getMeasurement).withPosition(0, 3);

        final ShuffleboardLayout thetaPID = PIDTab.getLayout("THETA-PID", BuiltInLayouts.kGrid)
            .withPosition(2, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        thetaPID.add("θ-PID Controller", drivetrain.thetaPID.getPIDController()).withPosition(0, 0);
        thetaPID.addBoolean("At SetPoint?", drivetrain.thetaPID::free).withPosition(0, 1);
        thetaPID.addDouble("Speed", drivetrain.thetaPID::getSpeed).withPosition(0, 2);
        thetaPID.addDouble("Measurement", drivetrain.thetaPID::getMeasurement).withPosition(0, 3);

        final ShuffleboardLayout elevatorPID = PIDTab.getLayout("ELEVATOR-PID", BuiltInLayouts.kGrid)
            .withPosition(3, 0)
            .withSize(1, 5)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 6));
        elevatorPID.add("Elevator PID", elevator.PID.getPIDController()).withPosition(0, 0);
        elevatorPID.addBoolean("At SetPoint?", elevator.PID::free).withPosition(0, 1);
        elevatorPID.addDouble("Speed", elevator.PID::getSpeed).withPosition(0, 2);
        elevatorPID.addDouble("Measurement", elevator.PID::getMeasurement).withPosition(0, 3);
    }

    public void setupCommandsTab()
    {
        ShuffleboardTab tab = Shuffleboard.getTab("Commands");
        ShuffleboardTab trj = Shuffleboard.getTab("Test Trajectories");
        
        tab.add(Commands555.celebrate());

        tab.add(Commands555.signalCube());
        tab.add(Commands555.signalCone());

        tab.add(Commands555.openGrabber());
        tab.add(Commands555.closeGrabber());
        tab.add(Commands555.toggleGrabber());
        
        tab.add(Commands555.setGrabberHasCone());
        tab.add(Commands555.setGrabberHasCube());
        tab.add(Commands555.toggleGrabberHasCone());
        
        tab.add(Commands555.extendStinger());
        tab.add(Commands555.retractStinger());
        tab.add(Commands555.toggleStinger());

        tab.add(Commands555.shwooperSuck());
        tab.add(Commands555.shwooperSpit());
        tab.add(Commands555.stopShwooper());
        
        tab.add(Commands555.elevatorStingerReturn());

        tab.add(Commands555.scoreLow(false, true));
        tab.add(Commands555.scoreLowPeg(false, true));
        tab.add(Commands555.scoreLowShelf(false, true));
        tab.add(Commands555.scoreMid(false, true));
        tab.add(Commands555.scoreMidPeg(false, true));
        tab.add(Commands555.scoreMidShelf(false, true));

        tab.add(Commands555.pickup());

        tab.add(Commands555.turnToObject(() -> DetectionType.TAPE).withName("TURN TO TAPE"));
        tab.add(Commands555.moveToObjectSideways(() -> DetectionType.TAPE).withName("SIDE TO TAPE"));

        tab.add(Commands555.scoreCubeLow());
        tab.add(Commands555.elevatorHumanPlayerLevel().withName("humanz"));

        tab.add(Commands555.elevatorStingerReturn().withName("return elevator + stinger"));

        tab.add(Commands555.alignWithAprilTagForScore().withName("Align to april tag!!!!"));
        
        for(CommandBase testTraj : Trajectories.getAllTests().values())
        {
            trj.add(testTraj);
        }
    }
}