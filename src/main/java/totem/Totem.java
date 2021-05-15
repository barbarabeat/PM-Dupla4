package totem;
import java.util.HashSet;
import tranca.Tranca;
import lombok.Data;

@Data
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
        return this.trancas.add(tranca);
    }

    public HashSet<Tranca> getTrancas() {
        return this.trancas;
    }

    public boolean deleteTranca(Tranca tranca) {
        return this.trancas.remove(tranca);
    }

    public String getLocal() {
        return this.localizacao;
    }

    public void setLocal(String localizacao) {
        this.localizacao = localizacao;
    }
    
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
		
	}
}