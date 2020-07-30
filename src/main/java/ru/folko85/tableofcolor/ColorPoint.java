package ru.folko85.tableofcolor;

public class ColorPoint implements Comparable<ColorPoint> {
    private static final int rCodeIndex = 0;
    private static final int gCodeIndex = 1;
    private static final int bCodeIndex = 2;
    private String colorName;
    private final String hexCode;
    private final int rValue;
    private final int gValue;
    private final int bValue;
    private final int[] coordinates;

    protected ColorPoint(String colorName, String hexCode) {   // для каждого цвета определим все возможные представления параметров
        this.colorName = colorName;
        this.hexCode = hexCode;
        this.coordinates = coordinateFromHex(hexCode);
        this.rValue = coordinates[rCodeIndex];
        this.gValue = coordinates[gCodeIndex];
        this.bValue = coordinates[bCodeIndex];
    }

    protected ColorPoint(String hexCode) {   // конструктор для безымянных точек для служебных целей
        this.hexCode = hexCode;
        this.coordinates = coordinateFromHex(hexCode);
        this.rValue = coordinates[rCodeIndex];
        this.gValue = coordinates[gCodeIndex];
        this.bValue = coordinates[bCodeIndex];
    }

    protected static int[] coordinateFromHex(String hexCode) {
        String lowerHex = hexCode.toLowerCase();
        int r = Integer.valueOf(lowerHex.substring(0, 2), 16);
        int g = Integer.valueOf(lowerHex.substring(2, 4), 16);
        int b = Integer.valueOf(lowerHex.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    protected String getColorName() {
        return this.colorName;
    }

    protected static double calculateDistance(ColorPoint one, ColorPoint two) {
        return Math.sqrt(Math.pow(one.getRValue() - two.getRValue(), 2) +
                Math.pow(one.getGValue() - two.getGValue(), 2) +
                Math.pow(one.getBValue() - two.getBValue(), 2));
    }

    protected int[] getCoordinates() {
        return coordinates;
    }

    protected int getRValue() {
        return rValue;
    }

    protected int getGValue() {
        return gValue;
    }

    protected int getBValue() {
        return bValue;
    }

    @Override
    public String toString() {
        return this.colorName;
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((ColorPoint) obj) == 0;
    }  // для тестов, чтоб сравнивать цвета

    @Override
    public int compareTo(ColorPoint point) {
        return this.hexCode.compareTo(point.hexCode);
    }
}