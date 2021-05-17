package bicicletario;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import app.App;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import tranca.Tranca;
import tranca.Tranca.TrancaStatus;

public class TrancaTest {

	@Test
	public void TrancaConstructorTest() {
		Tranca tranca = new Tranca(0, null, 0, null, null, null);
        tranca.setId(424);
        tranca.setBicicleta(null);
        tranca.setNumero(420);
        tranca.setLocalizacao("Tijuca");
        tranca.setAnoDeFabricacao("2000");
        tranca.setModelo("1999");
        tranca.setStatus(TrancaStatus.LIVRE);
    }

	@Test
	public void testIdTranca() {
	   int testId = 420;
	   Tranca tranca = new Tranca(424, null, 420, "Tijuca", "1999", "2000");
	   tranca.setId(testId);
	    assertEquals(testId, tranca.getId());
	  }	

    private App app = new App(); // inject any dependencies you might have

    @Test
    public void unirestTest() {
      //  app.startApp(7000);
        HttpResponse response = Unirest.get("http://localhost:7000/tranca").asString();
        assertEquals(200, response.getStatus());
        app.stop();
    }
}
