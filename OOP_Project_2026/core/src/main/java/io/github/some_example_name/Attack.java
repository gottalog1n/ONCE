package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Attack extends GameObject {
    // Constants for the attack behavior
    private static final float SIZE = 4f;
    private static final float DURATION = 0.2f;

    private float lifeSpan = DURATION;
    private final Animation<Texture> animation;

    /**
     * @param position The starting center-point of the attack
     * @param direction The vector representing which way the attack is facing/moving
     * @param animation The animation sequence to play
     */
    public Attack(Vector2 position, Vector2 direction, Animation<Texture> animation) {
        // We pass the coordinates directly to GameObject's constructor.
        // Subtracting SIZE / 2 centers the hitbox on the 'position' vector.
        super(position.x - (SIZE / 2), position.y - (SIZE / 2), SIZE, SIZE);

        this.animation = animation;
    }

    @Override
    public void update(float deltaTime) {
        // Countdown the lifespan
        this.lifeSpan -= deltaTime;
    }

    @Override
    public void draw(Batch batch) {
        // Calculate which frame of the animation to show based on remaining life
        float animationDuration = animation.getAnimationDuration();

        // This calculates a 0.0 to 1.0 percentage of completion
        float animationPerc = 1f - (Math.max(0f, lifeSpan) / DURATION);

        // Find the exact time-stamp in the animation
        float stateTime = animationDuration * animationPerc;

        // Update the texture field inherited from GameObject
        texture = animation.getKeyFrame(stateTime, false);

        // Call the parent draw method to render the current texture
        super.draw(batch);
    }

    /**
     * Checks if the attack has finished its duration.
     * @return true if lifeSpan has run out.
     */
    public boolean isDone() {
        return this.lifeSpan <= 0f;
    }
}
