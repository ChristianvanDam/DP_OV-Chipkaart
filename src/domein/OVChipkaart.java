package domein;

import java.sql.Date;
import java.util.Objects;

public class OVChipkaart {
    private int kaart_nummer;
    private Date geldig_tot;
    private int klasse;
    private double saldo;
    private int reiziger_id;

    private Reiziger reiziger;

    public OVChipkaart(int kaart_nummer, Date geldig_tot, int klasse, double saldo, int reiziger_id) {
        this.kaart_nummer = kaart_nummer;
        this.geldig_tot = geldig_tot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger_id = reiziger_id;
    }

    public OVChipkaart(int kaart_nummer, Date geldig_tot, int klasse, double saldo, int reiziger_id, Reiziger reiziger) {
        this(kaart_nummer, geldig_tot, klasse, saldo, reiziger_id);
        this.reiziger = reiziger;
    }

    public int getKaart_nummer() {
        return this.kaart_nummer;
    }

    public void setKaart_nummer(int kaart_nummer) {
        this.kaart_nummer = kaart_nummer;
    }

    public Date getGeldig_tot() {
        return this.geldig_tot;
    }

    public void setGeldig_tot(Date geldig_tot) {
        this.geldig_tot = geldig_tot;
    }

    public int getKlasse() {
        return this.klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public double getSaldo() {
        return this.saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getReiziger_id() {
        return this.reiziger_id;
    }

    public void setReiziger_id(int reiziger_id) {
        this.reiziger_id = reiziger_id;
    }

    public Reiziger getReiziger() {
        return this.reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OVChipkaart that = (OVChipkaart) o;
        return kaart_nummer == that.kaart_nummer && klasse == that.klasse && Double.compare(that.saldo, saldo) == 0
                && reiziger_id == that.reiziger_id && Objects.equals(geldig_tot, that.geldig_tot)
                && Objects.equals(reiziger, that.reiziger);
    }

    @Override
    public String toString() {
        return String.format("{%s %s %s %s}", this.kaart_nummer, this.geldig_tot,
                this.klasse, this.saldo);
    }
}
