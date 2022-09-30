package persistence;

import domein.Adres;
import domein.OVChipkaart;
import domein.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO{
    private final Connection connection;
    private AdresDAO adao;
    private OVChipkaartDAO ovdao;

    public ReizigerDAOPsql(Connection connection) {
        this.connection = connection;
    }

    public void setAdao(AdresDAO adao) {
        this.adao = adao;
    }

    public void setOvdao(OVChipkaartDAO ovdao) {
        this.ovdao = ovdao;
    }

    private Reiziger createReiziger(ResultSet rs) throws SQLException {
        Reiziger reiziger = new Reiziger(rs.getInt(1), rs.getString(2), rs.getString(3),
                rs.getString(4), rs.getDate(5));
        reiziger.setAdres(adao.findByReiziger(reiziger));
        if (reiziger.getAdres() != null) reiziger.getAdres().setReizigerId(reiziger);
        reiziger.setOVChipkaarten(ovdao.findByReiziger(reiziger));
        return reiziger;
    }

    private boolean containsId(List<OVChipkaart> kaarten, OVChipkaart kaart) {
        for (OVChipkaart ovChipkaart : kaarten) {
            if (ovChipkaart.getKaartNummer() == kaart.getKaartNummer()) {
                return true;
            }
        }
        return false;
    }

    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public boolean save(Reiziger reiziger) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("""
            INSERT INTO ovchip.public.reiziger(reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum)
            VALUES (?, ?, ?, ?, ?)
            """);
            pst.setInt(1, reiziger.getId());
            pst.setString(2, reiziger.getVoorletters());
            pst.setString(3, reiziger.getTussenvoegsel());
            pst.setString(4, reiziger.getAchternaam());
            pst.setDate(5, reiziger.getGeboortedatum());

            boolean adres = true;
            boolean reizigerSave = pst.executeUpdate() > 0;

            for (OVChipkaart kaart : reiziger.getOvChipkaarten()) {
                if (!ovdao.save(kaart)) return false;
            }

            Adres adresReiziger = reiziger.getAdres();
            if (adresReiziger != null) {
                adres = adao.save(adresReiziger);
            }

            return reizigerSave && adres;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            closeStatement(pst);
        }
        return false;
    }

    @Override
    public boolean update(Reiziger reiziger) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("""
            UPDATE ovchip.public.reiziger
            SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ?
            WHERE reiziger_id = ?
            """);
            pst.setString(1, reiziger.getVoorletters());
            pst.setString(2, reiziger.getTussenvoegsel());
            pst.setString(3, reiziger.getAchternaam());
            pst.setDate(4, reiziger.getGeboortedatum());
            pst.setInt(5, reiziger.getId());

            boolean reizigerUpdate = pst.executeUpdate() > 0;

            Adres adresReiziger = reiziger.getAdres();
            Adres adresDB = adao.findByReiziger(reiziger);

            if (adresReiziger == null && adresDB != null) {
                adao.delete(adresDB);
            }

            if (adresReiziger != null) {
                if (adresDB == null) {
                    adao.save(adresReiziger);
                }

                if (adresReiziger != adresDB) {
                    adao.update(adresReiziger);
                }
            }

            List<OVChipkaart> kaartenDB = ovdao.findByReiziger(reiziger);
            List<OVChipkaart> kaartenReiziger = reiziger.getOvChipkaarten();

            kaartenReiziger.forEach(kaartReiziger -> {
                if (!containsId(kaartenDB, kaartReiziger)) {
                    ovdao.save(kaartReiziger);
                }
                else if (!kaartenDB.contains(kaartReiziger)) {
                    ovdao.update(kaartReiziger);
                }
            });

            kaartenDB.forEach(kaartDB -> {
                if (!containsId(kaartenReiziger, kaartDB)) {
                    ovdao.delete(kaartDB);
                }
            });

            return reizigerUpdate;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            closeStatement(pst);
        }
        return false;
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("""
            DELETE FROM ovchip.public.reiziger
            WHERE reiziger_id=?
            """);
            pst.setInt(1, reiziger.getId());
            boolean adres = true;

            for (OVChipkaart kaart : reiziger.getOvChipkaarten()) {
                if (!ovdao.delete(kaart)) return false;
            }

            Adres adresReiziger = reiziger.getAdres();
            if (adresReiziger != null) {
                adres = adao.delete(adresReiziger);
            }

            return pst.executeUpdate() > 0 && adres;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            closeStatement(pst);
        }
        return false;
    }

    @Override
    public Reiziger findById(int id) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("""
            SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum
            FROM ovchip.public.reiziger
            WHERE reiziger_id = ?
            """);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                return createReiziger(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(pst);
        }
        return null;
    }

    @Override
    public List<Reiziger> findByGbDatum(String datum) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("""
            SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum
            FROM ovchip.public.reiziger
            WHERE geboortedatum = ?
            """);
            pst.setDate(1, Date.valueOf(datum));
            rs = pst.executeQuery();
            List<Reiziger> reizigers = new ArrayList<>();
            while (rs.next()) {
                reizigers.add(createReiziger(rs));
            }
            return reizigers;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(pst);
        }
        return null;
    }

    @Override
    public List<Reiziger> findAll() {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = connection.createStatement();
            rs = st.executeQuery("""
            SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum
            FROM ovchip.public.reiziger
            """);
            List<Reiziger> reizigers = new ArrayList<>();
            while (rs.next()) {
                reizigers.add(createReiziger(rs));
            }
            return reizigers;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(st);
        }
        return null;
    }
}
