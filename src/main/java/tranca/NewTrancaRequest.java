package tranca;

public class NewTrancaRequest {
	public int numero;
	public String localizacao;
    public String anoDeFabricacao;
    public String modelo;

    public NewTrancaRequest() {
    }
    	 
    public NewTrancaRequest(int numero, String localizacao, String anoDeFabricacao, String modelo) {
    	this.numero = numero;
    	this.localizacao = localizacao;
    	this.anoDeFabricacao = anoDeFabricacao;
        this.modelo = modelo;
    }
}