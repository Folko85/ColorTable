package ru.folko85.tableofcolor;

/*
 *  Каждое ведро представляет из себя прямоугольный параллелепипед(кубоид) со сторонами, параллельными осям координат
 * а значит его можно определить двумя крайними точками
 *
 */

import java.util.ArrayList;
import java.util.List;

public class BucketOfColor {
    static final int rCodeIndex = 0;
    static final int gCodeIndex = 1;
    static final int bCodeIndex = 2;
    static final int minRangeIndex = 0;
    static final int maxRangeIndex = 1;
    private int[] startCoordinates;   // размер задан заранее в нашем случае
    private int[] endCoordinates;     // поэтому мы фиксируем ещё и разброс точкее по осям
    private int[] rRange;
    private int[] gRange;
    private int[] bRange;
    private List<ColorPoint> bucketPoints = new ArrayList<>();
    private int size;    // текущий размер


    public BucketOfColor(int[] start, int[] end) {
        this.startCoordinates = start;
        this.endCoordinates = end;
        this.size = 0;           // при создании в ведре нет точек
        rRange[minRangeIndex] = rRange[maxRangeIndex] = (endCoordinates[rCodeIndex] - startCoordinates[rCodeIndex]) / 2;
        gRange[minRangeIndex] = gRange[maxRangeIndex] = (endCoordinates[gCodeIndex] - startCoordinates[gCodeIndex]) / 2;
        bRange[minRangeIndex] = bRange[maxRangeIndex] = (endCoordinates[bCodeIndex] - startCoordinates[bCodeIndex]) / 2;
        //разброс мы считаем для корректного деления ведра, у вновьсозданного ведра нулевой разброс
        //громоздко, но что поделаешь
    }

    public int[] getStartCoordinates() {
        return startCoordinates;
    }

    public int[] getEndCoordinates() {
        return endCoordinates;
    }

    public boolean isContainPoint(ColorPoint colorPoint) {
        int r = colorPoint.getRValue();
        int g = colorPoint.getGValue();
        int b = colorPoint.getBValue();
        if (r >= this.startCoordinates[rCodeIndex] && r <= this.endCoordinates[rCodeIndex] &&
                g >= this.startCoordinates[gCodeIndex] && g <= this.endCoordinates[gCodeIndex] &&
                b >= this.startCoordinates[bCodeIndex] && b <= this.endCoordinates[bCodeIndex]) {
            return true;
        } else return false;
    }

    public int getSize() {
        return this.size;
    }

    public void addColorPoint(ColorPoint point) {
        this.bucketPoints.add(point);        // добавляем точку в список и обновляем диапазон разброса
        if (rRange[minRangeIndex] > point.getRValue()) rRange[minRangeIndex] = point.getRValue();
        if (rRange[maxRangeIndex] < point.getRValue()) rRange[maxRangeIndex] = point.getRValue();
        if (gRange[minRangeIndex] > point.getGValue()) gRange[minRangeIndex] = point.getGValue();
        if (gRange[maxRangeIndex] < point.getGValue()) gRange[maxRangeIndex] = point.getGValue();
        if (bRange[minRangeIndex] > point.getBValue()) bRange[minRangeIndex] = point.getBValue();
        if (bRange[maxRangeIndex] < point.getBValue()) bRange[maxRangeIndex] = point.getBValue();
        // потом упрощу
    }

    public int getBestColorAxis() {
        int rBoundRange = this.rRange[maxRangeIndex] - this.rRange[minRangeIndex];
        int gBoundRange = this.gRange[maxRangeIndex] - this.gRange[minRangeIndex];
        int bBoundRange = this.bRange[maxRangeIndex] - this.bRange[minRangeIndex];
        int result = rCodeIndex;
        if (gBoundRange > rBoundRange) result = gCodeIndex;
        if (bBoundRange > rBoundRange && bBoundRange > gBoundRange) result = bCodeIndex;
        return result;
    }

    public int getBoundPlane(int bestAxis) {
        if(bestAxis == rCodeIndex) return rRange[maxRangeIndex] - ((rRange[maxRangeIndex] - rRange[minRangeIndex])/2);
        if(bestAxis == gCodeIndex) return gRange[maxRangeIndex] - ((gRange[maxRangeIndex] - gRange[minRangeIndex])/2);
        else return bRange[maxRangeIndex] - ((bRange[maxRangeIndex] - bRange[minRangeIndex])/2);
    }

    public List<ColorPoint> getBucketPoints() {
        return bucketPoints;
    }
}