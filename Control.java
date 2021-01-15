/*
 * Erstellt am 27.02.2008
 * Gehört zum Paket 
 * Im Projekt Pathfinder
 */

/**
 * @author Claus Wollnik & Ines Herrmann
 */
import javax.swing.*;

public class Control {
	static final int[][] numbers = new int[][]
		{{0, 8, 6, 86, 23, 51, 8, 25, 18, 51, 64},
		{9, 55, 41, 2, 32, 9, 9, 99, 89, 52, 89},
		{17, 9, 33, 7, 2, 43, 5, 4, 30, 44, 45},
		{32, 12, 5, 10, 51, 7, 32, 56, 54, 19, 54},
		{14, 46, 3, 39, 8, 29, 65, 10, 6, 81, 0}};	
	static final int HEIGHT = 5;
	static final int WIDTH = 11;
	
	///Startet das Programm (entnommen und gekürzt aus den Java-Tutorials)
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	private static void createAndShowGUI() {
		JFrame frame = new View(5, 11, numbers);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public Control() {
	}
}