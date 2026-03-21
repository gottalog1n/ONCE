package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;

public class GameScreen implements Screen {
    SpriteBatch batch;
    Texture playerTex, enemyTex, bulletTex;

    Player player;
    Array<Enemy> enemies = new Array<>();
    Array<Projectile> bullets = new Array<>();

    float spawnTimer = 0;

    public GameScreen() {
        batch = new SpriteBatch();

        playerTex = new Texture("player.png");
        enemyTex = new Texture("enemy.png");
        bulletTex = new Texture("bullet.png");

        player = new Player(playerTex, 400, 300);
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        player.draw(batch);

        for (Enemy e : enemies) e.draw(batch);
        for (Projectile b : bullets) b.draw(batch);

        batch.end();
    }

    void update(float delta) {
        player.update(delta);

        // spawn enemies
        spawnTimer += delta;
        if (spawnTimer > 1f) {
            spawnTimer = 0;
            enemies.add(new Enemy(enemyTex,
                MathUtils.random(0, Gdx.graphics.getWidth()),
                MathUtils.random(0, Gdx.graphics.getHeight())));
        }

        // enemies move
        for (Enemy e : enemies) {
            e.moveToward(player.x, player.y, delta);
        }

        // auto shoot
        player.shoot(bullets, enemies);
        // update bullets
        for (Projectile b : bullets) {
            b.update(delta);
        }

        // collision
        for (Projectile b : bullets) {
            for (Enemy e : enemies) {
                if (b.getBounds().overlaps(e.getBounds())) {
                    e.dead = true;
                    b.dead = true;
                }
            }
        }

        // cleanup
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).dead) enemies.removeIndex(i);
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            if (bullets.get(i).dead) bullets.removeIndex(i);
        }
    }

    @Override public void dispose() {
        batch.dispose();
        playerTex.dispose();
        enemyTex.dispose();
        bulletTex.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
