package demo;

/*
编写四方格类
    属性:包含四个方格数组
    方法：向左移、向右移、向下移、随机生成四方格
 */

import java.util.Objects;

public class Tetromino {
    //定义包含四个方格数组
    protected Cell[] cells = new Cell[4];

    //定义旋转状态
    protected State[] states;
    //声明旋转状态
    protected int count = 10000;

    //编写四方格的内部类
    class State {
        int row0,col0,row1,col1,row2,col2,row3,col3;

        public State(int row0, int col0, int row1, int col1, int row2, int col2, int row3, int col3) {
            this.row0 = row0;
            this.col0 = col0;
            this.row1 = row1;
            this.col1 = col1;
            this.row2 = row2;
            this.col2 = col2;
            this.row3 = row3;
            this.col3 = col3;
        }

        public int getRow0() {
            return row0;
        }

        public void setRow0(int row0) {
            this.row0 = row0;
        }

        public int getCol0() {
            return col0;
        }

        public void setCol0(int col0) {
            this.col0 = col0;
        }

        public int getRow1() {
            return row1;
        }

        public void setRow1(int row1) {
            this.row1 = row1;
        }

        public int getCol1() {
            return col1;
        }

        public void setCol1(int col1) {
            this.col1 = col1;
        }

        public int getRow2() {
            return row2;
        }

        public void setRow2(int row2) {
            this.row2 = row2;
        }

        public int getCol2() {
            return col2;
        }

        public void setCol2(int col2) {
            this.col2 = col2;
        }

        public int getRow3() {
            return row3;
        }

        public void setRow3(int row3) {
            this.row3 = row3;
        }

        public int getCol3() {
            return col3;
        }

        public void setCol3(int col3) {
            this.col3 = col3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return row0 == state.row0 &&
                    col0 == state.col0 &&
                    row1 == state.row1 &&
                    col1 == state.col1 &&
                    row2 == state.row2 &&
                    col2 == state.col2 &&
                    row3 == state.row3 &&
                    col3 == state.col3;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row0, col0, row1, col1, row2, col2, row3, col3);
        }

        @Override
        public String toString() {
            return "State{" +
                    "row0=" + row0 +
                    ", col0=" + col0 +
                    ", row1=" + row1 +
                    ", col1=" + col1 +
                    ", row2=" + row2 +
                    ", col2=" + col2 +
                    ", row3=" + row3 +
                    ", col3=" + col3 +
                    '}';
        }
    }

    //随机生成四方格
    public static Tetromino randomOne() {
        int num = (int) (Math.random()*7);
        Tetromino tetromino = null;
        switch (num) {
            case 0:
                tetromino = new I();
                break;
            case 1:
                tetromino = new J();
                break;
            case 2:
                tetromino = new L();
                break;
            case 3:
                tetromino = new O();
                break;
            case 4:
                tetromino = new S();
                break;
            case 5:
                tetromino = new T();
                break;
            case 6:
                tetromino = new Z();
                break;
        }
        return tetromino;
    }

    //向左移动
    public void leftMove() {
        for (Cell cell : cells) {
            cell.left();
        }
    }

    //向右移动
    public void rightMove() {
        for (Cell cell : cells) {
            cell.right();
        }
    }

    //向下移动
    public void dropMove() {
        for (Cell cell : cells) {
            cell.drop();
        }
    }

    //顺时针旋转四方格
    public void rotataeRight() {
        if (states.length == 0) {
            return;
        }else {
            //旋转次数+1
            count++;
            State s = states[count % states.length];
            int row = cells[0].getRow();
            int col = cells[0].getCol();
            cells[1].setRow(row + s.row1);
            cells[1].setCol(col + s.col1);
            cells[2].setRow(row + s.row2);
            cells[2].setCol(col + s.col2);
            cells[3].setRow(row + s.row3);
            cells[3].setCol(col + s.col3);
        }
    }

    //逆时针旋转四方格
    public void rotataeLeft() {
        if (states.length == 0) {
            return;
        }else {
            //旋转次数+1
            count--;
            State s = states[count % states.length];
            int row = cells[0].getRow();
            int col = cells[0].getCol();
            cells[1].setRow(row + s.row1);
            cells[1].setCol(col + s.col1);
            cells[2].setRow(row + s.row2);
            cells[2].setCol(col + s.col2);
            cells[3].setRow(row + s.row3);
            cells[3].setCol(col + s.col3);
        }
    }
}
