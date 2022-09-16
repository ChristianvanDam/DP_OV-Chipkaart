package persistence;

import domein.OVChipkaart;
import domein.Reiziger;

import java.util.List;

public interface OVChipkaartDAO {
    boolean save(OVChipkaart ovChipkaart);
    boolean update(OVChipkaart ovChipkaart);
    boolean delete(OVChipkaart ovChipkaart);
    OVChipkaart findById(int id);
    List<OVChipkaart> findByReiziger(Reiziger reiziger);
    List<OVChipkaart> findAll();
}
