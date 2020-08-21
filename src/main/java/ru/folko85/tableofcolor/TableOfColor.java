package ru.folko85.tableofcolor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Основной класс библиотеки - таблица именованных цветов.
 * При создании таблицы именованные цвета извлекаются из yml-файла и раскладываются по вёдрам
 * @author Othernik aka Folko85
 * @version 1.0.6
 */

public class TableOfColor {
    /** Поле - имя  файла */
    private final String ymlFile;
    /** Поле - список точек-цветов */
    private final List<ColorPoint> colors;
    /** Поле - список вёдер */
    private final List<BucketOfColor> buckets = new ArrayList<>();
    /** Поле - начальная граничная точка рабочей области */
    private int[] startPoint = new int[]{0, 0, 0};       // все наши цвета находятся в этом диапазоне
    /** Поле - конечная граничная точка рабочей области */
    private int[] endPoint = new int[]{256, 256, 256};
    /** Поле - максимальное количество точек-цветов в ведре  */
    private static int maxPointsCount = 16;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     * @param locale - в зависимости от языка создаваемой таблицы из соответствующего файла загружается
     *               список точек-цветов и распределяется по вёдрам
     */
    public TableOfColor(Locale locale) {
        this.ymlFile = locale.getLanguage() + ".yml";
        this.colors = extractYml(this.ymlFile);
        this.buckets.add(new BucketOfColor(startPoint, endPoint));
        distributePoints(colors);               // распределим все точки по вёдрам
    }

