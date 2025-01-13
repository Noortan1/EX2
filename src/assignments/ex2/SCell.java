package assignments.ex2;

public class SCell implements Cell {


    private String ll;
    private int type;
    private int order;

    public SCell(String s) {
        setinfo(s);
        determineType();
    }

    private void determineType() {
        if (ll == null || ll.trim().isEmpty()) {
            type = Ex2Utils.TEXT;
            return;
        }

        String info = ll.trim();
        if (info.startsWith("=")) {
            if (isValidBITUI(info.substring(1))) {
                type = Ex2Utils.FORM;
            } else {
                type = Ex2Utils.ERR_CYCLE_FORM;
            }
            return;
        }

        try {
            Double.parseDouble(info);
            type = Ex2Utils.NUMBER;
            return;
        } catch (NumberFormatException e) {
            type = Ex2Utils.TEXT;
        }
    }
    private boolean isValidBITUI(String BITUI) {
        if (BITUI == null || BITUI.trim().isEmpty()) {
            return false;
        }
        BITUI = BITUI.trim();

        if (BITUI.contains("**") || BITUI.contains("++") ||
                BITUI.endsWith("+") || BITUI.endsWith("-") ||
                BITUI.endsWith("*") || BITUI.endsWith("/")) {
            return false;
        }

        int parenthesesCount = 0;
        for (char c : BITUI.toCharArray()) {
            if (c == '(') parenthesesCount++;
            if (c == ')') parenthesesCount--;
            if (parenthesesCount < 0) return false;
        }
        if (parenthesesCount != 0) return false;

        if (BITUI.matches("^[A-Z]\\d+$")) {
            return true;
        }

        try {
            Double.parseDouble(BITUI);
            return true;
        } catch (NumberFormatException e) {
            return BITUI.matches(".*[+\\-*/].*") &&
                    !BITUI.matches(".*[^A-Z0-9+\\-*/()\\s\\.].*");
        }
    }

    @Override
    public String getOrder() {
        return String.valueOf(order);
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    @Override
    public String toString() {
        return getinfo();
    }

    @Override
    public String getData() {
        return this.ll;
    }

    @Override
    public void setinfo(String s) {
        this.ll = s;
        determineType();
    }

    @Override
    public String getinfo() {
        return ll;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        this.type = t;
    }
}

