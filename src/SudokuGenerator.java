import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuGenerator {

    private final int[][] board;
    private final Random random;
    private final int BOARD_SIZE = 9;

    public SudokuGenerator() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.random = new Random();
    }

    public List<List<Space>> generateBoard(int emptyCells) {
        fillBoard(); // preenche o tabuleiro completamente
        removeCells(emptyCells); // e então remove células para criar o jogo

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_SIZE; j++) {
                int expectedValue = board[i][j];
                boolean isFixed = (expectedValue != 0); 
                spaces.get(i).add(new Space(expectedValue, isFixed));
            }
        }
        return spaces;
    }

    private void fillBoard() {
        fillDiagonalBlocks();
        solveSudoku(0, 3); 
    }

    // Preenche os blocos 3x3 da diagonal (para garantir um bom ponto de partida)
    private void fillDiagonalBlocks() {
        for (int i = 0; i < BOARD_SIZE; i += 3) {
            fillBlock(i, i);
        }
    }

    // Preenche um bloco 3x3
    private void fillBlock(int row, int col) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= BOARD_SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers); 

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[row + i][col + j] = numbers.remove(0);
            }
        }
    }

    // Algoritmo de backtracking para preencher o resto do tabuleiro
    private boolean solveSudoku(int row, int col) {
        if (row == BOARD_SIZE - 1 && col == BOARD_SIZE) {
            return true; 
        }

        if (col == BOARD_SIZE) {
            row++;
            col = 0;
        }

        if (board[row][col] != 0) { // Se a célula já está preenchida, pule para a próxima
            return solveSudoku(row, col + 1);
        }

        for (int num = 1; num <= BOARD_SIZE; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (solveSudoku(row, col + 1)) {
                    return true;
                }
                board[row][col] = 0; 
            }
        }
        return false;
    }

    // Verifica se um número é válido em uma dada posição
    private boolean isValid(int row, int col, int num) {
        // Verifica a linha
        for (int d = 0; d < BOARD_SIZE; d++) {
            if (board[row][d] == num && d != col) { 
                return false;
            }
        }

        // Verifica a coluna
        for (int r = 0; r < BOARD_SIZE; r++) {
            if (board[r][col] == num && r != row) { 
                return false;
            }
        }

        // Verifica o bloco 3x3
        int sqrt = (int) Math.sqrt(BOARD_SIZE);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int d = boxColStart; d < boxColStart + sqrt; d++) {
                if (board[r][d] == num && r != row && d != col) { 
                    return false;
                }
            }
        }
        return true;
    }

    private void removeCells(int count) {
        int removed = 0;
        while (removed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);

            if (board[row][col] != 0) { 
                int backup = board[row][col];
                board[row][col] = 0; 

                removed++;
            }
        }
    }
}