import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Graph {

  private Map<Airport, Set<Flight>> outputFlights; // Ce sont les vols destination
  private Map<String, Airport> iataToAiportMap;

  public Graph(File aeroports, File vols) {
    outputFlights = new HashMap<>();
    iataToAiportMap = new HashMap<>();

    try (BufferedReader readerAirports = new BufferedReader(new FileReader(aeroports))
        ; BufferedReader readerVols = new BufferedReader(
        new FileReader(vols));) { // try with resource
      String line;
      while ((line = readerAirports.readLine()) != null) {
        String airport[] = line.split(",");
        Double longitude = Double.parseDouble(airport[4]);
        Double latitude = Double.parseDouble(airport[5]);
        Airport a = new Airport(airport[0], airport[1], latitude, longitude);

        outputFlights.putIfAbsent(a, new HashSet<>());
        iataToAiportMap.putIfAbsent(airport[0], a);
      }

      while ((line = readerVols.readLine()) != null) {
        String vol[] = line.split(",");
        Airport src = iataToAiportMap.get(vol[1]);
        Airport dest = iataToAiportMap.get(vol[2]);
        Flight f = new Flight(src, dest, vol[0]);
        outputFlights.get(src).add(f);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public void calculerItineraireMinimisantNombreVol(String airport1, String airport2) {

    //TODO Faut faire les bons print mtn

    Airport dest = iataToAiportMap.get(airport2);

    HashMap<Airport, Flight> previousAirport = rechercheBFS(airport1,
        airport2); // Vol dest -> Vol src

    if (previousAirport.containsKey(dest)) { // back
      int nbVols = 0;
      Airport actual = dest;
      do {
        System.out.println(actual);
        Flight x = previousAirport.get(actual);
        if (x != null) {
          actual = x.getSource();
        } else {
          break;
        }
        nbVols++;
      } while (actual != null);
      System.out.println("On a " + nbVols + " à parcourir");
    } else {
      // pas trouvé
      System.out.println("J'ai pas trouvé");
    }

  }

  public void calculerItineraireMiniminantDistance(String airport1, String airport2) {
    Airport sommet = iataToAiportMap.get(airport1);
    Airport dest = iataToAiportMap.get(airport1);
    HashMap<Airport,Flight> retour = new HashMap<>();
    HashMap<Airport, Double> etiquetteDefinitive = rechercheDijkstra(retour,airport1, airport2);
    System.out.println("J'ai un chemin de " + etiquetteDefinitive.get(dest) + " km");
    Airport toPrint = dest;



    Flight volToPrint;
    while(!toPrint.equals(sommet)){
      volToPrint = retour.get(toPrint);
      if(volToPrint==null)break;
      System.out.println(volToPrint);
      toPrint = volToPrint.getSource();
    }

  }

  /**
   * Méthode de lisibilité retournant la distance entre deux stations
   *
   * @param src  airport src
   * @param dest airport dest
   * @return la distance calculée
   */
  private double getDistance(Airport src, Airport dest) {
    return Util.distance(src.getLatitude(), src.getLongitude(), dest.getLatitude(),
        dest.getLongitude());
  }

  private HashMap<Airport, Double> rechercheDijkstra(HashMap<Airport,Flight> retour,String airport1, String airport2) {
    HashMap<Airport, Double> etiquetteDefinitive = new HashMap<>();
    TreeSet<Airport> etiquetteProvisoire = new TreeSet<>(
        Comparator.comparing(Airport::getDistance).thenComparing(Airport::getIata));
    HashMap<Airport, Double> cout = new HashMap<>();

    Airport sommet = iataToAiportMap.get(airport1);
    sommet.setDistance(0.0);
    Airport cible = iataToAiportMap.get(airport2);
    cout.put(sommet,0.0);

    Airport actual = sommet;

    while (true) {

      // Si je trouve la distance la plus opti pour ma destination cible j'arrête tout
      if (etiquetteDefinitive.containsKey(cible)) {
        break;
      }


      for (Flight f : outputFlights.get(actual)) {
        Airport destination = f.getDestination();

        // Si déjà dans l'etiquette définitive on l'ajoute pas de nouveau
        if(etiquetteDefinitive.containsKey(destination))continue;

        // Si pas dans etiquette definitive
        if (!etiquetteProvisoire.contains(destination)) {
          double distance = cout.get(actual) + getDistance(actual, destination); // On calcule sa distance , si sommet cout.get(actuel) vaut 0 sinon il vaut tout les chemins d'avant
          destination.setDistance(distance);
          cout.put(destination, distance);
          etiquetteProvisoire.add(destination);
          retour.put(destination, f);
        } else {
          double distanceAvant = cout.get(destination);
          double distanceMtn = cout.get(actual) + getDistance(actual, destination);
          if (distanceMtn < distanceAvant) { // Je compare ma distance d'avant et ma distance que je viens de trouver
            destination.setDistance(distanceMtn); // Si elle est plus petite je la set
            cout.replace(destination, distanceMtn); // J'actualise ma table cout
            etiquetteProvisoire.remove(destination); // J'actualise mon treemap
            etiquetteProvisoire.add(destination);
            retour.replace(destination, f); // J'actualise le chemin optimisé
          }
        }
      }

      etiquetteDefinitive.put(etiquetteProvisoire.first(), etiquetteProvisoire.first().getDistance()); // Je met le plus petit dans l'etiquette definitive;
      actual = etiquetteProvisoire.pollFirst();

    }

    return etiquetteDefinitive;
  }

  private HashMap<Airport, Flight> rechercheBFS(String airport1, String airport2) {
    Airport sommet = iataToAiportMap.get(airport1);
    Airport dest = iataToAiportMap.get(airport2);

    ArrayDeque<Airport> file = new ArrayDeque<>();
    HashSet<Airport> alreadySeen = new HashSet<>();
    HashMap<Airport, Flight> previousAirport = new HashMap<>(); // Vol dest -> Vol src
    file.addLast(sommet);
    alreadySeen.add(sommet);
    while (!alreadySeen.contains(dest) && !file.isEmpty()) {
      Airport actual = file.removeFirst(); // 1
      for (Flight f : outputFlights.get(actual)) { // {1,2,3}
        if (!alreadySeen.contains(f.getDestination())) { // 2
          file.addLast(f.getDestination()); // {}
          previousAirport.put(f.getDestination(), f); // [2,1],[4,1],[5,1],[3,2],[6,2]
          alreadySeen.add(f.getDestination()); // 6 -> 2 -> 1 if(1==sommet)
        }
      }
    }
    return previousAirport;
  }

}
