/*
 * Erstellt am 27.02.2008
 * Gehört zum Paket 
 * Im Projekt Pathfinder
 */

/**
 * @author Claus Wollnik & Ines Herrmann
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class View extends JFrame {
	JButton[][] btn;
	JButton btnStart, btnReset;
	JTextArea textArea;
	JSlider target;
	JRadioButtonMenuItem rbShort;
	JCheckBoxMenuItem cbDebug;
	int h, w;
	Model back;
	int[][] numbers;
	
	static final int MIN = 0;
	static final int INIT = 400;
	
	///Initialisiert die Komponente mit h * w Buttons und registriert die Listener auf btnAction
	///Die Werte für die Buttons werden aus numbers entnommen
	
	public View(int h, int w, int[][] numbers) {
		super("Pathfinder");
		
		this.numbers = numbers;
		this.w = w;
		this.h = h;
				
		createMenu();
		
		getContentPane().setLayout(new BorderLayout());

		createNumberpad();
		
		createConfig();
		
		createDebug();
		
		newBack();
	}

	private void newBack() {
		back = new Model(target.getValue(), this.numbers, this.h, this.w, this);
	}

	private void createDebug() {
		//Debugausgabe
		textArea = new JTextArea(5, 20);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		textArea.setEditable(false);
		textArea.setVisible(false);
		getContentPane().add(textArea, BorderLayout.SOUTH);
	}

	private void createConfig() {
		//neues Label zur Beschriftung des Sliders
		this.getContentPane().add(new Label("Zielwert:"), BorderLayout.WEST);
		
		//Berechnung des maximalen Zielwerts
		int max = 0;		
		for (int x = 0; x < w; ++x)
		{
			 for (int y = 0; y < h; ++y)
			 {
				max += numbers[y][x];
			 }
		}
		
		//erstelle neuen Slider
		target = new JSlider(JSlider.HORIZONTAL, MIN, max, INIT);
		
		target.addChangeListener(new BtnSlider());
		
		target.setMajorTickSpacing(200);
		target.setMinorTickSpacing(50);
		target.setPaintTicks(true);
		target.setPaintLabels(true);
		
		getContentPane().add(target, BorderLayout.CENTER);
		
		//Gruppierung der beiden Buttons Start und Reset
		JPanel btnPane = new JPanel();
		btnStart = new JButton(new Pathfinding("Start"));
		btnReset = new JButton(new Reset("Reset"));
		btnReset.setEnabled(false);
		btnPane.setLayout(new BorderLayout());
		btnPane.add(btnStart, BorderLayout.WEST);
		btnPane.add(btnReset, BorderLayout.EAST);		
		getContentPane().add(btnPane, BorderLayout.EAST);
	}

	private void createNumberpad() {
		JPanel nrPane = new JPanel();
		nrPane.setLayout(new GridLayout(h, w));
		btn = new JButton[h][w];
		
		for (int y = 0; y < h; ++y){
			for (int x = 0; x < w; ++x){
				btn[y][x] = new JButton(numbers[y][x] + "");
				btn[y][x].setEnabled(false);
				btn[y][x].setBackground(Color.GRAY);
				nrPane.add(btn[y][x]);
			}
		}
		btn[0][0].setText("Start");
		btn[h-1][w-1].setText("Ziel");
		
		
		getContentPane().add(nrPane, BorderLayout.NORTH);
	}
	
	private void createMenu() {
				//Menüleiste
				//Ergebnis - kürzesten Weg anzeigen, längsten Weg anzeigen
				//Debug - Statusfeld einblenden
				JMenuBar menuBar;
				JMenu menu;
				JMenuItem menuItem;
				JRadioButtonMenuItem rbMenuItem;
				
				//Create the menu bar.
				menuBar = new JMenuBar();
		
				//Build the first menu.	 	
				menu = new JMenu("Ergebnis");
				menuBar.add(menu);
				ButtonGroup group = new ButtonGroup();
				rbShort = new JRadioButtonMenuItem(new BtnShort("kürzesten Weg anzeigen"));
				group.add(rbShort);
				rbShort.setSelected(true);
				menu.add(rbShort);
				rbMenuItem = new JRadioButtonMenuItem(new BtnLong("längsten Weg anzeigen"));
				group.add(rbMenuItem);
				menu.add(rbMenuItem);
				
				menu = new JMenu("Debug");
				menuBar.add(menu);
				cbDebug = new JCheckBoxMenuItem(new BtnDebug("Statusfeld einblenden"));
				menu.add(cbDebug);
				
				setJMenuBar(menuBar);
	}
	
	public void setText(String text) {
		textArea.setText(text);
	}
	
	///Übergibt die Weg- bzw. Belegungsdaten
	///Belegte Felder haben die Werte > 0
	///Die Richtung ist mit den statischen Variablen von Model definiert
	public void setMatrix(int[][] array){
		for (int y = 0; y < h; ++y){
			for (int x = 0; x < w; ++x){
				//System.out.println(y+":"+x);
				if (array[y][x] > 0){
					btn[y][x].setBackground(Color.RED);
				}
				else{
					btn[y][x].setBackground(Color.GRAY);
				}
			}
		}
	}
	
	public void showPath() {
		int[][] arr;
		
		if (rbShort.isSelected()){
			arr = back.getShortestMatrix();
		} else {
			arr = back.getLongestMatrix();
		}
		setMatrix(arr);
	}
	
	class Pathfinding extends AbstractAction{
		//beschriftet Button Start
		public Pathfinding(String desc){
			super(desc);
			putValue(Action.SHORT_DESCRIPTION, desc);
		}
		//Button geklickt
		public void actionPerformed(ActionEvent e){
			btnStart.setEnabled(false);
			btnReset.setEnabled(true);
			//löst die Berechnung aller Wege aus
			back.calculate();
		}
	}
	
	class Reset extends AbstractAction{
		//beschriftet Button Reset
		public Reset(String desc){
			super(desc);
			putValue(Action.SHORT_DESCRIPTION, desc);
		}
		//Button geklickt
		public void actionPerformed(ActionEvent e){
			btnReset.setEnabled(false);
			back.stopIt();
			//erstelle neue Instanz von Model
			newBack();
			showPath();
			btnStart.setEnabled(true);
		}
	}
	
	class BtnShort extends AbstractAction{
		public BtnShort(String desc){
			super(desc);
			putValue(Action.SHORT_DESCRIPTION, desc);
		}
		
		public void actionPerformed(ActionEvent e){
			showPath();
		}
	}
	
	class BtnLong extends AbstractAction{
		public BtnLong(String desc){
			super(desc);
			putValue(Action.SHORT_DESCRIPTION, desc);
		}
		
		public void actionPerformed(ActionEvent e){
			showPath();
		}
	}
	
	class BtnDebug extends AbstractAction{
		public BtnDebug(String desc){
			super(desc);
			putValue(Action.SHORT_DESCRIPTION, desc);
		}
		
		public void actionPerformed(ActionEvent e){
			//sichtbar, wenn Checkbox mit einem Haken versehen ist
			textArea.setVisible(cbDebug.isSelected());
			//komprimiert das Fenster
			pack();
		}
	}
	
	class BtnSlider implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			textArea.setText("Zielwert: "+target.getValue());
			//aktualisiert Model mit dem neuen Zielwert
			back.setTarget(target.getValue());
		}
	}
}
