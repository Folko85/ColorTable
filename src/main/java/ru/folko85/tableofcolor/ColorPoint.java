package ru.folko85.tableofcolor;

/**
 * Вспомогательный класс библиотеки - цвет-точка.
 * Каждый объект является подобием точки с координатами R, G, B, определяющими интенсивность соответствующего
 * цвета, вместо стандартных X, Y, Z и с дополнительным полем - названием именованного цвета
 *
 * @version 1.0.6
 * @author Othernik aka Folko85
 */

public class ColorPoint implements Comparable<ColorPoint> {
    /**
     * Все координаты хранятся в массивах, элементы которых соответствуют интенсивности определённого цвета
     */
    private static final int R_CODE_INDEX = 0;
    private static final int G_CODE_INDEX = 1;
    private static final int B_CODE_INDEX = 2;
    /** Поле с названием именованного цвета */
    private String colorName;
    /** Поле с кодом именованого цвета */
    private final String hexCode;
    /** Поля с координатами от 0 до 256 */
    private final int rValue;
    private final int gValue;
    private final int bValue;
    /** массив с теми же координатами */
    private final int[] coordinates;

    /**
     * Конструктор для создания именованной точки
     * @param colorName - имя цвета-точки
     * @param hexCode - шестизначный шестнадцатеричный код цвета-точки
     */
    protected ColorPoint(String colorName, String hexCode) {   // для каждого цвета определим все возможные представления параметров
        this.colorName = colorName;
        this.hexCode = hexCode;
        this.coordinates = coordinateFromHex(hexCode);
        this.rValue = coordinates[R_CODE_INDEX];
        this.gValue = coordinates[G_CODE_INDEX];
        this.bValue = coordinates[B_CODE_INDEX];
    }

    /**
     * Конструктор создания безымянной точки. Применяется для создания точки, к которой ищут ближайшую именованную
     * @param hexCode - шестизначный шестнадцатеричный код цвета-точки
     */
    protected ColorPoint(String hexCode) {   // конструктор для безымянных точек для служебных целей
        this.hexCode = hexCode;
        this.coordinates = coordinateFromHex(hexCode);
        this.rValue = coordinates[R_CODE_INDEX];
        this.gValue = coordinates[G_CODE_INDEX];
        this.bValue = coordinates[B_CODE_INDEX];
    }

    /**
     * Статический метод, преобразующий строку с шестнадцатеричным числом в массив из трёх десятичных
     * @param hexCode - шестизначный шестнадцатеричный код цвета-точки
     * @return - метод возвращает массив из трёх десятичных чисел
     */
    protected static int[] coordinateFromHex(String hexCode) {
        int r = Integer.valueOf(hexCode.substring(0, 2), 16);
        int g = Integer.valueOf(hexCode.substring(2, 4), 16);
        int b = Integer.valueOf(hexCode.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    /**
     * Статический метод, преобразующий десятичные координаты в строку с шестнадцатеричным числом
     * @param r - десятичное число от 0 до 256 характеризующее интенсивность красного цвета
     * @param g - десятичное число от 0 до 256 характеризующее интенсивность зелёного цвета
     * @param b - десятичное число от 0 до 256 характеризующее интенсивность голубого цвета
     * @return - метод возвращает строку с шестизначным шестнадцатеричным числом
     */
    protected static String rgbToHex(int r, int g, int b) {
        return String.format("%02x%02x%02x", r, g, b);
    }

    /**
     * Геттер названия цвета-точки
     * @return - возвращает название
     */
    protected String getColorName() {
        return this.colorName;
    }

    /**
     * Статический метод вычисления расстояния между точками. Принимает на вход
     * @param one - первую точку
     * @param two - вторую точку
     * @return - метод возвращает переменную double с расстоянием между точками
     */
    protected static double calculateDistance(ColorPoint one, ColorPoint two) {
        return Math.sqrt(Math.pow(one.getRValue() - (double) two.getRValue(), 2) +
                Math.pow(one.getGValue() - (double) two.getGValue(), 2) +
                Math.pow(one.getBValue() - (double) two.getBValue(), 2));
    }

    /**
     * Геттер координат
     * @return - возвращает массив с координатами цвета-точки
     */
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
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        return compareTo((ColorPoint) obj) == 0;
    }  // для тестов, чтоб сравнивать цвета

    @Override
    public int compareTo(ColorPoint point) {
        return this.hexCode.compareTo(point.hexCode);
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.hexCode, 16);
    }
}