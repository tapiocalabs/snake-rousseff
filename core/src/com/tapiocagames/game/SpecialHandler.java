package com.tapiocagames.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by thiago on 14/02/16.
 */
public class SpecialHandler {

    private final GlyphLayout glyphLayout;
    private final BitmapFont font;
    private final Viewport viewport;

    private float time;

    public SpecialHandler(Viewport viewport) {
        this.viewport = viewport;
        {
            //            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("UbuntuMono-B.ttf"));
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arcade-classic.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 28;
            font = generator.generateFont(parameter);
            generator.dispose(); // don't forget to dispose to avoid memory leaks!
        }

        glyphLayout = new GlyphLayout();
    }

    public void start() {
        this.time = 8.0f;
    }

    public boolean isRunning() {
        return time > 0.0f;
    }

    public void update(float delta) {

        time -= delta;
    }

    public void render(Batch batch) {

        String s = String.format("SPECIAL: %.0f", time);

        glyphLayout.reset();
        glyphLayout.setText(font, s);

        font.setColor(time < 2.0f ? Color.RED : Color.WHITE);
        font.draw(batch, s, (viewport.getWorldWidth() / 2) - (glyphLayout.width / 2), viewport.getWorldHeight() - glyphLayout.height);
    }

    public void stop() {
        time = 0.0f;
    }
}
