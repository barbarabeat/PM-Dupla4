package app;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;

import user.UserController;
import bicicleta.BicicletaController;
import totem.TotemController;
import tranca.TrancaController;

public class App {

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.registerPlugin(getConfiguredOpenApiPlugin());
            config.defaultContentType = "application/json";
        }).routes(() -> {
            path("bicicleta", () -> {
                get(BicicletaController::getAll);
                post(BicicletaController::create);
                path(":idBicicleta", () -> {
                    get(BicicletaController::getOne);
                    put(BicicletaController::update);
                    delete(BicicletaController::delete);
                    path("status", () -> {
                        path(":acao", () -> {
                            post(BicicletaController::alterarStatusBicicleta);                       	
                        });
                    });
                });
                path("integrarNaRede", () -> {
                    post(BicicletaController::integrarNaRedeBicicleta);
                });
                path("retirarDaRede", () -> {
                    post(BicicletaController::retirarDaRedeBicicleta);
                });
            });
            path("totem", () -> {
                get(TotemController::getAll);
                post(TotemController::create);
                path(":idTotem", () -> {
                    get(TotemController::getOne);
                    put(TotemController::update);
                    delete(TotemController::delete);
                    path("trancas", () -> {
                        post(TotemController::adicionarTranca);
                        get(TotemController::listarTrancasDoTotem);
                        path(":idTranca", () -> {
                        	post(TotemController::removerTranca);
                        });
                    });
                });
            });
            path("tranca", () -> {
                get(TrancaController::getAll);
                post(TrancaController::create);
                path(":idTranca", () -> {
                    get(TrancaController::getOne);
                    put(TrancaController::update);
                    delete(TrancaController::delete);
                    path("bicicleta", () -> {
                    	get(TrancaController::obterBicicletaNaTranca);
                    });
                    path("status", () -> {
                        path(":acao", () -> {
                            post(TrancaController::alterarStatusTranca);                       	
                        });
                    });
                });
                path("integrarNaRede", () -> {
                    post(TrancaController::integrarNaRedeTranca);
                });
                path("retirarDaRede", () -> {
                    post(TrancaController::retirarDaRedeTranca);
                });
            });
        }).start(7002);//Integer.valueOf(System.getenv("PORT")));

        System.out.println("Check out ReDoc docs at http://localhost:7002/redoc");
        System.out.println("Check out Swagger UI docs at http://localhost:7002/swagger-ui");
    
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
        }));
        
        app.events(event -> {
            event.serverStopping(() -> { });
            event.serverStopped(() -> { });
        });
    
    }

    private static OpenApiPlugin getConfiguredOpenApiPlugin() {
        Info info = new Info().version("1.0").description("Equipamento API");
        OpenApiOptions options = new OpenApiOptions(info)
                .activateAnnotationScanningFor("io.javalin.example.java")
                .path("/swagger-docs") // endpoint for OpenAPI json
                .swagger(new SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
                .reDoc(new ReDocOptions("/redoc")) // endpoint for redoc
                .defaultDocumentation(doc -> {
                    doc.json("500", ErrorResponse.class);
                    doc.json("503", ErrorResponse.class);
                });
        return new OpenApiPlugin(options);
    }

}