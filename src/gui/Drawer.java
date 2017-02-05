/**
 * @author djeck
 * L'affichage est fait sur une image qui est ensuite dessine a l'ecran.
 */

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import core.ComponentArray;
import core.Embranchement;
import core.EmbranchementX;

public class Drawer extends JLabel implements MouseMotionListener, MouseListener, ComponentListener, KeyListener{
	private static final long serialVersionUID = -8085388376890918612L;
	private static int ecranHauteur=2000, ecranLargeur=2000;
	private static int ecranCurseurX=0, ecranCurseurY=0;
	private static int ecranHauteurZoom=ecranHauteur, ecranLargeurZoom=ecranLargeur;
	

	private EmbranchementX selection = null;
	private EmbranchementX selectionVue = null; // la vue est centree sur cette selection et ses branches
	enum SelectionType {ALL, OVER, UNDER};
	private SelectionType selectionType = SelectionType.UNDER;
	private boolean move = false;
	private AidePlacement aidePlacementX;
	private AidePlacement aidePlacementY;
	private ComponentArray mArray;

	private BufferedImage ecran;
	private Graphics2D g2d;
	
	private String mFilePath;
	
	
	
	//fait la projection d'une coordonee de l'ecran sur l'image
	public int convertXToRelative(int x) {
		int reX = (int)((x - 3 - ecranCurseurX)*ecranLargeur/(double)ecranLargeurZoom);
		return reX;
	}
	public int convertYToRelative(int y) {
		int reY = (int)((y - 45 - ecranCurseurY)*ecranHauteur/(double)ecranHauteurZoom);
		return reY;
	}
	
	//fait la projection d'une coordonee de l'image sur l'ecran
	public int convertXToAbsolute(int x) {
		int absX = (int) (x*ecranLargeurZoom/(double)ecranLargeur)+3+ecranCurseurX;
		return absX;
	}
	public int convertYToAbsolute(int y) {
		int absY = (int) (y*ecranHauteurZoom/(double)ecranHauteur)+45+ecranCurseurY;
		return absY;
	}
	
	public Drawer() {
		super();
		ecran = new BufferedImage(ecranLargeur, ecranHauteur, BufferedImage.TYPE_INT_ARGB);
		g2d = ecran.createGraphics();
		g2d.setBackground(Color.white);
		mArray = new ComponentArray();
		mFilePath = "output.xml";
		
		aidePlacementX = new AidePlacement(this.getWidth(), this.getHeight());
		aidePlacementY = new AidePlacement(this.getWidth(), this.getHeight());
	}
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.clearRect(0, 0, ecranLargeur, ecranHauteur);

		mArray.resetIteration();
		switch(selectionType)
		{
		case UNDER:
			mArray.paintComponentUnder(selectionVue, g2d);
			break;
		case OVER:
			mArray.paintComponentOver(selectionVue, g2d);
			break;
		default:
			mArray.paintComponentAll(g2d);
		}
		
		//quoi qu'il arrive on s'assure de voir la selection
		if(selection!=null)
			selection.draw(g2d);
		
		g2d.setColor(Color.red);
		((Graphics2D)g).drawImage(ecran, ecranCurseurX, ecranCurseurY, ecranLargeurZoom,ecranHauteurZoom, null);
		
