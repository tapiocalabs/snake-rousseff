package com.tapiocagames.game;

import com.badlogic.gdx.Game;

public class SnakeRousseffGame extends Game {

    @Override
    public void create() {
        setScreen(new StartScreen(this));
    }
}
