package jogocobra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Controlador extends JPanel implements Runnable, KeyListener {

    public static final int LARGURA = 400;
    public static final int ALTURA = 400;
    //Render
    private Graphics2D g2d;
    private BufferedImage imagem;

    //Loop principal
    private Thread thread;
    private boolean running;
    private long targetTime;

    //Game recursos
    private final int LADO = 10;
    private DoadoraDePartesxD cabeca, maca;
    private ArrayList<DoadoraDePartesxD> cobra;
    private int pontos;
    private int level;
    private boolean gameover;
    //movimento
    private int dx, dy;
    //key input
    private boolean cima, baixo, esquerda, direita, start;

    public Controlador() {
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
    }

    public void addNotify() {
        super.addNotify();
        thread = new Thread(this);
        thread.start();
    }

    private void setFPS(int fps) {
        targetTime = 1000 / fps;
    }

    @Override
    public void run() {
        if (running) {
            return;
        }
        init();
        long startTime;
        long elapsed;
        long wait;

        while (running) {
            startTime = System.nanoTime();

            update();
            requestRender();

            elapsed = System.nanoTime() - startTime;
            wait = targetTime - elapsed / 1000000;
            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        imagem = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_ARGB);
        g2d = imagem.createGraphics();
        running = true;
        setUplevel();
        gameover = false;
        level = 1;
        setFPS(level * 8);
    }

    private void setUplevel() {
        cobra = new ArrayList<DoadoraDePartesxD>();
        cabeca = new DoadoraDePartesxD(LADO);
        cabeca.setPosition(LARGURA / 2, ALTURA / 2);
        cobra.add(cabeca);
        for (int i = 1; i < 3; i++) {

            DoadoraDePartesxD corpo = new DoadoraDePartesxD(LADO);
            corpo.setPosition(cabeca.getX() + (i * LADO), cabeca.getY());
            cobra.add(corpo);
        }
        maca = new DoadoraDePartesxD(LADO);
        setMaca();
        pontos = 0;
        gameover = false;
        level = 1;
        dx = dy = 0;
        setFPS(level * 8);
    }
    
    public void setMaca(){
        int x = (int)(Math.random() * (LARGURA - LADO));
        int y = (int)(Math.random() * (ALTURA - LADO));
        x = x - (x%LADO);
        y = y - (y%LADO);
        
        maca.setPosition(x, y);
    }

    private void update() {
        if(gameover){
            
            if(start){
                setUplevel();
            }
            return;
        }
        if (cima && dy == 0) {
            dy = -LADO;
            dx = 0;
        }
        if (baixo && dy == 0) {
            dy = LADO;
            dx = 0;
        }
        if (direita && dx == 0 && dy != 0) {
            dy = 0;
            dx = -LADO;
        }
        if (esquerda && dx == 0 && dy != 0) {
            dy = 0;
            dx = LADO;
        }

        if (dx != 0 || dy != 0) {

            for (int i = cobra.size() - 1; i > 0; i--) {

                cobra.get(i).setPosition(cobra.get(i - 1).getX(), cobra.get(i - 1).getY());
            }
            cabeca.mover(dx, dy);
        }
        for(DoadoraDePartesxD e: cobra){
            if(e.isCollision(cabeca)){
                gameover=true;
                break;
            }
        }
        if(maca.isCollision(cabeca)){
            pontos++;
            setMaca();
            DoadoraDePartesxD e = new DoadoraDePartesxD(LADO);
            e.setPosition(-100,-100);
            cobra.add(e);
            if(pontos % 10 == 0){
                level++;
                if(level > 10)level = 10;
                setFPS(level *10);
            }
        }

        if (cabeca.getX() < 0) {
            cabeca.setX(LARGURA);
        }
        if (cabeca.getY() < 0) {
            cabeca.setY(ALTURA);
        }
        if (cabeca.getX() > LARGURA) {
            cabeca.setX(0);
        }
        if (cabeca.getY() > ALTURA) {
            cabeca.setY(0);
        }

    }

    private void requestRender() {

        render(g2d);
        Graphics g = getGraphics();
        g.drawImage(imagem, 0, 0, null);
        g.dispose();
    }

    public void render(Graphics2D g2d) {
        g2d.clearRect(0, 0, LARGURA, ALTURA);
        g2d.setColor(Color.GREEN);
        
        for (DoadoraDePartesxD corpo : cobra) {
            corpo.render(g2d);
        }
        
        g2d.setColor(Color.red);
        maca.render(g2d);
        if(gameover){
           g2d.drawString("Fim de Jogo!", 150, 200); 
           if(start){
               gameover = false;
           }
        }
         
        g2d.setColor(Color.WHITE);
        g2d.drawString("Pontos: " + pontos + " Level: " + level, 10, 10);
        
        if(dx == 0 && dy == 0){
           g2d.drawString("Pronto!", 150, 200); 
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        if (k == KeyEvent.VK_W) {
            cima = true;
        }
        if (k == KeyEvent.VK_S) {
            baixo = true;
        }
        if (k == KeyEvent.VK_D) {
            esquerda = true;
        }
        if (k == KeyEvent.VK_A) {
            direita = true;
        }
        if (k == KeyEvent.VK_ENTER) {
            start = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();

        if (k == KeyEvent.VK_W) {
            cima = false;
        }
        if (k == KeyEvent.VK_S) {
            baixo = false;
        }
        if (k == KeyEvent.VK_D) {
            esquerda = false;
        }
        if (k == KeyEvent.VK_A) {
            direita = false;
        }
        if (k == KeyEvent.VK_ENTER) {
            start = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
