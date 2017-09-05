package Vzporedno;

import java.awt.Color;

public class VzporednoWorkerClass implements Runnable{

	private int X;
	public VzporednoWorkerClass(int i) {
		X = i;
	}
	
	@Override
	public void run() {
//		System.out.println(Thread.currentThread().getName() + " start" + this.X);
		calculate();	
//		System.out.println(Thread.currentThread().getName() + " end");
	}

	private void calculate() {
		for (int y = 0; y < 600; y++) {
			float x_tmp = (this.X - VzporednoClass.WIDTH/2f+VzporednoClass.vodoravno)/VzporednoClass.SCALE;
			float y_tmp = (y- VzporednoClass.HEIGHT/2f+VzporednoClass.navpicno)/VzporednoClass.SCALE;
			int color = calcColor(x_tmp,y_tmp);
			VzporednoClass.buffer.setRGB(this.X, y, color);
		}		
	}
	
	public int calcColor(float x, float y){
		float cx = x;
		float cy = y;
		int i = 0;

		for (; i < VzporednoClass.ITERATIONS; i++) {
			float nx = x*x - y*y +cx;
			float ny = 2*x*y +cy;
			x = (nx);
			y = (ny);
			if(x*x + y*y >4)break;
		}
		if(i == VzporednoClass.ITERATIONS) return 0x00000000;
		return Color.HSBtoRGB(((float)i / VzporednoClass.ITERATIONS)%1f, 0.55555f, 1);
	}
	
}
