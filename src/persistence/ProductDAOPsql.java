package persistence;

import domein.OVChipkaart;
import domein.Product;
import org.w3c.dom.ls.LSOutput;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;
    private OVChipkaartDAO ovdao;

    public  ProductDAOPsql(Connection connection) {
        this.conn = connection;
    }

    public void setOvdao(OVChipkaartDAO ovdao) {
        this.ovdao = ovdao;
    }

    public Product createProduct(ResultSet rs) {
        try {
            Product product = new Product(rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getDouble(4));
            return product;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void closeStatement(Statement st) {
        try {
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private boolean containsId(List<OVChipkaart> ovChipkaarten, OVChipkaart k) {
        for (OVChipkaart o : ovChipkaarten) {
            if (o.getKaartNummer() == k.getKaartNummer()) return true;
        }
        return false;
    }

    @Override
    public boolean save(Product product) {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("""
            INSERT INTO ovchip.public.product(product_nummer, naam, beschrijving, prijs)
            VALUES (?, ?, ?, ?)
            """);
            pst.setInt(1, product.getProductNummer());
            pst.setString(2, product.getNaam());
            pst.setString(3, product.getBeschrijving());
            pst.setDouble(4, product.getPrijs());

            for (OVChipkaart o : product.getOvChipkaarten()) {
                if (!addKoppeltabel(o, product)) return false;
            }

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            assert pst != null;
            closeStatement(pst);
        }
        return false;
    }

    @Override
    public boolean update(Product product) {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("""
            UPDATE ovchip.public.product
            SET naam=?, beschrijving=?, prijs=?
            WHERE product_nummer=?
            """);
            pst.setString(1, product.getNaam());
            pst.setString(2, product.getBeschrijving());
            pst.setDouble(3, product.getPrijs());
            pst.setInt(4, product.getProductNummer());

            List<OVChipkaart> kaartenDB = findById(product.getProductNummer()).getOvChipkaarten();
            List<OVChipkaart> kaartenProduct = product.getOvChipkaarten();

            for (OVChipkaart o : kaartenDB) {
                if (!kaartenProduct.contains(o)) {
                    if (!removeKoppeltabel(o, product)) return false;
                }
            }

            for (OVChipkaart o : kaartenProduct) {
                if (!containsId(kaartenDB, o)) {
                    if (!addKoppeltabel(o, product)) return false;
                }
            }

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            assert pst != null;
            closeStatement(pst);
        }
        return false;
    }

    @Override
    public boolean delete(Product product) {
        PreparedStatement pst = null;
        try {

            pst = conn.prepareStatement("""
            DELETE FROM ovchip.public.product
            WHERE product_nummer=?
            """);
            pst.setInt(1, product.getProductNummer());

            return removeKoppeltabel(product) && pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            assert pst != null;
            closeStatement(pst);
        }
        return false;
    }

    @Override
    public Product findById(int id) {
        List<OVChipkaart> kaarten = ovdao.findAll();
        for (OVChipkaart kaart : kaarten) {
            for (Product p : kaart.getProducten()) {
                if (p.getProductNummer() == id) return p;
            }
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("""
            SELECT product_nummer, naam, beschrijving, prijs
            FROM ovchip.public.product
            WHERE product_nummer=?
            """);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                return createProduct(rs);
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
    public List<Product> findAll() {
        List<Product> producten = new ArrayList<>();
        for (OVChipkaart o : ovdao.findAll()) {
            for (Product p : o.getProducten()) {
                if (!producten.contains(p)) producten.add(p);
            }
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("""
            SELECT product_nummer, naam, beschrijving, prijs
            FROM ovchip.public.product
            """);
            rs = pst.executeQuery();
            while (rs.next()) {
                Product p = createProduct(rs);
                if (!producten.contains(p)) {
                    producten.add(p);
                }
            }
            return producten;
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
    public List<Product> findByOvchipkaart(OVChipkaart ovChipkaart) {
        List<Product> producten = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("""
            SELECT p.product_nummer, naam, beschrijving, prijs
            FROM ovchip.public.product p
            INNER JOIN ovchip.public.ov_chipkaart_product ocp on p.product_nummer = ocp.product_nummer
            INNER JOIN ovchip.public.ov_chipkaart oc on oc.kaart_nummer = ocp.kaart_nummer
            WHERE oc.kaart_nummer=?
            """);
            pst.setInt(1, ovChipkaart.getKaartNummer());
            rs = pst.executeQuery();
            while (rs.next()) {
                producten.add(createProduct(rs));
            }
            return producten;
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

    private boolean removeKoppeltabel(Product product) {
        PreparedStatement pstKoppel = null;
        try {
            pstKoppel = conn.prepareStatement("""
            DELETE FROM ovchip.public.ov_chipkaart_product
            WHERE product_nummer=?
            """);

        pstKoppel.setInt(1, product.getProductNummer());

        return pstKoppel.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            assert pstKoppel != null;
            closeStatement(pstKoppel);
        }
        return false;
    }

    private boolean removeKoppeltabel(OVChipkaart ovChipkaart, Product product) {
        PreparedStatement pstKoppel = null;
        try {
            pstKoppel = conn.prepareStatement("""
            DELETE FROM ovchip.public.ov_chipkaart_product
            WHERE kaart_nummer=? AND product_nummer=?
            """);

            pstKoppel.setInt(1, ovChipkaart.getKaartNummer());
            pstKoppel.setInt(2, product.getProductNummer());

            return pstKoppel.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            assert pstKoppel != null;
            closeStatement(pstKoppel);
        }
        return false;
    }

    private boolean addKoppeltabel(OVChipkaart ovChipkaart, Product product) {
        PreparedStatement pstKoppel = null;
        try {
            pstKoppel = conn.prepareStatement("""
            INSERT INTO ovchip.public.ov_chipkaart_product(kaart_nummer, product_nummer, last_update)
            VALUES (?, ?, ?)
            """);

            pstKoppel.setInt(1, ovChipkaart.getKaartNummer());
            pstKoppel.setInt(2, product.getProductNummer());
            pstKoppel.setDate(3, Date.valueOf(LocalDate.now()));

            return pstKoppel.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            assert pstKoppel != null;
            closeStatement(pstKoppel);
        }
        return false;
    }
}
