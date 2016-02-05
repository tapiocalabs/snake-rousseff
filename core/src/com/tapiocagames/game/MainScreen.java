package com.tapiocagames.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.Stack;

/**
 * Created by thiago on 03/02/16.
 */
public class MainScreen extends ScreenAdapter {

    public static final int CELL_WIDTH = 32;
    public static final int CELL_HEIGHT = 32;
    private static final int MAX_FOOD = 3;
    public static float walkTime = 0.4f;
    Batch batch;
    ShapeRenderer shapeRenderer;
    private Texture thead;
    private Texture tchest;
    private Texture tbody;
    private Texture tfeet;
    private float spentTime = 0.0f;
    private Dilma dilma;
    private Stack<Food> foods;
    private Stack<Food> deadFoods;

    private Texture foodTexture1;
    private int width;
    private int height;

    private int numCellsX;
    private int numCellsY;
    private BodyPart newBodyPart;
    private boolean gameOver;

    @Override
    public void show() {

        foodTexture1 = new Texture("food1.png");

        height = Gdx.graphics.getHeight();
        width = Gdx.graphics.getWidth();

        numCellsX = (int) MathUtils.floor(width / CELL_WIDTH);
        numCellsY = (int) MathUtils.floor(height / CELL_HEIGHT);

        Gdx.app.log("MainScreen", String.format(" width %d and height %d", width, height));

        int midX = (int) Math.floor(numCellsX / 2) * CELL_WIDTH;
        int midY = (int) Math.floor(numCellsY / 2) * CELL_HEIGHT;

        foods = new Stack<>();
        deadFoods = new Stack<>();
        for (int i = 0; i < MAX_FOOD; i++) {
            deadFoods.add(new Food());
        }

        thead = new Texture("head.png");
        tchest = new Texture("chest.png");
        tfeet = new Texture("feet.png");
        tbody = new Texture("body.png");

        gameOver = false;
        dilma = new Dilma(midX, midY, thead, tchest, tfeet);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        Gdx.app.log("show", String.format(" initial position (x,y) (%d,%d)", midX, midY));
        Gdx.app.log("show", String.format(" head position (x,y) (%d,%d)", dilma.bodyParts.get(0).x, dilma.bodyParts.get(0).y));

        addFood();
        addFood();
        addFood();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        spentTime += delta;

        queryInput();

        if (gameOver) {
            Gdx.app.log("render", "Game Over");
            return;
        }

        if (spentTime >= walkTime) {
            spentTime -= walkTime;

            move();
            checkCollisions();
        }

        draw();
    }

    private void queryInput() {

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        BodyPart head = dilma.head();

        if (left && (head.direction == Dilma.UP || head.direction == Dilma.DOWN)) {
            head.direction = Dilma.LEFT;
        } else if (right && (head.direction == Dilma.UP || head.direction == Dilma.DOWN)) {
            head.direction = Dilma.RIGHT;
        } else if (up && (head.direction == Dilma.LEFT || head.direction == Dilma.RIGHT)) {
            head.direction = Dilma.UP;
        } else if (down && (head.direction == Dilma.LEFT || head.direction == Dilma.RIGHT)) {
            head.direction = Dilma.DOWN;
        }
    }

