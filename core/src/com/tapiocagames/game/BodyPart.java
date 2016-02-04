package com.tapiocagames.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by thiago on 03/02/16.
 */
public class BodyPart {

    int x;
    int y;
    Texture texture;
    int direction;

    Color color;

    public BodyPart(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("x=%d,y=%d", x, y);
    }
}
