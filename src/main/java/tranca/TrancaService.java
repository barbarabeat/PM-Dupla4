package tranca;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import bicicleta.Bicicleta;
import tranca.Tranca.TrancaStatus;

// This is a service, it should be independent from Javalin
public class TrancaService {

    private static Map<Integer, Tranca> tranca = new HashMap<>();
    private static AtomicInteger lastId;

    static {
        tranca.put(0, new Tranca(0, null, 10, null, "2020", "Cadeado Vagabundo"));
        tranca.put(1, new Tranca(1, null, 10, null, "2020", "Cadeado Vagabundo"));
        tranca.put(2, new Tranca(2, null, 10, null, "2020", "Cadeado Vagabundo"));
        lastId = new AtomicInteger(tranca.size());
    }

    public static void save(Bicicleta bicicleta, int numero, String localizacao,
                            String anoDeFabricacao, String modelo, TrancaStatus status) {
        
        int id = lastId.incrementAndGet();
        Tranca t = new Tranca(id, bicicleta, numero, localizacao, anoDeFabricacao, modelo);
        t.setStatus(status);

        tranca.put(id, t);
    }

    public static Collection<Tranca> getAll() {
        return tranca.values();
    }

    public static void definirStatus(int trancaId, TrancaStatus status) {
        Tranca t = findById(trancaId);
        t.setStatus(status);
    }

    public static void removeBicicleta(int trancaId) {
        Tranca t = findById(trancaId);

        t.removeBicicleta();
    }

    public static void update(int id, Bicicleta bicicleta, int numero, String localizacao,
                            String anoDeFabricacao, String modelo, TrancaStatus status) {

        
    }

    public static void addBicicleta(int trancaId, Bicicleta b) {
        Tranca t = findById(trancaId);
        t.setBicicleta(b);
    }

    public static Tranca findById(int trancaId) {
        return tranca.get(trancaId);
    }

    public static void delete(int trancaId) {
    	tranca.remove(trancaId);
    }
}