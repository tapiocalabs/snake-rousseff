package com.tapiocagames.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by thiago on 03/02/16.
 */
public class Dilma {

    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;

    List<BodyPart> bodyParts;

    public Dilma(int x, int y, Texture thead, Texture tchest, Texture tfeet) {

        bodyParts = new LinkedList<>();

        BodyPart head = new BodyPart(x, y);
        head.color = Color.DARK_GRAY;
        head.texture = thead;
        head.direction = UP;
        BodyPart chest = new BodyPart(x, y - (MainScreen.CELL_HEIGHT * 1));
        chest.color = Color.CORAL;
        chest.texture = tchest;
        chest.direction = UP;
        BodyPart feet = new BodyPart(x, y - (MainScreen.CELL_HEIGHT * 2));
        feet.color = Color.ORANGE;
        feet.texture = tfeet;
        chest.direction = UP;

        bodyParts.add(head);
        bodyParts.add(chest);
        bodyParts.add(feet);
    }

    public BodyPart head() {
        return bodyParts.get(0);
    }
}
