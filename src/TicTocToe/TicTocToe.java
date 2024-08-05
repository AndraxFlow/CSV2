/*
package TicTocToe;
//игрок ставит х
// бот ставит о
// бот выбирает случайную пустую клетку
// без ооп

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class TicTocToe {
    private final static int ROW_COUNT = 3;
    private final static int COL_COUNT = 3;
    private final static String CELL_STATE_EMPTY = " ";
    private final static String CELL_STATE_X = "X";
    private final static String CELL_STATE_O = "O";
    private final static String GAME_STATE_X = "X победил";
    private final static String GAME_STATE_O = "O победил";
    private final static String GAME_STATE_DRAW = "ничья!!!!";
    private final static String GAME_STATE_NOT_OVER = "Игра не закончена";

    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();
    public static void main(String[] args) {
        //while (true){
            startGameRound();
        //}
    }

    public static void startGameRound() {

        // create board
        String[][] board = createBoard();
        // start game cycle
        startGameLoop(board);

    }


    public static String[][] createBoard() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                createGUI gui = new createGUI();
                JFrame frame = new JFrame("TicTacToe");
                frame.add(gui.getGui());
                frame.setLocationByPlatform(true);
                frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(500,500));
                frame.setMinimumSize(new Dimension(500,500));
                frame.setLocation(50,50);
                frame.pack();
                frame.setVisible(true);

            }
        });
        String[][] board = new String[ROW_COUNT][COL_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                board[row][col] = String.valueOf(CELL_STATE_EMPTY);
            }
        }
        return board;
    }

    public static void startGameLoop(String[][] board) {
        // while (gameNotOver)
        do {
            //playerTurn
            makePlayerTurn(board);
            updateBoard(board);

            final String ANSI_CLS = "\u001b[2J";
            final String ANSI_HOME = "\u001b[H";
            System.out.print(ANSI_CLS + ANSI_HOME);
            System.out.flush();

            //botTurn
            System.out.println();
            String gameState = checkGameState(board);
            if (!Objects.equals(gameState, GAME_STATE_NOT_OVER)){
                System.out.println(gameState);
                return;
            }else{
                System.out.println(gameState);
            }


            makeBotTurn(board);
            updateBoard(board);
            gameState = checkGameState(board);
            if (!Objects.equals(gameState, GAME_STATE_NOT_OVER)){
                System.out.println(gameState);
                return;
            }else{
                System.out.println(gameState);
            }
        }while (true);
    }

    public static void makePlayerTurn(String[][] board){
        // get input
        int[] coordinates = inputCellCoordinates(board);

        // place x on board
        board[coordinates[0]][coordinates[1]] = CELL_STATE_X;
    }

    public static int[] inputCellCoordinates(String[][] board){
        System.out.println("Введите координаты (ряд и колонку) через пробел (0-2):");

        do {
            // допущение - не проверяем на наличие пробела и корректность цифр
            String[] input = scanner.nextLine().split(" ");

            int row = Integer.parseInt(input[0]);
            int col = Integer.parseInt(input[1]);

            if ((row < 0) || (row >= ROW_COUNT )|| (col < 0) || (col >= COL_COUNT)){
                System.out.println("Некоректное значение. Введите значение от 0 до 2");
            }else if (!Objects.equals(board[row][col], CELL_STATE_EMPTY)){
                System.out.println("Данная ячейка цже занята");
            }else{
                return new int[]{row,col};
            }

        }while (true);
    }

    public static void makeBotTurn(String[][] board){
        System.out.println("\nХодит ботик");
        int[] coordinates = getRandomEmptyCell(board);
        board[coordinates[0]][coordinates[1]] = CELL_STATE_O;
    }

    public static int[] getRandomEmptyCell(String[][] board){
        // get random empty cell
        do {
            int row = random.nextInt(ROW_COUNT);
            int col = random.nextInt(COL_COUNT);

            if (Objects.equals(board[row][col], CELL_STATE_EMPTY)) {
                return new int[]{row,col};
            }
        }while(true);
        // place o on board

    }

    public static String checkGameState(String[][] board){
        ArrayList<Integer> sums = new ArrayList<>();
        // rows
        for (int row = 0; row < ROW_COUNT; row++) {
            int rowSum = 0;
            for (int col = 0; col < COL_COUNT; col++) {
                rowSum += calculateNumValue(board[row][col]);
            }
            sums.add(rowSum);
        }
        // collums
        for (int col = 0; col < COL_COUNT; col++) {
            int colSum = 0;
            for (int row = 0; row < ROW_COUNT; row++) {
                colSum += calculateNumValue(board[row][col]);
            }
            sums.add(colSum);
        }

        //diagonales
        int leftDiagonaleSum = 0;
        for (int i = 0; i < ROW_COUNT; i++) {
            leftDiagonaleSum += calculateNumValue(board[i][i]);
        }
        sums.add(leftDiagonaleSum);

        int rightDiagonaleSum = 0;
        for (int i = 0; i < COL_COUNT; i++) {
            rightDiagonaleSum += calculateNumValue(board[i][COL_COUNT - 1 - i]);
        }
        sums.add(rightDiagonaleSum);

        if (sums.contains(3)) {
            return GAME_STATE_X;
        }else if (sums.contains(-3)) {
            return  GAME_STATE_O;
        }else if(CheckAreAllCellsTaken(board)){
            return GAME_STATE_DRAW;
        }else {
            return GAME_STATE_NOT_OVER;
        }
    }

    private static int calculateNumValue(String cellState) {
        if (Objects.equals(cellState, CELL_STATE_X)) {
            return 1;
        }else if (Objects.equals(cellState, CELL_STATE_O)) {
            return -1;
        }else {
            return 0;
        }
    }

    public static boolean CheckAreAllCellsTaken(String[][] board) {
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (Objects.equals(board[row][col], CELL_STATE_EMPTY)){
                    return  false;
                }
            }
        }
        return true;
    }

    public static void updateBoard(String[][] board) {
        System.out.println("-----------");
        for (int row = 0; row < ROW_COUNT; row++) {
            String line = "|";
            for (int col = 0; col < COL_COUNT; col++) {
                line += board[row][col] + " |";
            }
            //line += "|";
            System.out.println(line);
            System.out.println("-----------");
        }
        //System.out.println("---------");

    }
}
class createGUI extends JFrame {
    private  JPanel board = new JPanel(new BorderLayout(3, 3));
    private JButton[][] buttons = new JButton[3][3];
    public int[][] cells = { {0,0,0}, {0,0,0}, {0,0,0} };
    private JPanel c1Board, c2Board;
    static int turn = 0;
    private  JLabel messagec1 = new JLabel("Board");
    Font BigFontTR = new Font("TimesRoman", Font.BOLD, 30);//Тут все про шрифт)
    JToolBar tool = new JToolBar();
    Insets Margin = new Insets(0,0,0,0);
    int squares = 3;
    int space = 30;
    createGUI() {
        initialize();
    }

    public void initialize(){
        board.setBorder(new EmptyBorder(5, 5, 5, 5));
        board.add(tool, BorderLayout.PAGE_START);
        setTitle("TicTacToe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setResizable(true);
        tool.add(messagec1);
        c1Board = new JPanel(new GridLayout(0, 3));
        c1Board.setBorder(new LineBorder(Color.BLACK));
        board.add(c1Board);

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                JButton b = new JButton();
                b.setMargin(Margin);
                b.setBackground(Color.WHITE);

                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(turn % 2 == 0) {
                            b.setFont(BigFontTR);
                            b.setText("X");
                            b.setBorder(BorderFactory.createTitledBorder("Player 1"));
                            turn += 1;
                        }else{
                            b.setFont(BigFontTR);
                            b.setText("O");
                            b.setBorder(BorderFactory.createTitledBorder("Player 2"));
                            turn += 1;
                        }
                    }
                });
                buttons[j][i] = b;
            }
        }
        for (int i = 0; i < squares; i++) {
            for (int j = 0; j < squares; j++) {
                c1Board.add(buttons[j][i]);
            }
        }




    }

    public final JComponent getGui() {
        return board;
    }
}



*/
