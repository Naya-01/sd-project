import java.util.Objects;

public class Flight {
	private final Airport source;
	private final Airport destination;
	private final String airline;
	public Flight(Airport source, Airport destination, String airline) {
		this.source = source;
		this.destination = destination;
		this.airline = airline;
	}
	public Airport getSource() {
		return source;
	}
	public Airport getDestination() {
		return destination;
	}
	public String getAirline() {
		return airline;
	}
	@Override
	public String toString() {
		return "Flight [source=" + source.getIata() + ", destination=" + destination.getIata() + ", airline=" + airline + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Flight flight = (Flight) o;
		return Objects.equals(source, flight.source) && Objects.equals(destination, flight.destination) && Objects.equals(airline, flight.airline);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, destination, airline);
	}
}
