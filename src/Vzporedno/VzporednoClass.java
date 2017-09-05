package Vzporedno;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.Scanner;
//import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class VzporednoClass extends JComponent implements ActionListener,KeyListener,MouseListener{

//	public static int WIDTH = 2560;
//	public static int HEIGHT = 1440;
	public static int WIDTH = 800;
	public static int HEIGHT = 600;
	public static int ITERATIONS = 100;
	public static long SCALE = 100;
	public static long SCALE_VAL = 100;
	
	public static int navpicno = 0;
	public static int vodoravno = 0;
		
	public static JFrame frame;
	
	public static BufferedImage buffer;
	
	private float hueOffset = 0;
	
	public static void main(String[] args) throws InterruptedException {
		new VzporednoClass();
	}
	
	public VzporednoClass() throws InterruptedException {
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	
		frame = new JFrame("Mandelbrot set - zap");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().add(this);
		frame.pack();
		frame.addKeyListener(this);
		frame.setVisible(true);
		
		renderSet();	
	}
	
	public void renderSet() throws InterruptedException {
//		SCALE += ITERATION_VAL;
		long time = System.currentTimeMillis();
//		ITERATION_VAL += 10;
		long start1 = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(4);
		for (int i = 0; i < WIDTH; i++) {
			Runnable worker = new VzporednoWorkerClass(i);
			executor.execute(worker);
		}
		executor.shutdown();
		while(!executor.isTerminated()){
			
		}
		frame.repaint();
		time = System.currentTimeMillis()-time;
//		System.out.println("Porabljen cas: " + time + "ms");
	}
	
	
	
	@Override
	public void addNotify() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
	@Override
	public void paint(Graphics g){
		g.drawImage(buffer, 0, 0, null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
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
