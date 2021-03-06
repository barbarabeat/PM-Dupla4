package bicicletario;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import app.App;
import bicicleta.Bicicleta;


public class BicicletaTest {
   
	@Test
	public void BicicletaConstructorTest() {
        Bicicleta bicicleta = new Bicicleta(0, 0, null, null, null);
        bicicleta.setId(404);
        bicicleta.setNumero(101010);
        bicicleta.setMarca("Marcou");
        bicicleta.setModelo("Bike Nova");
        bicicleta.setAno("1999"); 
    }
	
	@Test
	public void testIdBicicleta() {
	   int testId = 420;
	   Bicicleta bicicleta = new Bicicleta(testId, 404, "Marcou", "Bike Nova", "1999");
	   bicicleta.setId(testId);
	    assertEquals(testId, bicicleta.getId());
	}

    @Test
    public void unirestTest() {
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "7002";  
        
        HttpResponse response = Unirest.get("http://localhost:" + port + "/bicicleta").asString();
        assertEquals(200, response.getStatus());
    }
    
}
