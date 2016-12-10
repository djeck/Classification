package core;

import java.awt.Graphics2D;


public abstract class IterableFunction {
	private Graphics2D g;
	private int mx, my;
	public IterableFunction(Graphics2D g) {
		this.g=g;
	}
	
	public IterableFunction() {
	}
	
	public IterableFunction(int mx, int my) {
		this.mx = mx;
		this.my = my;
	}
	
	public abstract EmbranchementX execute(EmbranchementX obj);
}
