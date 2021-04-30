package bicicleta;

public class Bicicleta {
	public enum BicicletaStatus { DISPONIVEL, NOVA, EM_USO, EM_REPARO, APOSENTADA };

	public int id;
    public int numero;
	public String marca;
    public String modelo;
    public String ano;
    public BicicletaStatus status;

    public Bicicleta(int id, int numero, String marca, String modelo, String ano) {
        this.id = id;
    	this.numero = numero;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.status = BicicletaStatus.NOVA;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public BicicletaStatus getStatus() {
		return status;
	}

	public void setStatus(BicicletaStatus status) {
		this.status = status;
	}
}