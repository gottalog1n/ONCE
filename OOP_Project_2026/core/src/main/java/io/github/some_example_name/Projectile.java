package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {
    Texture tex;
    float x, y;
    float vx, vy;

    boolean dead = false;

    public Projectile(Texture tex, float x, float y, float vx, float vy) {
        this.tex = tex;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void update(float delta) {
        x += vx * delta;
        y += vy * delta;

        if (x < 0 || x > 2000 || y < 0 || y > 2000) dead = true;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, tex.getWidth(), tex.getHeight());
    }

    public void draw(SpriteBatch batch) {
        batch.draw(tex, x, y);
    }
}
