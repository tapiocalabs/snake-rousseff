package com.tapiocagames.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by thiago on 03/02/16.
 */
public class Food {
    float x;
    float y;
    Texture texture;

    public Food() {
        set(0, 0, null);
    }

    public void set(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }
}
