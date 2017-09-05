import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Timer;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class Zaporedno1 extends JComponent implements ActionListener,KeyListener{

	public static int WIDTH = 1200;
	public static int HEIGHT = 800;
	public static int ITERATIONS = 20;
	public static long SCALE = 1;
	public static long ITERATION_VAL = 50;
	
	public static int navpicno = 1;
	public static int vodoravno = 1;
	
	public JFrame frame;
	
	private BufferedImage buffer;
	private javax.swing.Timer timer;
	
	
	public static void main(String[] args) {
		new Zaporedno1();
	}
	
	public Zaporedno1() {
		timer = new javax.swing.Timer(10, this);
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		renderSet();
		frame = new JFrame("Mandelbrot set - zap");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().add(this);
		frame.pack();
		frame.addKeyListener(this);
		frame.setVisible(true);		
		
	}
	
	public void renderSet() {
		SCALE += ITERATION_VAL;
		ITERATION_VAL += 10;
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				
				int color = calcColor((x+vodoravno - WIDTH/2f)/SCALE, (y+navpicno - HEIGHT/2f)/SCALE);
				buffer.setRGB(x, y, color);
			}
		}
	}
	
	public int calcColor(float x, float y){
		float cx = x;
		float cy = y;
		int i = 0;
		
		for (; i < ITERATIONS; i++) {
			float nx = x*x - y*y +cx;
			float ny = 2*x*y +cy;
			x = (nx);
			y = (ny);
			if(x*x + y*y >4)break;
		}
		if(i == ITERATIONS) return 0x00000000;
		return Color.HSBtoRGB(((float)i / ITERATIONS)%1f, 0.5f, 1);
	}
	
	@Override
	public void addNotify() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		timer.start();
	}
	
	@Override
	public void paint(Graphics g){
//		System.out.println("paint");
		g.drawImage(buffer, 0, 0, null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
//		System.out.println(event);
		System.out.println("TICK");
//		hueOffset += 0.01f;
		renderSet();
		frame.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println(e.getKeyCode());
		switch (e.getKeyCode()) {
		case 37: //levo
			vodoravno -= 10;
			break;
		case 39: //desno
			vodoravno += 10;		
			break;
		case 38: //gor
			navpicno += 10;
			break;
		case 40: //dol
			navpicno-= 10;
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
//		System.out.println(e);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
