import org.joml.Vector2i;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

enum GemType {
    GEM_GREEN(0),
    GEM_RED(1),
    GEM_YELLOW(2),
    GEM_BLUE(3),
    GEM_SKULL(4),
    GEM_EXP(5),
    GEM_GOLD(6);

    private final int columnIndex;

    GemType(int columnIndex) {
        this.columnIndex = columnIndex;
    }
    public int getColumnIndex() {
        return columnIndex;
    }
}

public class Gem {
    public GemType gemType;
    public BufferedImage sprite;

    public Vector2i coord;
    public Vector2i pos;
    public float alpha;

    public boolean isFadingOut;
    public boolean isSwapping;
    public boolean isMoving;

    public GemAnimation fadeoutAnim;
    public GemAnimation swapAnim;
    public GemAnimation movingDownAnim;

    public Vector2i start;
    public Vector2i end;

    public int fromY;
    public int toY;
    private Gem parent;

    int randX;
    int randY;
    public boolean unscramble;

    Gem() {
        this.alpha = 1f;
        this.coord = new Vector2i();
        this.pos = new Vector2i();
        this.fadeoutAnim = new GemAnimation();
        this.swapAnim = new GemAnimation();
        this.movingDownAnim = new GemAnimation();
        Random rand = new Random();
        this.randX =  MathUtils.randInt(-10, 10);
        this.randY = MathUtils.randInt(10, 90);
    }

    public boolean isEqual(Gem parent) {
        if (parent.gemType == null || this.gemType == null) {
            return false;
        }

        return this.gemType == parent.gemType;
    }

    public void setCoord(int x, int y) {
        this.coord = new Vector2i(x, y);
    }

    public void setPosition(Vector2i position) {
        this.pos = position;
    }

    public void setType(GemType gemType) {
        this.gemType = gemType;
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    public void moveDown(int toY, boolean unscramble, double speed) {
        this.fromY = pos.y;
        this.toY = toY;
        this.movingDownAnim = new GemAnimation(0, speed);
        this.isMoving = true;
        this.unscramble = unscramble;
    }

    public void move(Gem parent) {
        this.start = new Vector2i(pos);
        this.end = new Vector2i(parent.pos);
        this.parent = parent;
        this.swapAnim = new GemAnimation(0, 0.4f);
        this.isSwapping = true;
    }

    public void fadeOut() {
        this.fadeoutAnim = new GemAnimation(0, 0.2f);
        this.isFadingOut = true;
    }

    public boolean isFadeOutComplete() {
        return this.fadeoutAnim.time >= this.fadeoutAnim.total && !this.isFadingOut;
    }

    public boolean hasSwapped() {
        return !(this.swapAnim.time < this.swapAnim.total);
    }

    public boolean hasMovedDown() {
        return this.movingDownAnim.time >= this.movingDownAnim.total && !isMoving;
    }

    public double updateFade(double dt, double animTime, double animDuration) {
        if (animTime < 0f) {
            return animTime;
        }

        animTime += dt;

        if (isMoving) {
            if (animTime >= animDuration) {
                animTime = animDuration;
                isMoving = false;
            } else {
                if (unscramble) {
                    pos.x += (int) (randX * 0.5);
                }
                pos.y += (int) (animTime * 60 / animDuration);
                if (pos.y >= toY) {
                    pos.y = toY;
                }
            }
        } else if (isSwapping) {
            if (animTime >= animDuration) {
                animTime = animDuration;
                isSwapping = false;
            }

            int dx = Math.abs(coord.x - parent.coord.x);
            int dy = Math.abs(coord.y - parent.coord.y);
            if (dx == 1) {
                pos.x = (int) MathUtils.lerp(this.start.x, this.end.x, animTime / animDuration);
            }
            if (dy == 1) {

                pos.y = (int) MathUtils.lerp(this.start.y, this.end.y, animTime / animDuration);
            }
        } else if (isFadingOut) {
            if (animTime >= animDuration) {
                animTime = animDuration;
                isFadingOut = false;
            }
            alpha = (float) Math.clamp(1f - animTime / animDuration, 0f, 1f);
        }

        return animTime;
    }

    public void update(double dt) {
        if (this.isMoving) {
            this.movingDownAnim.time = this.updateFade(dt, this.movingDownAnim.time, this.movingDownAnim.total);
        } else if (this.isFadingOut) {
            this.fadeoutAnim.time = this.updateFade(dt, this.fadeoutAnim.time, this.fadeoutAnim.total);
        } else if (isSwapping) {
            this.swapAnim.time = this.updateFade(dt, this.swapAnim.time, this.swapAnim.total);
        }
    }

    public void draw(Graphics g) {
        if (this.sprite != null) {
            int w = this.sprite.getWidth();
            int h = this.sprite.getHeight();
            Graphics2D g2d = (Graphics2D) g;

            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));
            g2d.drawImage(this.sprite, Grid.offset.x + this.pos.x, Grid.offset.y + this.pos.y, w, h, null);
            g2d.setComposite(originalComposite);
        }
    }
}
