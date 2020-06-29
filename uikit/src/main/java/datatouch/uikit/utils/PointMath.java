package datatouch.uikit.utils;

import android.graphics.PointF;

public class PointMath {

    public static float getLength(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static float getDistance(PointF a, PointF b) {
        float x = a.x - b.x;
        float y = a.y - b.y;
        return getLength(x, y);
    }

    public static float getDistance(float ax, float ay, float bx, float by) {
        float x = ax - bx;
        float y = ay - by;
        return getLength(x, y);
    }

    public static PointF sum(PointF a, PointF b) {
        return new PointF(a.x + b.x, a.y + b.y);
    }

    public static void add(PointF dst, PointF src) {
        dst.x += src.x;
        dst.y += src.y;
    }

    public static void sub(PointF dst, PointF src) {
        dst.x -= src.x;
        dst.y -= src.y;
    }

    public static void sub(PointF dst, float srcX, float srcY) {
        dst.x -= srcX;
        dst.y -= srcY;
    }

    public static PointF getMiddle(PointF a, PointF b) {
        PointF res = sum(a, b);
        res.x /= 2;
        res.y /= 2;
        return res;
    }

    public static float getAngleRad(float ax, float ay, float bx, float by) {
        float xd = bx - ax;
        float yd = by - ay;
        return (float) Math.atan2(yd, xd);
    }

    public static PointF getPolarLineEndPoint(float startX, float startY, float lineLen, float angleRad) {
        PointF result = new PointF();
        result.x = startX + lineLen * (float) Math.cos(angleRad);
        result.y = startY + lineLen * (float) Math.sin(angleRad);
        return result;
    }

    public static void getPolarLineEndPoint(float startX, float startY, float lineLen, float angleRad, PointF result) {
        result.x = startX + lineLen * (float) Math.cos(angleRad);
        result.y = startY + lineLen * (float) Math.sin(angleRad);
    }

    public static void getPolarLineEndPoint(PointF start, float lineLen, float angleRad, PointF result) {
        result.x = start.x + lineLen * (float) Math.cos(angleRad);
        result.y = start.y + lineLen * (float) Math.sin(angleRad);
    }
}