package org.team555.structure;

import org.team555.util.frc.Logging;

public enum ScoringType 
{
    PEG, SHELF;

    public DetectionType getDetectionType()
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

    public static ScoringType from(GamePiece gamePiece)
    {
        if(gamePiece == GamePiece.CUBE) return SHELF;
        if(gamePiece == GamePiece.CONE) return PEG;

        Logging.error("Attempted to score while not holding a game piece: this is bad! Defaulting to peg mode.");
        return PEG;
    }
}
