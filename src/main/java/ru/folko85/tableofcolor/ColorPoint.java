package ru.folko85.tableofcolor;

public class ColorPoint {
    private static final int rCodeIndex = 0;
    private static final int gCodeIndex = 1;
    private static final int bCodeIndex = 2;
    String colorName;
    String hexCode;
    private int rValue;
    private int gValue;
    private int bValue;
    private int[] coordinates;

    public ColorPoint(String colorName, String hexCode) {   // для каждого цвета определим все возможные представления параметров
        this.colorName = colorName;
        this.hexCode = hexCode;
        this.coordinates = coordinateFromHex(hexCode);
        this.rValue = coordinates[rCodeIndex];
        this.gValue = coordinates[gCodeIndex];
        this.bValue = coordinates[bCodeIndex];
    }

    public ColorPoint(String colorName, int[] coordinates) {
        this.colorName = colorName;
        this.hexCode = hexFromCoordinates(coordinates);
        this.coordinates = coordinates;
        this.rValue = coordinates[rCodeIndex];
        this.gValue = coordinates[gCodeIndex];
        this.bValue = coordinates[bCodeIndex];
    }

    public ColorPoint(String colorName, int rValue, int gValue, int bValue) {
        this.colorName = colorName;
        this.rValue = rValue;
        this.gValue = gValue;
        this.bValue = bValue;
        this.coordinates = new int[]{rValue, gValue, bValue};
        this.hexCode = hexFromRGB(rValue, gValue, bValue);
    }

    public ColorPoint(String hexCode) {   // конструктор для безымянных точек для служебных целей
        this.hexCode = hexCode;
        this.coordinates = coordinateFromHex(hexCode);
        this.rValue = coordinates[rCodeIndex];
        this.gValue = coordinates[gCodeIndex];
        this.bValue = coordinates[bCodeIndex];
    }

    private String hexFromCoordinates(int[] coordinates) {
        int r = coordinates[rCodeIndex];
        int g = coordinates[gCodeIndex];
        int b = coordinates[bCodeIndex];
        return String.format("%02x%02x%02x", r, g, b);
    }

    public static int[] coordinateFromHex(String hexCode) {
        int r = Integer.valueOf(hexCode.substring(0, 2), 16);
        int g = Integer.valueOf(hexCode.substring(2, 4), 16);
        int b = Integer.valueOf(hexCode.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    private static String hexFromRGB(int rValue, int gValue, int bValue) {
        return String.format("%02x%02x%02x", rValue, gValue, bValue);
    }

    public String getColorName() {
        return this.colorName;
    }

    public static double calculateDistance(ColorPoint one, ColorPoint two) {
        return Math.sqrt(Math.pow(one.getRValue() - two.getRValue(), 2) +
               Math.pow(one.getGValue() - two.getGValue(), 2) +
               Math.pow(one.getBValue() - two.getBValue(), 2));
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public int getRValue() {
        return rValue;
    }

    public int getGValue() {
        return gValue;
    }

    public int getBValue() {
        return bValue;
    }
}