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
	
    private App app = new App(); // inject any dependencies you might have

    @Test
    public void unirestTest() {
        //app.startApp(7000);
        HttpResponse response = Unirest.get("https://d4-equipamento.herokuapp.com/totem").asString();
        assertEquals(200, response.getStatus());
        app.stop();
    }
}
