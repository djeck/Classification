/**
 * @author djeck
 * L'affichage est fait sur une image qui est ensuite dessine a l'ecran.
 */

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.Embranchement;

public class Drawer extends JLabel implements MouseMotionListener, MouseListener, ComponentListener, KeyListener{
	private static final long serialVersionUID = -8085388376890918612L;
	private static int ecranHauteur=2000, ecranLargeur=2000;
	private static int ecranCurseurX=0, ecranCurseurY=0;
	private static int ecranHauteurZoom=ecranHauteur, ecranLargeurZoom=ecranLargeur;
	
	private ArrayList<Embranchement> array;
	private Embranchement selection = null;
	private boolean move = false;
	private AidePlacement aidePlacementX;
	private AidePlacement aidePlacementY;
	
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder db;
	private Document doc;
	private BufferedImage ecran;
	private Graphics2D g2d;
	
	//fait la projection d'une coordonee de l'ecran sur l'image
	public int convertXToRelative(int x) {
		int reX = (int)((x - 3 - ecranCurseurX)*ecranLargeur/(double)ecranLargeurZoom);
		return reX;
	}
	public int convertYToRelative(int y) {
		int reY = (int)((y - 45 - ecranCurseurY)*ecranHauteur/(double)ecranHauteurZoom);
		return reY;
	}
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
		
