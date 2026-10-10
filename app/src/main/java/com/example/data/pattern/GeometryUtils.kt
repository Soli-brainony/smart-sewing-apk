package com.example.data.pattern

// Helper to calculate bounds of pattern points
import com.example.data.model.Point2D

object GeometryUtils {
    fun calculateBounds(points: List<Point2D>): Bounds {
        if (points.isEmpty()) return Bounds(0f, 0f, 10f, 10f)
        var minX = points.first().x
        var maxX = points.first().x
        var minY = points.first().y
        var maxY = points.first().y
        for (p in points) {
            if (p.x < minX) minX = p.x
            if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y
            if (p.y > maxY) maxY = p.y
        }
        return Bounds(minX, minY, maxX - minX, maxY - minY)
    }
}

data class Bounds(val minX: Float, val minY: Float, val width: Float, val height: Float)
