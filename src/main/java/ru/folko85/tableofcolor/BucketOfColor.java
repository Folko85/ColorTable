package ru.folko85.tableofcolor;

import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс библиотеки - ведро с цветами.
 * Каждое ведро представляет из себя прямоугольный параллелепипед(кубоид) со сторонами,
 * параллельными осям координат, а значит его можно определить двумя крайними точками
 *
 * @author Othernik aka Folko85
 * @version 1.0.6
 */

public class BucketOfColor {
    /**
     * Все координаты хранятся в массивах, элементы которых соответствуют интенсивности определённого цвета
     */
    private static final int R_CODE_INDEX = 0;
    private static final int G_CODE_INDEX = 1;
    private static final int B_CODE_INDEX = 2;
    /**
     * У каждого ведра кроме координат границ есть координаты разброса точек по каждой из осей
     */
    private static final int MIN_RANGE_INDEX = 0;
    private static final int MAX_RANGE_INDEX = 1;
    /**
     * массив с начальной координатой ведра
     */
    private final int[] startCoordinates;   // размер задан заранее в нашем случае
    /**
     * массив с конечной координатой ведра
     */
    private final int[] endCoordinates;     // поэтому мы фиксируем ещё и разброс точке по осям
    /**
     * массив с координатами разброса по каждой из осей
     */
    private int[][] range = new int[2][3];
    /**
     * список всех точек принадлежащих данному ведру
     */
    private List<ColorPoint> bucketPoints = new ArrayList<>();

    /**
     * Конструктор - создание нового ведра. При создании нового ведра в нём нет точек, а координаты
     * начального и конечного разброса находятся ровно в центре параллелепипеда и равны друг другу
     *
     * @param start - начальная координата
     * @param end   - конечная координата
     */
    protected BucketOfColor(int[] start, int[] end) {
        this.startCoordinates = start;
        this.endCoordinates = end;
        for (int i = 0; i < 3; i++) {
            range[MAX_RANGE_INDEX][i] = startCoordinates[i] + (endCoordinates[i] - startCoordinates[i]) / 2;
            range[MIN_RANGE_INDEX][i] = range[MAX_RANGE_INDEX][i];
        }
    }

    /**
     * Геттер начальной координаты
     *
     * @return - возвращает массив с начальной координатой
     */
    protected int[] getStartCoordinates() {
        return startCoordinates;
    }

    /**
     * Геттер конечной координаты
     *
     * @return - возвращает массив с конечной координатой
     */
    protected int[] getEndCoordinates() {
        return endCoordinates;
    }

    /**
     * Метод, проверяющий, принадлежит ли цвет-точка данному ведру.
     *
     * @param colorPoint - точка-цвет, которую нужно проверить
     * @return - метод возвращает true, если координаты точки внутри координат ведра
     */
    protected boolean isContainPoint(ColorPoint colorPoint) {
        int r = colorPoint.getRValue();
        int g = colorPoint.getGValue();
        int b = colorPoint.getBValue();
        return r >= this.startCoordinates[R_CODE_INDEX] && r <= this.endCoordinates[R_CODE_INDEX] &&
                g >= this.startCoordinates[G_CODE_INDEX] && g <= this.endCoordinates[G_CODE_INDEX] &&
                b >= this.startCoordinates[B_CODE_INDEX] && b <= this.endCoordinates[B_CODE_INDEX];
    }

    /**
     * Геттер текущего размера ведра(количества точек в нём)
     *
     * @return - метод возвращает количество цветов-точек в ведре
     */
    protected int getSize() {
        return this.bucketPoints.size();
    }

    /**
     * Метод добавления точки к ведру. Кроме добавления к списку вёдер, метод также изменяет
     * координаты разброса точек по осям
     *
     * @param point - метод принимает на вход добавляемую к ведру цвет-точку
     */
    protected void addColorPoint(ColorPoint point) {
        this.bucketPoints.add(point);        // добавляем точку в список и обновляем диапазон разброса
        for (int i = 0; i < 3; i++) {
            if (range[MIN_RANGE_INDEX][i] > point.getCoordinates()[i])
                range[MIN_RANGE_INDEX][i] = point.getCoordinates()[i];
            if (range[MAX_RANGE_INDEX][i] < point.getCoordinates()[i])
                range[MAX_RANGE_INDEX][i] = point.getCoordinates()[i];
        }
    }

    /**
     * Метод получения лучшей оси для деления ведра. Возвращается ось (R, G или B) с наибольшим
     * диапазоном разброса точек
     *
     * @return метод возвращает 0, 1 или 2 соответствующее индексу координаты ведра,
     * по которому будет проходить его деление
     */
    protected int getBestColorAxis() {
        int boundRange = R_CODE_INDEX;
        for (int i = 1; i < 3; i++) {
            if ((this.range[MAX_RANGE_INDEX][i] - this.range[MIN_RANGE_INDEX][i]) > (this.range[MAX_RANGE_INDEX][boundRange] - this.range[MIN_RANGE_INDEX][boundRange])) {
                boundRange = i;
            }
        }
        return boundRange;
    }

    /**
     * Метод вычисляет координату соответствующей оси, по которой лучше всего произвести деление ведра
     *
     * @param bestAxis - на входе у нас индекс оси (0, 1 или 2) по которой мы будем делить ведро
     * @return - на выходе число от 0 до 256, являющееся серединой диапазона разброса цветов-точек в ведре
     */
    protected int getBoundPlane(int bestAxis) {
        return range[MAX_RANGE_INDEX][bestAxis] - ((range[MAX_RANGE_INDEX][bestAxis] - range[MIN_RANGE_INDEX][bestAxis]) / 2);
    }

    /**
     * Геттер списка точек, принадлежащих ведру
     *
     * @return - возвразает список точек ведра
     */
    protected List<ColorPoint> getBucketPoints() {
        return bucketPoints;
    }
}