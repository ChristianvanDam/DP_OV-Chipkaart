package domein;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product {
    private int productNummer;
    private String naam;
    private String beschrijving;
    private double prijs;

    List<OVChipkaart> ovChipkaarten;

    public Product(int productNummer, String naam, String beschrijving, double prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
        this.ovChipkaarten = new ArrayList<>();
    }

    public void setProductNummer(int productNummer) {
        this.productNummer = productNummer;
    }

    public int getProductNummer() {
        return this.productNummer;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getNaam() {
        return this.naam;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public String getBeschrijving() {
        return this.beschrijving;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public double getPrijs() {
        return this.prijs;
    }

    public void setOvChipkaarten(List<OVChipkaart> ovChipkaarten) {
        this.ovChipkaarten = ovChipkaarten;
    }

    public void addovChipkaart(OVChipkaart ovChipkaart) {
        if (!this.ovChipkaarten.contains(ovChipkaart)) {
            this.ovChipkaarten.add(ovChipkaart);
            ovChipkaart.addProduct(this);
        }
    }

    public List<OVChipkaart> getOvChipkaarten() {
        return this.ovChipkaarten;
    }

    public void removeOvchipkaart(OVChipkaart ovChipkaart) {
        this.ovChipkaarten.remove(ovChipkaart);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Product) {
            Product p = (Product) o;
            return this.productNummer == p.productNummer
                    && this.naam.equals(p.naam)
                    && this.beschrijving.equals(p.beschrijving)
                    && this.prijs == p.prijs;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, %s, %s}", this.productNummer, this.naam, this.beschrijving, this.prijs);
    }
}
