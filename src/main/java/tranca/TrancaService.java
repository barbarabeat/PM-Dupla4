package tranca;

import bicicleta.Bicicleta.BicicletaStatus;
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

    public static void save(int numero, String localizacao,
                            String anoDeFabricacao, String modelo, TrancaStatus status) {
        
        int id = lastId.incrementAndGet();
        Tranca t = new Tranca(id, null, numero, localizacao, anoDeFabricacao, modelo);
        t.setStatus(status);

        tranca.put(id, t);
    }

    public static Collection<Tranca> getAll() {
        return tranca.values();
    }

    public static void reintegrarAoSistema(Tranca t) {
        t.setStatus(TrancaStatus.LIVRE);
    }

    public static void removeBicicleta(Tranca t) {
        t.removeBicicleta();
        t.setStatus(TrancaStatus.LIVRE);
    }

    public static void setStatus(Tranca t, TrancaStatus s) {
        t.setStatus(s);
    }


    public static boolean addBicicleta(Tranca t, Bicicleta b) {
		if (t.status != TrancaStatus.LIVRE ||
			t.status != TrancaStatus.NOVA) {
			
			return false;
		}

		if (b.getStatus() != BicicletaStatus.DISPONIVEL ||
			b.getStatus() != BicicletaStatus.NOVA) {
			
			return false;
		}

		b.setStatus(BicicletaStatus.DISPONIVEL);
        t.setBicicleta(b);
        return true;
    }

    public static Tranca findById(int trancaId) {
        return tranca.get(trancaId);
    }

    public static void delete(int trancaId) {
    	tranca.remove(trancaId);
    }

    public static void update(Tranca t, int numero, String localizacao,
                            String anoDeFabricacao, String modelo, TrancaStatus s) {

        t.setNumero(numero);
        t.setLocalizacao(localizacao);
        t.setAnoDeFabricacao(anoDeFabricacao);
        t.setModelo(modelo);
        t.setStatus(s);
    }
}