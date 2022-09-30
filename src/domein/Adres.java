package domein;

public class Adres {
    private int id;
    private String postcode;
    private String huisnummer;
    private String straat;
    private String woonplaats;
    private Reiziger reizigerId;

    public Adres() {}

    public Adres(int id, String postcode, String huisnummer, String straat, String woonplaats, Reiziger reizigerId) {
        this.id = id;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaats = woonplaats;
        this.reizigerId = reizigerId;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getHuisnummer() {
        return this.huisnummer;
    }

    public void setHuisnummer(String huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getStraat() {
        return this.straat;
    }

    public void setStraat(String straat) {
        this.straat = straat;
    }

    public String getWoonplaats() {
        return this.woonplaats;
    }

    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }

    public Reiziger getReizigerId() {
        return this.reizigerId;
    }

    public void setReizigerId(Reiziger reizigerId) {
        this.reizigerId = reizigerId;
    }

    @Override
    public String toString() {
        return "Adres {#" + this.id + " " + this.postcode + " " + this.huisnummer + " " + this.straat + " " + this.woonplaats + "}";
    }
}
