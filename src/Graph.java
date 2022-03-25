import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
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


  public void calculerItineraireMinimisantNombreVol(String airport1, String airport2)
      throws NoFlightException {

    //TODO Faut faire les bons print mtn

    Airport dest = iataToAiportMap.get(airport2);

    HashMap<Airport, Flight> previousAirport = rechercheBFS(airport1,
        airport2); // Vol dest -> Vol src

    ArrayList<Flight> retour = cheminRetour(previousAirport, dest);

    System.out.println("On a un chemin de " + (retour.size()) + " vols");
    for (Flight flight : retour) {
      System.out.println(flight);
    }

  }

  private ArrayList<Flight> cheminRetour(HashMap<Airport, Flight> previousAirport, Airport dest)
      throws NoFlightException {
    ArrayList<Flight> airports = new ArrayList<>();
    if (previousAirport.containsKey(dest)) { // back
      Airport actual = dest;
      do {
        //System.out.println(actual);
        Flight x = previousAirport.get(actual);
        if (x != null) {
          airports.add(x);
          actual = x.getSource();
        } else {
          break;
        }
      } while (actual != null);
    } else {
      throw new NoFlightException("J'ai rien");
    }
    return airports;
  }

  public void calculerItineraireMiniminantDistance(String airport1, String airport2)
      throws NoFlightException {
    Airport dest = iataToAiportMap.get(airport2);

    HashMap<Airport, Flight> retour =  rechercheDijkstra2(airport1, airport2);
    System.out.println("J'ai un chemin de " + dest.getDistance() + " km");
    ArrayList<Flight> airports = cheminRetour(retour, dest);
    for (Flight flight : airports) {
      System.out.println(flight);
    }

  }

  /**
   * Méthode de lisibilité retournant la distance entre deux
   *
   * @param src  airport src
   * @param dest airport dest
   * @return la distance calculée
   */
  private double getDistance(Airport src, Airport dest) {
    return Util.distance(src.getLatitude(), src.getLongitude(), dest.getLatitude(),
        dest.getLongitude());
  }

  private HashMap<Airport, Flight> rechercheDijkstra2(String airport1, String airport2) {
    HashMap<Airport, Flight> retour = new HashMap<>();
    HashSet<Airport> etiquetteDefinitive = new HashSet<>();
    TreeSet<Airport> etiquetteProvisoire = new TreeSet<>(Comparator.comparing(Airport::getDistance).thenComparing(Airport::getIata));

    Airport sommet = iataToAiportMap.get(airport1);
    Airport cible = iataToAiportMap.get(airport2);

    sommet.setDistance(0.0);
    etiquetteProvisoire.add(sommet);
    while (!etiquetteDefinitive.contains(cible) && !etiquetteProvisoire.isEmpty()) {
      Airport actual = etiquetteProvisoire.pollFirst();
      etiquetteDefinitive.add(actual);

      for (Flight f : outputFlights.get(actual)) {
        Airport destination = f.getDestination();
        double newDistance = actual.getDistance() + getDistance(actual, destination);

        if (etiquetteDefinitive.contains(destination)) {
          continue;
        } else if (!etiquetteProvisoire.contains(destination)) {
          destination.setDistance(newDistance);
          etiquetteProvisoire.add(destination);
          retour.put(destination, f);
        } else {
          double distanceAvant = destination.getDistance();
          if (newDistance < distanceAvant) {
            etiquetteProvisoire.remove(destination);
            destination.setDistance(newDistance);
            etiquetteProvisoire.add(destination);
            retour.replace(destination, f);
          }
        }
      }
    }

    return retour;
  }

  private HashMap<Airport, Flight> rechercheBFS(String airport1, String airport2) {
    Airport sommet = iataToAiportMap.get(airport1);
    Airport dest = iataToAiportMap.get(airport2);

    ArrayDeque<Airport> file = new ArrayDeque<>();
    HashSet<Airport> alreadySeen = new HashSet<>();
    HashMap<Airport, Flight> previousAirport = new HashMap<>();
    file.addLast(sommet);
    alreadySeen.add(sommet);
    while (!alreadySeen.contains(dest) && !file.isEmpty()) {
      Airport actual = file.removeFirst();
      for (Flight f : outputFlights.get(actual)) {
        if (!alreadySeen.contains(f.getDestination())) {
          file.addLast(f.getDestination());
          previousAirport.put(f.getDestination(), f);
          alreadySeen.add(f.getDestination());
        }
      }
    }
    return previousAirport;
  }

}
