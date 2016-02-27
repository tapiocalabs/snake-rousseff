package com.tapiocagames.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by thiago on 26/02/16.
 */
public class LoadingScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 640.0f;
    private static final float WORLD_HEIGHT = 480.0f;
    private static final float PROGRESS_BAR_HEIGHT = 25.0f;
    private static final float PROGRESS_BAR_WIDTH = 200;
    private final SnakeRousseffGame game;
    private final ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private float progress;

    public LoadingScreen(SnakeRousseffGame game) {
        this.game = game;
        this.progress = 0.0f;
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        super.show();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        AssetManager assetManager = game.getAssetManager();

        assetManager.load("bg.png", Texture.class);
        assetManager.load("start-screen-logo.png", Texture.class);
        assetManager.load("play-down.png", Texture.class);
        assetManager.load("play-up.png", Texture.class);

        assetManager.load("bg-grass-and-holes.png", Texture.class);
        assetManager.load("food1.png", Texture.class);
        assetManager.load("special-food1.png", Texture.class);

        assetManager.load("head.png", Texture.class);
        assetManager.load("chest.png", Texture.class);
        assetManager.load("feet.png", Texture.class);
        assetManager.load("body.png", Texture.class);

        assetManager.load("mandioca-loop.ogg", Music.class);
        assetManager.load("game-over.ogg", Sound.class);
        assetManager.load("plin.ogg", Sound.class);

        assetManager.finishLoading();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void update() {
        AssetManager assetManager = game.getAssetManager();

        if (assetManager.update()) {
            game.setScreen(new StartScreen(game));
        } else {
            progress = assetManager.getProgress();
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        update();
        clearScreen();
        draw();
    }

    private void draw() {

        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);

        shapeRenderer.rect((WORLD_WIDTH / 2) - (PROGRESS_BAR_WIDTH / 2), WORLD_HEIGHT / 2, PROGRESS_BAR_WIDTH * progress, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }
}
