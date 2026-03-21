package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    Texture tex;
    float x, y;
    float speed = 100;

    boolean dead = false;

    public Enemy(Texture tex, float x, float y) {
        this.tex = tex;
        this.x = x;
        this.y = y;
    }

    public void moveToward(float px, float py, float delta) {
        float dx = px - x;
        float dy = py - y;
        float len = (float)Math.sqrt(dx*dx + dy*dy);

        x += (dx / len) * speed * delta;
        y += (dy / len) * speed * delta;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, tex.getWidth(), tex.getHeight());
    }

    public void draw(SpriteBatch batch) {
        batch.draw(tex, x, y);
    }
}
