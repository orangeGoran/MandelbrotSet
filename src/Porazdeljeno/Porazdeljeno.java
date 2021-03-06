package Porazdeljeno;

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

import mpi.*; //ty


public class Porazdeljeno extends JComponent implements ActionListener,KeyListener,MouseListener{

	public static int WIDTH = 2560;
	public static int HEIGHT = 1440;
	private static int id;
	private static int size;
//	public static int WIDTH = 800;
//	public static int HEIGHT = 600;
	public static int ITERATIONS = 100;
	public static long SCALE = 100;
	public static long SCALE_VAL = 100;
	
	public static int navpicno = 0;
	public static int vodoravno = 0;
	
	public JFrame frame;
	
	public static BufferedImage buffer;
//	private javax.swing.Timer timer;
	
	private float hueOffset = 0;
	
	public static void main(String[] args) throws InterruptedException {
		MPI.Init(args);
		
		id = MPI.COMM_WORLD.Rank();
		size = MPI.COMM_WORLD.Size();
		
		if(id == 0){ //4 proccess
			int[] fromTo = new int[4];
			long[] scalling = new long[1];
			scalling[0] = SCALE;
			fromTo[0] = 0;
			fromTo[1] = WIDTH/3;
			fromTo[2] = vodoravno;
			fromTo[3] = navpicno;

			MPI.COMM_WORLD.Send(fromTo, 0, 4, MPI.INT, 1, 0);
			MPI.COMM_WORLD.Send(scalling, 0, 1, MPI.LONG, 1, 0);
			
			fromTo[0] = WIDTH/3;
			fromTo[1] = WIDTH/3*2;
			fromTo[2] = vodoravno;
			fromTo[3] = navpicno;
			MPI.COMM_WORLD.Send(fromTo, 0, 4, MPI.INT, 2, 0);
			MPI.COMM_WORLD.Send(scalling, 0, 1, MPI.LONG, 2, 0);
			
			fromTo[0] = WIDTH/3*2;
			fromTo[1] = WIDTH;
			fromTo[2] = vodoravno;
			fromTo[3] = navpicno;
			MPI.COMM_WORLD.Send(fromTo, 0, 4, MPI.INT, 3, 0);
			MPI.COMM_WORLD.Send(scalling, 0, 1, MPI.LONG, 3, 0);
			
			int[][] colors1 = new int [WIDTH][HEIGHT];
			int[][] colors2 = new int [WIDTH][HEIGHT];
			int[][] colors3 = new int [WIDTH][HEIGHT];
			int[][] colors0 =  new int [WIDTH][HEIGHT];
			MPI.COMM_WORLD.Recv(colors1, 0, WIDTH, MPI.OBJECT, 1, 1);
			for (int x = 0; x < WIDTH/3; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					colors0[x][y] = colors1[x][y];	
				}
			}
			MPI.COMM_WORLD.Recv(colors2, 0, WIDTH, MPI.OBJECT, 2, 1);
			for (int x = WIDTH/3; x < WIDTH/3*2; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					colors0[x][y] = colors2[x][y];	
				}
			}
			MPI.COMM_WORLD.Recv(colors3, 0, WIDTH, MPI.OBJECT, 3, 1);
			for (int x = WIDTH/3*2; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					colors0[x][y] = colors3[x][y];	
				}
			}			
			new Porazdeljeno(colors0);
		}else {
			while(true){
				int[] buffer = new int[4];
				long scalling[] = new long[1];
				MPI.COMM_WORLD.Recv(buffer, 0, 4, MPI.INT, 0, 0);
				MPI.COMM_WORLD.Recv(scalling, 0, 1, MPI.LONG, 0, 0);
				
				System.out.println("ID: " +id + ", od " + buffer[0] + " do " + buffer[1] + " vodoravno " + buffer[2] + " navpicno " + buffer[3] + " scalling" + scalling[0]);
				
				int[][] finalColorsForMe = renderSet1(buffer[0], buffer[1], buffer[2], buffer[3], scalling[0]);
				MPI.COMM_WORLD.Send(finalColorsForMe, 0, WIDTH, MPI.OBJECT, 0, 1);
			}
			
			
		}
		MPI.Finalize(); 
	}
	
	public Porazdeljeno(int [][] colors) throws InterruptedException {
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	
		frame = new JFrame("Mandelbrot set - porazdeljeno");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().add(this);
		frame.pack();
		frame.addKeyListener(this);
//		frame.addMouseListener(this);
		frame.setVisible(true);
		long sum = 0;
		int i = 0;
		long time = System.currentTimeMillis();
		renderSet(colors);
		time = System.currentTimeMillis()-time;
	
	}
	
	public static int[][] renderSet1(int from, int to, int horizontal, int vertical, long scalling) throws InterruptedException { //called only from workers
		int[][] colors = new int[WIDTH][HEIGHT];
		for (int x = from; x < to; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				float x_tmp = (x - WIDTH/2f+horizontal)/scalling;
				float y_tmp = (y- HEIGHT/2f+vertical)/scalling;
				colors[x][y] = calcColor(x_tmp,y_tmp);
			}
		}
		return colors;
	}
	
	public void renderSet(int [][] colors) throws InterruptedException {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				buffer.setRGB(x, y, colors[x][y]);				
				frame.repaint();
			}			
		}
	}
	
	public static int calcColor(float x, float y){
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
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyCode());
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
		System.out.println("evo me" + id);
		int[] fromTo = new int[4];
		long[] scalling = new long[1];
		scalling[0] = SCALE;
		fromTo[0] = 0;
		fromTo[1] = WIDTH/3;
		fromTo[2] = vodoravno;
		fromTo[3] = navpicno;

		MPI.COMM_WORLD.Send(fromTo, 0, 4, MPI.INT, 1, 0);
		MPI.COMM_WORLD.Send(scalling, 0, 1, MPI.LONG, 1, 0);
		
		fromTo[0] = WIDTH/3;
		fromTo[1] = WIDTH/3*2;
		fromTo[2] = vodoravno;
		fromTo[3] = navpicno;
		MPI.COMM_WORLD.Send(fromTo, 0, 4, MPI.INT, 2, 0);
		MPI.COMM_WORLD.Send(scalling, 0, 1, MPI.LONG, 2, 0);
		
		fromTo[0] = WIDTH/3*2;
		fromTo[1] = WIDTH;
		fromTo[2] = vodoravno;
		fromTo[3] = navpicno;
		MPI.COMM_WORLD.Send(fromTo, 0, 4, MPI.INT, 3, 0);
		MPI.COMM_WORLD.Send(scalling, 0, 1, MPI.LONG, 3, 0);
		
		int[][] colors1 = new int [WIDTH][HEIGHT];
		int[][] colors2 = new int [WIDTH][HEIGHT];
		int[][] colors3 = new int [WIDTH][HEIGHT];
		int[][] colors0 =  new int [WIDTH][HEIGHT];
		MPI.COMM_WORLD.Recv(colors1, 0, WIDTH, MPI.OBJECT, 1, 1);
		for (int x = 0; x < WIDTH/3; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				colors0[x][y] = colors1[x][y];	
			}
		}
		MPI.COMM_WORLD.Recv(colors2, 0, WIDTH, MPI.OBJECT, 2, 1);
		for (int x = WIDTH/3; x < WIDTH/3*2; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				colors0[x][y] = colors2[x][y];	
			}
		}
		MPI.COMM_WORLD.Recv(colors3, 0, WIDTH, MPI.OBJECT, 3, 1);
		for (int x = WIDTH/3*2; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				colors0[x][y] = colors3[x][y];	
			}
		}
		try {
			renderSet(colors0);
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
