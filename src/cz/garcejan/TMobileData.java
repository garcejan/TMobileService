package cz.garcejan;


/**
 * This class saves data from tables from TMobile - Muj tarif & Nastaveni sluzeb
 */
public class TMobileData {

    private String name;        // name of the service
    private double price;       // how many service costs
    private boolean enabled;    // if service is enabled


    public TMobileData(String name, double price, boolean enabled) {
        this.name = name;
        this.price = price;
        this.enabled = enabled;
    }

    public TMobileData(String name) {
        this(name, 0, true);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "TMobileData{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", enabled=" + enabled +
                '}';
    }
}
