package frc.robot.structure;

public enum ScoringType 
{
    PEG, SHELF;

    public DetectionType getType()
    {
        if (this == PEG)   return DetectionType.TAPE;
        if (this == SHELF) return DetectionType.APRIL_TAG;

        return DetectionType.NONE;
    }

    public GamePiece getCorrespondingPeice()
    {
        if(this == PEG)   return GamePiece.CONE;
        if(this == SHELF) return GamePiece.CUBE;

        return GamePiece.NONE;
    }

    public static ScoringType from(GamePiece gamePeice)
    {
        if(gamePeice == GamePiece.CUBE) return SHELF;
        if(gamePeice == GamePiece.CONE) return PEG;

        assert false: "Cannot get a scoring type from a 'NONE' GamePeice";
        return null;
    }
}
