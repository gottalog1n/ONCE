package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GDXGame extends Game {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Load TrueType Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("cherry_cream_soda.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        setScreen(new ControlScreen(this));
    }

    public SpriteBatch getBatch() { return batch; }
    public BitmapFont getFont() { return font; }
    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
