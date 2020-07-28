package ru.folko85.tableofcolor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TableOfColor {
    private Locale locale;
    private String ymlFile;
    private List<ColorPoint> colors;
    private List<BucketOfColor> buckets = new ArrayList<>();
    private final int[] startPoint = new int[]{0, 0, 0};       // все наши цвета находятся в этом диапазоне
    private final int[] endPoint = new int[]{256, 256, 256};
    static int maxPointsCount = 5;        // захардкодим это во имя наивысшей справедливости

    public TableOfColor() {            // если язык не указан, то будет русский и цвета не будут сортироваться по вёдрам
        this.locale = new Locale("ru");      // тестим на русской версии
        this.ymlFile = locale.getLanguage() + ".yml";
        this.colors = extractYml(this.ymlFile);
    }

    public TableOfColor(Locale locale) {
        this.locale = locale;
        this.ymlFile = locale.getLanguage() + ".yml";
        this.colors = extractYml(this.ymlFile);
        this.buckets.add(new BucketOfColor(startPoint, endPoint));
        distributePoints(colors);               // распределим все точки по вёдрам
    }

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
            ex.printStackTrace();            // сюда привинтим логгер
        }
        return colorPoints;
    }

    private void distributePoints(List<ColorPoint> points) {
        points.forEach(point -> {
            BucketOfColor bucket = findBucket(point);  // для каждой точки находим подходящее ведро
            bucket.addColorPoint(point);                          // и добавляем туда точку
        });
    }

    private BucketOfColor findBucket(ColorPoint point) {
        BucketOfColor resultBucket = buckets.stream().filter(bucket -> bucket.isContainPoint(point)).findFirst().orElseThrow(); // находим ведро для точки
        if (resultBucket.getSize() < maxPointsCount) {
            return resultBucket;
        } else {
            splitBucket(resultBucket);
            return findBucket(point);              // рекурсия иногда бывает полезна
        }
    }

    private void splitBucket(BucketOfColor resultBucket) {
        int bestAxis = resultBucket.getBestColorAxis();
        int newBound = resultBucket.getBoundPlane(bestAxis);       // при делении параллерограма плоскостью
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

    public String findNamedColorFromHex(String hexCode) {
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
            if (secondArea.size() == 0) {       // если точек в близлежащих вёдрах нет
                return minDistancePoint.getKey();
            }
            Map.Entry<String, Double> secondPoint = getNearestNamedColor(secondArea, targetPoint);

            return (secondPoint.getValue() < minDistancePoint.getValue()) ? secondPoint.getKey() : minDistancePoint.getKey();
            // без тернарного оператора нам не обойтись
        }
    }

    private static Map.Entry<String, Double> getNearestNamedColor(List<ColorPoint> candidates, ColorPoint targetPoint) {
        Map<String, Double> candidatesMap = candidates.stream()
                .collect(Collectors.toMap(ColorPoint::getColorName, cp -> ColorPoint.calculateDistance(cp, targetPoint)));
        return candidatesMap.entrySet().stream()
                .min(Map.Entry.comparingByValue()).orElseThrow();
    }

    private static double getDistanceToBucketSide(ColorPoint targetPoint, BucketOfColor targetBucket)  // у параллерограмма 6 сторон
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
            if (distance > (endBucketCoordinates[i] - pointCoordinates[i])) { // конечные столроны заведомо больше
                distance = endBucketCoordinates[i] - pointCoordinates[i];
            }
        }
        return distance;
    }
}