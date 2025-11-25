package moe.cameo.collision;

public class Collision {
    public static boolean intersects(Rect a, Rect b) {
        return  a.left < b.right &&
                a.right > b.left &&
                a.top < b.bottom && 
                a.bottom > b.top;
    }
}
