package org.team555.util.frc.can;

public enum CANErrorCode 
{
    OK,
    NOT_FOUND,
    BAD_ID;

    public boolean isOk() {return this == OK;}
    public boolean isErr() {return this != OK;}
}