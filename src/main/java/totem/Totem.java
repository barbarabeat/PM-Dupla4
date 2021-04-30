package totem;
import java.util.HashSet;
import tranca.Tranca;
import tranca.Tranca.TrancaStatus;

public class Totem {
    public int id;
    public String localizacao;
    public HashSet<Tranca> trancas;

    public Totem(int id, String localizacao) {
        this.id = id;
        this.localizacao = localizacao;
        this.trancas = new HashSet<Tranca>();
    }

    public boolean addTranca(Tranca tranca) {
        if (tranca.getStatus() == TrancaStatus.LIVRE ||
            tranca.getStatus() == TrancaStatus.NOVA) {

            return this.trancas.add(tranca);
        }
        return false;
    }

    public HashSet<Tranca> getTrancas() {
        // FIX ME: return read-only
        return this.trancas;
    }

    public boolean deleteTranca(Tranca tranca) {
        if (tranca.getStatus() != TrancaStatus.LIVRE)
            return false;

        return this.trancas.remove(tranca);
    }
}