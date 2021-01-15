/*
 * Erstellt am 27.02.2008
 * Gehört zum Paket 
 * Im Projekt Pathfinder
 */

/**
 * @author Claus Wollnik & Ines Herrmann
 */
//import java.io.*;

public class Model extends Thread {
	private int[][][] cube;
	///0: Zahlenwerte
	///1: Bewegungen
	///2: kleinster Weg
	///3: größter Weg
	
	private int[] data;
	///0: Zielwert
	///1: (ungenutzt)
	///2: Schrittzahl kürzester Weg
	///3: Schrittzahl längster Weg
	///4: Anzahl gefundener Wege
	///5: Anzahl gefundener kürzester Wege
	///6: Anzahl gefundener längster Wege
	///7: Höhe
	///8: Breite
	
	private int[][] move = {{0, 0}, {0, 1}, {1, 0}, {0, -1}, {-1, 0}};
	///Bewegungsvektoren für
	///0: Keine Bewegung
	///1: Rechts
	///2: Unten
	///3: Links
	///4: Oben
	
	//PrintWriter out;
	
	View view;

	public final static int RIGHT = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	public final static int UP = 4;
	
	private boolean running = true;
	
	public Model(int target, int[][] values, int h, int w, View view) {
		/*try {
			out = new PrintWriter("C:/test.txt");
		} catch (FileNotFoundException e) {
			System.out.println("Ausgabe fehlgeschlagen");
		}*/
		
		cube = new int[4][h][w];
		this.view = view;
		
		data = new int[9];
		setTarget(target);
		data[7] = h;
		data[8] = w;
		
		for (int x = 0; x < data[8]; ++x) {
			for (int y = 0; y < data[7]; ++y) {
				cube[0][y][x] = values[y][x];
			}
		}
		
		for (int z = 1; z < 4; ++z)	{
			for (int y = 0; y < data[7]; ++y) {
				for (int x = 0; x < data[8]; ++x) {
					cube[z][y][x] = 0;
				}
			}
		}
		
		//Erkennung für neuen kürzesten Weg bei Programmbeginn ermöglichen
		//Anzahl Schritte ist einer größer als die mögliche Anzahl
		data[2] = h*w+1;
	}
	
	public void calculate() {
		// Create the thread supplying it with the runnable object
		// Start the thread
		this.start();
	}
	
	public void run() {
		view.setText("Berechne Lösungen...");
		findWays();
		view.setText("Wegsuche mit Zielsumme "+data[0]+"\n"+
						"Kürzester Weg: "+data[2]+" Schritte, "+data[5]+" mal\n"+
						"Längster Weg: "+data[3]+" Schritte, "+data[6]+" mal\n"+
						"Insgesamt "+data[4]+" Wege");

		view.showPath();
	}
	
	public void setTarget(int target) {
		data[0] = target;
	}

	///Bewegt den Cursor und speichert die Bewegung
	///sofern Bewegung möglich
	public void findWays() {
		int[] cur = {0,0};
		cube[1][data[7]-1][data[8]-1] = 5;
		findWays(cur, 0, 0);
	}
	
	private void findWays(int[] cur, int value, int steps) {
		if (!running) return;
		
		//out.println(cur[0]+":"+cur[1]+" Schritte: "+steps+" Wert: "+value);
		for (int a = 1; a <= 4; ++a) {
			//out.print("Bewegungscode "+a);
			//Potentielles neues Feld
			int[] c = new int[2];
			c[0] = cur[0] + move[a][0];
			c[1] = cur[1] + move[a][1];
			//Neue Schrittzahl
			int s = steps + 1;
			
			//Ausschlusskriterien:
			//Rand erreicht
			if (c[0] < 0 || c[0] >= data[7] || c[1] < 0 || c[1] >= data[8]) {
				//out.println(" Rand erreicht");
				continue;
			}
			//Feld bereits belegt
			if (cube[1][c[0]][c[1]] > 0) {
				//out.println(" Feld belegt");
				continue;
			}
			
			//Neuer Wert für erreichtes Feld
			int v = value + cube[0][c[0]][c[1]];
			//Zielwert überschritten
			if (v > data[0]) {
				//out.println(" Zielwert überschritten");
				continue;
			}
			//Zug würde Rückweg versperren
			if ((a == 3 && (c[0] == 0 || c[0] == (data[7]-1))) || (a == 4 && (c[1] == 0 || c[1] == (data[8]-1)))) {
				//out.println(" Rückweg versperrt");
				continue;
			}
			
			//Besuche Feld tatsächlich
			//out.println(" Feld betreten");
			cube[1][cur[0]][cur[1]] = a;
						
			//Ziel erreicht?
			if (data[0] == v && ((c[0] == data[7]-2 && c[1] == data[8]-1) || (c[0] == data[7]-1 && c[1] == data[8]-2))) {
				//Ziel erreicht!
				
				cube[1][c[0]][c[1]] = 5;
				//out.println("Ziel gefunden");
				//Anzahl gefundener Wege hochsetzen
				view.setText(++data[4]+" Ziele gefunden");
				
				if(s == data[2]){ //schauen ob es ein kürzester Weg ist
					++data[5];			//Anzahl der kürzesten Wege hochzählen
				}
				
				if(s == data[3]){ //schauen ob es ein längster Weg ist
					++data[6];			//Anzahl der längsten Wege hochzählen
				}
				
				if(s < data[2]){ //schauen ob es der kürzeste Weg ist
					data[5] = 1;		//Anzahl gefundener kürzester Wege auf 1 zurücksetzen
					copySlice(2);	//Bewegungen für kürzesten Weg abspeichern
					data[2] = s;	//Schrittzahl für kürzesten Weg festhalten
				}
				
				if(s > data[3]) { //schauen ob es der längste Weg ist
					data[6] = 1;		//Anzahl gefundener längster Wege auf 1 zurücksetzen
					copySlice(3);	//Bewegungen für längsten Weg abspeichern
					data[3] = s;		//Schrittzahl für längsten Weg festhalten
				}
				
				cube[1][c[0]][c[1]] = 0;
				
			} else {
				//Weitersuchen
				findWays(c, v, s);
			}
			//Mit dem Feld bin ich fertig
			cube[1][cur[0]][cur[1]] = 0;
			//out.println("Zurück auf "+cur[0]+":"+cur[1]+" Schritte: "+steps+" Wert: "+value);
		}
		return;
	}
	
	public void copySlice(int to) {
		for (int x = 0; x < data[8]; ++x) {
			for (int y = 0; y < data[7]; ++y) {
				cube[to][y][x] = cube[1][y][x];
			}
		}
	}
	
	public int[][] getLongestMatrix() {
		return cube[3];
	}
	
	public int[][] getShortestMatrix() {
		return cube[2];
	}
	
	public int[][] getCurrentWay() {
		return cube[1];
	}
	
	public void stopIt() {
		running = false;
	}
}
