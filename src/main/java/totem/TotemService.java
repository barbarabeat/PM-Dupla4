package totem;

import tranca.Tranca;
import tranca.Tranca.TrancaStatus;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// This is a service, it should be independent from Javalin
public class TotemService {

    private static Map<Integer, Totem> totem = new HashMap<>();
    private static AtomicInteger lastId;

    static {
        totem.put(0, new Totem(0, "Rua das Laranjeiras"));
        totem.put(1, new Totem(1, "Silent Hill Avenue"));
        lastId = new AtomicInteger(totem.size());
    }

    public static void save(String localizacao) {
        int id = lastId.incrementAndGet();
        totem.put(id, new Totem(id, localizacao));
    }

    public static Collection<Totem> getAll() {
        return totem.values();
    }

    public static void addTranca(Totem totem, Tranca t) {
        if (t.getStatus() == TrancaStatus.LIVRE ||
            t.getStatus() == TrancaStatus.NOVA) {
                
            totem.addTranca(t);
            t.setLocalizacao(totem.getLocal());
        }
    }

    public static void update(Totem totem, String l) {
        totem.setLocal(l);

        for (Tranca t : totem.getTrancas()) {
            t.setLocalizacao(l);
        }
    }

    public static Totem findById(int idTotem) {
        return totem.get(idTotem);
    }

    public static Tranca removeTranca(Totem totem, Tranca t) {
        if (t.getStatus() != TrancaStatus.LIVRE)
            return null;

        if (totem.deleteTranca(t)) {
            t.setLocalizacao(null);
            return t;
        }

        return null;
    }

    public static Totem delete(Totem tot) {
        for (Tranca t : tot.getTrancas()) {
            if (t.getStatus() != TrancaStatus.LIVRE)
                return null;
        }

        for (Tranca t : tot.getTrancas()) {
            removeTranca(tot, t);
        }

        return totem.remove(tot.id);
    }

}