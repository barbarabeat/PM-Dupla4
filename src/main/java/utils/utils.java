package utils;
import bicicleta.Bicicleta.BicicletaStatus;
import tranca.Tranca.TrancaStatus;

public class utils {
    public static int paramToInt(String param) {
        return Integer.parseInt(param);
    }

    public static BicicletaStatus paramToBicicletaStatus(String param) {
        return BicicletaStatus.APOSENTADA;
    }

    public static TrancaStatus paramToTrancaStatus(String param) {
    	return TrancaStatus.valueOf(param);
    	
    }

}
