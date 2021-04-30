package tranca;

import bicicleta.Bicicleta;
import bicicleta.Bicicleta.BicicletaStatus;;

public class Tranca {
	public enum TrancaStatus { LIVRE, OCUPADA, NOVA, APOSENTADA, EM_REPARO }

	public int id;
	public Bicicleta bicicleta;
	public int numero;
	public String localizacao;
    public String anoDeFabricacao;
    public String modelo;
    public TrancaStatus status;

    public Tranca(int id, Bicicleta bicicleta, int numero,
				String localizacao, String anoDeFabricacao, String modelo) {

    	this.id = id;
    	this.bicicleta = bicicleta;
    	this.numero = numero;
    	this.localizacao = localizacao;
    	this.anoDeFabricacao = anoDeFabricacao;
        this.modelo = modelo;
        this.status = TrancaStatus.LIVRE;
    }

	public boolean setBicicleta(Bicicleta b) {
		if (this.status != TrancaStatus.LIVRE ||
			this.status != TrancaStatus.NOVA) {
			
			return false;
		}

		if (b.getStatus() != BicicletaStatus.DISPONIVEL ||
			b.getStatus() != BicicletaStatus.NOVA) {
			
			return false;
		}

		b.setStatus(BicicletaStatus.DISPONIVEL);
		this.bicicleta = b;
		return true;
	}

	public Bicicleta removeBicicleta() {
		Bicicleta b = this.bicicleta;
		this.bicicleta = null;
		this.status = TrancaStatus.LIVRE;
		return b;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bicicleta getBicicleta() {
		// FIX ME: return read-only
		return bicicleta;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getAnoDeFabricacao() {
		return anoDeFabricacao;
	}

	public void setAnoDeFabricacao(String anoDeFabricacao) {
		this.anoDeFabricacao = anoDeFabricacao;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public TrancaStatus getStatus() {
		return status;
	}

	public void setStatus(TrancaStatus status) {
		this.status = status;
	}

	public boolean equals(Tranca t) {
		return this.id == t.id ? true : false;
	}

}