import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;


public class SudokuGUI extends JFrame {

    private Board board; 
    private JTextField[][] cells; 
    private static final int BOARD_SIZE = 9;

    public SudokuGUI() {
        setTitle("Jogo de Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700); 
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        cells = new JTextField[BOARD_SIZE][BOARD_SIZE];

        add(createBoardPanel(), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);

        startGame();

        setVisible(true);
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); 

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("Arial", Font.BOLD, 24));
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                if (i % 3 == 0 && i != 0) {
                    cell.setBorder(BorderFactory.createMatteBorder(2, cell.getBorder().getBorderInsets(cell).left, cell.getBorder().getBorderInsets(cell).bottom, cell.getBorder().getBorderInsets(cell).right, Color.BLACK));
                }
                if (j % 3 == 0 && j != 0) {
                    cell.setBorder(BorderFactory.createMatteBorder(cell.getBorder().getBorderInsets(cell).top, 2, cell.getBorder().getBorderInsets(cell).bottom, cell.getBorder().getBorderInsets(cell).right, Color.BLACK));
                }

                if (i % 3 == 0 && i != 0 && j % 3 == 0 && j != 0) {
                    cell.setBorder(BorderFactory.createMatteBorder(2, 2, cell.getBorder().getBorderInsets(cell).bottom, cell.getBorder().getBorderInsets(cell).right, Color.BLACK));
                }

                cells[i][j] = cell;
                boardPanel.add(cell);


                final int row = i;
                final int col = j;
                cell.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        
                        if (cell.isEditable()) {
                            handleCellInput(row, col, cell.getText());
                        }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        
                        if (cell.isEditable()) {
                            handleCellInput(row, col, cell.getText());
                        }
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        
                    }
                });
            }
        }
        return boardPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton newGameButton = new JButton("Novo Jogo");
        newGameButton.addActionListener(e -> startGame());
        controlPanel.add(newGameButton);

        JButton checkButton = new JButton("Verificar Jogo");
        checkButton.addActionListener(e -> checkGame());
        controlPanel.add(checkButton);

        JButton clearButton = new JButton("Limpar Meu Progresso");
        clearButton.addActionListener(e -> clearCurrentProgress());
        controlPanel.add(clearButton);

        JButton finishButton = new JButton("Finalizar Jogo");
        finishButton.addActionListener(e -> finishGameGUI());
        controlPanel.add(finishButton);

        return controlPanel;
    }

    private void startGame() {
        String[] difficulties = {"Fácil", "Médio", "Difícil"};
        int difficultyChoice = JOptionPane.showOptionDialog(this,
                "Selecione a dificuldade:", "Novo Jogo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, difficulties, difficulties[0]);

        int emptyCells;
        switch (difficultyChoice) {
            case 0: emptyCells = 40; break; // Fácil (número de células a remover)
            case 1: emptyCells = 50; break; // Médio
            case 2: emptyCells = 60; break; // Difícil
            default: emptyCells = 45; 
        }

        SudokuGenerator generator = new SudokuGenerator();
        List<List<Space>> generatedSpaces = generator.generateBoard(emptyCells);
        board = new Board(generatedSpaces);
        updateBoardGUI(); 
        JOptionPane.showMessageDialog(this, "Novo jogo iniciado!");
    }

    private void updateBoardGUI() {
        if (board == null) return;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JTextField cell = cells[i][j];
                
                DocumentListener[] listeners = ((AbstractDocument) cell.getDocument()).getDocumentListeners();
                for (DocumentListener dl : listeners) {
                    ((AbstractDocument) cell.getDocument()).removeDocumentListener(dl);
                }

                Space space = board.getSpaces().get(i).get(j);

                if (space.getActual() != null && space.getActual() != 0) {
                    cell.setText(String.valueOf(space.getActual()));
                } else {
                    cell.setText("");
                }

                if (space.isFixed()) {
                    cell.setEditable(false);
                    cell.setBackground(new Color(230, 230, 230));
                    cell.setForeground(Color.BLACK); 
                } else {
                    cell.setEditable(true);
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(Color.BLUE);
                }

                for (DocumentListener dl : listeners) {
                    ((AbstractDocument) cell.getDocument()).addDocumentListener(dl);
                }
            }
        }
    }

    private void handleCellInput(int row, int col, String text) {

        try {
            if (text.isEmpty()) {
                board.clearValue(row, col);
            } else {
                int value = Integer.parseInt(text);
                if (value >= 1 && value <= 9) {
                    board.changeValue(row, col, value);
                    cells[row][col].setForeground(Color.BLUE);
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, insira um número entre 1 e 9.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                    cells[row][col].setText("");
                    board.clearValue(row, col); 
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Entrada inválida. Por favor, insira um número.", "Erro", JOptionPane.ERROR_MESSAGE);
            cells[row][col].setText("");
            board.clearValue(row, col);
        }
    }


    private void checkGame() {
        if (board == null) {
            JOptionPane.showMessageDialog(this, "Inicie um novo jogo primeiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GameStatusEnum status = board.getStatus();
        boolean hasErrors = board.hasErrors();

        String message = "Status do Jogo: " + status.getLabel() + "\n";
        if (hasErrors) {
            message += "O jogo contém erros.";
            highlightErrors();
        } else {
            message += "O jogo não contém erros.";
            clearErrorHighlight();
        }
        JOptionPane.showMessageDialog(this, message, "Verificar Jogo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void highlightErrors() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Space space = board.getSpaces().get(i).get(j);
                JTextField cell = cells[i][j];

                if (space.getActual() != null && !space.isFixed() && !space.getActual().equals(space.getExpected())) {
                    cell.setBackground(new Color(255, 200, 200));
                } else if (!space.isFixed()){
                    cell.setBackground(Color.WHITE);
                }
            }
        }
    }

    private void clearErrorHighlight() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Space space = board.getSpaces().get(i).get(j);
                JTextField cell = cells[i][j];
                if (!space.isFixed()) {
                    cell.setBackground(Color.WHITE);
                } else {
                    cell.setBackground(new Color(230, 230, 230));
                }
            }
        }
    }


    private void clearCurrentProgress() {
        if (board == null) {
            JOptionPane.showMessageDialog(this, "Inicie um novo jogo primeiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja limpar seu progresso? Isso não afeta os números iniciais.",
                "Limpar Progresso", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            board.reset(); // Seu método reset() deve limpar apenas os valores "actual"
            updateBoardGUI(); // Atualiza a GUI
            JOptionPane.showMessageDialog(this, "Progresso limpo!");
        }
    }

    private void finishGameGUI() {
        if (board == null) {
            JOptionPane.showMessageDialog(this, "Inicie um novo jogo primeiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (board.gameIsFinished()) {
            JOptionPane.showMessageDialog(this, "Parabéns! Você concluiu o jogo!", "Jogo Finalizado", JOptionPane.INFORMATION_MESSAGE);
            // Opcional: Desabilitar edição das células após a conclusão
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    cells[i][j].setEditable(false);
                    cells[i][j].setForeground(Color.GREEN); // Exemplo: Cor verde para números corretos ao finalizar
                }
            }
        } else {
            // Reutiliza a lógica de verificação de erros para a mensagem
            checkGame(); // Isso exibirá se há erros
            if (!board.hasErrors() && board.getStatus() == GameStatusEnum.INCOMPLETE) {
                JOptionPane.showMessageDialog(this, "Você ainda precisa preencher todos os espaços.", "Jogo Incompleto", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Garante que a GUI seja iniciada na Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new SudokuGUI());
    }
}