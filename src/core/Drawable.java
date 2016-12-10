package core;

import java.awt.Graphics;

public interface Drawable {
	public void draw(Graphics g);
	public boolean mouseCollision(int mx, int my);
}