package cg.render;

/**
 * Created by Hobbit on 5/14/16.
 */
public class Bucket {
    private int width;
    private int height;
    private int x;
    private int y;

    public Bucket(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
