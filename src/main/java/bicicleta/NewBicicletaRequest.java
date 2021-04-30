package bicicleta;
import bicicleta.Bicicleta.BicicletaStatus;

public class NewBicicletaRequest {
	public int numero;
	public String marca;
    public String modelo;
    public String ano;
    public BicicletaStatus status;

    public NewBicicletaRequest() {
    }
    	 
    public NewBicicletaRequest(int numero, String marca, String modelo, String ano, BicicletaStatus status) {
    	this.numero = numero;
    	this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.status = status;
    }
}