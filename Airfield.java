import java.util.HashMap;

public class Airfield {
    public String name;
    public HashMap<String, Airport> airports;
    public HashMap<Long, Integer> timeStampToWeatherCode = new HashMap<Long, Integer>();

    public Airfield(String name) {
        this.name = name;
        this.airports = new HashMap<String, Airport>();
    }

    public Double weatherMultiplier(int weatherCode) {
        int Bw = (weatherCode & 16) >> 4; // Wind
        int Br = (weatherCode & 8) >> 3; // Rain
        int Bs = (weatherCode & 4) >> 2; // Snow
        int Bh = (weatherCode & 2) >> 1; // Hail
        int Bb = weatherCode & 1; // Bolt

        // Calculating the weather multiplier
        double W = (Bw * 1.05 + (1 - Bw)) *
                (Br * 1.05 + (1 - Br)) *
                (Bs * 1.10 + (1 - Bs)) *
                (Bh * 1.15 + (1 - Bh)) *
                (Bb * 1.20 + (1 - Bb));

        return W;
    }

    public Double weatherCoefficient(Airfield airfield, Long time, Long duration) {
        return weatherMultiplier(timeStampToWeatherCode.get(time))
                * airfield.weatherMultiplier(airfield.timeStampToWeatherCode.get(time + duration));
    }
}
