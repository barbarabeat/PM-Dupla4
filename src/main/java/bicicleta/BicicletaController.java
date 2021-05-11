package bicicleta;

import utils.Email;
import utils.utils;
import app.ErrorResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.*;
import bicicleta.Bicicleta.BicicletaStatus;
import tranca.Tranca;
import tranca.TrancaService;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;



// This is a controller, it should contain logic related to client/server IO
public class BicicletaController {
	
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
        BicicletaService.save(bicicleta.numero, bicicleta.marca, bicicleta.modelo, bicicleta.ano, BicicletaStatus.NOVA);
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
        String idFuncionario = ctx.formParam("idFuncionario");
        
        if (bicicleta == null) {
            throw new NotFoundResponse("Dados Inválidos - Bicicleta nao encontrada");
        } 
        if (tranca == null) {
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        }

        if(bicicleta != BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")))){ //new bike
            NewBicicletaRequest newBicicleta = ctx.bodyAsClass(NewBicicletaRequest.class);
            
        } else { // Integracao: Incluir na rede uma bicicleta reparada e enviar email para o reparador
        	
            BicicletaService.update(bicicleta.id, BicicletaStatus.DISPONIVEL);
            
        	HttpResponse<JsonNode> funcionario = Unirest.get("https://grupo3-aluguel.herokuapp.com/funcionario/{idFuncionario}")
        			     .header("accept", "application/json")
        			     .routeParam("idFuncionario", idFuncionario)
        			     .asJson();
        	Email email = (Email) funcionario.getBody().getObject().get("email");
        	Email.sendEmail(email, "Bicicleta integrada na rede com sucesso!");
            
        }
        TrancaService.addBicicleta(tranca, bicicleta);
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
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.formParam("idTranca")));
        BicicletaStatus status = utils.paramToBicicletaStatus(ctx.formParam("acao"));
        BicicletaStatus statusToBe = utils.paramToBicicletaStatus(ctx.formParam("idBicicletaToBe"));
        String idFuncionario = ctx.formParam("idFuncionario");
        
        if (bicicleta == null) {
            throw new NotFoundResponse("Dados Inválidos - Bicicleta nao encontrada");
        } 
        if (tranca == null) {
            throw new NotFoundResponse("Dados Inválidos - Tranca nao encontrada");
        }
        if(status != BicicletaStatus.REPARO_SOLICITADO) {
        	 throw new NotFoundResponse("Bicicleta não está com status 'EM_REPARO'");
        }
     // Integracao: Excluir da rede uma bicicleta reparada e enviar email para o reparador
        
        else {
        	if(statusToBe == BicicletaStatus.APOSENTADA) {
                BicicletaService.setStatus(bicicleta, BicicletaStatus.APOSENTADA);	
        	}else {
                BicicletaService.setStatus(bicicleta, BicicletaStatus.EM_REPARO);
        	}
        	
            BicicletaService.update(bicicleta.id, BicicletaStatus.DISPONIVEL);
            
        	HttpResponse<JsonNode> funcionario = Unirest.get("https://grupo3-aluguel.herokuapp.com/funcionario/{idFuncionario}")
        			     .header("accept", "application/json")
        			     .routeParam("idFuncionario", idFuncionario)
        			     .asJson();
        	Email email = (Email) funcionario.getBody().getObject().get("email");
        	Email.sendEmail(email, "Bicicleta removida da rede com sucesso!");  
            TrancaService.removeBicicleta(tranca);
            ctx.status(200);        	
        } 
    }    
    
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
    public static void update(Context ctx) {
        Bicicleta bicicleta = BicicletaService.findById(utils.paramToInt(ctx.pathParam("idBicicleta")));
        BicicletaStatus status = BicicletaStatus.EM_REPARO;
        if (bicicleta == null) {
            throw new NotFoundResponse("Bicicleta nao encontrada");
        } else {
            NewBicicletaRequest bicicletaRequest = ctx.bodyAsClass(NewBicicletaRequest.class);
            BicicletaService.update(bicicleta, bicicletaRequest.numero, bicicletaRequest.marca, bicicletaRequest.modelo, bicicletaRequest.ano, status);
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

        BicicletaService.setStatus(bicicleta, BicicletaStatus.DISPONIVEL);

        if (bicicleta == null) {
            throw new NotFoundResponse("Bicicleta nao encontrada");
        } else {
            BicicletaService.setStatus(bicicleta, BicicletaStatus.DISPONIVEL);
            ctx.status(204);
        }
    }
}