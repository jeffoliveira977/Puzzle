import org.joml.Vector2i;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Component {
    public Vector2i position;
    public Vector2i size;
    protected boolean isInside;
    protected boolean isPressed;
    public boolean disable;

    public interface ComponentCallback {
        void onItemClicked();
    }

    private ComponentCallback callback;

    Component(Vector2i position) {
        this.position = position;
        this.size = new Vector2i();
    }

    Component(Vector2i position, Vector2i size) {
        this.position = position;
        this.size = size;
    }

    protected boolean contains(Point p) {
        return p.x >= this.position.x && p.x <= this.position.x + size.x &&
                p.y >= this.position.y && p.y <= this.position.y + size.y;
    }

    public void mouseEvent(MouseHandler handler) {
        if(this.disable) return;

        MouseEvent e = handler.event;

        switch (handler.type) {
            case MOUSE_PRESSED:
                if (contains(e.getPoint())) {
                    isPressed = true;
                }
                break;
            case MOUSE_RELEASED:
                if (isPressed && contains(e.getPoint())) {
                    onClick();
                } else {
                    isInside = false;
                }
                isPressed = false;
                break;
            case MOUSE_MOVED:
                this.isInside = contains(e.getPoint());
                break;
            default:
        }
    }

    public void update (double dt){}
    public void render(Graphics g) {}

    public void onClick() {
        if (callback != null) {
            callback.onItemClicked();
        }
    }

    public void setCallback(ComponentCallback callback) {
        this.callback = callback;
    }
}