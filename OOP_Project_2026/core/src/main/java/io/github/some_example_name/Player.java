package io.github.some_example_name;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player extends GameObject {
    private static final float SCALE = 1 / 32f;
    private static final float SPEED = 2f;
    private static final float LIFE = 5f;
    private static final float ATTACK_COOLDOWN = 1.6f;

    private final Vector2 moveDirection = new Vector2(0, 0);
    private final Vector2 lastDirection = new Vector2(1, 0);
    private float life = LIFE;
    private float attackTimer;
    private final Array<Attack> attacks = new Array<>();

    private final Animation<Texture> attackAnimation;
    private final Animation<Texture> idleAnimation;
    private final Animation<Texture> walkWestAnimation;
    private final Animation<Texture> walkEastAnimation;
    private float animTime = 0f;
    private boolean facingWest = false;
    private boolean isMoving = false;

    private final Viewport gameViewport;
    private final Sound attackSfx;

    public Player(float x, float y,
                  Viewport gameViewport,
                  Animation<Texture> idleAnimation,
                  Animation<Texture> walkWestAnimation,
                  Animation<Texture> walkEastAnimation,
                  Animation<Texture> attackAnimation,
                  Sound attackSfx) {
        super(x, y,
            idleAnimation.getKeyFrame(0).getWidth() * SCALE,
            idleAnimation.getKeyFrame(0).getHeight() * SCALE,
            idleAnimation.getKeyFrame(0)
        );
        this.gameViewport = gameViewport;
        this.idleAnimation = idleAnimation;
        this.walkWestAnimation = walkWestAnimation;
        this.walkEastAnimation = walkEastAnimation;
        this.attackAnimation = attackAnimation;
        this.attackSfx = attackSfx;
        reset(x, y);
    }

    public void reset(float x, float y) {
        rect.setPosition(x, y);
        life = LIFE;
        attackTimer = ATTACK_COOLDOWN;
        attacks.clear();
        animTime = 0f;
    }

    @Override
    void update(float deltaTime) {
        animTime += deltaTime;
        isMoving = !moveDirection.isZero();
        if (moveDirection.x < 0) facingWest = true;
        else if (moveDirection.x > 0) facingWest = false;

        if (canAttack(deltaTime)) {
            Vector2 playerCenter = getCenter(TMP_VEC2);
            attackSfx.play();
            attacks.add(new Attack(playerCenter, lastDirection, attackAnimation));
        }

        var iterator = attacks.iterator();
        while (iterator.hasNext()) {
            Attack attack = iterator.next();
            attack.update(deltaTime);
            if (attack.isDone()) iterator.remove();
        }

        move(deltaTime);
    }

    private void move(float deltaTime) {
        if (moveDirection.isZero()) return;

        float newX = rect.getX() + moveDirection.x * SPEED * deltaTime;
        float newY = rect.getY() + moveDirection.y * SPEED * deltaTime;

        // Guard: only clamp if world is actually larger than the player
        float maxX = Math.max(0, gameViewport.getWorldWidth() - rect.getWidth());
        float maxY = Math.max(0, gameViewport.getWorldHeight() - rect.getHeight());

        newX = Math.clamp(newX, 0, maxX);
        newY = Math.clamp(newY, 0, maxY);

        rect.setPosition(newX, newY);
    }

    @Override
    public void draw(Batch batch) {
        Animation<Texture> current;
        if (isMoving) {
            current = facingWest ? walkWestAnimation : walkEastAnimation;
        } else {
            current = idleAnimation;
        }
        Texture frame = current.getKeyFrame(animTime);
        batch.draw(frame, rect.x, rect.y, rect.width, rect.height);
    }

    public void changeDirection(Vector2 direction) {
        if (!direction.isZero()) lastDirection.set(direction);
        moveDirection.set(direction);
    }

    public float getLife() { return life; }
    public void subLife(float amount) { this.life -= amount; }
    public boolean isDead() { return life <= 0f; }
    public Array<Attack> getAttacks() { return attacks; }

    private boolean canAttack(float delta) {
        attackTimer -= delta;
        if (attackTimer <= 0f) {
            attackTimer = ATTACK_COOLDOWN;
            return true;
        }
        return false;
    }
}
