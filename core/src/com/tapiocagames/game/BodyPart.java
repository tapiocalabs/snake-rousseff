package com.tapiocagames.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by thiago on 03/02/16.
 */
public class BodyPart extends GameObject {

    Texture texture;
    int direction;

    Color color;

    public BodyPart(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("BodyPart x=%d,y=%d", x, y);
    }
}
