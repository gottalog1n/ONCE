package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class FirstScreen implements Screen {

    Main game;

    ShapeRenderer shape;
    Rectangle player;
    Array<Rectangle> enemies;

    long lastSpawnTime;

    public FirstScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        shape = new ShapeRenderer();

        player = new Rectangle();
        player.x = 400;
        player.y = 20;
        player.width = 50;
        player.height = 50;

        enemies = new Array<>();
        spawnEnemy();
    }

    void spawnEnemy() {
        Rectangle enemy = new Rectangle();
        enemy.x = (float) (Math.random() * (800 - 50));
        enemy.y = 480;
        enemy.width = 50;
        enemy.height = 50;

        enemies.add(enemy);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // movement
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.x -= 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.x += 200 * delta;

        // spawn
        if (TimeUtils.nanoTime() - lastSpawnTime > 1_000_000_000) {
            spawnEnemy();
        }

        // update enemies
        Iterator<Rectangle> iter = enemies.iterator();
        while (iter.hasNext()) {
            Rectangle enemy = iter.next();
            enemy.y -= 200 * delta;

            // 💥 GAME OVER
            if (enemy.overlaps(player)) {
                game.setScreen(new GameOverScreen(game));
                return; // IMPORTANT: stop this frame
            }

            if (enemy.y < 0) iter.remove();

            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }

        // draw
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(player.x, player.y, player.width, player.height);

        for (Rectangle enemy : enemies) {
            shape.rect(enemy.x, enemy.y, enemy.width, enemy.height);
        }
        shape.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shape.dispose();
    }
}
