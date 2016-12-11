/**
 * @file componentarray.java
 */
package core;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

/**
 * @class ComponentArray
 * @brief A set of tools to manage the main "Embranchement" array
 * @author djeck
 */
public class ComponentArray {
	private ArrayList<EmbranchementX> mArray;/*!< all part of the graph = main "Embranchement" array*/
	
	//XML tools
	private DocumentBuilderFactory mDocumentFactory;
	private DocumentBuilder mDocumentBuilder;
	private Document mDocument;
	
	private int paintIteration = 0; /*!< count number of iteration when cover the main array TODO*/
	private static final int MAX_PAINT_ITERATION = 100;
	
	public ComponentArray() {
		mArray = new ArrayList<EmbranchementX>();
	}
	
	//TODO
	public void resetIteration() {
		paintIteration=0;
	}
	
	/**
	 * @brief generic method to cover the main array from parent bloc to all his children (all if 'parent' == null)
	 * @param parent starting point
	 * @param func, function to apply on all components, stop recursing if return != null
	 * @return component returned by 'func' or null
	 */
	private EmbranchementX coverComponentUnder(EmbranchementX parent, IterableFunction func) {
		EmbranchementX buff;
		if(parent !=null)
		{
			buff=func.execute(parent);
			if(buff!=null)
				return buff;
		}
		for (EmbranchementX obj : mArray) {
			if(obj.origine == parent)
			{
				buff=coverComponentUnder(obj, func);
				if(buff!=null)
					return buff;
			}
		}
		return null;
	}
	
	/**
	 * @brief generic method to cover the main array from 'fils' to all his direct parents
	 * @param fils, child: starting point of recursing
	 * @param func, function to apply on all components, stop covering if return != null
	 * @return component returned by 'func' or null if none
	 */
	private EmbranchementX coverComponentOver(EmbranchementX fils, IterableFunction func) {
		EmbranchementX result;
		EmbranchementX buff = fils;
		if(buff!=null) {
			result = func.execute(buff);
			if(result != null)
				return result;
		}
		while(buff!=null && buff.getParent()!=null)
		{
			buff=(EmbranchementX)buff.getParent();
			result = func.execute(buff);
			if(result != null)
				return result;
		}
		return null;
	}
	/**
	 * @brief generic method to cover all the main array
	 * @param func, function to apply on all components, stop covering if return != null
	 * @return component returned by 'func' or null
	 */
	private EmbranchementX coverComponentAll(IterableFunction func) {
		EmbranchementX result;
		for(EmbranchementX obj: mArray) {
			result = func.execute(obj);
			if(result != null)
				return result;
		}
		return null;
	}
	
	/**
	 * @brief draw all components under 'parent' (all if 'parent' == null)
	 * For more information see 'coverComponentUnder' witch implement covering the array
	 * @param g "pen to draw with"
	 */
	public void paintComponentUnder(EmbranchementX parent, Graphics2D g) {
		coverComponentUnder(parent, new IterableFunction(g){
			public EmbranchementX execute(EmbranchementX obj) {
				obj.draw(g);
				return null;
			}
		});
	}
	
	/**
	 * @brief cover and test collision with all components under 'parent' stopping when found a collision
	 * For more information see 'coverComponentUnder' witch implement covering the array
	 * @param mx, x position of the mouse or whatever point you want to test collision with
	 * @param my, y position of the mouse...
	 * @return the component with collision, null otherwise
	 */
	public EmbranchementX selectComponentUnder(EmbranchementX parent, int mx, int my) {
		EmbranchementX buff = null;
		buff = coverComponentUnder(parent, new IterableFunction(mx, my){
			public EmbranchementX execute(EmbranchementX obj) {
				if(obj.mouseCollision(mx, my))
					return obj;
				return null;
			}
		});
		if(buff!=null)
			buff.setSelected(true);
		return buff;
	}
	
	/**
	 * @brief draw all components over 'fils' (warning: nothing if 'fils' == null)
	 * For more information see 'coverComponentOver' witch implement covering the array
	 * @param g "pen to draw with"
	 */
	public void paintComponentOver(EmbranchementX fils, Graphics2D g) {
		coverComponentOver(fils, new IterableFunction(g){
			public EmbranchementX execute(EmbranchementX obj) {
				obj.draw(g);
				return null;
			}
		});
	}
	
