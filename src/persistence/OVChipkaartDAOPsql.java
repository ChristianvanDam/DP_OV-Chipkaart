package persistence;

import domein.OVChipkaart;
import domein.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO rdao;

    public OVChipkaartDAOPsql(Connection connection) {
        this.conn = connection;
    }

    public void setRdao(ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    private OVChipkaart createOVChipkaart(ResultSet rs) throws SQLException {
        OVChipkaart ovChipkaart = new OVChipkaart(rs.getInt(1), rs.getDate(2),
                rs.getInt(3), rs.getDouble(4), rs.getInt(5)
                );
        return ovChipkaart;
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
    public boolean save(OVChipkaart ovChipkaart) {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("""
            INSERT INTO ovchip.public.ov_chipkaart(kaart_nummer, geldig_tot, klasse, saldo, reiziger_id)
            VALUES (?, ?, ?, ?, ?)
""");
            pst.setInt(1, ovChipkaart.getKaart_nummer());
            pst.setDate(2, ovChipkaart.getGeldig_tot());
            pst.setInt(3, ovChipkaart.getKlasse());
            pst.setDouble(4, ovChipkaart.getSaldo());
            pst.setInt(5, ovChipkaart.getReiziger_id());

            return pst.executeUpdate() > 0;
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
    public boolean update(OVChipkaart ovChipkaart) {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("""
            UPDATE ovchip.public.ov_chipkaart
            SET geldig_tot=?, klasse=?, saldo=?, reiziger_id=?
            WHERE kaart_nummer=?
""");
            pst.setDate(1, ovChipkaart.getGeldig_tot());
            pst.setInt(2, ovChipkaart.getKlasse());
            pst.setDouble(3, ovChipkaart.getSaldo());
            pst.setInt(4, ovChipkaart.getReiziger_id());
            pst.setInt(5, ovChipkaart.getKaart_nummer());

            return pst.executeUpdate() > 0;
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
    public boolean delete(OVChipkaart ovChipkaart) {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("""
            DELETE FROM ovchip.public.ov_chipkaart
            WHERE kaart_nummer=?
""");
            pst.setInt(1, ovChipkaart.getKaart_nummer());

            return pst.executeUpdate() > 0;
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
    public OVChipkaart findById(int id) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("""
            SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id
            FROM ovchip.public.ov_chipkaart
            WHERE kaart_nummer=?
""");
            pst.setInt(1, id);

            rs = pst.executeQuery();
            if (rs.next()) {
                OVChipkaart kaart = createOVChipkaart(rs);
                kaart.setReiziger(rdao.findById(kaart.getReiziger_id()));
                return kaart;
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
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("""
            SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id
            FROM ovchip.public.ov_chipkaart
            WHERE reiziger_id=?
""");
            pst.setInt(1, reiziger.getId());

            rs = pst.executeQuery();
            while (rs.next()) {
                ovChipkaarten.add(createOVChipkaart(rs));
            }
            return ovChipkaarten;
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
    public List<OVChipkaart> findAll() {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("""
            SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id
            FROM ovchip.public.ov_chipkaart
""");
            rs = pst.executeQuery();
            while (rs.next()) {
                OVChipkaart kaart = createOVChipkaart(rs);
                kaart.setReiziger(rdao.findById(kaart.getReiziger_id()));
                ovChipkaarten.add(kaart);
            }
            return ovChipkaarten;
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
}
