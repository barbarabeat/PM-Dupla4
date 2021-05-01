package bicicleta;

import bicicleta.Bicicleta.BicicletaStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// This is a service, it should be independent from Javalin
public class BicicletaService {

    private static Map<Integer, Bicicleta> bicicleta = new HashMap<>();
    private static AtomicInteger lastId;

    static {
        bicicleta.put(0, new Bicicleta(0, 10, "Caloi", "Alice", "2020"));
        bicicleta.put(1, new Bicicleta(1, 10232, "CaloiFake", "AM", "2019"));
        bicicleta.put(2, new Bicicleta(2, 1032, "Caloi", "OldFashion", "2000"));
        lastId = new AtomicInteger(bicicleta.size());
    }

    public static void save(int numero, String marca, String modelo, String ano, BicicletaStatus status) {
        int id = lastId.incrementAndGet();
        Bicicleta b = new Bicicleta(id,numero,marca,modelo,ano);
        b.setStatus(status);
        bicicleta.put(id, b);
    }

    public static Collection<Bicicleta> getAll() {
        return bicicleta.values();
    }

    public static void reintegrarAoSistema(Bicicleta b) {
        b.setStatus(BicicletaStatus.DISPONIVEL);
    }

    public static void setStatus(Bicicleta b, BicicletaStatus s) {
        b.setStatus(s);
    }
    
    public static void update(int idBicicleta, BicicletaStatus status) {
    	
        Bicicleta b = bicicleta.get(idBicicleta);
        b.setStatus(status);
    }

    public static void update(Bicicleta b, int numero, 
                            String marca, String modelo, String ano,
                            BicicletaStatus status) {
        
        b.setNumero(numero);
        b.setMarca(marca);
        b.setModelo(modelo);
        b.setAno(ano);
        b.setStatus(status);
    }

    public static Bicicleta findById(int idBicicleta) {
        return bicicleta.get(idBicicleta);
    }

    public static void delete(int idBicicleta) {
    	bicicleta.remove(idBicicleta);
    }
}