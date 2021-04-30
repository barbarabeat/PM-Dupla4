package tranca;
import bicicleta.Bicicleta;
import tranca.Tranca.TrancaStatus;

public class NewTrancaRequest {
	public Bicicleta bicicleta;
	public int numero;
	public String localizacao;
    public String anoDeFabricacao;
    public String modelo;
    public TrancaStatus status;

    public NewTrancaRequest() {
    }
    	 
    public NewTrancaRequest(Bicicleta bicicleta, int numero, String localizacao, String anoDeFabricacao, String modelo, TrancaStatus status) {
    	this.bicicleta = bicicleta;
    	this.numero = numero;
    	this.localizacao = localizacao;
    	this.anoDeFabricacao = anoDeFabricacao;
        this.modelo = modelo;
        this.status = status;
    }
}