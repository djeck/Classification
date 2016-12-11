package gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.Embranchement;

// TODO Viewer avec informations et edit
public class Viewer extends JPanel {
	private static final long serialVersionUID = 7755580827736664445L;
	private Embranchement selection;
	private BufferedImage image;
	private int width, height;
	private JTextArea text;
	private JScrollPane mScrollPane;
	
	public Viewer(Embranchement selection) {
		this.setLayout(new BorderLayout());
		this.selection = selection;
		text = new JTextArea();
		text.setEditable(false);
		mScrollPane = new JScrollPane(text);
		
		try {
			image = ImageIO.read(new File(this.selection.getImagePath()));
		} catch(IOException e) {
			//e.printStackTrace();
			image = null;
		}
		
		//reglage selon la hauteur = fit Width
		width = 600;// TODO: detection hauteur la plus apropriee
		if(image == null)
			height = 800;
		else
			height = (int)(image.getHeight() * width/(double)image.getWidth());
		
		this.add(mScrollPane, BorderLayout.SOUTH);
		Embranchement buff=selection;
		while(buff!=null) {
			text.append(buff.getType()+": "+buff.getEmbranchement()+"\n");
			buff = buff.getParent();
		}
	}
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		if(image != null)
			((Graphics2D)g).drawImage(image, 0, 0, width, height, null);
	}

}
