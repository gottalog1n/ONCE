package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;
    private static final float ENEMY_SPAWN_INTERVAL = 1.5f;
    private static final float DAMAGE_PER_SECOND = 1.0f;
    private static final boolean DRAW_DEBUG = false;

    // General
    private final Batch batch;
    private final ShapeRenderer shapeRenderer;
    private final Viewport gameViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT);
    private final Viewport uiViewport = new ScreenViewport();
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    // Assets
    private final Texture bgdTexture = new Texture(Gdx.files.internal("bgd.png"));
    private final Texture enemyTexture = new Texture(Gdx.files.internal("slime.png"));

    // Attack
    private final Array<Texture> attackTextures = loadAttackTextures();
    private final Animation<Texture> attackAnimation = new Animation<>(1 / 12f, attackTextures);

    // Player animations — must be declared BEFORE player
    private final Array<Texture> idleTextures = loadIdleTextures();
    private final Array<Texture> walkWestTextures = loadWalkWestTextures();
    private final Array<Texture> walkEastTextures = loadWalkEastTextures();
    private final Animation<Texture> idleAnimation = new Animation<>(1 / 8f, idleTextures, Animation.PlayMode.LOOP);
    private final Animation<Texture> walkWestAnimation = new Animation<>(1 / 10f, walkWestTextures, Animation.PlayMode.LOOP);
    private final Animation<Texture> walkEastAnimation = new Animation<>(1 / 10f, walkEastTextures, Animation.PlayMode.LOOP);

    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("nightsplitter.mp3"));
    private final Sound slashSfx = Gdx.audio.newSound(Gdx.files.internal("slash.wav"));

    // Player
    private final Player player = new Player(
        WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f,
        gameViewport,
        idleAnimation,
        walkWestAnimation,
        walkEastAnimation,
        attackAnimation,
        slashSfx
    );
    private final Vector2 inputMovement = new Vector2();

    // Enemies
    private final Array<Enemy> enemies = new Array<>();
    private float enemySpawnTimer;

    // Game State
    private int score;

    public GameScreen(GdxGame game) {
        this.batch = game.getBatch();
        this.shapeRenderer = game.getShapeRenderer();
        this.font = game.getFont();

        bgdTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    private Array<Texture> loadAttackTextures() {
        Array<Texture> textures = new Array<>(14);
        for (int i = 0; i <= 13; i++) {
            textures.add(new Texture(Gdx.files.internal(String.format("slash_%02d.png", i))));
        }
        return textures;
    }

    private Array<Texture> loadIdleTextures() {
        Array<Texture> textures = new Array<>(4);
        for (int i = 0; i <= 3; i++) {
            textures.add(new Texture(Gdx.files.internal(String.format("Char_east_Idle_%02d.png", i))));
        }
        return textures;
    }

    private Array<Texture> loadWalkWestTextures() {
        Array<Texture> textures = new Array<>(6);
        for (int i = 0; i <= 5; i++) {
            textures.add(new Texture(Gdx.files.internal(String.format("Char_west_run_%02d.png", i))));
        }
        return textures;
    }

    private Array<Texture> loadWalkEastTextures() {
        Array<Texture> textures = new Array<>(6);
        for (int i = 0; i <= 5; i++) {
            textures.add(new Texture(Gdx.files.internal(String.format("Char_east_Run_%02d.png", i))));
        }
        return textures;
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        resetGame();
    }

    private void resetGame() {
        player.reset(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

        enemies.clear();
        enemySpawnTimer = 0f;

        score = 0;

        music.stop();
        music.setLooping(true);
        music.play();
    }

    @Override
    public void render(float deltaTime) {
        if (!player.isDead()) {
            processInput();
            updateLogic(deltaTime);
            checkCollisions(deltaTime);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
        }
        // DELETE the broken block that was here

        draw();
    }

    private void processInput() {
        inputMovement.setZero();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) inputMovement.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) inputMovement.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) inputMovement.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) inputMovement.x += 1;

        inputMovement.nor();
        player.changeDirection(inputMovement);
    }

    private void updateLogic(float deltaTime) {
        player.update(deltaTime);

        enemySpawnTimer += deltaTime;
        if (enemySpawnTimer >= ENEMY_SPAWN_INTERVAL) {
            enemySpawnTimer = 0f;
            enemies.add(Enemy.spawn(gameViewport, enemyTexture, player));
        }

        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
        }
    }

    private void checkCollisions(float deltaTime) {
        for (Attack attack : player.getAttacks()) {
            var iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                if (attack.overlaps(enemy)) {
                    iterator.remove();
                    score++;
                }
            }
        }

        int numHits = 0;
        for (Enemy enemy : enemies) {
            if (player.overlaps(enemy)) ++numHits;
        }

        if (numHits > 0) {
            player.subLife(DAMAGE_PER_SECOND * numHits * deltaTime);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        gameViewport.apply();
        batch.setProjectionMatrix(gameViewport.getCamera().combined);
        batch.begin();
        drawBackground();
        for (Enemy enemy : enemies) enemy.draw(batch);
        for (Attack attack : player.getAttacks()) attack.draw(batch);
        player.draw(batch);
        batch.end();

        drawDebug();

        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();
        font.draw(batch, "Score: " + score, 20, uiViewport.getWorldHeight() - 20);
        font.draw(batch, "Life: " + String.format("%.1f", Math.max(0f, player.getLife())), 20, uiViewport.getWorldHeight() - 60);
        if (player.isDead()) {
            layout.setText(font, "GAME OVER");
            font.draw(batch, layout, uiViewport.getWorldWidth() / 2 - layout.width / 2, uiViewport.getWorldHeight() / 2 + 40);
            layout.setText(font, "Press R to Restart");
            font.draw(batch, layout, uiViewport.getWorldWidth() / 2 - layout.width / 2, uiViewport.getWorldHeight() / 2 - 30);
        }
        batch.end();
    }

    private void drawBackground() {
        float u2 = gameViewport.getWorldWidth() / WORLD_WIDTH;
        float v2 = gameViewport.getWorldHeight() / WORLD_HEIGHT;
        batch.draw(bgdTexture, 0, 0, gameViewport.getWorldWidth(), gameViewport.getWorldHeight(), 0, 0, u2, v2);
    }

    private void drawDebug() {
        if (!DRAW_DEBUG) return;

        shapeRenderer.setProjectionMatrix(gameViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        player.drawDebug(shapeRenderer, Color.GREEN);
        for (Enemy enemy : enemies) enemy.drawDebug(shapeRenderer, Color.RED);
        for (Attack attack : player.getAttacks()) attack.drawDebug(shapeRenderer, Color.YELLOW);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        bgdTexture.dispose();
        enemyTexture.dispose();
        attackTextures.forEach(Texture::dispose);
        idleTextures.forEach(Texture::dispose);
        walkWestTextures.forEach(Texture::dispose);
        walkEastTextures.forEach(Texture::dispose);
        music.dispose();
        slashSfx.dispose();
    }
}
