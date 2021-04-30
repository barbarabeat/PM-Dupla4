package tranca;

import bicicleta.Bicicleta;

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
    }

	public void setBicicleta(Bicicleta b) {
		this.bicicleta = b;
	}

	public Bicicleta removeBicicleta() {
		Bicicleta b = this.bicicleta;
		this.bicicleta = null;
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

	// Overloading equals(Object o) in order to add Trancas to sets.
	public boolean equals(Tranca t) {
		return this.id == t.id ? true : false;
	}

}