		aidePlacementX.draw(g);
		aidePlacementY.draw(g);
	}
	
	/**
	 * Affiche un pop-up pour demander a saisir un element 
	 * @param parent noeud parent de l'element a ajouter, null si racine
	 */
	public void addComponentForm(EmbranchementX parent) {
		selection = mArray.addComponentForm(parent);
		if(selection != null)
			move = true;
		repaint();
	    
	}
	public void loadFromFile(String filePath) {
		String buff = filePath;
		if (buff == null) { // use default file path
			buff = mFilePath;
		}
		if(mArray.loadFromFile(buff)) {
			mFilePath = buff; // keep file path somewhere
		}
		repaint();
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		int mouseX = convertXToRelative(arg0.getX());
		int mouseY = convertYToRelative(arg0.getY());
		int bX, bY;//meilleur position ou positionner le bloc
		boolean aideUtileX, aideUtileY;
		
		if(move && selection != null) {
			aideUtileX = false;
			aideUtileY = false;
			bX = mouseX;
			bY = mouseY;
			for (Embranchement obj : mArray.getArray()) { // aide au placement
				if(obj != selection)
				{
					if(Math.abs(obj.getX()-mouseX)<30) { // proche de l'axe x d'un autre bloc
						bX = obj.getX();
						aidePlacementX.setX(convertXToAbsolute(bX)-3);
						aideUtileX = true;
					}
					if(Math.abs(obj.getY()-mouseY)<30) { // proche de l'axe y d'un autre bloc
						bY = obj.getY();
						aidePlacementY.setY(convertYToAbsolute(bY)-45);
						aideUtileY = true;
					}
				}
			}
			
			aidePlacementX.setVisible(aideUtileX); // si l'aide au placement est utilse on la met
			aidePlacementY.setVisible(aideUtileY);
			
			selection.setPosition(bX, bY);
			repaint();
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		int x = convertXToRelative(arg0.getX());
		int y = convertYToRelative(arg0.getY());
		repaint();
		
		boolean foundMatch;
		
		if (selection != null && !moveBloc(selection, x, y)) { // on deselectionne si on clic pas sur le bloc déjà selectionné (ou en mouvement)
			selection.setSelected(false);
			selection = null;
			move = false;
			aidePlacementX.setVisible(false);
			aidePlacementY.setVisible(false);
			repaint();
		} else {
			switch(arg0.getButton()) {
			case 1: // clic gauche
				switch(selectionType)
				{
				case UNDER:
					selection = mArray.selectComponentUnder(selectionVue, x, y);
					break;
				case OVER:
					selection = mArray.selectComponentOver(selectionVue, x, y);
					break;
				default:
					selection = mArray.selectComponentAll(x, y);
				}
				repaint();
				break;
			case 3: // clic droit
				foundMatch = false;
				for(EmbranchementX obj : mArray.getArray()) {
					if(obj.mouseCollision(x, y)) {
						foundMatch = true;// on a clique sur une branche (ou racine)
						addComponentForm(obj);
						break;
					}
				}
				if(!foundMatch)
					addComponentForm(null);// ajoute une racine
				break;
			default:
				System.out.println("Le bouton de la souris: "+arg0.getButton()+" ne fait rien");
			}
		}
			
	}
	private boolean moveBloc(EmbranchementX selection, int x, int y) {
		if(selection.mouseCollision(x, y) && !move) { // si on clic de nouveau sur le bloc et que on ne le bouge pas deja
			selection.setPosition(x, y); // on met le bloc derriere le curseur (on le deplace)
			move = true;
			repaint();
			return true;
		}
		return false;
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		aidePlacementX.setLargeur(arg0.getComponent().getWidth());
		aidePlacementX.setHauteur(arg0.getComponent().getHeight());
		aidePlacementY.setLargeur(arg0.getComponent().getWidth());
		aidePlacementY.setHauteur(arg0.getComponent().getHeight());
		System.out.println("Resized: "+arg0.getComponent().getWidth()+"  "+arg0.getComponent().getHeight());
		
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()) {
		case 38: // haut
			ecranCurseurY -= 20;
			repaint();
			break;
		case 39: // droite
			ecranCurseurX += 20;
			repaint();
			break;
		case 40: // bas
			ecranCurseurY += 20;
			repaint();
			break;
		case 37: // gauche
			ecranCurseurX -= 20;
			repaint();
			break;
		default:
			System.out.println(arg0.getKeyCode());
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}
	
	// supprime element et ses enfants
	private void remove(EmbranchementX element) {
		mArray.remove(element);
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		if(selection != null) // on a une selection: mode selection
			keyTypedSelection(arg0);
		else  // mode lecture
			keyTypedLecture(arg0);
	}
	
	public void keyTypedSelection(KeyEvent arg0) {
		switch(arg0.getKeyChar()) {
		case 'x':
			remove(selection);
			selection = null;
			move = false;
			aidePlacementX.setVisible(false);
			aidePlacementY.setVisible(false);
			repaint();
			break;
		case 'r': // refresh
			repaint();
			break;
		case 'e': // edit
			UIManager.put("OptionPane.minimumSize",new Dimension(600,800)); 
			int result = JOptionPane.showConfirmDialog(null, new Viewer(selection), "Vue détaillée",
			        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			    if (result == JOptionPane.OK_OPTION) {
			        repaint();
			    } else {
			        System.out.println("Cancelled");
			    }
			break;
		case 'v':
			selectionVue = selection;
			selectionType = SelectionType.UNDER;
			repaint();
			break;
		case 'V':
			selectionVue = selection;
			selectionType = SelectionType.OVER;
			repaint();
			break;
		case 'o':
			selectionVue = selection;
			selectionType = SelectionType.OVER;
			repaint();
			break;
		case 'u':
			selectionVue = selection;
			selectionType = SelectionType.UNDER;
			repaint();
			break;
		case 'i':
			if(selection!=null) {
				System.out.println(selection.toString());
			}
			break;
		default:
			System.out.println("La touche "+arg0.getKeyChar()+" (KeyCode:"+arg0.getKeyCode()+") ne fait rien en mode selection");
		}
	}
	
	public void keyTypedLecture(KeyEvent arg0) {
		switch(arg0.getKeyChar()) {
		case '+':
			ecranHauteurZoom *= 1.1f;
		    ecranLargeurZoom *= 1.1f;
		    revalidate();     
		    repaint();
			break;
		case '-':
			ecranHauteurZoom *= 0.9f;
		    ecranLargeurZoom *= 0.9f;
		    revalidate();     
		    repaint();
			break;
		case 'i':
			System.out.println("Largeur image:"+ecranLargeur);
			System.out.println("Hauteur image:"+ecranHauteur);
			System.out.println("Largeur image zooméé:"+ecranLargeurZoom);
			System.out.println("Hauteur image zoomée:"+ecranHauteurZoom);
			System.out.println("Largeur Fenetre:"+getWidth());
			System.out.println("Hauteur Fenetre:"+getHeight());
			System.out.println("Position fenetre X:"+getX());
			System.out.println("Position fenetre Y:"+getY());
			System.out.println("curseur x:"+ecranCurseurX);
			System.out.println("curseur y:"+ecranCurseurY);
			break;
		case 'v':
			selectionVue = null;
			if(selectionType == SelectionType.OVER) // car il n'y a rien au dessus de null
				selectionType = SelectionType.ALL;
			repaint();
			break;
		default:
			System.out.println("La touche "+arg0.getKeyChar()+" (KeyCode:"+arg0.getKeyCode()+") ne fait rien en mode lecture");
		}
	}
	public void saveAs(String filePath) {
		mArray.saveAs(filePath);
	}
	public void openFileForm() {
		String filePath="output.xml";
		File file;
		JFileChooser chooser = new JFileChooser();
    	chooser.setCurrentDirectory(new File("./"));
    	int returnVal = chooser.showOpenDialog(null);
    	if(returnVal == JFileChooser.FILES_ONLY) {
    		file = chooser.getSelectedFile();
    		filePath = file.getPath();
    		loadFromFile(filePath);
    	}
	}
	public void saveAsForm() {
		String filePath="output.xml";
		File file;
		JFileChooser chooser = new JFileChooser();
    	chooser.setCurrentDirectory(new File("./"));
    	int returnVal = chooser.showOpenDialog(null);
    	if(returnVal == JFileChooser.FILES_ONLY) {
    		file = chooser.getSelectedFile();
    		filePath = file.getPath();
    	}
    	saveAs(filePath);
	}
	public void clearComponentArray() {
		mArray.clear();
		repaint();
	}
}
