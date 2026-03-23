package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Enemy extends GameObject {
    private final float SPEED = 1.5f;
    private Player player;

    public Enemy(float x, float y, Texture texture, Player player) {
        super(x, y, texture.getWidth() / 32f, texture.getHeight() / 32f, texture);
        this.player = player;
    }

    @Override
    public void update(float delta) {
        Vector2 dir = player.getCenter(new Vector2()).sub(getCenter(new Vector2())).nor();
        rect.x += dir.x * SPEED * delta;
        rect.y += dir.y * SPEED * delta;
    }

    public static Enemy spawn(Viewport viewport, Texture tex, Player player) {
        int edge = MathUtils.random(3);
        float x = 0, y = 0;
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        switch (edge) {
            case 0: x = MathUtils.random(w); y = h; break; // Top
            case 1: x = w; y = MathUtils.random(h); break; // Right
            case 2: x = MathUtils.random(w); y = -tex.getHeight()/32f; break; // Bottom
            case 3: x = -tex.getWidth()/32f; y = MathUtils.random(h); break; // Left
        }
        return new Enemy(x, y, tex, player);
    }
}
