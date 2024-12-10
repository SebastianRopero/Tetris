import java.awt.Color;
import java.util.Random;

public class TetrisFormas {
    private static final Random random = new Random();

    public static final FiguraMain L_FIGURA = new FiguraMain(new int[][]{
        {1, 0},
        {1, 0},
        {1, 1}
    });

    public static final FiguraMain J_FIGURA = new FiguraMain(new int[][]{
        {0, 1},
        {0, 1},
        {1, 1}
    });

    public static final FiguraMain O_FIGURA = new FiguraMain(new int[][]{
        {1, 1},
        {1, 1}
    });

    public static final FiguraMain I_FIGURA = new FiguraMain(new int[][]{
        {1},
        {1},
        {1},
        {1}
    });

    public static final FiguraMain Z_FIGURA = new FiguraMain(new int[][]{
        {1, 1, 0},
        {0, 1, 1}
    });

    public static final FiguraMain S_FIGURA = new FiguraMain(new int[][]{
        {0, 1, 1},
        {1, 1, 0}
    });

    public static final FiguraMain T_FIGURA = new FiguraMain(new int[][]{
        {1, 1, 1},
        {0, 1, 0}
    });

    public static final FiguraMain P_FIGURA = new FiguraMain(new int[][]{
        {0, 1, 0},
        {0, 1, 0},
        {0, 1, 0},
        {1, 1, 1}
    });

    public static Color getColor(FiguraMain piece) {
        if (piece == null) {
            throw new IllegalArgumentException("La pieza no puede ser null");
        }
        return new Color(
            random.nextInt(156) + 100, 
            random.nextInt(156) + 100,  
            random.nextInt(156) + 100   
        );
    }
}
