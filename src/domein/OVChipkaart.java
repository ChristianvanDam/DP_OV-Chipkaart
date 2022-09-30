package domein;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OVChipkaart {
    private int kaartNummer;
    private Date geldigTot;
    private int klasse;
    private double saldo;
    private int reizigerId;

    private Reiziger reiziger;

    private List<Product> producten;

    public OVChipkaart(int kaartNummer, Date geldigTot, int klasse, double saldo, int reizigerId) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reizigerId = reizigerId;
        this.producten = new ArrayList<>();
    }

    public OVChipkaart(int kaartNummer, Date geldigTot, int klasse, double saldo, int reizigerId, Reiziger reiziger) {
        this(kaartNummer, geldigTot, klasse, saldo, reizigerId);
        this.reiziger = reiziger;
    }

    public int getKaartNummer() {
        return this.kaartNummer;
    }

    public void setKaartNummer(int kaartNummer) {
        this.kaartNummer = kaartNummer;
    }

    public Date getGeldigTot() {
        return this.geldigTot;
    }

    public void setGeldigTot(Date geldigTot) {
        this.geldigTot = geldigTot;
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

    public int getReizigerId() {
        return this.reizigerId;
    }

    public void setReizigerId(int reizigerId) {
        this.reizigerId = reizigerId;
    }

    public Reiziger getReiziger() {
        return this.reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    public void setProducten(List<Product> producten) {
        this.producten = producten;
        for (Product p : producten) {
            p.addovChipkaart(this);
        }
    }

    public List<Product> getProducten() {
        return this.producten;
    }

    public void addProduct(Product product) {
        if (!this.producten.contains(product)) {
            this.producten.add(product);
            product.addovChipkaart(this);
        }
    }

    public void removeProduct(Product product) {
        this.producten.remove(product);
        product.removeOvchipkaart(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OVChipkaart) {
            OVChipkaart ov = (OVChipkaart) o;
            return this.kaartNummer == ov.getKaartNummer()
                    && this.geldigTot.equals(ov.getGeldigTot())
                    && this.klasse == ov.getKlasse()
                    && this.saldo == ov.getSaldo()
                    && this.reizigerId == ov.getReizigerId()
                    && this.producten.equals(ov.getProducten());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getProducten().size() > 0) {
            for (Product p : getProducten()) {
                sb.append(p.toString()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length() - 1);
        }
        return String.format("{%s %s %s %s, %s}", this.kaartNummer, this.geldigTot,
                this.klasse, this.saldo, sb);
    }
}
