package persistence;

import domein.Adres;
import domein.Reiziger;

import java.util.List;

public interface AdresDAO {
    public boolean save(Adres adres);
    public boolean update(Adres adres);
    public boolean delete(Adres adres);
    public Adres findById(int id);
    public Adres findByReiziger(Reiziger reiziger);
    public List<Adres> findAll();
}
