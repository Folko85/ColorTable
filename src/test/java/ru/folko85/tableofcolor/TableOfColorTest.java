package ru.folko85.tableofcolor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

public class TableOfColorTest {
   String ymlTest = "en.yml";
    TableOfColor table;

    @Before
    public void setUp() {
        table = new TableOfColor(new Locale("en"));
    }

    @Test
    public void testExtractYml() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<ColorPoint> mustBe = new ArrayList<>();
        mustBe.add(new ColorPoint("PaleTurquoise", "AFEEEE"));
        mustBe.add(new ColorPoint("PaleGreen", "98FB98"));
        mustBe.add(new ColorPoint("SpringGreen", "00FF7F"));
        mustBe.add(new ColorPoint("Crimson", "DC143C"));
        Method method = table.getClass().getDeclaredMethod("extractYml", String.class);
        method.setAccessible(true);
        List<ColorPoint> really = (List<ColorPoint>) method.invoke(table, ymlTest);
        assertEquals(mustBe, really.subList(0,4));
    }

    @Test
    public void testFindNamedColorFromHex() {
        String code = "ab67fa";
        String mustBe = "MediumPurple";
        String really = table.findNamedColorFromHex(code);
        assertEquals(mustBe, really);
    }

    @Test
    public void testFindNamedColorFromRGB() {
        String mustBe = "RoyalBlue";
        String really = table.findNamedColorFromRGB(11, 102, 154);
        assertEquals(mustBe, really);
    }

    @After
    public void tearDown() {

    }
}
