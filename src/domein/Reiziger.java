package domein;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Reiziger {

    private int id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private Date geboortedatum;

    private Adres adres;

    private List<OVChipkaart> OVChipkaarten;

    public Reiziger() {}

    public Reiziger(int id, String voorletters, String tussenvoegsel, String achternaam, Date geboortedatum) {
        this.id = id;
        this.voorletters = voorletters;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
        this.OVChipkaarten = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoorletters() {
        return this.voorletters;
    }

    public void setVoorletters(String voorletters) {
        this.voorletters = voorletters;
    }

    public String getTussenvoegsel() {
        return this.tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        return this.achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public java.sql.Date getGeboortedatum() {
        return this.geboortedatum;
    }

    public void setGeboortedatum(Date geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public String getNaam() {
        if (tussenvoegsel == null) return this.voorletters + " " + this.achternaam;
        return this.voorletters + " " + this.tussenvoegsel + " " + this.achternaam;
    }

    public Adres getAdres() {
        return this.adres;
    }

    public void setAdres(Adres adres) {
        this.adres = adres;
    }

    public List<OVChipkaart> getOvChipkaarten() {
        return this.OVChipkaarten;
    }

    public void setOVChipkaarten(List<OVChipkaart> OVChipkaarten) {
        this.OVChipkaarten = OVChipkaarten;
        OVChipkaarten.forEach(chipkaart -> {
            if (chipkaart.getReiziger() != this) chipkaart.setReiziger(this);
        });
    }

    public void addOvChipkaart(OVChipkaart OVChipkaart) {
        this.OVChipkaarten.add(OVChipkaart);
        if (OVChipkaart.getReiziger() != this) OVChipkaart.setReiziger(this);
    }

    public boolean equals(Reiziger reiziger) {
        return reiziger.getId() == this.id;
    }


    @Override
    public String toString() {
        String adrs = "";
        if (this.adres != null) adrs += ", " + this.adres;
        StringBuilder kaarten = new StringBuilder();
        if (OVChipkaarten.size() > 0) {
            OVChipkaarten.forEach(kaart -> kaarten.append(kaart.toString()).append(", "));
            kaarten.delete(kaarten.length() - 2, kaarten.length());
        }
        return String.format("Reiziger {#%s %s (%s)%s, [%s]}", id, getNaam(), geboortedatum.toString(), adrs, kaarten);
    }
}
