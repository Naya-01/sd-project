import java.io.File;

public class Main {
	public static void main(String[] args) {
		try {
			File aeroports = new File("aeroports.txt");
			File vols = new File("vols.txt");
			Graph g = new Graph(aeroports,vols);
			g.calculerItineraireMinimisantNombreVol("BRU", "CHC");
			System.out.println("-----------------");
			g.calculerItineraireMiniminantDistance("BRU", "CHC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
