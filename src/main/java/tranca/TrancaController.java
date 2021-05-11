package tranca;

import utils.Email;
import utils.utils;

import java.sql.Array;

import com.google.gson.JsonObject;

import app.ErrorResponse;
import tranca.Tranca.TrancaStatus;
import bicicleta.Bicicleta;
import bicicleta.Bicicleta.BicicletaStatus;
import bicicleta.BicicletaController;
import bicicleta.BicicletaService;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.*;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;


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
        TrancaService.save(tranca.numero, null, tranca.anoDeFabricacao, tranca.modelo, TrancaStatus.NOVA);
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
        requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Tranca.class)}),
        responses = {
                @OpenApiResponse(status = "200"),
                @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public static void integrarNaRedeTranca(Context ctx) {
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.formParam("idTranca")));
        TrancaStatus status = utils.paramToTrancaStatus(ctx.formParam("acao"));
        String idFuncionario = ctx.formParam("idFuncionario");
        
        if(status != TrancaStatus.NOVA || status !=  TrancaStatus.EM_REPARO) {
       	 throw new NotFoundResponse("Tranca não está com status 'NOVA' ou 'EM_REPARO'");
       } else {
    	   
        //Integracao: Incluir na rede uma Tranca e enviar email para o reparador
        if (tranca == null) { // Nova tranca sendo adicionada
            NewTrancaRequest newTranca = ctx.bodyAsClass(NewTrancaRequest.class);
            TrancaService.save(newTranca.numero, null, newTranca.anoDeFabricacao, newTranca.modelo, TrancaStatus.NOVA);
        } else { // Tranca antiga que retorna ao sistema
            TrancaService.reintegrarAoSistema(tranca);
        }
        
    	HttpResponse<JsonNode> funcionario = Unirest.get("https://grupo3-aluguel.herokuapp.com/funcionario/{idFuncionario}")
			     .header("accept", "application/json")
			     .routeParam("idFuncionario", idFuncionario)
			     .asJson();
    	Email email = (Email) funcionario.getBody().getObject().get("email");
    	Email.sendEmail(email, "Tranca integrada na rede com sucesso!");
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
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.formParam("idTranca")));
        TrancaStatus status = utils.paramToTrancaStatus(ctx.formParam("acao"));
        TrancaStatus statusToBe = utils.paramToTrancaStatus(ctx.formParam("idTrancaToBe"));
        String idFuncionario = ctx.formParam("idFuncionario");
        
        if(status != TrancaStatus.APOSENTADA || status !=  TrancaStatus.EM_REPARO) {
          	 throw new NotFoundResponse("Tranca não está com status 'Aposentada' ou 'EM_REPARO'");
          }

        if (tranca == null)
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        
        if (tranca.getStatus() == TrancaStatus.OCUPADA)
            throw new NotFoundResponse("Desocupe a tranca antes de retirá-la do sistema.");
        
        if (tranca.getLocalizacao() == null)
            throw new NotFoundResponse("Coloque a tranca no totem antes de retirá-la do sistema.");

        else {
        	
            //Integracao: Remover da rede uma Tranca e enviar email para o reparador
        	if(statusToBe == TrancaStatus.APOSENTADA) {
        		TrancaService.setStatus(tranca, TrancaStatus.APOSENTADA);	
        	} else {
        		TrancaService.setStatus(tranca, TrancaStatus.EM_REPARO);
        	}
            
        	HttpResponse<JsonNode> funcionario = Unirest.get("https://grupo3-aluguel.herokuapp.com/funcionario/{idFuncionario}")
        			     .header("accept", "application/json")
        			     .routeParam("idFuncionario", idFuncionario)
        			     .asJson();
        	Email email = (Email) funcionario.getBody().getObject().get("email");
        	Email.sendEmail(email, "Tranca removida da rede com sucesso!");  
            ctx.status(200);       	
        }
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

        TrancaService.setStatus(tranca, TrancaStatus.OCUPADA);

        if (tranca == null)
            throw new NotFoundResponse("Tranca nao encontrada");
        
        NewTrancaRequest trancaRequest = ctx.bodyAsClass(NewTrancaRequest.class);
        TrancaService.update(tranca, trancaRequest.numero, trancaRequest.localizacao, trancaRequest.anoDeFabricacao, trancaRequest.modelo, tranca.getStatus());
        ctx.status(204);
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

        TrancaService.setStatus(tranca, TrancaStatus.LIVRE);

        if (tranca == null) 
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        if (BicicletaStatus.DISPONIVEL == null) {
            TrancaService.setStatus(tranca, TrancaStatus.OCUPADA);        	
        }  
        ctx.status(200);
    }  

    @OpenApi(
            summary = "Consultar Bicicleta na Tranca",
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
        
        if (tranca == null)
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        
        BicicletaController.getOne(ctx);
        ctx.status(200);

    }  

}