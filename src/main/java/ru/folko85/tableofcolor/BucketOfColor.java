package ru.folko85.tableofcolor;

/*
 *  Каждое ведро представляет из себя прямоугольный параллелепипед(кубоид) со сторонами, параллельными осям координат
 * а значит его можно определить двумя крайними точками
 *
 */

import java.util.ArrayList;
import java.util.List;

public class BucketOfColor {
    private static final int rCodeIndex = 0;
    private static final int gCodeIndex = 1;
    private static final int bCodeIndex = 2;
    private static final int minRangeIndex = 0;
    private static final int maxRangeIndex = 1;
    private int[] startCoordinates;   // размер задан заранее в нашем случае
    private int[] endCoordinates;     // поэтому мы фиксируем ещё и разброс точкее по осям
    private int[] rRange = new int[2];
    private int[] gRange = new int[2];
    private int[] bRange = new int[2];
    private List<ColorPoint> bucketPoints = new ArrayList<>();
    private int size;    // текущий размер


    protected BucketOfColor(int[] start, int[] end) {
        this.startCoordinates = start;
        this.endCoordinates = end;
        this.size = 0;           // при создании в ведре нет точек
        rRange[maxRangeIndex] = startCoordinates[rCodeIndex] + (endCoordinates[rCodeIndex] - startCoordinates[rCodeIndex]) / 2;
        gRange[maxRangeIndex] = startCoordinates[gCodeIndex] + (endCoordinates[gCodeIndex] - startCoordinates[gCodeIndex]) / 2;
        bRange[maxRangeIndex] = startCoordinates[bCodeIndex] + (endCoordinates[bCodeIndex] - startCoordinates[bCodeIndex]) / 2;
        rRange[minRangeIndex] = rRange[maxRangeIndex];
        gRange[minRangeIndex] = gRange[maxRangeIndex];
        bRange[minRangeIndex] = bRange[maxRangeIndex];
    }

    protected int[] getStartCoordinates() {
        return startCoordinates;
    }

    protected int[] getEndCoordinates() {
        return endCoordinates;
    }

    protected boolean isContainPoint(ColorPoint colorPoint) {
        int r = colorPoint.getRValue();
        int g = colorPoint.getGValue();
        int b = colorPoint.getBValue();
        return r >= this.startCoordinates[rCodeIndex] && r <= this.endCoordinates[rCodeIndex] &&
                g >= this.startCoordinates[gCodeIndex] && g <= this.endCoordinates[gCodeIndex] &&
                b >= this.startCoordinates[bCodeIndex] && b <= this.endCoordinates[bCodeIndex];
    }

    protected int getSize() {
        return this.size;
    }

    protected void addColorPoint(ColorPoint point) {
        this.bucketPoints.add(point);        // добавляем точку в список и обновляем диапазон разброса
        if (rRange[minRangeIndex] > point.getRValue()) rRange[minRangeIndex] = point.getRValue();
        if (rRange[maxRangeIndex] < point.getRValue()) rRange[maxRangeIndex] = point.getRValue();
        if (gRange[minRangeIndex] > point.getGValue()) gRange[minRangeIndex] = point.getGValue();
        if (gRange[maxRangeIndex] < point.getGValue()) gRange[maxRangeIndex] = point.getGValue();
        if (bRange[minRangeIndex] > point.getBValue()) bRange[minRangeIndex] = point.getBValue();
        if (bRange[maxRangeIndex] < point.getBValue()) bRange[maxRangeIndex] = point.getBValue();
        this.size++;
    }

    protected int getBestColorAxis() {
        int rBoundRange = this.rRange[maxRangeIndex] - this.rRange[minRangeIndex];
        int gBoundRange = this.gRange[maxRangeIndex] - this.gRange[minRangeIndex];
        int bBoundRange = this.bRange[maxRangeIndex] - this.bRange[minRangeIndex];
        int result = rCodeIndex;
        if (gBoundRange > rBoundRange) result = gCodeIndex;
        if (bBoundRange > rBoundRange && bBoundRange > gBoundRange) result = bCodeIndex;
        return result;
    }

    protected int getBoundPlane(int bestAxis) {
        if(bestAxis == rCodeIndex) return rRange[maxRangeIndex] - ((rRange[maxRangeIndex] - rRange[minRangeIndex])/2);
        if(bestAxis == gCodeIndex) return gRange[maxRangeIndex] - ((gRange[maxRangeIndex] - gRange[minRangeIndex])/2);
        else return bRange[maxRangeIndex] - ((bRange[maxRangeIndex] - bRange[minRangeIndex])/2);
    }

    protected List<ColorPoint> getBucketPoints() {
        return bucketPoints;
    }
}