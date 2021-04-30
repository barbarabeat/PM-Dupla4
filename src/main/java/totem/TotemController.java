package totem;

import tranca.Tranca;
import tranca.TrancaService;
import utils.utils;
import app.ErrorResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.*;

// This is a controller, it should contain logic related to client/server IO
public class TotemController {

    @OpenApi(
            summary = "Incluir Totem",
            operationId = "createTotem",
            path = "/totem",
            method = HttpMethod.POST,
            tags = {"Totem"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewTotemRequest.class)}),
            responses = {
                    @OpenApiResponse(status = "201"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
            }
    )
    public static void create(Context ctx) {
        NewTotemRequest totem = ctx.bodyAsClass(NewTotemRequest.class);
        TotemService.save(totem.localizacao);
        ctx.status(201);
    }

    @OpenApi(
        summary = "Recuperar totens cadastrados",
        operationId = "getAllTotens",
        path = "/totem",
        method = HttpMethod.GET,
        tags = {"Totem"},
        responses = {
                @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Totem[].class)})
        }
    )
    public static void getAll(Context ctx) {
        ctx.json(TotemService.getAll());
    }

    @OpenApi(
        summary = "Get Totem pelo Id",
        operationId = "getTotemById",
        path = "/totem/:idTotem",
        method = HttpMethod.GET,
        pathParams = {@OpenApiParam(name = "totemId", type = Integer.class, description = "Totem Id")},
        tags = {"Totem"},
        responses = {
                @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Totem.class)}),
                @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)}),
                @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public static void getOne(Context ctx) {
        Totem totem = TotemService.findById(utils.paramToInt(ctx.pathParam("idTotem")));
        if (totem == null)
            throw new NotFoundResponse("Totem nao encontrado");

            ctx.json(totem);
    }

    @OpenApi(
        summary = "Editar Totem pelo id",
        operationId = "updateTotemId",
        path = "/totem/:idTotem",
        method = HttpMethod.PATCH,
        pathParams = {@OpenApiParam(name = "totemId", type = Integer.class, description = "Totem Id")},
        tags = {"Totem"},
        requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewTotemRequest.class)}),
        responses = {
            @OpenApiResponse(status = "204"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public static void update(Context ctx) {
        Totem totem = TotemService.findById(utils.paramToInt(ctx.pathParam("idTotem")));
        if (totem == null)
            throw new NotFoundResponse("Totem not found");

        NewTotemRequest newTotem = ctx.bodyAsClass(NewTotemRequest.class);
        TotemService.update(totem, newTotem.localizacao);
        ctx.status(204);
    }

    // MÉTODO FUNCIONANDO
    @OpenApi(
        summary = "Deletar totem pelo ID",
        operationId = "deleteTotemId",
        path = "/totem/:idTotem",
        method = HttpMethod.DELETE,
        pathParams = {@OpenApiParam(name = "totemId", type = Integer.class, description = "Totem id")},
        tags = {"Totem"},
        responses = {
            @OpenApiResponse(status = "200"),
            @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public static void delete(Context ctx) {
        if (TotemService.delete(utils.paramToInt(ctx.pathParam("idTotem"))) == null)
            throw new NotFoundResponse("Totem nao encontrado");

        ctx.status(204);
    }

    // MÉTODO FUNCIONANDO
    @OpenApi(
        summary = "Listar trancas de um totem",
        operationId = "idTotem",
        path = "/totem/:idTotem/trancas",
        method = HttpMethod.GET,
        pathParams = {@OpenApiParam(name = "totemId", type = Integer.class, description = "Totem id")},
        tags = {"Totem"},
        responses = {
            @OpenApiResponse(status = "200"),
            @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public static void listarTrancasDoTotem(Context ctx) {
        Totem totem = TotemService.findById(utils.paramToInt(ctx.pathParam("idTotem")));
        if (totem == null)
            throw new NotFoundResponse("Totem nao encontrado");
        
        ctx.json(totem.getTrancas());
        ctx.status(200);
    }

    // MÉTODO FUNCIONANDO
    @OpenApi(
        summary = "Adicionar trancas no totem",
        operationId = "idTotem",
        path = "/totem/:idTotem/trancas",
        method = HttpMethod.POST,
        pathParams = {@OpenApiParam(name = "totemId", type = Integer.class, description = "Totem id")},
        tags = {"Totem"},
        responses = {
            @OpenApiResponse(status = "200"),
            @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public static void adicionarTranca(Context ctx) {
        Totem totem = TotemService.findById(utils.paramToInt(ctx.pathParam("idTotem")));
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.formParam("idTranca")));
        if (totem == null)
            throw new NotFoundResponse("Totem nao encontrado");
        
        if (tranca == null)
            throw new NotFoundResponse("Tranca nao encontrada");

        TotemService.addTranca(totem, tranca);

        ctx.json(totem);
        ctx.status(200);
    }

    @OpenApi(
        summary = "Remove tranca de um totem",
        operationId = "idTotem",
        path = "/totem/:idTotem/trancas/:idTranca",
        method = HttpMethod.DELETE,
        pathParams = {@OpenApiParam(name = "totemId", type = Integer.class, description = "Totem id")},
        tags = {"Totem"},
        responses = {
            @OpenApiResponse(status = "200"),
            @OpenApiResponse(status = "422", content = {@OpenApiContent(from = ErrorResponse.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public static void removerTranca(Context ctx) {
        Totem totem = TotemService.findById(utils.paramToInt(ctx.pathParam("idTotem")));
        Tranca tranca = TrancaService.findById(utils.paramToInt(ctx.pathParam("idTranca")));
        if (totem == null)
            throw new NotFoundResponse("Totem não encontrado");
        
        if (tranca == null)
            throw new NotFoundResponse("Tranca não encontrada");

        Tranca result = TotemService.removeTranca(totem, tranca);

        if (result == null)
            throw new NotFoundResponse("Tranca não pertence ao totem especificado ou precisa ser desocupada antes de ser removida.");

        ctx.json(result);
        ctx.status(200);
    }   
}