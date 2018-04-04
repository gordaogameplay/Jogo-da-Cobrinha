
package jogocobra;

import java.awt.Dimension;
import javax.swing.JFrame;

public class JogoCobra {

    public static void main(String[] args) {
        JFrame tela = new JFrame("Jogo Da Croba xD");
        tela.setContentPane(new Controlador());
        tela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tela.setResizable(false);
        tela.pack();
        tela.setPreferredSize(new Dimension(Controlador.LARGURA,Controlador.ALTURA));
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);
    }
    
}