    /**
     * Метод извлечения точек из yml-файла
     * @param ymlFile - имя файла, зависящее от языка создаваемой таблицы
     * @return - возвращает список всех точек-цветов, извлечённых из файла
     */
    private List<ColorPoint> extractYml(String ymlFile) {  // так криво, потому что некогда разбираться в парсерах ради простенькой операции
        ClassLoader classLoader = this.getClass().getClassLoader();
        List<ColorPoint> colorPoints = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(ymlFile)))) {
            String colorLine;
            List<String> lines = new ArrayList<>();
            while ((colorLine = reader.readLine()) != null) {
                lines.add(colorLine);
            }
            lines.remove(1);
            lines.remove(0);
            colorPoints = lines.stream().map(line -> {
                String[] map = line.split(":");
                return new ColorPoint(map[0].trim(), map[1].trim());
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();            // сюда привинтим логгер, но позже
        }
        return colorPoints;
    }

    /**
     * Метод распределяет все именованные цвета по вёдрам
     * @param points - список всех цветов-точек, полученных из yml-файла
     */
    private void distributePoints(List<ColorPoint> points) {
        points.forEach(point -> {
            BucketOfColor bucket = findBucket(point);  // для каждой точки находим подходящее ведро
            bucket.addColorPoint(point);                          // и добавляем туда точку
        });
    }

    /**
     * Метод поиска подходящего ведра для точки-цвета. При переполнении ведра вызывается метод splitBucket, делящий
     * ведро на две части, а затем метод рекурсивно вызывает сам себя.
     * @param point - точка-цвет, которую нужно положить в ведро. Метод выполняется для каждого именованного цвета
     * @return - метод возвращает ведро, в границах которого находится наша точка-цвет
     */
    private BucketOfColor findBucket(ColorPoint point) {
        BucketOfColor resultBucket = buckets.stream()
                .filter(bucket -> bucket.isContainPoint(point))
                .findFirst().orElseThrow(); // находим ведро для точки
        if (resultBucket.getSize() < maxPointsCount) {
            return resultBucket;
        } else {
            splitBucket(resultBucket);
            return findBucket(point);              // рекурсия иногда бывает полезна
        }
    }

    /**
     * Метод разделения вёдер при переполнении.
     * @param resultBucket - на вход принимается полное ведро, которое нужно разделить.
     *                     В ходе работы метода ведро делится надвое по координате с самым большим разбросом
     *                     точек. Новые вёдра добавляются в список вёдер, а старое удаляется из списка.
     *                     Точки распределяются между вёдрами рекурсивным вызовом метода distributePoints
     */
    private void splitBucket(BucketOfColor resultBucket) {
        int bestAxis = resultBucket.getBestColorAxis();
        int newBound = resultBucket.getBoundPlane(bestAxis);       // при делении параллелограмма плоскостью
        int[] leftBoundCoordinates = new int[3];
        // у условно левой части изменится конечная координата
        int[] rightBoundCoordinates = new int[3];
        resultBucket.getStartCoordinates(); // у условно правой части начальная координата
        for (int j = 0; j < 3; j++) {
            leftBoundCoordinates[j] = resultBucket.getEndCoordinates()[j];
            rightBoundCoordinates[j] = resultBucket.getStartCoordinates()[j];
        }
        leftBoundCoordinates[bestAxis] = newBound;
        rightBoundCoordinates[bestAxis] = newBound + 1;
        BucketOfColor leftBucket = new BucketOfColor(resultBucket.getStartCoordinates(), leftBoundCoordinates);
        BucketOfColor rightBucket = new BucketOfColor(rightBoundCoordinates, resultBucket.getEndCoordinates());
        List<ColorPoint> reDistributedPoints = resultBucket.getBucketPoints();
        buckets.add(leftBucket);
        buckets.add(rightBucket);      // добавляем новые вёдра
        buckets.remove(resultBucket);   // удаляем старое ведро
        distributePoints(reDistributedPoints);   //перераспределяем точки тем же методом, что и начали распределять их
    }

    /**
     * Основной метод библиотеки - поиск ближайшего именованного цвета
     * @param hexCode - на вход метод принимает шестизначный шестнадцатеричный код
     * @return - метод возвращает название ближайшего именованного цвета.
     */
    public String findNamedColorFromHex(String hexCode) {
        ColorPoint targetPoint = new ColorPoint(hexCode);
        BucketOfColor targetBucket = findBucket(targetPoint); // даже если в ходе поиска у нас прибавится вёдер - не страшно
        List<ColorPoint> searchArea = targetBucket.getBucketPoints();
        // строим карту названий - расстояний

        Map.Entry<String, Double> minDistancePoint = getNearestNamedColor(searchArea, targetPoint);// и вычисляем минимальное

        double distanceToSide = getDistanceToBucketSide(targetPoint, targetBucket);  // находим расстояние до ближайшей стороны

        if (distanceToSide > minDistancePoint.getValue()) {  // если именованная точка ближе стороны, то возвращаем её
            return minDistancePoint.getKey();
        } else {
            int[] startBigBucket = new int[3];
            int[] endBigBucket = new int[3];
            for (int i = 0; i < 3; i++) {
                startBigBucket[i] = targetPoint.getCoordinates()[i] - minDistancePoint.getValue().intValue();  // точность? ну уж нет
                endBigBucket[i] = targetPoint.getCoordinates()[i] + minDistancePoint.getValue().intValue();
            }
            BucketOfColor extendedBucked = new BucketOfColor(startBigBucket, endBigBucket);
            List<ColorPoint> secondArea = colors.stream().filter(extendedBucked::isContainPoint)
                    .filter(p -> !searchArea.contains(p)).collect(Collectors.toList());
            if (secondArea.isEmpty()) {       // если точек в близлежащих вёдрах нет
                return minDistancePoint.getKey();
            }
            Map.Entry<String, Double> secondPoint = getNearestNamedColor(secondArea, targetPoint);
            return (secondPoint.getValue() < minDistancePoint.getValue()) ? secondPoint.getKey() : minDistancePoint.getKey();
            // без тернарного оператора нам не обойтись
        }
    }

    /**
     * Метод повторяет предыдущий и отличается лишь входными параметрами, являющимися RGB-кодом
     * @param r - десятичное число от 0 до 256 характеризующее интенсивность красного цвета
     * @param g - десятичное число от 0 до 256 характеризующее интенсивность зелёного цвета
     * @param b - десятичное число от 0 до 256 характеризующее интенсивность голубого цвета
     * @return - метод возвращает название ближайшего именованного цвета
     */
    public String findNamedColorFromRGB(int r, int g, int b) {
        String hexCode = ColorPoint.rgbToHex(r, g, b);
        return findNamedColorFromHex(hexCode);
    }

    /**
     *  Вспомогательный метод используемый для поиска ближайшего именованного цвета. Метод принимает на вход
     * @param candidates - список цветов-точек, являющихся ближайшими к введённому цвету-точке
     * @param targetPoint - цвет-точку, созданную на основе переданного пользователем цветового кода
     * @return - метод возвращает пару ключ-значение с координатами и именем ближайшего цвета-точки
     */
    private static Map.Entry<String, Double> getNearestNamedColor(List<ColorPoint> candidates, ColorPoint targetPoint) {
        Map<String, Double> candidatesMap = candidates.stream()
                .collect(Collectors.toMap(ColorPoint::getColorName, cp -> ColorPoint.calculateDistance(cp, targetPoint)));
        return candidatesMap.entrySet().stream()
                .min(Map.Entry.comparingByValue()).orElseThrow();
    }

    /**
     * Вспомогательный метод для поиска расстояния от цвета-точки до границы ведра
     * @param targetPoint - цвет-точка, созданная на основе переданного пользователем цветового кода
     * @param targetBucket - ведро, в котором находится данная точка
     * @return - метод возвращает переменную типа double с минимальным расстоянием до границы ведра
     */
    private static double getDistanceToBucketSide(ColorPoint targetPoint, BucketOfColor targetBucket)  // у параллелограмма 6 сторон
    {
        //расстояние до стороны - разность соответствующих координат
        int[] pointCoordinates = targetPoint.getCoordinates();
        int[] startBucketCoordinates = targetBucket.getStartCoordinates();
        int[] endBucketCoordinates = targetBucket.getStartCoordinates();
        int distance = 256;  // выбираем заведомо большое расстояние
        for (int i = 0; i < pointCoordinates.length; i++) {
            if (distance > (pointCoordinates[i] - startBucketCoordinates[i])) { // начальные стороны будут заведомо меньше координат точки
                distance = pointCoordinates[i] - startBucketCoordinates[i];
            }
            if (distance > (endBucketCoordinates[i] - pointCoordinates[i])) { // конечные стороны заведомо больше
                distance = endBucketCoordinates[i] - pointCoordinates[i];
            }
        }
        return distance;
    }
}