	/**
	 * @brief cover and test collision with all components over 'fils' stopping when found a collision
	 * For more information see 'coverComponentOver' witch implement covering the array
	 * @param mx, x position of the mouse or whatever point you want to test collision with
	 * @param my, y position of the mouse...
	 * @return the component with collision, null otherwise
	 */
	public EmbranchementX selectComponentOver(EmbranchementX fils, int mx, int my) {
		EmbranchementX buff = null;
		buff = coverComponentOver(fils, new IterableFunction(mx, my){
			public EmbranchementX execute(EmbranchementX obj) {
				if(obj.mouseCollision(mx, my))
					return obj;
				return null;
			}
		});
		if(buff!=null)
			buff.setSelected(true);
		return buff;
	}
	
	/**
	 * @brief draw all components of the main array
	 * For more information see 'coverComponentAll' witch implement covering the array
	 * @param g "pen to draw with"
	 */
	public void paintComponentAll(Graphics2D g) {
		coverComponentAll(new IterableFunction(g){
			public EmbranchementX execute(EmbranchementX obj) {
				obj.draw(g);
				return null;
			}
		});
	}
	
	/**
	 * @brief cover and test collision with all components stopping when found a collision
	 * For more information see 'coverComponentAll' witch implement covering the array
	 * @param mx, x position of the mouse or whatever point you want to test collision with
	 * @param my, y position of the mouse...
	 * @return the component with collision, null otherwise
	 */
	public EmbranchementX selectComponentAll(int mx, int my) {
		EmbranchementX buff = null;
		buff = coverComponentAll(new IterableFunction(mx, my){
			public EmbranchementX execute(EmbranchementX obj) {
				if(obj.mouseCollision(mx, my))
					return obj;
				return null;
			}
		});
		if(buff!=null)
			buff.setSelected(true);
		return buff;
	}
	
	public void add(EmbranchementX obj) {
		mArray.add(obj);
	}
	