		array = new ArrayList<Embranchement>();
		aidePlacementX = new AidePlacement(this.getWidth(), this.getHeight());
		aidePlacementY = new AidePlacement(this.getWidth(), this.getHeight());
	}
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.clearRect(0, 0, ecranLargeur, ecranHauteur);

		// TODO: desiner les embranchements seulement fils de la selection et que sur 3-4 iterations
		for (Embranchement obj : array) {
			obj.draw(g2d);
		}
		g2d.setColor(Color.red);
		((Graphics2D)g).drawImage(ecran, ecranCurseurX, ecranCurseurY, ecranLargeurZoom,ecranHauteurZoom, null);
		
		aidePlacementX.draw(g);
		aidePlacementY.draw(g);
	}
	/**
	 * Affiche un pop-up pour demander a saisir un element 
	 * @param parent noeud parent de l'element a ajouter, null si racine
	 */
	public void addComponentForm(Embranchement parent) {
	    JTextField type = new JTextField("");
	    JTextField nom = new JTextField("");
	    JTextField description = new JTextField("");
	    String imagePath ="noimg.png"; // image lorsque aucune n'est preciser
	    File fichier;
	    
	    JPanel panel = new JPanel(new GridLayout(0, 2));
	    Embranchement sortie;
	    if(parent != null) { // relie a la racine
	    	panel.add(new JLabel("Branche de:"));
	    	panel.add(new JLabel(parent.getEmbranchement()));
	    	panel.add(new JLabel("Sous famille des:"));
	    	panel.add(new JLabel(parent.getType()));
	    }
	    
	    panel.add(new JLabel("Type:"));
	    panel.add(type);
	    panel.add(new JLabel("Nom:"));
	    panel.add(nom);
	    panel.add(new JLabel("Description:"));
	    panel.add(description);
	    
	   int result = JOptionPane.showConfirmDialog(null, panel, "Ajout bloc",
	        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    if (result == JOptionPane.OK_OPTION) {
	    	JFileChooser chooser = new JFileChooser();
	    	chooser.setCurrentDirectory(new File("./"));
	    	int returnVal = chooser.showOpenDialog(null);
	    	if(returnVal == JFileChooser.FILES_ONLY) {
	    		fichier = chooser.getSelectedFile();
	    		imagePath = fichier.getPath();
	    	}
	        System.out.println("Ajout" + type.getText()
	            + " " + nom.getText()
	            + " " + description.getText());
	        sortie = new Embranchement(parent, type.getText(), nom.getText(), description.getText(),imagePath);
	        array.add(sortie);
	        selection = sortie;
	        selection.setSelected(true);
	        move = true;
	        repaint();
	    } else {
	        System.out.println("Cancelled");
	    }
	}
	public void loadFromFile(String filePath) {
		File inputFile = new File(filePath);
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(inputFile);
			Node root = doc.getFirstChild();
			
			parseEmbranchement(root.getChildNodes(), null);
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		repaint();
	}
	private void parseEmbranchement(NodeList embranchementList, Embranchement origine) {
		for(int z = 0; z < embranchementList.getLength(); z++) {
			Node embranchement = embranchementList.item(z);
			if(embranchement.getNodeType() == Node.ELEMENT_NODE) {
				Element eEmbranchement = (Element) embranchement;
				System.out.println("\n\tEmbranchement nodeName: "+eEmbranchement.getNodeName());
				System.out.println("\tEmbranchement txtContent: "+eEmbranchement.getTextContent());
				System.out.println("\tEmbranchement Description: "+eEmbranchement.getAttribute("description"));
				System.out.println("\tEmbranchement nom: "+eEmbranchement.getAttribute("nom"));
				System.out.println("\tEmbranchement x: "+eEmbranchement.getAttribute("x"));
				System.out.println("\tEmbranchement y: "+eEmbranchement.getAttribute("y"));
				System.out.println("\tEmbranchement image: "+eEmbranchement.getAttribute("image"));
				Embranchement oEmbranchement = new Embranchement(origine, eEmbranchement.getAttribute("nom"), eEmbranchement.getNodeName(), eEmbranchement.getAttribute("description"), eEmbranchement.getAttribute("image"));
				oEmbranchement.setPosition(Integer.parseInt(eEmbranchement.getAttribute("x")), Integer.parseInt(eEmbranchement.getAttribute("y")));
				array.add(oEmbranchement);
				parseEmbranchement(embranchement.getChildNodes(), oEmbranchement);
			}
		}
	}
	public void saveAs(String filePath) {
		dbFactory = DocumentBuilderFactory.newInstance();
		try {
			// creation de l'XML
			db = dbFactory.newDocumentBuilder();
			doc = db.newDocument();

			Element root = doc.createElement("root");
			doc.appendChild(root);
			
			saveAs_child(null, root);
			
			// sauvegarde dans le fichier
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			Result output = new StreamResult(new File(filePath));
			Source input = new DOMSource(doc);
			transformer.transform(input, output);
			
			// affichage (debug)
			StreamResult term = new StreamResult(System.out);
			transformer.transform(input, term);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	private void saveAs_child(Embranchement parent, Element parentNode) {
		for(Embranchement obj : array) {
			if(obj.origine == parent) { // on a trouve un enfant
				Element enfant = doc.createElement(obj.getEmbranchement());
				parentNode.appendChild(enfant);
				Attr enfantAttr = doc.createAttribute("description");
				enfantAttr.setValue(obj.getDescriptif());
				enfant.setAttributeNode(enfantAttr);
				enfantAttr = doc.createAttribute("x");
				enfantAttr.setValue(Integer.toString(obj.getX()));
				enfant.setAttributeNode(enfantAttr);
				enfantAttr = doc.createAttribute("y");
				enfantAttr.setValue(Integer.toString(obj.getY()));
				enfant.setAttributeNode(enfantAttr);
				enfantAttr = doc.createAttribute("nom");
				enfantAttr.setValue(obj.getType());
				enfant.setAttributeNode(enfantAttr);
				enfantAttr = doc.createAttribute("image");
				enfantAttr.setValue(obj.getImagePath());
				enfant.setAttributeNode(enfantAttr);
				
				saveAs_child(obj, enfant);
			}
		}
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
			for (Embranchement obj : array) { // aide au placement
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
		
		if (selection != null && !moveBloc(x,y)) { // on deselectionne
			selection.setSelected(false);
			selection = null;
			move = false;
			aidePlacementX.setVisible(false);
			aidePlacementY.setVisible(false);
			repaint();
		} else {
			switch(arg0.getButton()) {
			case 1: // clic gauche
				selectBloc(x, y);
				break;
			case 3: // clic droit
				foundMatch = false;
				for(Embranchement obj : array) {
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
	private void selectBloc(int x, int y){
		selection = null;
		for(Embranchement obj : array) {
			if(obj.mouseCollision(x, y)) {
				selection = obj;
				break;
			}
		}
		if(selection == null) // impossible de trouver une collision
			return;
		selection.setSelected(true);
		repaint();
	}
	private boolean moveBloc(int x, int y) {
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
	private void remove(Embranchement element) {
		for(Embranchement obj : array) {
			if(obj.origine == element)
				remove(obj);
		}
		array.remove(element);
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
		default:
			System.out.println("La touche "+arg0.getKeyChar()+" (KeyCode:"+arg0.getKeyCode()+") ne fait rien en mode lecture");
		}
	}
}
