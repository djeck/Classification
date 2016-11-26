package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Window extends JFrame implements ActionListener {
	private static final long serialVersionUID = 7446192599263749847L;

	private JLabel container;
	private Drawer drawer;

	private JMenuBar menubar;
	private JMenu file;
	private JMenuItem save;
	private JMenuItem exit;

	private String filePath = "output.xml";

	public Window() {
		setTitle("Classification");

		setSize(600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		createMenuBar();

		container = new JLabel();
		container.setBackground(Color.white);
		container.setLayout(new BorderLayout());
		container.setVisible(true);

		drawer = new Drawer();
		drawer.setVisible(true);
		container.add(drawer);

		this.addMouseListener(drawer);
		this.addMouseMotionListener(drawer);
		this.addComponentListener(drawer);
		this.addKeyListener(drawer);
		getContentPane().add(container, BorderLayout.CENTER);
		setVisible(true);
		drawer.loadFromFile(filePath);
		repaint();
	}

	private void createMenuBar() {

		menubar = new JMenuBar();

		file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		save = new JMenuItem("Save");
		save.setMnemonic(KeyEvent.VK_S);
		save.setToolTipText("Save file");
		save.addActionListener(this);
		file.add(save);

		// TODO: save as

		exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setToolTipText("Exit All");
		exit.addActionListener(this);
		file.add(exit);

		// TODO: Open file

		menubar.add(file);

		setJMenuBar(menubar);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == save) {
			drawer.saveAs(filePath);
		} else if (arg0.getSource() == exit) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
}
