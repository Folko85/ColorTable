package ru.folko85.tableofcolor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.folko85.tableofcolor.ColorPoint;
import ru.folko85.tableofcolor.TableOfColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

public class TableOfColorTest {
   String ymlTest = "ru.yml";
    TableOfColor table;

    @Before
    public void setUp() {
        table = new TableOfColor(new Locale("en"));
    }

    @Test
    public void testExtractYml() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<ColorPoint> mustBe = new ArrayList<>();
        mustBe.add(new ColorPoint("Aспидно-серый", "2f4f4f"));
        mustBe.add(new ColorPoint("Матовый белый", "f2f3f4"));
        mustBe.add(new ColorPoint("Зелёная лужайка", "7cfc00"));
        mustBe.add(new ColorPoint("Белокурый", "faf0be"));
        Method method = table.getClass().getDeclaredMethod("extractYml", String.class);
        method.setAccessible(true);
        List<ColorPoint> really = (List<ColorPoint>) method.invoke(table, ymlTest);
        assertEquals(mustBe, really);
    }

    @Test
    public void testFindNamedColorFromHex() {
        String code = "ABAB09";
        String mustBe = "DarkGoldenrod";
        String really = table.findNamedColorFromHex(code);
        assertEquals(mustBe, really);
    }

    @After
    public void tearDown() {

    }
}
