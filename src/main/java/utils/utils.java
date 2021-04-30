package utils;
import bicicleta.Bicicleta.BicicletaStatus;
import tranca.Tranca.TrancaStatus;

public class utils {
    public static int paramToInt(String param) {
        return Integer.parseInt(param);
    }

    public static BicicletaStatus paramToBicicletaStatus(String param) {
        // TODO: return params according to string value

        return BicicletaStatus.APOSENTADA;
    }

    public static TrancaStatus paramToTrancaStatus(String param) {
        // TODO: return params according to string value

        return TrancaStatus.APOSENTADA;
    }

}
