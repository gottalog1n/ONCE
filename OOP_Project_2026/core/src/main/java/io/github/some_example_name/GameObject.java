package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected Rectangle rect;
    protected Texture texture;

    public GameObject(float x, float y, float width, float height, Texture texture) {
        this.rect = new Rectangle(x, y, width, height);
        this.texture = texture;
    }

    public boolean overlaps(GameObject other) {
        return rect.overlaps(other.rect);
    }

    public void draw(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        }
    }

    public abstract void update(float delta);

    public void drawDebug(ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    protected Vector2 getCenter(Vector2 result) {
        return result.set(rect.x + rect.width / 2, rect.y + rect.height / 2);
    }
}
