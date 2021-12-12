package demo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
编写俄罗斯方块主类
    属性:需要加载的图片资源、单元格的像素值、定义游戏区域的单元格
    方法：
 */

public class Tetris extends JPanel {
    //定义正在下落的四方格
    private Tetromino currentOne = Tetromino.randomOne();
    //定义将要下落的四方格
    private Tetromino nextOne = Tetromino.randomOne();
    //定义游戏区域的单元格
    private Cell[][] wall = new Cell[16][8];
    //单元格的像素值
    private static final int CELL_SIZE = 47;

    //定义游戏池、当前获得游戏的分数、当前已消除的行数
    int[] scorePool = {0,1,2,5,10};
    private int totalScore = 0;
    private int totalLine = 0;

    //声明三种游戏状态
    private static final int PLAYING = 0;
    private static final int PAUSE = 1;
    private static final int GAMEOVER = 2;
    //定义当前游戏状态的值
    private int currentState;
    //定义用于显示游戏状态的变量
    private String[] states = {"[P] PAUSE","[C] CONTINUE","[R] RESTART"};


    //图片资源
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage background;

    //加载资源
    static {
        try {
            I = ImageIO.read(new File("images/I.png"));
            J = ImageIO.read(new File("images/J.png"));
            L = ImageIO.read(new File("images/L.png"));
            O = ImageIO.read(new File("images/O.png"));
            S = ImageIO.read(new File("images/S.png"));
            T = ImageIO.read(new File("images/T.png"));
            Z = ImageIO.read(new File("images/Z.png"));
            background = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        //平移坐标轴
        g.translate(15, 9);
        paintWall(g);
        paintCurrentOne(g);
        paintNextOne(g);
        paintScore(g);
        paintGameState(g);
    }

    public void start() {
        currentState = PLAYING;
        KeyListener l = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_DOWN:
                        softDropAction(); //下落一格
                        break;
                    case KeyEvent.VK_LEFT:
                        toLeftAction(); //左移一格
                        break;
                    case KeyEvent.VK_RIGHT:
                        toRightAction(); //右移一格
                        break;
                    case KeyEvent.VK_UP:
                        rotateRightAction(); //顺时针旋转
                        break;
                    case KeyEvent.VK_SPACE:
                        handDropAction(); //快速下落
                        break;
                    case KeyEvent.VK_P:
                        if (currentState == PLAYING) {
                            currentState = PAUSE;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if (currentState == PAUSE) {
                            currentState = PLAYING;
                        }
                        break;
                    case KeyEvent.VK_R:
                        //游戏重新开始
                        currentState = PLAYING;
                        wall = new Cell[16][8];
                        currentOne = Tetromino.randomOne();
                        nextOne = Tetromino.randomOne();
                        totalLine = 0;
                        totalScore = 0;
                        break;
                }
                //重新绘制
                repaint();
            }


            @Override
            public void keyReleased(KeyEvent e) {
            }

        };
        //将俄罗斯方块窗口设为焦点
        this.addKeyListener(l);
        this.requestFocus();
        while (true) {
            //判断当游戏状态为在游戏中(PLAYING)，每隔0.5秒下落
            if (currentState == PLAYING) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //判断能否下落
                if (canDrop()) {
                    currentOne.dropMove();
                } else {
                    utils();
                }
            }
            repaint();
        }
    }

    //创建顺时针旋转
    public void rotateRightAction() {
        currentOne.rotataeRight();
        //判断是否越界或是否重合
        if (isOutOfBound() || isCoincide()) {
            currentOne.rotataeLeft();
        }
    }

    //四方格瞬间下落
    public void handDropAction() {
        while (true) {
            //判断四方格能否下落
            if (canDrop()) {
                currentOne.dropMove();
            } else {
                break;
            }
        }
        utils();
    }

    //按键四方格下落一次
    public void softDropAction() {
        //判断四方格能否下落
        if (canDrop()) {
            //当前四方格下落一格
            currentOne.dropMove();
        }else {
            utils();
        }
    }

    //工具方法(将方格嵌入墙中、判断能否消行、判断游戏是否结束)
    public void utils() {
        //将方格嵌入墙中
        landToWall();
        //判断能否消行
        destroyLine();
        //判断游戏是否结束
        if (isGameOver()) {
            currentState = GAMEOVER;
        }else {
            //当游戏没有结束时，生成新的四方格
            currentOne = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    //将四方格嵌入墙中
    public void landToWall() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    //判断四方格能否下落
    public boolean canDrop() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            //判断是否到达底部
            if (row == wall.length - 1) {
                return false;
            } else if (wall[row + 1][col] != null) { //判断是否有方块
                return false;
            }
        }
        return true;
    }

    //消行计分
    public void destroyLine() {
        int line = 0;
        for (int i = 0; i < wall.length; i++) {
            if (canDestroy(i)) {
                line++;
                for(int j = i; j > 0; j--) {
                    wall[j] = wall[j-1];
                }
                wall[0] = new Cell[wall[i].length];
            }
        }
        totalScore += scorePool[line];
        totalLine += line;
    }

    //判断是否可以消行
    public boolean canDestroy(int row) {
        for (int i = 0; i < wall[0].length; i++) {
            if (wall[row][i] == null) {
                return false;
            }
        }
        return true;
    }

    //判断游戏是否结束
    public boolean isGameOver() {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    //判断方块是否出界
    public boolean isOutOfBound() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if (col < 0 || col > wall[0].length - 1 || row < 0 || row > wall.length - 1) {
                return true;
            }
        }
        return false;
    }

    //判断方块是否重合
    public boolean isCoincide() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    //按键一次四方格左移一次
    public void toLeftAction() {
        currentOne.leftMove();
        if (isOutOfBound() || isCoincide()) {
            currentOne.rightMove();
        }
    }

    //按键一次四方格右移一次
    public void toRightAction() {
        currentOne.rightMove();
        if (isOutOfBound() || isCoincide()) {
            currentOne.leftMove();
        }
    }

    //绘制游戏状态
    public void paintGameState(Graphics g) {
        switch (currentState) {
            case PLAYING:
                g.setFont(new Font( Font.DIALOG, Font.BOLD, 30));
                g.drawString(states[currentState], 450, 580);
                break;
            case PAUSE:
                g.setFont(new Font( Font.DIALOG, Font.BOLD, 30));
                g.drawString(states[currentState], 450, 580);
                break;
            case GAMEOVER:
                g.setFont(new Font( Font.DIALOG, Font.BOLD, 30));
                g.drawString(states[currentState], 450, 580);
                g.setFont(new Font( Font.DIALOG, Font.BOLD, 50));
                g.setColor(Color.red);
                g.drawString("GAME OVER!", 30, 380);
                break;
        }
    }

    //绘制游戏得分和消除行数
    public void paintScore(Graphics g) {
        g.setFont(new Font( Font.DIALOG, Font.BOLD, 30));
        g.drawString("Scores: "+totalScore, 450, 220);
        g.drawString("Lines: "+totalLine, 450, 380);
    }

    //绘制将要下落的四方格
    public void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE + 300;
            int y = cell.getRow() * CELL_SIZE + 5;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    //绘制正在下落的四方格
    public void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE ;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    //绘制游戏主区域
    public void paintWall(Graphics g) {
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[0].length; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];
                //判断当前单元格是否有小方格，如果没有则绘制矩形；否则将小方格嵌入到墙中
                if (cell == null) {
                    g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
                }else{
                    g.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }

    //主函数
    public static void main(String[] args) {
        //创建一个窗口对象
        JFrame frame = new JFrame("俄罗斯方块");
        //创建面板
        Tetris panel = new Tetris();
        //将面板嵌入窗口
        frame.add(panel);
        //设置窗口设置可见
        frame.setVisible(true);
        //设置窗口尺寸
        frame.setSize(701,815);
        //设置窗口居中
        frame.setLocationRelativeTo(null);
        //设置窗口关闭时程序终止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //执行游戏的主要逻辑
        panel.start();
    }
}
