package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {
    private final GDXGame game;
    private Player player;
    private Array<Enemy> enemies = new Array<>();
    private Texture background, playerTex, slimeTex;
    private Viewport gameViewport, uiViewport;
    private int score = 0;
    private float spawnTimer;

    public GameScreen(GDXGame game) {
        this.game = game;
        gameViewport = new ExtendViewport(16, 9);
        uiViewport = new ScreenViewport();

        background = new Texture("background.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        playerTex = new Texture("player.png");
        slimeTex = new Texture("slime.png");

        // Load Attack Animation
        Array<Texture> frames = new Array<>();
        for(int i=0; i<14; i++) frames.add(new Texture("slash" + String.format("%02d", i) + ".png"));
        Animation<Texture> attackAnim = new Animation<>(1f/12f, frames);

        player = new Player(8, 4.5f, gameViewport, playerTex, attackAnim, Gdx.audio.newSound(Gdx.files.internal("slash.wav")));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (player.isAlive()) {
            handleInput();
            update(delta);
            checkCollisions(delta);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
        }

        draw();
    }

    private void handleInput() {
    }

    private void update(float delta) {
        player.update(delta);
        spawnTimer += delta;
        if (spawnTimer >= 1.5f) {
            enemies.add(Enemy.spawn(gameViewport, slimeTex, player));
            spawnTimer = 0;
        }
        for (Enemy e : enemies) e.update(delta);
    }

    private void checkCollisions(float delta) {
        // Attack vs Enemy
        for (Attack a : player.getAttacks()) {
            Iterator<Enemy> it = enemies.iterator();
            while (it.hasNext()) {
                if (a.overlaps(it.next())) {
                    it.remove();
                    score++;
                }
            }
        }
        // Enemy vs Player
        for (Enemy e : enemies) {
            if (player.overlaps(e)) player.subtractLife(delta * 1.0f);
        }
    }

    private void draw() {
        gameViewport.apply();
        game.getBatch().setProjectionMatrix(gameViewport.getCamera().combined);
        game.getBatch().begin();
        // Texture wrapping logic for background here...
        game.getBatch().draw(background, 0, 0, gameViewport.getWorldWidth(), gameViewport.getWorldHeight());
        for (Enemy e : enemies) e.draw(game.getBatch());
        for (Attack a : player.getAttacks()) a.draw(game.getBatch());
        player.draw(game.getBatch());
        game.getBatch().end();
    }

    private void resetGame() {
        score = 0;
        enemies.clear();
        player.reset(8, 4.5f);
    }
}
