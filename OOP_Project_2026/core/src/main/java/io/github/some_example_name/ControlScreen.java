package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ControlScreen extends ScreenAdapter {
    private final GDXGame game;
    private final Viewport viewport;
    private final GlyphLayout layout;

    public ControlScreen(GDXGame game) {
        this.game = game;
        this.viewport = new ScreenViewport();
        this.layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        // Clear screen to black
        ScreenUtils.clear(Color.BLACK);

        // Switch to GameScreen on Space press
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
            return;
        }

        viewport.apply();
        game.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        game.getBatch().begin();

        float centerX = viewport.getWorldWidth() / 2;
        float centerY = viewport.getWorldHeight() / 2;

        // Draw Instructions
        drawCenteredText("W A S D - Move", centerX, centerY + 60);
        drawCenteredText("SPACE - Start Game", centerX, centerY + 20);
        drawCenteredText("R - Restart (on Game Over)", centerX, centerY - 20);

        game.getBatch().end();
    }

    private void drawCenteredText(String text, float x, float y) {
        layout.setText(game.getFont(), text);
        game.getFont().draw(game.getBatch(), layout, x - layout.width / 2, y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
