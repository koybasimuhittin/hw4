public class Node implements Comparable<Node> {

    public String airportCode;
    public Double cost;
    public Long time;
    public Double distanceToDestination;

    public Node(String airportCode, Double cost, Long time, Double distanceToDestination) {
        this.airportCode = airportCode;
        this.cost = cost;
        this.time = time;
        this.distanceToDestination = distanceToDestination;
    }

    @Override
    public int compareTo(Node o) {
        if (this.distanceToDestination + cost < o.distanceToDestination + o.cost) {
            return -1;
        } else if (this.distanceToDestination + cost > o.distanceToDestination + o.cost) {
            return 1;
        } else {
            if (this.cost < o.cost) {
                return -1;
            } else if (this.cost > o.cost) {
                return 1;
            } else {
                if (this.time < o.time) {
                    return -1;
                } else if (this.time > o.time) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        Node other = (Node) obj;
        return this.airportCode.equals(other.airportCode) && this.time.equals(other.time);
    }

    @Override
    public int hashCode() {
        return String.valueOf(this.airportCode + this.time).hashCode();
    }
}
