package assignments.ex2;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

public class Ex2Test {

    @Test
    public void testFirstone() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals(5, sheet.width());
        assertEquals(5, sheet.height());
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(x, y));
            }
        }
    }

    @Test
    public void testSetAndGet() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "20");
        assertEquals("10", sheet.value(0, 0));
        assertEquals("20", sheet.value(1, 1));
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(2, 2));
    }

    @Test
    public void testGetByCoordinates() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "20");
        assertEquals("10", sheet.get("A1").toString());
        assertEquals("20", sheet.get("B2").toString());
        assertNull(sheet.get("Invalid"));
    }

    @Test
    public void testerMath() {
        Ex2Sheet sheet = new Ex2Sheet();
        assertEquals("25.0", sheet.computer("=5*5"));
        assertEquals("-25.0", sheet.computer("=-5*5"));
        assertEquals("33.0", sheet.computer("=(5*5)+(3+5)"));
        assertEquals("34.0", sheet.computer("=((5/5)+(4*7+5))"));
        assertEquals("ERROR_FORM", sheet.computer("=5/0"));
    }

    @Test
    public void testCircularDependency() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=A1");
        sheet.eval();
        assertEquals("Circular Dependency", sheet.value(0, 0));
    }

    @Test
    public void testDepth() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "6");
        sheet.set(0, 1, "=A1+7");
        sheet.set(0, 2, "=B1+5");
        int[][] depths = sheet.depth();
        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[0][1]);
        assertEquals(1, depths[0][2]);
    }

    @Test
    public void testEval() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "15");
        sheet.set(0, 1, "=A1+5");
        sheet.set(0, 2, "=B1*2");
        sheet.eval();
        assertEquals("10", sheet.value(0, 0));
        assertEquals("15.0", sheet.value(0, 1));
        assertEquals("30.0", sheet.value(0, 2));
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "8");
        sheet.set(1, 0, "6");
        sheet.set(0, 1, "=A1+9");
        sheet.set(0, 2, "=B1*2");
        sheet.eval();

        String fileName = "test_sheet.txt";
        sheet.save(fileName);

        Ex2Sheet loadedSheet = new Ex2Sheet(3, 3);
        loadedSheet.load(fileName);
        loadedSheet.eval();

        assertEquals("8", loadedSheet.value(0, 0));
        assertEquals("17.0", loadedSheet.value(0, 1));
        assertEquals("12.0", loadedSheet.value(0, 2));

        new File(fileName).delete();
    }

    @Test
    public void testNonFormulaValues() {
        Ex2Sheet sheet = new Ex2Sheet();
        assertEquals("111122223333", sheet.computer("111122223333"));
        assertEquals("2345678", sheet.computer("2345678"));
        assertEquals("ABCDEFG", sheet.computer("ABCDEFG"));
        assertEquals("abcdefg", sheet.computer("abcdefg"));
    }

    @Test
    public void testInvalidFormulas() {
        Ex2Sheet sheet = new Ex2Sheet();
        assertEquals("ERROR_FORM", sheet.computer("=5*"));
        assertEquals("ERROR_FORM", sheet.computer("=5*abc"));
        assertEquals("ERROR_FORM", sheet.computer("=5*Z100"));
    }

    @Test
    public void testCellEntry1() {
        CellEntry cell = new CellEntry();
        cell.Index2D_Impl("B9");
        assertEquals(1, cell.getX());
        assertEquals(9, cell.getY());
        assertTrue(cell.isValid());
        assertEquals("B9", cell.toString());
    }

    @Test
    public void testCellEntry2() {
        CellEntry cell = new CellEntry();
        cell.Index2D_Impl(2, 8);
        assertEquals(2, cell.getX());
        assertEquals(8, cell.getY());
        assertTrue(cell.isValid());
        assertEquals("C8", cell.toString());
    }

    @Test
    public void testCellEntry3() {
        CellEntry cell = new CellEntry();
        cell.Index2D_Impl(26, 100);
        assertEquals(26, cell.getX());
        assertEquals(100, cell.getY());
        assertFalse(cell.isValid());
        assertEquals("", cell.toString());
    }

    @Test
    public void testSCell() {
        SCell cell = new SCell("10");
        cell.setinfo("=A1+B1");
        assertEquals(Ex2Utils.FORM, cell.getType());
        assertEquals("=A1+B1", cell.getinfo());

        cell.setinfo("Invalid Formula");
        assertEquals(Ex2Utils.TEXT, cell.getType());
        assertEquals("Invalid Formula", cell.getinfo());
    }
}