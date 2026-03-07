import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Clase principal del programa
 * Lanza la interfaz grafica (Swing)
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // usa el look and feel por defecto
            }
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
