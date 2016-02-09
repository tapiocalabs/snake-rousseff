package com.tapiocagames.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Stack;

/**
 * Created by thiago on 03/02/16.
 */
public class MainScreen extends ScreenAdapter {

    public static final float CELL_WIDTH = 32.0f;
    public static final float CELL_HEIGHT = 32.0f;
    private static final int MAX_FOOD = 3;
    private static final float MINIMUM_WALK_TIME = 0.076f;
    private static final float WORLD_WIDTH = 640.0f;
    private static final float WORLD_HEIGHT = 480.0f;
    private static final float INITIAL_WALK_TIME = 0.4f;
    private Batch batch;
    private ShapeRenderer shapeRenderer;
    private long gameOverTimer;
    private float walkTime;
    private float time;
    private float spentTime;
    private BitmapFont font;
    private BitmapFont scoreFont;
    private Sound gameOverSound;
    private Sound eatFoodSound;
    private Texture thead;
    private Texture tchest;
    private Texture tbody;
    private Texture tfeet;
    private Snake snake;
    private Stack<Food> foods;
    private Stack<Food> deadFoods;
    private Texture foodTexture1;
    private int score;
    private boolean restart;
    private int numCellsX;
    private int numCellsY;
    private BodyPart newBodyPart;
    private boolean gameIsOver;
    private boolean executedGameOver;
    private GlyphLayout glyphLayout;

    private OrthographicCamera camera;
    private Viewport viewport;
    private long lastTimeOfEating;

    @Override
    public void show() {

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

//        camera.zoom = 1.1f;
//        camera.update();
//        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth);

        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        {
            //            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("UbuntuMono-B.ttf"));
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arcade-classic.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 28;
            scoreFont = generator.generateFont(parameter);
            scoreFont.setColor(Color.GOLDENROD);
            generator.dispose(); // don't forget to dispose to avoid memory leaks!
        }

        {
//            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Bold.ttf"));
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arcade-classic.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 36;
            font = generator.generateFont(parameter);
            font.setColor(Color.DARK_GRAY);
            generator.dispose(); // don't forget to dispose to avoid memory leaks!
        }

        foodTexture1 = new Texture("food1.png");

        numCellsX = (int) MathUtils.floor(viewport.getWorldWidth() / CELL_WIDTH);
        numCellsY = (int) MathUtils.floor(viewport.getWorldHeight() / CELL_HEIGHT);

        Gdx.app.log("MainScreen", String.format("WORLD_WIDTH %.2f and WORLD_HEIGHT %.2f", viewport.getWorldWidth(), viewport.getWorldHeight()));
        Gdx.app.log("MainScreen", String.format("ZOOM %.2f ", camera.zoom));

        foods = new Stack<>();
        deadFoods = new Stack<>();
        for (int i = 0; i < MAX_FOOD; i++) {
            deadFoods.add(new Food());
        }

        thead = new Texture("head.png");
        tchest = new Texture("chest.png");
        tfeet = new Texture("feet.png");
        tbody = new Texture("body.png");

        glyphLayout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        snake = new Snake();

        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("game-over.ogg"));
        eatFoodSound = Gdx.audio.newSound(Gdx.files.internal("plin.ogg"));

