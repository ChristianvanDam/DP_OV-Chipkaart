import domein.Adres;
import domein.Reiziger;
import persistence.AdresDAO;
import persistence.AdresDAOPsql;
import persistence.ReizigerDAO;
import persistence.ReizigerDAOPsql;

import java.sql.*;
import java.util.List;
import java.util.Random;

public class Main {
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        getConnection();

        ReizigerDAOPsql rdao = new ReizigerDAOPsql(conn);
        AdresDAOPsql adao = new AdresDAOPsql(conn);
        testReizigerDAO(rdao);
        testAdresDAO(adao, rdao);
        testEenOpEen(rdao, adao);

        closeConnection();
    }

    private static void getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5433/ovchip";
        conn = DriverManager.getConnection(url, "postgres", "pg@min");
    }

    private static void closeConnection() throws SQLException {
        conn.close();
    }

    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("ReizigerDAO.findAll() geeft het volgende:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }

        System.out.println();

        String gbDatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbDatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        System.out.println("[Test] Update");
        System.out.println("reiziger voor update:");
        System.out.println(sietske);
        sietske.setVoorletters("A");
        sietske.setTussenvoegsel("van den");
        sietske.setAchternaam("Berg");
        sietske.setGeboortedatum(Date.valueOf("2012-12-08"));
        System.out.println("reiziger na ReizigerDAO.update()");
        rdao.update(sietske);
        Reiziger result = rdao.findById(sietske.getId());
        System.out.println(result + "\n");

        System.out.println("[Test] Delete");
        System.out.println("Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete() ");
        rdao.delete(sietske);
        reizigers = rdao.findAll();
        if (reizigers.contains(sietske)) throw new SQLException("reiziger is niet verwijderd");
        System.out.println(reizigers.size() + " reizigers\n");

        System.out.println("[Test] findById");
        Random rn = new Random();
        int number = rn.nextInt(0, 5);
        System.out.println("zoeken naar reiziger met id [" + number + "] door middel van ReizigerDAO.findById()");
        System.out.println(rdao.findById(number) + "\n");

        System.out.println("[Test] findByGbDatum");
        number = rn.nextInt(0, 5);
        Reiziger r1 = reizigers.get(number);
        System.out.println("zoeken naar reiziger(s) met geboortedatum [" + r1.getGeboortedatum().toString() + "] door middel van ReizigerDAO.findByGbDatum()");
        List<Reiziger> reizigersGb = rdao.findByGbDatum(r1.getGeboortedatum().toString());
        for (Reiziger rz : reizigersGb) {
            System.out.println(rz);
        }

    }
    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException{
        System.out.println("\nTests AdresDAO\n");

        System.out.println("[Test] Save");
        System.out.println("Reiziger aanmaken daarna adres aanmaken");
        Reiziger reiziger = new Reiziger(6, "R", "van", "Dam", Date.valueOf("2004-08-01"));
        Adres adres1 = new Adres(6, "2317HJ", "17", "Cameliadal", "Leiden",6);
        rdao.save(reiziger);
        adao.save(adres1);
        System.out.println(adao.findById(adres1.getId()));
        System.out.println(rdao.findById(reiziger.getId()));

        System.out.println("\n[Test] Update");
        Adres a = adao.findById(4);
        System.out.println("voor update: " + a);
        a.setPostcode("5367KL");
        a.setHuisnummer("64");
        a.setStraat("Stadsbrink");
        a.setWoonplaats("Wageningen");
        adao.update(a);
        System.out.println("na update: " + adao.findById(4));
        a.setPostcode("3817CH");
        a.setHuisnummer("4");
        a.setStraat("Arnhemseweg");
        a.setWoonplaats("Amersfoort");
        adao.update(a);

        System.out.println("\n[Test] findAll()\n");
        for (Adres adres : adao.findAll()) {
            System.out.println(adres);
        }

        System.out.println("\n[Test] Delete");
        System.out.println("Aantal adressen voor delete: " + adao.findAll().size());
        adao.delete(adres1);
        rdao.delete(reiziger);
        System.out.println("Aantal adressen na delete: " + adao.findAll().size());

    }
    private static void testEenOpEen(ReizigerDAO rdao, AdresDAO adao) throws SQLException {
        System.out.println("\nTests 1-1 relatie\n");
        System.out.println("[Test] Save");
        Reiziger reiziger = new Reiziger(6, "R", null, "Been", Date.valueOf("2002-05-23"));
        Adres a1 = new Adres(6, "3452NA", "400", "Het Horseler", "Ede", 6);
        reiziger.setAdres(a1);
        rdao.save(reiziger);
        if (!(rdao.findAll().size() == 6 && adao.findAll().size() == 6)) throw new Error("test gefaald");
        System.out.println(rdao.findById(6));

        System.out.println("\n[Test] Update");
        System.out.println("voor de update: " + reiziger);
        reiziger.setVoorletters("D");
        reiziger.setTussenvoegsel("de");
        reiziger.setAchternaam("Graaf");
        reiziger.setGeboortedatum(Date.valueOf("1977-12-01"));
        reiziger.setAdres(null);
        rdao.update(reiziger);
        System.out.println("na de update: " + rdao.findById(reiziger.getId()));
        reiziger.setAdres(new Adres(6, "3563AT", "12", "Jaarbeursplein", "Utrecht", 6));
        rdao.update(reiziger);
        System.out.println("nieuw adres erin zetten:");
        System.out.println(rdao.findById(reiziger.getId()));
        System.out.println("adres veranderen:");
        reiziger.setAdres(a1);
        rdao.update(reiziger);
        System.out.println(rdao.findById(reiziger.getId()));

        System.out.println("\n[Test] Delete");
        System.out.println("Aantal reizigers voor delete: " + rdao.findAll().size());
        rdao.delete(rdao.findById(6));
        if (rdao.findById(6) == null) {
            System.out.println("Aantal reizigers na delete: " + rdao.findAll().size());
        }
    }
}
