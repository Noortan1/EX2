//package assignments.ex2;
//
//public class Spreadsheet {
//    private Cell[][] cells;
//
//    public  Spreadsheet(int x, int y) {
//        cells = new Cell[x][y];
//    }
//    public Cell get(int x, int y){
//        return cells[x][y];
//    }
//
//    public void set(int x, int y, Cell c){
//        cells[x][y] = c;
//
//    }
//   public int width() {
//      return cells.length;
//   }
//    public int height() {
//        return cells[0].length;
//    }
//
//    public int xCell(String c) {
//        char she = c.charAt(0);
//        return she - 'A';
//    }
//    // “F13” → 5 (A→0), “AA13” → -1 [0,26)
//    int yCell(String c) {
//        return Integer.parseInt(c.substring(1))-1;
//    }
//
//
//}
