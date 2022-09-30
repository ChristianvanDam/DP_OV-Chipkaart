package persistence;

import domein.Adres;
import domein.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO{
    private final Connection connection;
    private ReizigerDAO rdao;

    public AdresDAOPsql(Connection connection) {
        this.connection = connection;
    }

    public void setRdao(ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    private Adres createAdres(ResultSet rs) throws SQLException {
        Adres adres = new Adres(rs.getInt(1), rs.getString(2),
                rs.getString(3), rs.getString(4), rs.getString(5),
                null);
        return adres;
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
    public boolean save(Adres adres) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("""
        INSERT INTO ovchip.public.adres(adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id)
        VALUES (?, ?, ?, ?, ?, ?)
        """);
            pst.setInt(1, adres.getId());
            pst.setString(2, adres.getPostcode());
            pst.setString(3, adres.getHuisnummer());
            pst.setString(4, adres.getStraat());
            pst.setString(5, adres.getWoonplaats());
            pst.setInt(6, adres.getReizigerId().getId());

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
    public boolean update(Adres adres) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("""
            UPDATE ovchip.public.adres
            SET postcode=?, huisnummer=?, straat=?, woonplaats=?, reiziger_id=?
            WHERE adres_id=?
""");
            pst.setString(1, adres.getPostcode());
            pst.setString(2, adres.getHuisnummer());
            pst.setString(3, adres.getStraat());
            pst.setString(4, adres.getWoonplaats());
            pst.setInt(5, adres.getReizigerId().getId());
            pst.setInt(6, adres.getId());
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
    public boolean delete(Adres adres) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("""
            DELETE FROM ovchip.public.adres
            WHERE adres_id=?
""");
            pst.setInt(1, adres.getId());
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
    public Adres findById(int id) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("""
            SELECT adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id
            FROM ovchip.public.adres
            WHERE adres_id=?
""");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                Adres a = createAdres(rs);
                a.setReizigerId(rdao.findById(rs.getInt(6)));
                return a;
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
    public Adres findByReiziger(Reiziger reiziger) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("""
        SELECT ovchip.public.adres.adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id
        FROM ovchip.public.adres
        WHERE reiziger_id = ?
""");
            pst.setInt(1, reiziger.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                return createAdres(rs);
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
    public List<Adres> findAll() {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = connection.createStatement();
            rs = st.executeQuery("""
            SELECT adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id
            FROM ovchip.public.adres
""");
            List<Adres> adresList = new ArrayList<>();
            while (rs.next()) {
                Adres a = createAdres(rs);
                a.setReizigerId(rdao.findById(rs.getInt(6)));
                adresList.add(a);
            }
            return adresList;
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
