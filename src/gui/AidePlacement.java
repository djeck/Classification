package gui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author djeck
 *	ligne verticale ou horizontale pour l'aide au placement
 */
public class AidePlacement {
	private int x, y;
	boolean visible = false;
	enum MODE {X, Y};
	private MODE mode = MODE.X;
	private int largeur, hauteur;
	
	public AidePlacement(int hauteur, int largeur) {
		this.largeur = largeur;
		this.hauteur = hauteur;
	}
	public void draw(Graphics g) {
		g.setColor(Color.green);
		if(visible)
		{
			switch(mode) {
			case X:
				g.drawLine(x, 0, x, hauteur);
				break;
			case Y:
				g.drawLine(0, y, largeur, y);
			}
		}
	}
	public void setX(int x) {
		this.x =x;
		mode = MODE.X;
	}
	public void setY(int y) {
		this.y =y;
		mode = MODE.Y;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public void setLargeur(int largeur) {
		this.largeur = largeur;
	}
	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
	}
}
