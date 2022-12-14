import domein.Adres;
import domein.OVChipkaart;
import domein.Product;
import domein.Reiziger;
import persistence.*;

import java.sql.*;
import java.util.List;
import java.util.Random;

public class Main {
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        getConnection();

        ReizigerDAOPsql rdao = new ReizigerDAOPsql(conn);
        AdresDAOPsql adao = new AdresDAOPsql(conn);
        OVChipkaartDAOPsql ovdao = new OVChipkaartDAOPsql(conn);
        ProductDAOPsql pdao = new ProductDAOPsql(conn);
        ovdao.setRdao(rdao);
        rdao.setAdao(adao);
        rdao.setOvdao(ovdao);
        adao.setRdao(rdao);
        ovdao.setPdao(pdao);
        pdao.setOvdao(ovdao);

        testReizigerDAO(rdao);
        testAdresDAO(adao, rdao);
        testEenOpEen(rdao, adao);
        testOVChipkaartDAO(ovdao, rdao);
        testProductDAO(ovdao, pdao);

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
    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) {
        System.out.println("\nTests AdresDAO\n");

        System.out.println("[Test] Save");
        System.out.println("Reiziger aanmaken daarna adres aanmaken");
        Reiziger reiziger = new Reiziger(6, "R", "van", "Dam", Date.valueOf("2004-08-01"));
        Adres adres1 = new Adres(6, "2317HJ", "17", "Cameliadal", "Leiden",reiziger);
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
            System.out.println(adres.getReizigerId());
        }

        System.out.println("\n[Test] Delete");
        System.out.println("Aantal adressen voor delete: " + adao.findAll().size());
        adao.delete(adres1);
        rdao.delete(reiziger);
        System.out.println("Aantal adressen na delete: " + adao.findAll().size());

    }
    private static void testEenOpEen(ReizigerDAO rdao, AdresDAO adao) {
        System.out.println("\nTests 1-1 relatie\n");
        System.out.println("[Test] Save");
        Reiziger reiziger = new Reiziger(6, "R", null, "Been", Date.valueOf("2002-05-23"));
        Adres a1 = new Adres(6, "3452NA", "400", "Het Horseler", "Ede", reiziger);
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
        reiziger.setAdres(new Adres(6, "3563AT", "12", "Jaarbeursplein", "Utrecht", reiziger));
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

    private static void testOVChipkaartDAO(OVChipkaartDAOPsql ovdao, ReizigerDAOPsql rdao) {
        System.out.println("\n[Test] findAll");
        ovdao.findAll().forEach(ovchip -> System.out.println(ovchip.toString()));

        System.out.println("\n[Test] Save");
        System.out.println("aantal chipkaarten voor save: " + ovdao.findAll().size());
        OVChipkaart chip = new OVChipkaart(24657, Date.valueOf("2022-09-30"), 2,
                30.00, 1, rdao.findById(1));
        ovdao.save(chip);
        System.out.println("aantal chipkaarten na save: " + ovdao.findAll().size());

        System.out.println("\n[Test] Update");
        System.out.println("voor de update: " + ovdao.findById(chip.getKaartNummer()));
        chip.setSaldo(20.00);
        chip.setGeldigTot(Date.valueOf("2022-12-31"));
        ovdao.update(chip);
        System.out.println("na de update: " + ovdao.findById(chip.getKaartNummer()));

        System.out.println("\n[Test] Delete");
        System.out.println("aantal chipkaarten voor delete: " + ovdao.findAll().size());
        ovdao.delete(chip);
        if (ovdao.findAll().contains(chip)) throw new Error("Delete verwijderde het object niet");
        System.out.println("aantal chipkaarten na delete: " + ovdao.findAll().size());

        System.out.println("\n[Test] findByReiziger");
        ovdao.findByReiziger(rdao.findById(2)).forEach(System.out::println);

        System.out.println("\n[Test] findById");
        OVChipkaart kaart = ovdao.findById(35283);
        System.out.println(kaart);
        System.out.println(kaart.getReiziger());
        kaart.getReiziger().getOvChipkaarten().forEach(System.out::println);

        System.out.println("\n[Test] CRUD combinatie met Reiziger");

        System.out.println("\n[Test] Save");
        Reiziger reiziger = new Reiziger(6, "R", "van", "Dam", Date.valueOf("2004-08-01"));
        Adres adres1 = new Adres(6, "2317HJ", "17", "Cameliadal", "Leiden", reiziger);
        OVChipkaart kaart1 = new OVChipkaart(75389, Date.valueOf("2023-01-31"), 2, 10.00, 6);
        OVChipkaart kaart2 = new OVChipkaart(75390, Date.valueOf("2026-04-30"), 1, 20.00, 6);
        reiziger.setAdres(adres1);
        reiziger.addOvChipkaart(kaart1);
        reiziger.addOvChipkaart(kaart2);
        System.out.println("Aantal reizigers voor save: " + rdao.findAll().size());
        rdao.save(reiziger);
        System.out.println("Aantal reizigers na save: " + rdao.findAll().size());
        System.out.println(reiziger);

        System.out.println("\n[Test] Update");
        System.out.println("Voor de update: " + reiziger);
        reiziger.setVoorletters("C");
        reiziger.setAchternaam("Dijk");
        reiziger.setGeboortedatum(Date.valueOf("1990-02-23"));
        adres1.setHuisnummer("23");
        adres1.setStraat("Seringenlaan");
        adres1.setWoonplaats("Woerden");
        adres1.setPostcode("3442HK");
        kaart1.setKlasse(1);
        kaart1.setSaldo(1.00);
        kaart1.setGeldigTot(Date.valueOf("2027-09-30"));
        kaart2.setKlasse(2);
        kaart2.setSaldo(100.00);
        kaart2.setGeldigTot(Date.valueOf("2026-08-31"));
        rdao.update(reiziger);
        System.out.println("Na de update: " + rdao.findById(reiziger.getId()));


        System.out.println("\n[Test] Delete");
        System.out.println("aantal reizigers voor delete: " + rdao.findAll().size());
        rdao.delete(reiziger);
        System.out.println("aantal reizigers na delete: " + rdao.findAll().size());
        if (rdao.findAll().contains(reiziger)) throw new Error("Reiziger niet juist verwijderd");
    }

    private static void testProductDAO(OVChipkaartDAO ovdao, ProductDAO pdao) {
        System.out.println("\n[Test] Save");
        Product p = new Product(7, "TestProduct", "TestBeschrijving", 80.00);
        System.out.println("aantal voor save: " + pdao.findAll().size());
        pdao.save(p);
        System.out.println("aantal na save: " + pdao.findAll().size());

        System.out.println("\n[Test] Update");
        System.out.println("Voor de update: " + p);
        p.setNaam("Update op de naam");
        p.setBeschrijving("Update op de beschrijving");
        p.setPrijs(10.50);
        pdao.update(p);
        System.out.println("Na de update: " + pdao.findById(p.getProductNummer()));


        System.out.println("\n[Test] Koppeling");
        System.out.println("Toevoegen van product aan chipkaart");
        OVChipkaart ov = ovdao.findById(46392);
        System.out.println("voor: " + ov);
        ov.addProduct(pdao.findById(1));
        ov.addProduct(p);
        ovdao.update(ov);
        System.out.println("na: " + ovdao.findById(ov.getKaartNummer()).getProducten());
        ov.removeProduct(pdao.findById(1));
        ov.removeProduct(p);
        ovdao.update(ov);
        System.out.println("terugzetten: " + ovdao.findById(ov.getKaartNummer()));


        System.out.println("\n[Test] Delete");
        System.out.println("aantal voor delete: " + pdao.findAll().size());
        pdao.delete(p);

        System.out.println("aantal na delete: " + pdao.findAll().size());

    }
}