        restartGame();
    }

    private void restartGame() {

        gameIsOver = false;
        restart = false;
        score = 0;
        time = 0;
        lastTimeOfEating = System.currentTimeMillis();
        executedGameOver = false;
        spentTime = 0.0f;
        walkTime = INITIAL_WALK_TIME;

        float midX = (int) Math.floor(numCellsX / 2) * CELL_WIDTH;
        float midY = (int) Math.floor(numCellsY / 2) * CELL_HEIGHT;

        snake.setup(midX, midY, thead, tchest, tfeet);

        Gdx.app.log("show", String.format(" initial position (x,y) (%.2f,%.2f)", midX, midY));
        Gdx.app.log("show", String.format(" head position (x,y) (%.2f,%.2f)", snake.bodyParts.get(0).x, snake.bodyParts.get(0).y));

        for (int i = 0; i < MAX_FOOD; i++)
            addFood();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        spentTime += delta;
        time += delta;

        queryInput();

        if (gameIsOver) {
            gameOver();

            if (restart && System.currentTimeMillis() - gameOverTimer > 7000) {
                restartGame();
            }

            return;
        }

        if (spentTime >= walkTime) {
            spentTime -= walkTime;

            move();
            checkCollisions();
        }

        draw();
    }

    private void gameOver() {

        if (executedGameOver) {
            return;
        }

        gameOverTimer = System.currentTimeMillis();
        gameOverSound.play();

        executedGameOver = true;
    }

    private void queryInput() {

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean enter = Gdx.input.isKeyPressed(Input.Keys.ENTER);

        BodyPart head = snake.head();

        if (left && (head.direction == Snake.UP || head.direction == Snake.DOWN)) {
            head.direction = Snake.LEFT;
        } else if (right && (head.direction == Snake.UP || head.direction == Snake.DOWN)) {
            head.direction = Snake.RIGHT;
        } else if (up && (head.direction == Snake.LEFT || head.direction == Snake.RIGHT)) {
            head.direction = Snake.UP;
        } else if (down && (head.direction == Snake.LEFT || head.direction == Snake.RIGHT)) {
            head.direction = Snake.DOWN;
        } else if (enter) {
            if (gameIsOver) {
                restart = true;
            }
        }
    }

    private void move() {

        BodyPart head = snake.bodyParts.get(0);
        BodyPart chest = snake.bodyParts.get(1);
        BodyPart feet = snake.bodyParts.get(snake.bodyParts.size() - 1);

        BodyPart lastBody = snake.bodyParts.get(snake.bodyParts.size() - 2); // may be the chest
        boolean lastBodyIsTheChest = lastBody.x == chest.x && lastBody.y == chest.y;

        boolean justAdded = newBodyPart != null;

        BodyPart beforeLastBody = snake.bodyParts.get(snake.bodyParts.size() - 3); // may be the chest
        float beforeLastBodyY = beforeLastBody.y;
        float beforeLastBodyX = beforeLastBody.x;

        int lastHeadDirection = head.direction;
        float lastHeadY = head.y;
        float lastHeadX = head.x;

        int chestDirection = chest.direction;
        float chestX = chest.x;
        float chestY = chest.y;

        int lastBodyDirection = lastBody.direction;
        float lastBodyX = lastBody.x;
        float lastBodyY = lastBody.y;

        if (head.direction == Snake.UP) {

            head.y += CELL_HEIGHT;

            if (head.y >= viewport.getWorldHeight()) {
                head.y = 0;
            }
        } else if (head.direction == Snake.RIGHT) {

            head.x += CELL_WIDTH;

            if (head.x >= viewport.getWorldWidth()) {
                head.x = 0;
            }
        } else if (head.direction == Snake.DOWN) {

            head.y -= CELL_HEIGHT;

            if (head.y < 0) {
                head.y = viewport.getWorldHeight() - CELL_HEIGHT;
            }
        } else if (head.direction == Snake.LEFT) {

            head.x -= CELL_WIDTH;

            if (head.x < 0) {
                head.x = viewport.getWorldWidth() - CELL_WIDTH;
            }
        }

        Gdx.app.log("move", String.format("velocity %.2f direction %d. x=%.2f y=%.2f", walkTime, head.direction, head.x, head.y));

        chest.x = lastHeadX;
        chest.y = lastHeadY;
        chest.direction = lastHeadDirection;

        if (justAdded) {

            newBodyPart.x = chestX;
            newBodyPart.y = chestY;
            newBodyPart.direction = chestDirection;

            snake.bodyParts.add(2, newBodyPart);

            if (snake.bodyParts.size() > 4) {
                snake.bodyParts.set(snake.bodyParts.size() - 2, lastBody);
            }

            newBodyPart = null;
        } else {

            if (!lastBodyIsTheChest) {

                lastBody.x = chestX;
                lastBody.y = chestY;
                lastBody.direction = chestDirection;

                snake.bodyParts.remove(snake.bodyParts.size() - 2);
                snake.bodyParts.add(2, lastBody);
            }

            feet.x = lastBodyX;
            feet.y = lastBodyY;
            feet.direction = lastBodyDirection;
        }
    }

    private void checkCollisions() {

        boolean collided = false;
        BodyPart head = snake.head();

        l1:
        for (int i = 0, leni = foods.size(); i < leni; i++) {

            Food food = foods.get(i);

            if (food.x == head.x && food.y == head.y) {
                collided = true;
                foods.remove(i);
                deadFoods.push(food);
                leni--;
                i--;
                break l1;
            }
        }

        if (collided) {
            eatFoodSound.play();
        }

        l1:
        for (int i = 1, leni = snake.bodyParts.size(); i < leni; i++) {

            BodyPart bodyPart = snake.bodyParts.get(i);

            if (bodyPart.x == head.x && bodyPart.y == head.y) {
                gameIsOver = true;
                break l1;
            }
        }

        if (gameIsOver) {
            return;
        }

        if (collided) {
            addBodyPart();
            addFood();

            computeScoreFromEatingFood();

            lastTimeOfEating = System.currentTimeMillis();
            walkTime -= 0.01f;

            if (walkTime < MINIMUM_WALK_TIME) {
                walkTime = MINIMUM_WALK_TIME;
            }
        }
    }

    private void computeScoreFromEatingFood() {

        float deltaEating = System.currentTimeMillis() - lastTimeOfEating;

        score += Math.floor(10.0f * (5f - deltaEating / 5f));
    }

    private void addBodyPart() {

        BodyPart feet = snake.bodyParts.get(snake.bodyParts.size() - 1);

        newBodyPart = new BodyPart(feet.x, feet.y);
        newBodyPart.color = Color.RED;
        newBodyPart.texture = tbody;
    }

    private void addFood() {

        if (foods.size() >= MAX_FOOD) {
            return;
        }

        Food food = deadFoods.pop();

        float x = 0;
        float y = 0;

        boolean collision = false;

        do {

            collision = false;
            x = (int) (MathUtils.floor(MathUtils.random() * (float) numCellsX) * CELL_WIDTH);
            y = (int) (MathUtils.floor(MathUtils.random() * (float) numCellsY) * CELL_HEIGHT);

            for (int i = 0, leni = snake.bodyParts.size(); i < leni && collision == false; i++) {

                BodyPart bodyPart = snake.bodyParts.get(i);

                if (bodyPart.x == x && bodyPart.y == y) {
                    collision = true;
                }
            }
        } while (collision);

        food.set(x, y, foodTexture1);

        Gdx.app.log("addFood", String.format("x=%.2f, y=%.2f", x, y));

        foods.add(food);
    }

    private void draw() {

        Gdx.gl.glClearColor(0.4f, 1, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        for (BodyPart part : snake.bodyParts) {
//
//            if (part.color != null) {
//                shapeRenderer.setColor(part.color);
//            }
//
//            shapeRenderer.rect(part.x, part.y, CELL_WIDTH, CELL_HEIGHT);
//        }
//        shapeRenderer.end();

        batch.begin();
        for (Food food : foods) {
            batch.draw(food.texture, food.x, food.y);
        }

        for (int i = 1; i < snake.bodyParts.size(); i++) {
            drawBodyPart(snake.bodyParts.get(i));
        }

        drawBodyPart(snake.head());

        if (gameIsOver) {

            String s = "Game Over";

            glyphLayout.reset();
            glyphLayout.setText(font, s);

            Gdx.app.log("render ", s + " at " + ((viewport.getWorldHeight() / 2) + (glyphLayout.height / 2)));
            font.draw(batch, s, (viewport.getWorldWidth() / 2) - (glyphLayout.width / 2), (viewport.getWorldHeight() / 2) + (glyphLayout.height / 2));
        }

        drawScore();
        drawTime();

        batch.end();
    }

    private void drawScore() {

        String s = "SCORE: " + score;

        glyphLayout.reset();
        glyphLayout.setText(scoreFont, s);

        scoreFont.draw(batch, s, scoreFont.getSpaceWidth(), viewport.getWorldHeight() - glyphLayout.height);
    }

    private void drawTime() {

        String s = "TIME: " + (int) Math.floor(time);

        glyphLayout.reset();
        glyphLayout.setText(scoreFont, s);

        scoreFont.draw(batch, s, viewport.getWorldWidth() - glyphLayout.width - scoreFont.getSpaceWidth(), viewport.getWorldHeight() - glyphLayout.height);
    }

    private void drawBodyPart(BodyPart part) {

        float rotation = part.direction * 90;
        int srcWidth = part.texture.getWidth();
        int srcHeight = part.texture.getHeight();
        int srcX = 0;
        int srcY = 0;
        boolean flipX = false;
        boolean flipY = false;

        batch.draw(part.texture, part.x, part.y, part.texture.getWidth() / 2.0f, part.texture.getHeight() / 2.0f,
                part.texture.getWidth(), part.texture.getHeight(), 1.0f, 1.0f, rotation,
                srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}
