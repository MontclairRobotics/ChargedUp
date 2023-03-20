package org.team555.animation2;

import java.util.Random;

import org.team555.animation2.api.AnimationBase;

public class DeathAnimation extends AnimationBase
{
    @Override
    public void render()
    {
        for(int i = 0; i < getBuffer().getLength(); i++)
        {
            Random random = new Random();
            getBuffer().setHSV(i, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
    }
}
