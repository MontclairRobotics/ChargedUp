package frc.robot.util.frc;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import com.kauailabs.navx.AHRSProtocol.TuningVar;

import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.Topic;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

public abstract class Tunable<T>
{
    T value;
    ArrayList<Consumer<T>> updators = new ArrayList<>();

    public final GenericEntry entry;
    
    final String name;

    protected abstract void setNT(T value);
    protected abstract T getFromNT();

    protected Tunable(T value, String name)
    {
        this.value = value;
        this.name  = name;

        NetworkTable nt = NetworkTableInstance.getDefault().getTable("Tuning");
        entry = nt.getTopic(name).getGenericEntry();

        setNT(value);
        
        nt.addListener(name, EnumSet.of(Kind.kValueAll), (table, key, event) -> 
        {
            DriverStation.MatchType matchType = DriverStation.getMatchType();

            if(matchType == DriverStation.MatchType.None 
            || matchType == DriverStation.MatchType.Practice 
            || DriverStation.isTest())
            {
                T nval = getFromNT();

                if(!nval.equals(value))
                {
                    for(Consumer<T> updator : updators)
                    {
                        updator.accept(nval);
                    }
                }
                
                this.value = nval;
            }
        }); 
    }

    public static Tunable<Double> of(double value, String name) 
    {
        return new Tunable<Double>(value, name) 
        {
            @Override
            protected Double getFromNT() 
            {
                return entry.getDouble(value);
            }

            @Override
            protected void setNT(Double value) 
            {
                entry.setDouble(value);
            }
        };
    }
    public static Tunable<Boolean> of(boolean value, String name)
    {
        return new Tunable<Boolean>(value, name) 
        {
            @Override
            protected Boolean getFromNT() 
            {
                return entry.getBoolean(value);
            }

            @Override
            protected void setNT(Boolean value) 
            {
                entry.setBoolean(value);
            }
        };
    }

    public Tunable<T> whenUpdate(Consumer<T> updator)
    {
        updators.add(updator);
        return this;
    }

    public T get()
    {
        return value;
    }
}
