// package org.team555.components.subsystems;

// import edu.wpi.first.math.filter.SlewRateLimiter;
// import edu.wpi.first.wpilibj.DriverStation;
// import edu.wpi.first.wpilibj.PneumaticsModuleType;
// import edu.wpi.first.wpilibj.Solenoid;
// import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
// import edu.wpi.first.wpilibj.smartdashboard.MechanismObject2d;
// import edu.wpi.first.wpilibj.util.Color8Bit;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.Commands;
// import org.team555.ChargedUp;
// import org.team555.Commands555;
// import org.team555.constants.ElevatorConstants;
// import org.team555.constants.PneuConstants;
// import org.team555.constants.Ports;
// import org.team555.constants.SimulationConstants;
// import org.team555.constants.StingerConstants;
// import org.team555.math.Math555;
// import org.team555.util.LazyDouble;
// import org.team555.util.frc.commandrobot.ManagerSubsystemBase;

// import static org.team555.constants.StingerConstants.*;


// public class Stinger extends ManagerSubsystemBase
// {
//     private final Solenoid solenoid = new Solenoid(PneuConstants.PH_PORT, PneumaticsModuleType.REVPH, Ports.STINGER_PNEU_PORT);
//     private boolean target;
    
//     final MechanismLigament2d widthLigament;
//     final MechanismLigament2d[][] ligaments;
 
//     final SlewRateLimiter lengthLimiter = new SlewRateLimiter(SEGMENT_LENGTH / PNEU_TIME);

//     final double MIN_PNEU_LEN = SEGMENT_LENGTH * 0.2;
//     final double MAX_PNEU_LEN = SEGMENT_LENGTH * 0.7;

//     public Stinger()
//     {
//         // SIMULATION //
//         lengthLimiter.reset(MIN_PNEU_LEN);

//         ligaments = new MechanismLigament2d[9][2];

//         MechanismObject2d root = ChargedUp.elevator.getMechanismObject().append(new MechanismLigament2d("Stinger::root", 0, -90));
        
//         widthLigament = root.append(new MechanismLigament2d("Stinger::width", 0, -90));
//         widthLigament.setColor(new Color8Bit(SimulationConstants.STINGER_COLOR));

//         MechanismLigament2d lastObjectDown = root         .append(new MechanismLigament2d("Stinger::up::segment <root>",   0, 0));
//         MechanismLigament2d lastObjectUp   = widthLigament.append(new MechanismLigament2d("Stinger::down::segment <root>", 0, 90));
        
//         for(int i = 0; i < ligaments.length; i++)
//         {
//             lastObjectUp   = lastObjectUp  .append(new MechanismLigament2d("Stinger::up::segment " + i,   SEGMENT_LENGTH, 0));
//             lastObjectDown = lastObjectDown.append(new MechanismLigament2d("Stinger::down::segment " + i, SEGMENT_LENGTH, 0));
            
//             ligaments[i][0] = lastObjectUp;
//             ligaments[i][1] = lastObjectDown;

//             ligaments[i][0].setLineWeight(2);
//             ligaments[i][1].setLineWeight(2);
            
//             ligaments[i][0].setColor(new Color8Bit(SimulationConstants.STINGER_COLOR));
//             ligaments[i][1].setColor(new Color8Bit(SimulationConstants.STINGER_COLOR));
//         }

//         updateMechanism();
//     }

//     public boolean isOut() {return solenoid.get() == SOLENOID_OUT;}

//     public void targetOut() {target = SOLENOID_OUT;}
//     public void targetIn() {target = !SOLENOID_OUT;}

//     @Override
//     public void always() 
//     {
//         if(target == SOLENOID_OUT && solenoid.get() == !SOLENOID_OUT)
//         {
//             if(ChargedUp.elevator.getHeight() >= ElevatorConstants.BUFFER_SPACE_TO_INTAKE)
//             {
//                 solenoid.set(SOLENOID_OUT);
//             }
//         }
//         else 
//         {
//             solenoid.set(target);
//         }

//         // UPDATE SIMULATION IMAGES //
//         updateMechanism();
//     }

//     private void updateMechanism()
//     {
//         boolean solenoidMode = solenoid.get();
//         if(DriverStation.isDisabled()) solenoidMode = !SOLENOID_OUT;

//         double currentLength = lengthLimiter.calculate(
//             Math555.lerp(MIN_PNEU_LEN, MAX_PNEU_LEN, solenoidMode == SOLENOID_OUT ? 1 : 0)
//         );
//         double currentHeight = Math.sqrt(SEGMENT_LENGTH * SEGMENT_LENGTH - currentLength * currentLength);

//         widthLigament.setLength(currentHeight);
//         double mechAngle = Math.toDegrees(Math.acos(currentLength / SEGMENT_LENGTH));

//         for(int i = 0; i < ligaments.length; i++)
//         {
//             double angle = 0;

//             if(i == 0)          angle = +mechAngle;    
//             else if(i % 2 == 0) angle = +mechAngle * 2;
//             else                angle = -mechAngle * 2;

//             ligaments[i][0].setAngle(+angle);
//             ligaments[i][1].setAngle(-angle);
//         }
//     }

//     @Override
//     public void reset() 
//     {
//         target = !SOLENOID_OUT;
//     }
// }