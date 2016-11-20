/**
 * @author djeck
 * @date 20/11/2016
 * @file Embranchement.java
 * Simple maillon de la liste chainée
 */
package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Embranchement {
	private String type;// "ordre" ou "embranchement" ...
	private String nom;// animal, vegetal,... papillon...
	private String descriptif;

	public Embranchement origine = null;// parent
	private int x = 0, y = 0;// position pour l'affichage

	private String imagePath = null;
	private Image image;

	private static int largeur = 250, hauteur = 60;

	private boolean selection = false;

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (origine != null) {
			if (selection) // couleur si en cour de placement
				g.setColor(Color.red);
			else
				g.setColor(Color.black);
			g.drawLine(x + largeur / 2, y, origine.getX() + largeur / 2,
					origine.getY() + hauteur);
		}

		if (selection) // couleur si en cour de placement
			g.setColor(Color.gray);
		else
			g.setColor(Color.black);
		g.fillRoundRect(x, y, largeur, hauteur, 5, 5);

		g.setColor(Color.white);
		g.drawString(type, x + 15, y + 10);
		g.drawString(nom, x + 10, y + 35);
		g.drawString(descriptif, x + 5, y + 50);

		if (image != null)
			g2d.drawImage(image, x + 190, y + 5, 50, 50, null);

	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Embranchement(Embranchement origine, String type,
			String embranchement, String descriptif, String imagePath) {
		this.type = type;
		this.nom = embranchement;
		this.descriptif = descriptif;
		this.origine = origine;
		this.imagePath = imagePath;
		loadImage();
	}

	private void loadImage() {
		try {
			image = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
			image = null;
		}
	}

	public String getDescriptif() {
		return descriptif;
	}

	public String getEmbranchement() {
		return nom;
	}

	public void setEmbranchement(String embranchement) {
		this.nom = embranchement;
	}

	public static void setTaille(int largeur, int hauteur) {
		Embranchement.largeur = largeur;
		Embranchement.hauteur = hauteur;
	}

	public boolean mouseCollision(int mx, int my) {
		boolean haut = (my < y);// la souris est au dessu du bloc
		boolean bas = (my > y + hauteur);
		boolean droite = (mx > x + largeur);
		boolean gauche = (mx < x);
		return !haut && !bas && !droite && !gauche;
	}

	public void setSelected(boolean selection) {
		this.selection = selection;
	}

	public String getType() {
		return type;
	}

	public String getImagePath() {
		return imagePath;
	}
}
