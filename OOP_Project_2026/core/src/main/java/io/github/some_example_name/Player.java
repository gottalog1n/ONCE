package io.github.some_example_name;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class Player extends GameObject {
    private final float SPEED = 2f;
    private final float MAX_LIFE = 5f;
    private float life;
    private Vector2 moveDirection = new Vector2();
    private Vector2 lastDirection = new Vector2(1, 0);
    private Viewport viewport;
    private Array<Attack> attacks = new Array<>();
    private Animation<Texture> attackAnimation;
    private Sound attackSound;
    private float attackTimer;

    public Player(float x, float y, Viewport viewport, Texture texture, Animation<Texture> anim, Sound sound) {
        super(x, y, texture.getWidth() / 32f, texture.getHeight() / 32f, texture);
        this.viewport = viewport;
        this.attackAnimation = anim;
        this.attackSound = sound;
        reset(x, y);
    }

    public void reset(float x, float y) {
        rect.setPosition(x, y);
        life = MAX_LIFE;
        attacks.clear();
        attackTimer = 1.6f;
    }

    @Override
    public void update(float delta) {
        // Movement Logic
        if (!moveDirection.isZero()) {
            float newX = rect.x + moveDirection.x * SPEED * delta;
            float newY = rect.y + moveDirection.y * SPEED * delta;
            rect.x = MathUtils.clamp(newX, 0, viewport.getWorldWidth() - rect.width);
            rect.y = MathUtils.clamp(newY, 0, viewport.getWorldHeight() - rect.height);
        }

        // Attack Logic
        attackTimer -= delta;
        if (attackTimer <= 0) {
            attackTimer = 1.6f;
            Vector2 center = getCenter(new Vector2());
            attacks.add(new Attack(center.x, center.y, lastDirection, attackAnimation));
            attackSound.play();
        }

        // Update active attacks
        Iterator<Attack> it = attacks.iterator();
        while (it.hasNext()) {
            Attack a = it.next();
            a.update(delta);
            if (a.isDone()) it.remove();
        }
    }

    public void changeDirection(Vector2 dir) {
        moveDirection.set(dir).nor();
        if (!moveDirection.isZero()) lastDirection.set(moveDirection);
    }

    public void subtractLife(float amount) { life = Math.max(0, life - amount); }
    public float getLife() { return life; }
    public boolean isAlive() { return life > 0; }
    public Array<Attack> getAttacks() { return attacks; }
}
