package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
    Texture tex;
    public float x, y;
    float speed = 200;

    float shootCooldown = 0;

    public Player(Texture tex, float x, float y) {
        this.tex = tex;
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) x += speed * delta;
    }

    public void shoot(Array<Projectile> bullets, Array<Enemy> enemies) {
        shootCooldown -= Gdx.graphics.getDeltaTime();

        if (shootCooldown <= 0) {
            shootCooldown = 0.5f;

            Enemy nearest = null;
            float minDist = Float.MAX_VALUE;

            for (Enemy e : enemies) {
                float dist = Vector2.dst(x, y, e.x, e.y);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = e;
                }
            }

            if (nearest != null) {
                float dx = nearest.x - x;
                float dy = nearest.y - y;
                float len = (float)Math.sqrt(dx*dx + dy*dy);

                bullets.add(new Projectile(tex, x, y,
                    (dx / len) * 300,
                    (dy / len) * 300));
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(tex, x, y);
    }
}
