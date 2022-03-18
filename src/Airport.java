
public class Airport {
	private final String iata;
	private final String name;
	private final double latitude;
	private final double longitude;
	private double distance = Double.MAX_VALUE;

	public Airport(String iata, String name,double latitude,double longitude) {
		super();
		this.iata = iata;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getIata() {
		return iata;
	}
	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iata == null) ? 0 : iata.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Airport other = (Airport) obj;
		if (iata == null) {
			if (other.iata != null)
				return false;
		} else if (!iata.equals(other.iata))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Airport [iata=" + iata + ", name=" + name + "]";
	}

}
