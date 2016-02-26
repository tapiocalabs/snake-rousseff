package com.tapiocagames.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class SnakeRousseffGame extends Game {

    private final AssetManager assetManager;

    public SnakeRousseffGame() {
        assetManager = new AssetManager();
    }

    @Override
    public void create() {
        setScreen(new StartScreen(this));
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
