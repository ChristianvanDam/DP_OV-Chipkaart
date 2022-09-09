package persistence;

import domein.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO{
    private final Connection connection;
    private final AdresDAO adao;

    public ReizigerDAOPsql(Connection connection) {
        this.connection = connection;
        this.adao = new AdresDAOPsql(connection);
    }

    private Reiziger createReiziger(ResultSet rs) throws SQLException {
        Reiziger reiziger = new Reiziger();
        reiziger.setId(rs.getInt(1));
        reiziger.setVoorletters(rs.getString(2));
        reiziger.setTussenvoegsel(rs.getString(3));
        reiziger.setAchternaam(rs.getString(4));
        reiziger.setGeboortedatum(rs.getDate(5));
        reiziger.setAdres(adao.findByReiziger(reiziger));
        return reiziger;
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

            if (reiziger.getAdres() != null) {
                return pst.executeUpdate() > 0 && adao.save(reiziger.getAdres());
            }
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        } finally {
            closeStatement(pst);
        }
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

            if (reiziger.getAdres() == null && adao.findByReiziger(reiziger) != null) {
                return adao.delete(adao.findByReiziger(reiziger)) && pst.executeUpdate() > 0;
            }

            if (reiziger.getAdres() != null && adao.findByReiziger(reiziger) == null) {
                return adao.save(reiziger.getAdres()) && pst.executeUpdate() > 0;
            }

            if (reiziger.getAdres() != adao.findByReiziger(reiziger)) {
                return adao.update(reiziger.getAdres()) && pst.executeUpdate() > 0;
            }
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
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
            if (reiziger.getAdres() != null) {
                return adao.delete(reiziger.getAdres()) && pst.executeUpdate() > 0;
            }
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
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
        } finally {
            closeResultSet(rs);
            closeStatement(st);
        }
        return null;
    }
}
