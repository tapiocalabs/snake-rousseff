package com.tapiocagames.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by thiago on 03/02/16.
 */
public class Snake {

    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;

    List<BodyPart> bodyParts;
    private Texture headHurt;
    private Texture head;
    private float timeHurt = 0.0f;

    public Snake() {

        bodyParts = new LinkedList<>();

        BodyPart head = new BodyPart(0, 0);
        head.color = Color.DARK_GRAY;

        BodyPart chest = new BodyPart(0, 0);
        chest.color = Color.CORAL;

        BodyPart feet = new BodyPart(0, 0);
        feet.color = Color.ORANGE;

        bodyParts.add(head);
        bodyParts.add(chest);
        bodyParts.add(feet);
    }

    public void setup(float x, float y, Texture thead, Texture theadHurt, Texture tchest, Texture tfeet) {

        while (bodyParts.size() > 3) {
            bodyParts.remove(bodyParts.size() - 2);
        }

        headHurt = theadHurt;
        head = thead;

        BodyPart head = bodyParts.get(0);
        head.texture = thead;
        head.x = x;
        head.y = y;
        head.direction = UP;

        BodyPart chest = bodyParts.get(1);
        chest.texture = tchest;
        chest.x = x;
        chest.y = y - (MainScreen.CELL_HEIGHT * 1);
        chest.direction = UP;

        BodyPart feet = bodyParts.get(2);
        feet.texture = tfeet;
        feet.x = x;
        feet.y = y - (MainScreen.CELL_HEIGHT * 2);
        feet.direction = UP;
    }

    public BodyPart head() {
        return bodyParts.get(0);
    }

    public void collidedHurt() {
        timeHurt = 0;
    }

    public void update(float delta) {
        if (timeHurt >= 0) {
            timeHurt += delta;
        }

        if (timeHurt > 5) {
            timeHurt = -1;
            bodyParts.get(0).texture = head;
        }

        if (timeHurt > 0) {
            bodyParts.get(0).texture = headHurt;
        }
    }
}
