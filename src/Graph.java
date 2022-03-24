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
  private Map<String,Airport> iataToAiportMap;

  public Graph(File aeroports,File vols) {
    outputFlights = new HashMap<>();
    iataToAiportMap = new HashMap<>();

    try(BufferedReader readerAirports = new BufferedReader(new FileReader(aeroports))
        ;BufferedReader readerVols = new BufferedReader(new FileReader(vols));){ // try with resource
      String line;
      while ((line = readerAirports.readLine()) != null) {
        String airport[] = line.split(",");
        Double longitude = Double.parseDouble(airport[4]);
        Double latitude = Double.parseDouble(airport[5]);
        Airport a = new Airport(airport[0],airport[1],latitude,longitude);

        outputFlights.putIfAbsent(a,new HashSet<>());
        iataToAiportMap.putIfAbsent(airport[0],a);
      }

      while ((line = readerVols.readLine()) != null) {
        String vol[] = line.split(",");
        Airport src = iataToAiportMap.get(vol[1]);
        Airport dest = iataToAiportMap.get(vol[2]);
        Flight f = new Flight(src,dest,vol[0]);
        outputFlights.get(src).add(f);
      }

    } catch (IOException e){
      throw new RuntimeException(e);
    }
  }





  public void calculerItineraireMinimisantNombreVol(String airport1,String airport2){
    Airport dest = iataToAiportMap.get(airport2);

    HashMap<Airport,Flight> previousAirport = rechercheBFS(airport1,airport2); // Vol dest -> Vol src

    if(previousAirport.containsKey(dest)){ // back
      int nbVols = 0;
      Airport actual = dest;
      do{
        System.out.println(actual);
        Flight x = previousAirport.get(actual);
        if(x!=null)
          actual = x.getSource();
        else
          break;
        nbVols++;
      }while(actual!=null);
      System.out.println("On a "+nbVols+" à parcourir");
    }else{
      // pas trouvé
      System.out.println("J'ai pas trouvé");
    }

  }

  public void calculerItineraireMiniminantDistance(String airport1,String airport2){
    Airport sommet = iataToAiportMap.get(airport1);
    Airport dest = iataToAiportMap.get(airport1);
    HashMap<Airport,Double> etiquetteDefinitive = rechercheDijkstra(airport1,airport2);
    System.out.println("J'ai un chemin de "+etiquetteDefinitive.get(dest)+" km");

  }

  private HashMap<Airport,Double> rechercheDijkstra(String airport1,String airport2){
    Airport sommet = iataToAiportMap.get(airport1);
    Airport dest = iataToAiportMap.get(airport2);

    TreeSet<Airport> etiquetteProvisoire = new TreeSet<>(Comparator.comparing(Airport::getDistance).thenComparing(Airport::getIata));
    HashMap<Airport,Double> etiquetteDefinitve = new HashMap<>();
    HashMap<Airport,Flight> retour = new HashMap<>();

    sommet.setDistance(0.0); // tjr set avant d'ajouter pour le tri
    etiquetteProvisoire.add(sommet);
    etiquetteDefinitve.put(sommet,0.0);

    Airport actual = sommet;
    while(!etiquetteDefinitve.containsKey(dest)){
      for (Flight f : outputFlights.get(actual)) {
        Airport destVol = f.getDestination();
        if(!etiquetteProvisoire.contains(destVol)){
          if(retour.get(destVol)==null){
            double distance = Util.distance(sommet.getLatitude(),sommet.getLongitude(),
                destVol.getLatitude(),destVol.getLongitude());
            destVol.setDistance(distance);
            etiquetteProvisoire.add(destVol);
          }else{
            double distance = 0;
            Airport back = destVol;
            Flight backFlight = null;
            do{
              backFlight = retour.get(backFlight);
              if(retour!=null){
                back = backFlight.getSource();
                distance+= back.getDistance();
              }
            }while(backFlight!=null);
            distance+= Util.distance(actual.getLatitude(),actual.getLongitude(),
                destVol.getLatitude(),destVol.getLongitude());

            destVol.setDistance(distance);
            etiquetteProvisoire.add(destVol);
          }
        }else{
          // Si il le contient déjà on doit comparer les chemins et garder le plus petit
        }
      }
      etiquetteDefinitve.put(etiquetteProvisoire.first(),etiquetteProvisoire.first().getDistance());
      etiquetteProvisoire.remove(etiquetteProvisoire.first());
    }


    return etiquetteDefinitve;
  }


  private HashMap<Airport, Flight> rechercheBFS(String airport1,String airport2){
    Airport sommet = iataToAiportMap.get(airport1);
    Airport dest = iataToAiportMap.get(airport2);

    ArrayDeque<Airport> file = new ArrayDeque<>();
    HashSet<Airport> alreadySeen = new HashSet<>();
    HashMap<Airport,Flight> previousAirport = new HashMap<>(); // Vol dest -> Vol src
    file.addLast(sommet);
    alreadySeen.add(sommet);
    while(!alreadySeen.contains(dest) && !file.isEmpty()){
      Airport actual = file.removeFirst(); // 1
      for (Flight f : outputFlights.get(actual)){ // {1,2,3}
        if(!alreadySeen.contains(f.getDestination())){ // 2
          file.addLast(f.getDestination()); // {}
          previousAirport.put(f.getDestination(),f); // [2,1],[4,1],[5,1],[3,2],[6,2]
          alreadySeen.add(f.getDestination()); // 6 -> 2 -> 1 if(1==sommet)
        }
      }
    }
    return previousAirport;
  }

}
