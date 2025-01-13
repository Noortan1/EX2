package assignments.ex2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    private Set<Pair<Integer, Integer>> first = new HashSet<>();

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;

        Cell c = get(x, y);
        if (c != null) {
            ans = c.toString();
        }
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        if (cords == null || cords.isEmpty()) {
            return null;
        }
        String colPart = cords.substring(0, 1).toUpperCase();
        String rowPart = cords.substring(1);
        int x = colPart.charAt(0) - 'A';
        int y;
        try {
            y = Integer.parseInt(rowPart) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (isIn(x, y)) {
            return table[x][y];
        }
        return null;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
    }

    @Override
    public void eval() {
        int[][] depths = depth();
        List<CellWithDepth> cells = new ArrayList<>();
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                eval(x, y);
                cells.add(new CellWithDepth(x, y, depths[x][y]));
            }
        }
        cells.sort(Comparator.comparingInt(c -> c.depth));
        for (CellWithDepth c : cells) {
            if (depths[c.x][c.y] != Integer.MAX_VALUE) {
                String evaluatedValue = eval(c.x, c.y);
                set(c.x, c.y, evaluatedValue);
            } else {
                set(c.x, c.y, "Circular Dependency");
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && xx < width() && yy >= 0 && yy < height();
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = calculateDepth(x, y, new boolean[width()][height()]);
            }
        }
        return depths;
    }

    @Override
    public void save(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("I2CS ArielU: SpreadSheet (Ex2) assignment\n");
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                SCell cell = (SCell) table[x][y];
                String cellInfo = cell.getinfo();
                if (cellInfo != null && !cellInfo.isEmpty()) {
                    content.append(x).append(",").append(y).append(",").append(cellInfo).append("\n");
                }
            }
        }
        FileWriter writer = new FileWriter(new File(fileName));
        writer.write(content.toString());
        writer.close();
    }

    @Override
    public void load(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                table[x][y].setinfo("");
            }
        }
        for (String line : lines) {
            if (line.contains("I2CS ArielU")) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                try {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    String value = parts[2];
                    if (isIn(x, y)) {
                        table[x][y].setinfo(value);
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
    }

    private int calculateDepth(int x, int y, boolean[][] CICK) {
        if (CICK == null || x < 0 || x >= CICK.length || y < 0 || y >= CICK[0].length) {
            return Integer.MAX_VALUE;
        }

        if (CICK[x][y]) {
            return Integer.MAX_VALUE;
        }

        CICK[x][y] = true;

        String COUNT = value(x, y);

        if (COUNT.startsWith("=")) {
            String BITUI = COUNT.substring(1).replaceAll(" ", "");
            List<String> sour = cellsour(BITUI);
            int maxDepth = 0;

            for (String ref : sour) {
                int[] co = Taeim(ref);
                if (co == null || !isIn(co[0], co[1])) {
                    return Integer.MAX_VALUE;
                }

                int depDepth = calculateDepth(co[0], co[1], CICK);
                if (depDepth == Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }

                if (depDepth > maxDepth) {
                    maxDepth = depDepth;
                }
            }

            return maxDepth + 1;
        } else {
            return 0;
        }
    }

    @Override
    public String eval(int x, int y) {
        int[][] depths = depth();

        if (depths[x][y] == Integer.MAX_VALUE) {
            get(x, y).setType(Ex2Utils.ERR_CYCLE_FORM);
            return "Circular Dependency";
        }

        if (first.contains(Pair.of(x, y))) {
            return "Circular Dependency";
        }
        first.add(Pair.of(x, y));
        SCell cell = (SCell) get(x, y);
        if (cell == null) {
            first.remove(Pair.of(x, y));
            return "ERROR_FORM";
        }
        String ll = cell.toString();
        String ans = computer(ll);
        first.remove(Pair.of(x, y));

        if (ans.equals("ERROR_FORM")) {
            cell.setType(Ex2Utils.ERR_FORM_FORMAT);
        } else if (ans.equals("Circular Dependency")) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
        } else if (ll.startsWith("=")) {
            if (isValidFormula(ll.substring(1))) {
                cell.setType(Ex2Utils.FORM);
            } else {
                cell.setType(Ex2Utils.ERR_FORM_FORMAT);
            }
        } else if (isNum(ans)) {
            cell.setType(Ex2Utils.NUMBER);
        } else {
            cell.setType(Ex2Utils.TEXT);
        }

        return ans;
    }

    private boolean isValidFormula(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            return false;
        }
        formula = formula.trim();

        if (formula.contains("**") || formula.contains("++") ||
                formula.endsWith("+") || formula.endsWith("-") ||
                formula.endsWith("*") || formula.endsWith("/")) {
            return false;
        }

        int parenthesesCount = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') parenthesesCount++;
            if (c == ')') parenthesesCount--;
            if (parenthesesCount < 0) return false;
        }
        if (parenthesesCount != 0) return false;

        if (formula.matches("^[A-Z]\\d+$")) {
            return true;
        }

        try {
            Double.parseDouble(formula);
            return true;
        } catch (NumberFormatException e) {
            return formula.matches(".*[+\\-*/].*") &&
                    !formula.matches(".*[^A-Z0-9+\\-*/()\\s\\.].*");
        }
    }
    boolean isNum(String s) {
        try {
            Double.parseDouble(s);
            return true;
        }
        catch (Exception _) {return false;}
    }

    public String computer(String ll) {
        if (ll.startsWith("=")) {
            String BITUI = ll.substring(1).replaceAll(" ", "");
            List<String> sour = cellsour(BITUI);
            for (String ref : sour) {
                int[] co = Taeim(ref);
                if (co == null || !isIn(co[0], co[1])) {
                    return "ERROR_FORM";
                }
                String evaluatedValue = eval(co[0], co[1]);
                if (evaluatedValue.equals("Circular Dependency") || evaluatedValue.equals("ERROR_FORM")) {
                    return evaluatedValue;
                }
                BITUI = BITUI.replace(ref, evaluatedValue);
            }
            try {
                return evaluateExpression(BITUI);
            } catch (NumberFormatException e) {
                return "ERROR_FORM";
            }
        } else {
            return ll;
        }
    }

    private List<String> cellsour(String BITUI) {
        List<String> sour = new ArrayList<>();
        Matcher m = Pattern.compile("[A-Za-z]+\\d+").matcher(BITUI);
        while (m.find()) {
            sour.add(m.group());
        }
        return sour;
    }

    private int[] Taeim(String ref) {
        String colPart = ref.replaceAll("\\d", "");
        String rowPart = ref.replaceAll("[A-Za-z]", "");
        int x = colPart.toUpperCase().charAt(0) - 'A';
        int y;
        try {
            y = Integer.parseInt(rowPart) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            return null;
        }
        return new int[]{x, y};
    }

    private String evaluateExpression(String expression) {
        try {
            return String.valueOf(evalExpression(expression));
        } catch (Exception e) {
            return "ERROR_FORM";
        }
    }

    private double evalExpression(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        x /= divisor;
                    } else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        sheet.set(0, 0, "10");
        sheet.set(0, 1, "20");
        sheet.set(0, 2, "=A1+B1");
        sheet.set(1, 0, "30");
        sheet.set(1, 1, "=A2");
        sheet.set(1, 2, "=A1+A2");
        sheet.set(2, 0, "=B1+C1"); // Circular dependency test
        sheet.eval();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                System.out.println("Cell (" + x + "," + y + "): " + sheet.value(x, y));
            }
        }
    }

    public static class Pair<T1, T2> {
        public final T1 x;
        public final T2 y;

        public Pair(T1 x, T2 y) {
            this.x = x;
            this.y = y;
        }

        public static <T1, T2> Pair<T1, T2> of(T1 x, T2 y) {
            return new Pair<>(x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return x.equals(pair.x) && y.equals(pair.y);
        }

        @Override
        public int hashCode() {
            return 31 * x.hashCode() + y.hashCode();
        }
    }

    private class CellWithDepth {
        int x;
        int y;
        int depth;

        public CellWithDepth(int x, int y, int depth) {
            this.x = x;
            this.y = y;
            this.depth = depth;
        }
    }
}