    private void move() {

        BodyPart head = dilma.bodyParts.get(0);
        BodyPart chest = dilma.bodyParts.get(1);
        BodyPart feet = dilma.bodyParts.get(dilma.bodyParts.size() - 1);

        BodyPart lastBody = dilma.bodyParts.get(dilma.bodyParts.size() - 2); // may be the chest
        boolean lastBodyIsTheChest = lastBody.x == chest.x && lastBody.y == chest.y;

        boolean justAdded = newBodyPart != null;

        BodyPart beforeLastBody = dilma.bodyParts.get(dilma.bodyParts.size() - 3); // may be the chest
        int beforeLastBodyY = beforeLastBody.y;
        int beforeLastBodyX = beforeLastBody.x;

        int lastHeadY = head.y;
        int lastHeadX = head.x;

        int chestX = chest.x;
        int chestY = chest.y;

        int lastBodyX = lastBody.x;
        int lastBodyY = lastBody.y;

        if (head.direction == Dilma.UP) {

            head.y += CELL_HEIGHT;

            if (head.y >= height) {
                head.y = 0;
            }
        } else if (head.direction == Dilma.RIGHT) {

            head.x += CELL_WIDTH;

            if (head.x >= width) {
                head.x = 0;
            }
        } else if (head.direction == Dilma.DOWN) {

            head.y -= CELL_HEIGHT;

            if (head.y < 0) {
                head.y = height - CELL_HEIGHT;
            }
        } else if (head.direction == Dilma.LEFT) {

            head.x -= CELL_WIDTH;

            if (head.x < 0) {
                head.x = width - CELL_WIDTH;
            }
        }

        Gdx.app.log("move", String.format("velocity %.2f direction %d. x=%d y=%d", walkTime, head.direction, head.x, head.y));

        int chestDirection = getDirection(chest.x, chest.y, lastHeadX, lastHeadY);
        chest.x = lastHeadX;
        chest.y = lastHeadY;
        chest.direction = chestDirection;

        if (justAdded) {

            newBodyPart.x = chestX;
            newBodyPart.y = chestY;

            dilma.bodyParts.add(2, newBodyPart);

            if (dilma.bodyParts.size() > 4) {
                dilma.bodyParts.set(dilma.bodyParts.size() - 2, lastBody);
            }

            newBodyPart = null;
        } else {

            if (!lastBodyIsTheChest) {

                int lastBodyDirection = getDirection(lastBody.x, lastBody.y, chestX, chestY);
                lastBody.x = chestX;
                lastBody.y = chestY;
                lastBody.direction = lastBodyDirection;

                dilma.bodyParts.remove(dilma.bodyParts.size() - 2);
                dilma.bodyParts.add(2, lastBody);
            }

            int feetDirection = getDirection(feet.x, feet.y, lastBodyX, lastBodyY);
            feet.x = lastBodyX;
            feet.y = lastBodyY;
            feet.direction = feetDirection;
        }
    }

    private int getDirection(int oldX, int oldY, int newX, int newY) {

        if (newX > oldX && oldX + CELL_WIDTH < width) {
            return Dilma.RIGHT;
        } else if (newX < oldX) {
            return Dilma.LEFT;
        } else {

            Gdx.app.log("getDirection", String.format("newY=%d oldY=%d", newY, oldY));

            if (newY > oldY || oldY + CELL_HEIGHT >= height) {
                return Dilma.UP;
            } else {
                return Dilma.DOWN;
            }
        }
    }

    private void checkCollisions() {

        boolean collided = false;
        BodyPart head = dilma.head();

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

        l1:
        for (int i = 1, leni = dilma.bodyParts.size(); i < leni; i++) {

            BodyPart bodyPart = dilma.bodyParts.get(i);

            if (bodyPart.x == head.x && bodyPart.y == head.y) {
                gameOver = true;
                break l1;
            }
        }

        if (gameOver) {
            return;
        }

        if (collided) {
            addBodyPart();
            addFood();

            walkTime -= 0.01f;

            if (walkTime < 0.09f) {
                walkTime = 0.09f;
            }
        }
    }

    private void addBodyPart() {

        BodyPart feet = dilma.bodyParts.get(dilma.bodyParts.size() - 1);

        newBodyPart = new BodyPart(feet.x, feet.y);
        newBodyPart.color = Color.RED;
        newBodyPart.texture = tbody;
    }

    private void addFood() {

        if (foods.size() >= MAX_FOOD) {
            return;
        }

        Food food = deadFoods.pop();

        int x = (int) (MathUtils.floor(MathUtils.random() * (float) numCellsX) * CELL_WIDTH);
        int y = (int) (MathUtils.floor(MathUtils.random() * (float) numCellsY) * CELL_HEIGHT);

        food.set(x, y, foodTexture1);

        Gdx.app.log("addFood", String.format("x=%d, y=%d", x, y));

        foods.add(food);
    }

    private void draw() {

        Gdx.gl.glClearColor(0.4f, 1, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        for (BodyPart part : dilma.bodyParts) {
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

        int srcX = 0;
        int srcY = 0;
        boolean flipX = false;
        boolean flipY = false;

        for (int i = 0; i < dilma.bodyParts.size(); i++) {

            BodyPart part = dilma.bodyParts.get(i);

            float rotation = part.direction * 90;
            int srcWidth = part.texture.getWidth();
            int srcHeight = part.texture.getHeight();

            batch.draw(part.texture, part.x, part.y, part.texture.getWidth() / 2.0f, part.texture.getHeight() / 2.0f,
                    part.texture.getWidth(), part.texture.getHeight(), 1.0f, 1.0f, rotation,
                    srcX, srcY, srcWidth, srcHeight, flipX, flipY);
        }

        batch.end();
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

    }
}
