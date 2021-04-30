package bicicleta;

import utils.utils;
import app.ErrorResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.*;
import bicicleta.Bicicleta.BicicletaStatus;
import tranca.Tranca;
import tranca.TrancaService;

// This is a controller, it should contain logic related to client/server IO
public class BicicletaController {
    
	@OpenApi(
            summary = "Recuperar Bicicletas Cadastradas",
            operationId = "recuperarBicicleta",
            path = "/bicicleta",
            method = HttpMethod.GET,
            tags = {"Bicicleta"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Bicicleta[].class)})
            }
    )
    public static void getAll(Context ctx) {
        ctx.json(BicicletaService.getAll());
    }
	
    @OpenApi(
            summary = "Cadastrar Bicicleta",
            operationId = "cadastrarBicicleta",
            path = "/bicicleta",
            method = HttpMethod.POST,
            tags = {"Bicicleta"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewBicicletaRequest.class)}),
            responses = {
                    @OpenApiResponse(status = "200"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void create(Context ctx) {
        NewBicicletaRequest bicicleta = ctx.bodyAsClass(NewBicicletaRequest.class);
        BicicletaService.save(bicicleta.numero, bicicleta.marca, bicicleta.modelo, bicicleta.ano);
        ctx.status(200);
    }

    @SuppressWarnings("unused")
	@OpenApi(
            summary = "Colocar uma bicicleta nova ou retornando de reparo de volta na rede de totens",
            operationId = "integrarNaRedeBicicleta",
            path = "/bicicleta/integrarNaRede",
            method = HttpMethod.POST,
            pathParams = {@OpenApiParam(name = "integrarNaRede", type = Integer.class, description = "integrar na rede a Bicicleta")},
            tags = {"Bicicleta"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Bicicleta.class)}),
            responses = {
                    @OpenApiResponse(status = "200"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void integrarNaRedeBicicleta(Context ctx) {
        Bicicleta bicicleta = BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")));
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.formParam("idTranca")));
      
        if (bicicleta == null) {
            throw new NotFoundResponse("Dados Inválidos - Bicicleta nao encontrada");
        } 
        if (tranca == null) {
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        }

        if(bicicleta != BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")))){ //new bike
            NewBicicletaRequest newBicicleta = ctx.bodyAsClass(NewBicicletaRequest.class);
        } else { //old bike that returns to service
            BicicletaService.update(bicicleta.id, BicicletaStatus.DISPONIVEL);
        }
        TrancaService.addBicicleta(tranca.id, bicicleta);
        ctx.status(200);         
    }

	@SuppressWarnings("unused")
	@OpenApi(
            summary = "Retirar bicicleta para reparo ou aposentadoria",
            operationId = "RetirarDaRedeBicicleta",
            path = "/bicicleta/retirarDaRedeBicicleta",
            method = HttpMethod.POST,
            pathParams = {@OpenApiParam(name = "RetirarDaRedeBike", type = Integer.class, description = "retirar da rede a Bicicleta")},
            tags = {"Bicicleta"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Bicicleta.class)}),
            responses = {
                    @OpenApiResponse(status = "200"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void retirarDaRedeBicicleta(Context ctx) {
        Bicicleta bicicleta = BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")));
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
        if (bicicleta == null) {
            throw new NotFoundResponse("Dados Inválidos - Bicicleta nao encontrada");
        } 
        if (tranca == null) {
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        }

        /* Identify purpose for removal and call BicicletaService.update with appropriate status */

        TrancaService.removeBicicleta(tranca.id);
        ctx.status(200);

    }    
    
    @OpenApi(
            summary = "Obter Bicicleta",
            operationId = "getBicicletaId",
            path = "/bicicleta/:idBicicleta",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "bicicletaId", type = Integer.class, description = "Bicicleta Id")},
            tags = {"Bicicleta"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Bicicleta.class)}),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)}),
            }
    )
    public static void getOne(Context ctx) {
        Bicicleta bicicleta = BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")));
        if (bicicleta == null) {
            throw new NotFoundResponse("Bicicleta nao encontrada");
        } else {
            ctx.json(bicicleta);
        }
    }

    @OpenApi(
            summary = "Editar bicicleta",
            operationId = "updateBicicletaId",
            path = "/bicicleta/:idBicicleta",
            method = HttpMethod.PUT,
            pathParams = {@OpenApiParam(name = "bicicletaId", type = Integer.class, description = "Bicicleta Id")},
            tags = {"Bicicleta"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewBicicletaRequest.class)}),
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void updateBicicletaId(Context ctx) {
        Bicicleta bicicleta = BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")));
        if (bicicleta == null) {
            throw new NotFoundResponse("Bicicleta nao encontrada");
        } else {
            NewBicicletaRequest newBicicleta = ctx.bodyAsClass(NewBicicletaRequest.class);
            BicicletaService.update(bicicleta.id, newBicicleta.numero, newBicicleta.marca, newBicicleta.modelo, newBicicleta.ano, newBicicleta.status);
            ctx.status(204);
        }
    }

    @OpenApi(
            summary = "Deletar bicicleta",
            operationId = "deleteBicicletaId",
            path = "/bicicleta/:idBicicleta",
            method = HttpMethod.DELETE,
            pathParams = {@OpenApiParam(name = "bicicletaId", type = Integer.class, description = "Bicicleta Id")},
            tags = {"Tranca"},
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void delete(Context ctx) {
        Bicicleta bicicleta = BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")));
        if (bicicleta == null) {
            throw new NotFoundResponse("Bicicleta nao encontrada");
        } else {
            BicicletaService.delete(bicicleta.id);
            ctx.status(204);
        }
    }

    @OpenApi(
            summary = "Alterar status da bicicleta",
            operationId = "alterarStatusBicicleta",
            path = "/bicicleta/:idBicicleta/status/:acao",
            method = HttpMethod.POST,
            pathParams = {@OpenApiParam(name = "bicicletaId", type = Integer.class, description = "Bicicleta Id")},
            tags = {"Bicicleta"},
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)}),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void alterarStatusBicicleta(Context ctx) {
        Bicicleta bicicleta = BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")));
        // Status newStatus;
        if (bicicleta == null) {
            throw new NotFoundResponse("Bicicleta nao encontrada");
        } else {
            BicicletaService.update(bicicleta.id, bicicleta.getStatus());
            ctx.status(204);
        }
    }
}