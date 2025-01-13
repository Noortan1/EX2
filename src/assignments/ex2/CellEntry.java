package assignments.ex2;


    public class CellEntry  implements Index2D {
        private int x;
        private int y;
        private String indexStr;


        public void Index2D_Impl(String index) {
            this.indexStr = index.toUpperCase();
            parseIndex();
        }
        public void Index2D_Impl(int x, int y) {
            this.x = x;
            this.y = y;
            this.indexStr = convertToString(x, y);
        }
        private void parseIndex() {
            if (!isValid()) {
                x = -1;
                y = -1;
                return;
            }
            x = Character.toUpperCase(indexStr.charAt(0)) - 'A';
            y = Integer.parseInt(indexStr.substring(1));
        }
        private String convertToString(int x, int y) {
            if (x < 0 || x > 25 || y < 0 || y > 99) {
                return "";
            }
            char colChar = (char)('A' + x);
            return colChar + String.valueOf(y);
        }

        @Override
        public String toString() {
            return indexStr;
        }
        @Override
        public boolean isValid() {
            if (indexStr == null || indexStr.length() < 2) {
                return false;
            }
            char firstChar = Character.toUpperCase(indexStr.charAt(0));
            if (firstChar < 'A' || firstChar > 'Z') {
                return false;
            }

            try {
                String numPart = indexStr.substring(1);
                int num = Integer.parseInt(numPart);
                return num >= 0 && num <= 99 && numPart.equals(String.valueOf(num));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Index2D)) {
                return false;
            }
            Index2D other = (Index2D) obj;
            return this.x == other.getX() && this.y == other.getY();
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
