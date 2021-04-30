package tranca;

import utils.utils;
import app.ErrorResponse;
import tranca.Tranca.TrancaStatus;
import bicicleta.Bicicleta;
import bicicleta.BicicletaController;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.*;


// This is a controller, it should contain logic related to client/server IO
public class TrancaController {

    @OpenApi(
            summary = "Cadastrar Tranca",
            operationId = "cadastrarTranca",
            path = "/tranca",
            method = HttpMethod.POST,
            tags = {"Tranca"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewTrancaRequest.class)}),
            responses = {
                    @OpenApiResponse(status = "201"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void create(Context ctx) {
        NewTrancaRequest tranca = ctx.bodyAsClass(NewTrancaRequest.class);
        TrancaService.save(tranca.bicicleta,tranca.numero, tranca.localizacao, tranca.anoDeFabricacao, tranca.modelo, tranca.status);
        ctx.status(201);
    }
    @SuppressWarnings("unused")
   	@OpenApi(
               summary = "Colocar uma tranca nova ou retornando de reparo de volta na rede de totens",
               operationId = "integrarNaRedeTranca",
               path = "/tranca/integrarNaRede",
               method = HttpMethod.POST,
               pathParams = {@OpenApiParam(name = "integrarNaRede", type = Integer.class, description = "integrar na rede a Tranca")},
               tags = {"Tranca"},
               requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Bicicleta.class)}),
               responses = {
                       @OpenApiResponse(status = "200"),
                       @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
               }
       )
       public static void integrarNaRedeTranca(Context ctx) {
           Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));  
           if (tranca == null) {
               throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
           }else {
           	if(tranca != TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")))){ //new Tranca
           		NewTrancaRequest newTranca = ctx.bodyAsClass(NewTrancaRequest.class);
           	} else { //old Tranca that returns to service
               	TrancaService.definirStatus(tranca.id, TrancaStatus.LIVRE);
           	}
               ctx.status(200);
           } 
       }

   	@SuppressWarnings("unused")
   	@OpenApi(
               summary = "Retirar tranca para reparo ou aposentadoria",
               operationId = "RetirarDaRedeTranca",
               path = "/tranca/retirarDaRedeTranca",
               method = HttpMethod.POST,
               pathParams = {@OpenApiParam(name = "RetirarDaRedeTranca", type = Integer.class, description = "retirar da rede a Tranca")},
               tags = {"Tranca"},
               requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Bicicleta.class)}),
               responses = {
                       @OpenApiResponse(status = "200"),
                       @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
               }
       )
       public static void retirarDaRedeTranca(Context ctx) {
            Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
            // FIX ME: get status through POST params
            TrancaStatus status = TrancaStatus.APOSENTADA;
            if (tranca == null)
                throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
            if (tranca.getStatus() == TrancaStatus.OCUPADA)
                throw new NotFoundResponse("Desocupe a tranca antes de retirá-la do sistema.");
            
            TrancaService.definirStatus(tranca.id, status);
            ctx.status(200);
       }

    @OpenApi(
            summary = "Recuperar Trancas Cadastradas",
            operationId = "recuperarTranca",
            path = "/tranca",
            method = HttpMethod.GET,
            tags = {"Tranca"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Tranca[].class)})
            }
    )
    public static void getAll(Context ctx) {
        ctx.json(TrancaService.getAll());
    }

    @OpenApi(
            summary = "Get tranca pelo Id",
            operationId = "getTrancaId",
            path = "/tranca/:idTranca",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "idTranca", type = Integer.class, description = "Tranca Id")},
            tags = {"Tranca"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Tranca.class)}),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void getOne(Context ctx) {
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
        if (tranca == null) {
            throw new NotFoundResponse("Tranca nao encontrada");
        } else {
            ctx.json(tranca);
        }
    }

    @OpenApi(
            summary = "EditarTranca",
            operationId = "updateBicicletaId",
            path = "/tranca/:idTranca",
            method = HttpMethod.PUT,
            pathParams = {@OpenApiParam(name = "idTranca", type = Integer.class, description = "Tranca Id")},
            tags = {"Tranca"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewTrancaRequest.class)}),
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void update(Context ctx) {
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
        if (tranca == null) {
            throw new NotFoundResponse("Tranca nao encontrada");
        } else {
            TrancaService.update(tranca.id, tranca.bicicleta,tranca.numero, tranca.localizacao, tranca.anoDeFabricacao, tranca.modelo, tranca.status);
            ctx.status(204);
        }
    }

    @OpenApi(
            summary = "Deletar tranca pelo Id",
            operationId = "deleteTrancaId",
            path = "/tranca/:idTranca",
            method = HttpMethod.DELETE,
            pathParams = {@OpenApiParam(name = "idTranca", type = Integer.class, description = "Tranca Id")},
            tags = {"Tranca"},
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void delete(Context ctx) {
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
        if (tranca == null) {
            throw new NotFoundResponse("Tranca nao encontrada");
        } else {
            TrancaService.delete(tranca.id);
            ctx.status(204);
        }
    }

    @OpenApi(
            summary = "Alterar Status da Tranca",
            operationId = "alterarStatusTranca",
            path = "/tranca/:idTranca/bicicleta",
            method = HttpMethod.POST,
            pathParams = {@OpenApiParam(name = "trancaId", type = Integer.class, description = "Tranca Id")},
            tags = {"Tranca"},
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void alterarStatusTranca(Context ctx) {
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
        // FIX ME: get status through POST body
        TrancaStatus status = TrancaStatus.LIVRE;
        if (tranca == null) {
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        }else {
            TrancaService.definirStatus(tranca.id, status);
            ctx.status(200);
        }
    }  

    @OpenApi(
            summary = "Obter Bicicleta na Tranca",
            operationId = "obterBicicletaNaTranca",
            path = "/tranca/:trancaId/bicicleta",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "trancaId", type = Integer.class, description = "Tranca Id")},
            tags = {"Tranca"},
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void obterBicicletaNaTranca(Context ctx) {
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
        if (tranca == null) {
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        }else {
        	BicicletaController.getOne(ctx);
            ctx.status(200);
        }
    }
}