/**
 * @author djeck
 * @date 20/11/2016
 * @file Embranchement.java
 * Simple maillon de la liste chainée
 */
package core;


/**
 * @class Embranchement
 * @author djeck
 * @brief Matière premiere des blocs du graph
 */
public abstract class Embranchement {
	protected String type;// "ordre" ou "embranchement" ...
	protected String nom;// animal, vegetal,... papillon...
	protected String descriptif;

	protected Embranchement origine = null;// parent
	
	protected int x = 0, y = 0;// position pour l'affichage

	protected String imagePath = null;

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
	
	public String getType() {
		return type;
	}

	public String getImagePath() {
		return imagePath;
	}
	
	public Embranchement getParent() {
		return origine;
	}
	
	public String toString() {
		String str = new String();
		str += "Embranchement type: "+type+"\n";
		str += "Embranchement nom: "+nom+"\n";
		str += "Embranchement descriptif: "+descriptif+"\n";
		str += "Embranchement x:y: "+x+":"+y+"\n";
		str += "Embranchement ImagePath: "+imagePath;
		return str;
	}
}
