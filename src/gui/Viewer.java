package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import core.Embranchement;

// TODO Viewer avec information et edit
public class Viewer extends JPanel {
	private static final long serialVersionUID = 7755580827736664445L;
	private Embranchement selection;
	private BufferedImage image;
	private int width, height;
	
	public Viewer(Embranchement selection) {
		this.selection = selection;
		try {
			image = ImageIO.read(new File(this.selection.getImagePath()));
		} catch(IOException e) {
			e.printStackTrace();
			image = null;
		}
		//reglage selon la hauteur = fit Width
		width = 600;// TODO: detection hauteur la plus apropriee
		height = (int)(image.getHeight() * width/(double)image.getWidth());
	}
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		if(image != null)
			((Graphics2D)g).drawImage(image, 0, 0, width, height, null);
	}

}