	/**
	 * Save main array to file (using XML)
	 * @param filePath where to save data
	 */
	public void saveAs(String filePath) {
		mDocumentFactory = DocumentBuilderFactory.newInstance();
		try {
			// creation de l'XML
			mDocumentBuilder = mDocumentFactory.newDocumentBuilder();
			mDocument = mDocumentBuilder.newDocument();

			Element root = mDocument.createElement("root");
			mDocument.appendChild(root);
			
			saveAs_child(null, root);
			
			// sauvegarde dans le fichier
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			Result output = new StreamResult(new File(filePath));
			Source input = new DOMSource(mDocument);
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
	
	/**
	 * @brief recursing method called by saveAs to cover the main array
	 * @param parent, parent element of elements we are saving
	 * @param parentNode, parent element's XML node
	 */
	private void saveAs_child(EmbranchementX parent, Element parentNode) {
		for(EmbranchementX obj : mArray) {
			if(obj.origine == parent) { // found a child
				Element enfant;
				try {
					enfant = mDocument.createElement(escapeSpecialChar(obj.getEmbranchement()));
					
					parentNode.appendChild(enfant);
					Attr enfantAttr = mDocument.createAttribute("description");
					enfantAttr.setValue(obj.getDescriptif());
					enfant.setAttributeNode(enfantAttr);
					enfantAttr = mDocument.createAttribute("x");
					enfantAttr.setValue(Integer.toString(obj.getX()));
					enfant.setAttributeNode(enfantAttr);
					enfantAttr = mDocument.createAttribute("y");
					enfantAttr.setValue(Integer.toString(obj.getY()));
					enfant.setAttributeNode(enfantAttr);
					enfantAttr = mDocument.createAttribute("nom");
					enfantAttr.setValue(obj.getType());
					enfant.setAttributeNode(enfantAttr);
					enfantAttr = mDocument.createAttribute("image");
					enfantAttr.setValue(obj.getImagePath());
					enfant.setAttributeNode(enfantAttr);
					
					saveAs_child(obj, enfant);// recurse
				} catch (org.w3c.dom.DOMException e) {
					e.printStackTrace();
				}
			}
		} // no more child, stop this iteration
	}
	
	/**
	 * @brief Remove special characters is 'spe' to fit with XML element's name requirement
	 * Optimized for french
	 * @return normalized string
	 */
	private String escapeSpecialChar(String spe) {
		String result = new String();
		// only lower case are keep, upper case are converted to lower
		// space to '_'
		// other special characters (french's ones at least) to equivalent
		// otherwise they are removed
		for(int i=0; i< spe.length(); i++) {
			if(spe.charAt(i)>='a' && spe.charAt(i)<='z')
				result+=spe.charAt(i);
			else if(spe.charAt(i)>='A' && spe.charAt(i)<='Z')
				result+=spe.toLowerCase().charAt(i);
			else if(spe.charAt(i)==' ')
				result+='_';
			else if(spe.charAt(i)=='é' || spe.charAt(i)=='è' || spe.charAt(i)=='ë')
				result+='e';
			else if(spe.charAt(i)=='à' || spe.charAt(i)=='@')
				result+='a';
			else if(spe.charAt(i)=='ù')
				result+='u';
			else if(spe.charAt(i)=='ç')
				result+='c';
		}
		return result;
	}
	
	/**
	 * @brief give access to main array
	 * Should not be used
	 */
	public ArrayList<EmbranchementX> getArray() {
		return mArray;
	}
	
	/**
	 * remove 'obj' from main array an all his child
	 * @param obj
	 */
	public void remove(EmbranchementX obj) {
		for(EmbranchementX buff : mArray) { // TODO: remove child
			if(buff.getParent() == obj)
				mArray.remove(buff);
		}
		mArray.remove(obj);
	}
	
	/**
	 * @brief Load main array from file 'filePath'
	 * note: main array is not cleared
	 */
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
	}
	
	/**
	 * Called by 'loadFromFile' to cover the document
	 * @param embranchementList part of the document being explored
	 * @param origine parent to attach added child to
	 */
	private void parseEmbranchement(NodeList embranchementList, EmbranchementX origine) {
		for(int z = 0; z < embranchementList.getLength(); z++) {
			Node embranchement = embranchementList.item(z);
			if(embranchement.getNodeType() == Node.ELEMENT_NODE) {
				Element eEmbranchement = (Element) embranchement;
				System.out.println("\n\tEmbranchement nodeName: "+eEmbranchement.getNodeName());
				System.out.println("\tEmbranchement txtContent: "+eEmbranchement.getTextContent());
				System.out.println("\tEmbranchement Description: "+eEmbranchement.getAttribute("description"));
				System.out.println("\tEmbranchement nom: "+eEmbranchement.getAttribute("nom"));
				System.out.println("\tEmbranchement x: "+Integer.parseInt(eEmbranchement.getAttribute("x")));
				System.out.println("\tEmbranchement y: "+Integer.parseInt(eEmbranchement.getAttribute("y")));
				System.out.println("\tEmbranchement image: "+eEmbranchement.getAttribute("image"));
				EmbranchementX oEmbranchement = new EmbranchementX(origine, eEmbranchement.getAttribute("nom"), eEmbranchement.getNodeName(), eEmbranchement.getAttribute("description"), eEmbranchement.getAttribute("image"));
				oEmbranchement.setPosition(Integer.parseInt(eEmbranchement.getAttribute("x")), Integer.parseInt(eEmbranchement.getAttribute("y")));
				mArray.add(oEmbranchement);
				parseEmbranchement(embranchement.getChildNodes(), oEmbranchement);
			}
		}
	}
	
	/**
	 * Affiche un pop-up pour demander a saisir un element 
	 * @param parent, noeud parent de l'element a l'ajouter, null si racine
	 */
	public EmbranchementX addComponentForm(EmbranchementX parent) {
	    JTextField type = new JTextField("");
	    JTextField nom = new JTextField("");
	    JTextField description = new JTextField("");
	    String imagePath ="noimg.png"; // image lorsque aucune n'est preciser
	    File fichier;
	    
	    JPanel panel = new JPanel(new GridLayout(0, 2));
	    EmbranchementX sortie;
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
	        sortie = new EmbranchementX(parent, type.getText(), escapeSpecialChar(nom.getText()), description.getText(),imagePath);
	        mArray.add(sortie);
	        sortie.setSelected(true);
	        return sortie;
	    } else {
	        System.out.println("Cancelled");
	        return null;
	    }
	    
	}
}
