import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
//import java.util.Scanner;
//import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class Zaporedno extends JComponent implements ActionListener,KeyListener,MouseListener{

//	public static int WIDTH = 2560;
//	public static int HEIGHT = 1440;
	public static int WIDTH = 800;
	public static int HEIGHT = 600;
	public static int ITERATIONS = 100;
	public static long SCALE = 100;
	public static long SCALE_VAL = 100;
	
	public static int navpicno = 0;
	public static int vodoravno = 0;
	
	public JFrame frame;
	
	private BufferedImage buffer;
//	private javax.swing.Timer timer;
	
	private float hueOffset = 0;
	
	public static void main(String[] args) throws InterruptedException {
		new Zaporedno();
	}
	
	public Zaporedno() throws InterruptedException {
//		 timer = new javax.swing.Timer(1, this);
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	
		frame = new JFrame("Mandelbrot set - zap");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().add(this);
		frame.pack();
		frame.addKeyListener(this);
//		frame.addMouseListener(this);
		frame.setVisible(true);
		long sum = 0;
		int i = 0;
		for (; i < 1; i++) { //rendiras le enkrat na zacetku (ne vem zakaj sem dal for loop)
			long time = System.currentTimeMillis();
			renderSet();
			time = System.currentTimeMillis()-time;
			sum += time;
		}
		sum = sum/i;
		System.out.println("Porabljen cas: " + sum + "ms");
	
	}
	
	public void renderSet() throws InterruptedException {
//		SCALE += ITERATION_VAL;
//		ITERATION_VAL += 10;
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				float x_tmp = (x - WIDTH/2f+vodoravno)/SCALE;
				float y_tmp = (y- HEIGHT/2f+navpicno)/SCALE;
				int color = calcColor(x_tmp,y_tmp);
				
				buffer.setRGB(x, y, color);				
				frame.repaint();
			}
//			TimeUnit.MILLISECONDS.sleep(1);
			
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
		return Color.HSBtoRGB(((float)i / ITERATIONS)%1f, 0.55555f, 1);
	}
	
	@Override
	public void addNotify() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		//timer.start();
	}
	
	@Override
	public void paint(Graphics g){
//		System.out.println("paint");
		g.drawImage(buffer, 0, 0, null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
//		String event = e.getActionCommand();
//		System.out.println(event);
//		System.out.println("TICK");
		hueOffset += 0.005f;
		try {
			renderSet();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		frame.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case 37: //levo
			vodoravno -= 100;
			break;
		case 39: //desno
			vodoravno += 100;
			break;
		case 38: //gor
			navpicno -= 100;
			break;
		case 40: //dol
			navpicno += 100;
			break;
		case 107: //plus
			SCALE += SCALE_VAL;
			SCALE_VAL *= 2;
			vodoravno *= 2;
			navpicno *= 2;
			break;
		case 109: //minus
			SCALE -= SCALE_VAL/2;
			SCALE_VAL/=2;;
			if(SCALE <100) {
				SCALE = 100;
				SCALE_VAL = 100;
				return;
			}else {
				vodoravno /= 2;
				navpicno /= 2;
			}
			break;
		default:
			return;
		}
		try {
			renderSet();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
