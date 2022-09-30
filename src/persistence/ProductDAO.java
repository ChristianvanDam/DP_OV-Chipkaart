package persistence;

import domein.OVChipkaart;
import domein.Product;

import java.util.List;

public interface ProductDAO {
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(Product product);
    Product findById(int id);
    List<Product> findAll();
    List<Product> findByOvchipkaart(OVChipkaart ovChipkaart);
}
