import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tetris extends JPanel {
    private static final int TABLERO_ANCHO = 10;
    private static final int TABLERO_ALTO = 20;
    private static final int TAMAÑO_BLOQUE = 30;

    private FiguraMain forma_actual;
    private int Posicion_Actual_X = 0;
    private int Posicion_Actual_Y = 0;
    private boolean[][] Tablero;
    private Color[][] Colores_Tablero;
    private boolean gameOver = false;
    private Timer timer;

    public Tetris() {
        setPreferredSize(new Dimension(TABLERO_ANCHO * TAMAÑO_BLOQUE, TABLERO_ALTO * TAMAÑO_BLOQUE));
        setBackground(Color.BLACK);
        Tablero = new boolean[TABLERO_ANCHO][TABLERO_ALTO];
        Colores_Tablero = new Color[TABLERO_ANCHO][TABLERO_ALTO];

        try {
            forma_actual = getRandomPiece();
        } catch (Exception e) {
            System.err.println("Error inicializando pieza: " + e.getMessage());
        }

        timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!moveDown()) {
                    try {
                        fixToGround();
                        spawnNewPiece();
                        if (gameOver) {
                            timer.stop();
                        }
                    } catch (Exception ex) {
                        System.err.println("Error en el bucle de juego: " + ex.getMessage());
                    }
                }
            }
        });
        timer.start();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveRight();
                            break;
                        case KeyEvent.VK_DOWN:
                            moveDown();
                            break;
                        case KeyEvent.VK_UP:
                            rotatePiece();
                            break;
                    }
                }
            }
        });
        setFocusable(true);
    }

    private boolean moveDown() {
        try {
            if (canMove(forma_actual, Posicion_Actual_X, Posicion_Actual_Y + 1)) {
                Posicion_Actual_Y++;
                repaint();
                return true;
            }
        } catch (Exception e) {
            System.err.println("Movimiento hacia abajo fallido: " + e.getMessage());
        }
        return false;
    }

    private void moveLeft() {
        try {
            if (canMove(forma_actual, Posicion_Actual_X - 1, Posicion_Actual_Y)) {
                Posicion_Actual_X--;
                repaint();
            }
        } catch (Exception e) {
            System.err.println("Movimiento hacia la izquierda fallido: " + e.getMessage());
        }
    }

    private void moveRight() {
        try {
            if (canMove(forma_actual, Posicion_Actual_X + 1, Posicion_Actual_Y)) {
                Posicion_Actual_X++;
                repaint();
            }
        } catch (Exception e) {
            System.err.println("Movimiento hacia la derecha fallido: " + e.getMessage());
        }
    }

    private boolean canMove(FiguraMain piece, int x, int y) {
        try {
            for (int i = 0; i < piece.getFigura().length; i++) {
                for (int j = 0; j < piece.getFigura()[i].length; j++) {
                    if (piece.getFigura()[i][j] == 1) {
                        int newX = x + j;
                        int newY = y + i;
                        if (newX < 0 || newX >= TABLERO_ANCHO || newY >= TABLERO_ALTO || Tablero[newX][newY]) {
                            return false;
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Posición fuera del tablero.");
        }
        return true;
    }

    private void fixToGround() {
        try {
            Color pieceColor = TetrisFormas.getColor(forma_actual);
            for (int i = 0; i < forma_actual.getFigura().length; i++) {
                for (int j = 0; j < forma_actual.getFigura()[i].length; j++) {
                    if (forma_actual.getFigura()[i][j] == 1) {
                        Tablero[Posicion_Actual_X + j][Posicion_Actual_Y + i] = true;
                        Colores_Tablero[Posicion_Actual_X + j][Posicion_Actual_Y + i] = pieceColor;
                    }
                }
            }

            for (int y = 0; y < TABLERO_ALTO; y++) {
                if (isRowFull(y)) {
                    removeRow(y);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al fijar la pieza al tablero: " + e.getMessage());
        }
    }

    private void spawnNewPiece() {
        try {
            forma_actual = getRandomPiece();
            Posicion_Actual_X = 0;
            Posicion_Actual_Y = 0;

            if (!canMove(forma_actual, Posicion_Actual_X, Posicion_Actual_Y)) {
                gameOver = true;
                throw new IllegalStateException("El juego ha terminado: no se puede generar una nueva pieza.");
            }
        } catch (Exception e) {
            System.err.println("Error al generar una nueva pieza: " + e.getMessage());
        }
    }

    private FiguraMain getRandomPiece() {
        Random random = new Random();
        int NumFigura = random.nextInt(8); 
        switch (NumFigura) {
            case 0: return TetrisFormas.L_FIGURA;
            case 1: return TetrisFormas.J_FIGURA;
            case 2: return TetrisFormas.O_FIGURA;
            case 3: return TetrisFormas.I_FIGURA;
            case 4: return TetrisFormas.Z_FIGURA;
            case 5: return TetrisFormas.S_FIGURA;
            case 6: return TetrisFormas.T_FIGURA;
            default: return TetrisFormas.P_FIGURA;
        }
    }

    private void rotatePiece() {
        try {
            int[][] rotated = new int[forma_actual.getFigura()[0].length][forma_actual.getFigura().length];
            for (int i = 0; i < forma_actual.getFigura().length; i++) {
                for (int j = 0; j < forma_actual.getFigura()[i].length; j++) {
                    rotated[j][forma_actual.getFigura().length - 1 - i] = forma_actual.getFigura()[i][j];
                }
            }
            FiguraMain newPiece = new FiguraMain(rotated);
            if (canMove(newPiece, Posicion_Actual_X, Posicion_Actual_Y)) {
                forma_actual = newPiece;
                repaint();
            }
        } catch (Exception e) {
            System.err.println("Error al rotar la pieza: " + e.getMessage());
        }
    }

    private boolean isRowFull(int y) {
        for (int x = 0; x < TABLERO_ANCHO; x++) {
            if (!Tablero[x][y]) {
                return false;
            }
        }
        return true;
    }

    private void removeRow(int y) {
        for (int x = 0; x < TABLERO_ANCHO; x++) {
            Tablero[x][y] = false;
            Colores_Tablero[x][y] = null;
        }
        for (int row = y; row > 0; row--) {
            for (int x = 0; x < TABLERO_ANCHO; x++) {
                Tablero[x][row] = Tablero[x][row - 1];
                Colores_Tablero[x][row] = Colores_Tablero[x][row - 1];
            }
        }
        for (int x = 0; x < TABLERO_ANCHO; x++) {
            Tablero[x][0] = false;
            Colores_Tablero[x][0] = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics fm = g.getFontMetrics();
            String gameOverMessage = "Has perdido";
            int x = (getWidth() - fm.stringWidth(gameOverMessage)) / 2;
            int y = getHeight() / 2;
            g.drawString(gameOverMessage, x, y);
            return;
        }

        g.setColor(Color.GRAY);
        for (int x = 0; x < TABLERO_ANCHO; x++) {
            for (int y = 0; y < TABLERO_ALTO; y++) {
                g.drawRect(x * TAMAÑO_BLOQUE, y * TAMAÑO_BLOQUE, TAMAÑO_BLOQUE, TAMAÑO_BLOQUE);
            }
        }

        g.setColor(TetrisFormas.getColor(forma_actual));
        for (int i = 0; i < forma_actual.getFigura().length; i++) {
            for (int j = 0; j < forma_actual.getFigura()[i].length; j++) {
                if (forma_actual.getFigura()[i][j] == 1) {
                    g.fillRect((Posicion_Actual_X + j) * TAMAÑO_BLOQUE, (Posicion_Actual_Y + i) * TAMAÑO_BLOQUE, TAMAÑO_BLOQUE, TAMAÑO_BLOQUE);
                }
            }
        }

        for (int x = 0; x < TABLERO_ANCHO; x++) {
            for (int y = 0; y < TABLERO_ALTO; y++) {
                if (Tablero[x][y]) {
                    g.setColor(Colores_Tablero[x][y]);
                    g.fillRect(x * TAMAÑO_BLOQUE, y * TAMAÑO_BLOQUE, TAMAÑO_BLOQUE, TAMAÑO_BLOQUE);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame("Tetris");
            Tetris panel = new Tetris();
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
        }
    }
}