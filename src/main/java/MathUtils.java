public class MathUtils {
    public static double lerp(float v0, float v1, double t) {
        t = easeInOutCubic(t);
        return (1 - t) * v0 + t * v1;
    }
    public static double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }
}
