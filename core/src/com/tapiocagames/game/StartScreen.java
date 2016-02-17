package com.tapiocagames.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by thiago on 13/02/16.
 */
public class StartScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 640.0f;
    private static final float WORLD_HEIGHT = 480.0f;

    private final Game game;

    private Stage stage;
    private Texture bgTexture;
    private Texture logoTexture;
    private Texture playUpTexture;
    private Texture playDownTexture;

    public StartScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        bgTexture = new Texture(Gdx.files.internal("bg.png"));
        logoTexture = new Texture(Gdx.files.internal("start-screen-logo.png"));
        playDownTexture = new Texture(Gdx.files.internal("play-down.png"));
        playUpTexture = new Texture(Gdx.files.internal("play-up.png"));

        ImageButton playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playUpTexture)),
                new TextureRegionDrawable(new TextureRegion(playDownTexture)));

        playButton.setPosition(WORLD_WIDTH / 2, WORLD_HEIGHT / 4.0f, Align.center);

        playButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.setScreen(new MainScreen(game));
                dispose();
            }
        });

        Image bgImage = new Image(bgTexture);
        Image logoImage = new Image(logoTexture);

        logoImage.setPosition(WORLD_WIDTH / 2, WORLD_HEIGHT * 3.f / 4.f, Align.center);

        stage.addActor(bgImage);
        stage.addActor(logoImage);
        stage.addActor(playButton);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        playDownTexture.dispose();
        playUpTexture.dispose();
        bgTexture.dispose();
        stage.dispose();
    }
}
