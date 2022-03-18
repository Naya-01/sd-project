import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Airport a = new Airport(airport[0],airport[1]);
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
    Airport sommet = iataToAiportMap.get(airport1);
    Airport dest = iataToAiportMap.get(airport2);

    ArrayDeque<Airport> file = new ArrayDeque<>();
    HashSet<Airport> alreadySeen = new HashSet<>();
    HashMap<Airport,Airport> previousAirport = new HashMap<>(); // Vol dest -> Vol src
    file.push(sommet);

    while(!alreadySeen.contains(dest) && !file.isEmpty()){
      Airport actual = file.removeFirst();
      alreadySeen.add(actual);
      for (Flight f : outputFlights.get(actual)){
        if(!alreadySeen.contains(f.getDestination())){
          file.push(f.getDestination());
          previousAirport.put(f.getDestination(),actual);
          alreadySeen.add(f.getDestination());
        }
      }
    }

    if(alreadySeen.contains(dest)){ // back
      int nbVols = -1;
      Airport actual = dest;
      do{
        System.out.println(actual);
        actual = previousAirport.get(actual);
        nbVols++;
      }while(actual!=null);
      System.out.println("On a "+nbVols+" à parcourir");
    }else{
      // pas trouvé
      System.out.println("J'ai pas trouvé");
    }

  }

  public void calculerItineraireMiniminantDistance(String airport1,String airport2){

  }

}
