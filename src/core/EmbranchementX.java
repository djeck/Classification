package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @brief Blocs du Graphiques
 * @author djeck
 */
public class EmbranchementX extends Embranchement implements Drawable {
	
	private static int largeur = 250, hauteur = 60;
	private Image image;
	private boolean selection = false;


	public EmbranchementX(Embranchement origine, String type,
			String embranchement, String descriptif, String imagePath) {
		super(origine, type, embranchement, descriptif, imagePath);
		loadImage();
	}
	public EmbranchementX(Embranchement model) {
		super(model.getParent(), model.getType(), model.getEmbranchement(), model.getDescriptif(), model.getImagePath());
		setPosition(model.getX(), model.getY());
		loadImage();
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (origine != null) {
			if (selection) // couleur si en cour de placement
				g.setColor(Color.red);
			else
				g.setColor(Color.black);
			g.drawLine(super.x + largeur / 2, super.y, origine.getX() + largeur / 2,
					origine.getY() + hauteur);
		}

		if (selection) // couleur si en cour de placement
			g.setColor(Color.gray);
		else
			g.setColor(Color.black);
		g.fillRoundRect(super.x, super.y, largeur, hauteur, 5, 5);

		g.setColor(Color.white);
		g.drawString(type, super.x + 15, super.y + 10);
		g.drawString(nom, super.x + 10, super.y + 35);
		g.drawString(descriptif, super.x + 5, super.y + 50);

		if (image != null)
			g2d.drawImage(image, super.x + 190, super.y + 5, 50, 50, null);

	}

	private void loadImage() {
		try {
			if(super.imagePath.length()>3)
				image = ImageIO.read(new File(super.imagePath));
		} catch (IOException e) {
			image = null;
		}
	}
	
	public static void setTaille(int largeur, int hauteur) {
		EmbranchementX.largeur = largeur;
		EmbranchementX.hauteur = hauteur;
	}
	@Override
	public boolean mouseCollision(int mx, int my) {
		boolean haut = (my < super.y);// la souris est au dessu du bloc
		boolean bas = (my > super.y + hauteur);
		boolean droite = (mx > super.x + largeur);
		boolean gauche = (mx < super.x);
		return !haut && !bas && !droite && !gauche;
	}
	public void setSelected(boolean selection) {
		this.selection = selection;
	}
	public String toString() {
		String str = new String();
		str += super.toString();
		str += "EmbranchementX type: "+type+"\n";
		str += "EmbranchementX nom: "+nom+"\n";
		str += "EmbranchementX descriptif: "+descriptif+"\n";
		str += "EmbranchementX x:y: "+x+":"+y+"\n";
		str += "EmbranchementX ImagePath: "+imagePath;
		return str;
	}
	
}
