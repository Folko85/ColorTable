package ru.folko85.tableofcolor;

import java.util.*;
import java.util.stream.Collectors;

//ToDo
// 2. Реализовать парсинг yml
// 3. Протестировать все методы
// 4. Проверить на реальной программе
// 5. Выделить в библиотеку и выложить на гитхабе
// 6. Запилить статью

public class TableOfColor {
    Locale locale;
    String ymlFile;
    private List<ColorPoint> colors;
    private List<BucketOfColor> buckets = new ArrayList<>();
    private final int[] startPoint = new int[]{0, 0, 0};       // все наши цвета находятся в этом диапазоне
    private final int[] endPoint = new int[]{256, 256, 256};
    static int maxPointsCount = 5;        // захардкодим это во имя наивысшей справедливости

    public TableOfColor(Locale locale) {
        this.locale = locale;
        this.ymlFile = locale.getLanguage() + ".yml";
        this.colors = extractYml(this.ymlFile);
        this.buckets.add(new BucketOfColor(startPoint, endPoint));
        distributePoints(colors);               // распределим все точки по вёдрам
    }

    private List<ColorPoint> extractYml(String ymlFile) {
        return null;                                          // тут мы будем формировать список колорпойнтов
    }

    private void distributePoints(List<ColorPoint> points) {
        points.forEach(point -> {
            BucketOfColor bucket = findBucket(point);  // для каждой точки находим подходящее ведро
            bucket.addColorPoint(point);                          // и добавляем туда точку
        });
    }

    private BucketOfColor findBucket(ColorPoint point) {
        BucketOfColor resultBucket = buckets.stream().filter(bucket -> bucket.isContainPoint(point)).findFirst().get(); // находим ведро для точки
        if (resultBucket.getSize() < maxPointsCount) {
            return resultBucket;
        } else {
            splitBucket(resultBucket);
            return findBucket(point);              // рекурсия иногда бывает полезна
        }
    }

//    private List<ColorPoint> findSearchArea(ColorPoint point) {  // область поиска не всегда равна ведру
//        BucketOfColor resultBucket = buckets.stream().filter(bucket -> bucket.isContainPoint(point)).findFirst().get(); // находим ведро для точки
//        return resultBucket.getBucketPoints();
//    }

    private void splitBucket(BucketOfColor resultBucket) {
        int bestAxis = resultBucket.getBestColorAxis();
        int newBound = resultBucket.getBoundPlane(bestAxis);       // при делении параллерограма плоскостью
        int[] leftBoundCoordinates = resultBucket.getEndCoordinates();// у условно левой части изменится конечная координата
        int[] rightBoundCoordinates = resultBucket.getStartCoordinates(); // у условно правой части начальная координата
        leftBoundCoordinates[bestAxis] = newBound;
        rightBoundCoordinates[bestAxis] = newBound;
        BucketOfColor leftBucket = new BucketOfColor(resultBucket.getStartCoordinates(), leftBoundCoordinates);
        BucketOfColor rightBucket = new BucketOfColor(rightBoundCoordinates, resultBucket.getEndCoordinates());
        List<ColorPoint> reDistributedPoints = resultBucket.getBucketPoints();
        buckets.add(leftBucket);
        buckets.add(rightBucket);      // добавляем новые вёдра
        buckets.remove(resultBucket);   // удаляем старое ведро
        distributePoints(reDistributedPoints);   //перераспределяем точки тем же методом, что и начали распределять их
    }

    public String findNamedColor(String hexCode) {
        ColorPoint targetPoint = new ColorPoint(hexCode);
        BucketOfColor targetBucket = findBucket(targetPoint); // даже если в ходе поиска у нас прибавится вёдер - не страшно
        List<ColorPoint> searchArea = targetBucket.getBucketPoints();
        // строим карту названий - расстояний

        Map.Entry<String, Double> minDistancePoint = getNearestNamedColor(searchArea, targetPoint);// и вычисляем минимальное

        double distanceToSide = getDistanceToBucketSide(targetPoint, targetBucket);  // находим рассояние до ближайшей стороны

        if (distanceToSide > minDistancePoint.getValue()) {  // если именованная точка ближе стороны, то возвращаем её
            return minDistancePoint.getKey();
        } else {
            int[] startBigBucket = new int[3];
            int[] endBigBucket = new int[3];
            for (int i = 0; i < 3; i++) {
                startBigBucket[i] = targetPoint.getCoordinates()[i] - minDistancePoint.getValue().intValue();  // точность? да ну нафиг
                endBigBucket[i] = targetPoint.getCoordinates()[i] + minDistancePoint.getValue().intValue();
            }
            BucketOfColor extendedBucked = new BucketOfColor(startBigBucket, endBigBucket);
            List<ColorPoint> secondArea = colors.stream().filter(extendedBucked::isContainPoint)
                    .filter(p -> !searchArea.contains(p)).collect(Collectors.toList());

            Map.Entry<String, Double> secondPoint = getNearestNamedColor(secondArea, targetPoint);

            return (secondPoint.getValue() < minDistancePoint.getValue()) ? secondPoint.getKey() : minDistancePoint.getKey();
            // без тернарного оператора нам не обойтись
        }
    }

    public static Map.Entry<String, Double> getNearestNamedColor(List<ColorPoint> candidates, ColorPoint targetPoint) {
        Map<String, Double> candidatesMap = candidates.stream()
                .collect(Collectors.toMap(ColorPoint::getColorName, cp -> ColorPoint.calculateDistance(cp, targetPoint)));
        return candidatesMap.entrySet().stream()
                .min(Map.Entry.comparingByValue()).get();
    }

    public static double getDistanceToBucketSide(ColorPoint targetPoint, BucketOfColor targetBucket)  // у параллерограмма 6 сторон
    {
        //расстояние до староны - разность соответствующих координат
        int[] pointCoordinates = targetPoint.getCoordinates();
        int[] startBucketCoordinates = targetBucket.getStartCoordinates();
        int[] endBucketCoordinates = targetBucket.getStartCoordinates();
        int distance = 256;  // выбираем заведомо большое расстояние
        for (int i = 0; i < pointCoordinates.length; i++) {
            if (distance > (pointCoordinates[i] - startBucketCoordinates[i])) { // начальные стороны будут заведомо меньше координат точки
                distance = pointCoordinates[i] - startBucketCoordinates[i];
            }
            if (distance > (endBucketCoordinates[i] - pointCoordinates[i])) { // конечные столроны заведомо больше
                distance = endBucketCoordinates[i] - pointCoordinates[i];
            }
        }
        return distance;
    }
}