package bicicletario;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import app.App;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import totem.Totem;


public class TotemTest {
	@Test
	public void TotemConstructorTest() {
		Totem totem = new Totem(0, null);
		totem.setId(500);
        totem.setLocal("Tijuca");
    }
	
	@Test
	public void testIdTotem() {
	   int testId = 420;
	   Totem totem = new Totem(testId,"Tijuca");
	   totem.setId(testId);
	    assertEquals(testId, totem.getId());
	  }
	
    @Test
    public void unirestTest() {
		String port = System.getenv("PORT") != null ? System.getenv("PORT") : "7002";  
        
		HttpResponse response = Unirest.get("http://localhost:" + port + "/totem").asString();
		assertEquals(200, response.getStatus());
    }
}
