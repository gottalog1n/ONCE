package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Attack extends GameObject {
    private float lifespan = 0.4f;
    private Animation<Texture> anim;
    private float stateTime = 0;

    public Attack(float x, float y, Vector2 dir, Animation<Texture> anim) {
        super(x + dir.x * 0.75f - 0.5f, y + dir.y * 0.75f - 0.5f, 1f, 1f, null);
        this.anim = anim;
    }

    @Override
    public void update(float delta) {
        lifespan -= delta;
        stateTime += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        texture = anim.getKeyFrame(stateTime, true);
        super.draw(batch);
    }

    public boolean isDone() { return lifespan <= 0; }
}
