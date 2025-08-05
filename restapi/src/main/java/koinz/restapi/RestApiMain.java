/*
 * This file is part of KOINZ.
 *
 * KOINZ is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * KOINZ is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with KOINZ. If not, see <http://www.gnu.org/licenses/>.
 */

package koinz.restapi;

import koinz.common.config.Config;

import java.net.URI;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;



import koinz.restapi.endpoints.AccountAgeApi;
import koinz.restapi.endpoints.BondedReputationApi;
import koinz.restapi.endpoints.BondedRoleVerificationApi;
import koinz.restapi.endpoints.ExplorerBlocksApi;
import koinz.restapi.endpoints.ExplorerDaoApi;
import koinz.restapi.endpoints.ExplorerMarketsApi;
import koinz.restapi.endpoints.ExplorerTransactionsApi;
import koinz.restapi.endpoints.ProofOfBurnApi;
import koinz.restapi.endpoints.SignedWitnessApi;
import koinz.restapi.error.CustomExceptionMapper;
import koinz.restapi.error.StatusException;
import koinz.restapi.swagger.StaticFileHandler;
import koinz.restapi.swagger.SwaggerResolution;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Application to start and config the rest service.
 * This creates a rest service for clients to connect and for users to browse the documentation.
 * <p>
 * Swagger doc are available at <a href="http://localhost:8082/doc/v1/index.html">REST API documentation</a>
 */
@Slf4j
public class RestApiMain extends ResourceConfig {
    @Getter
    private static String baseUrl;

    public static void main(String[] args) throws Exception {
        RestApiMain daoNodeApplication = new RestApiMain();
        daoNodeApplication.start(args, config -> {
            daoNodeApplication
                    .register(CustomExceptionMapper.class)
                    .register(StatusException.StatusExceptionMapper.class)
                    .register(ProofOfBurnApi.class)
                    .register(BondedReputationApi.class)
                    .register(BondedRoleVerificationApi.class)
                    .register(AccountAgeApi.class)
                    .register(SignedWitnessApi.class)
                    .register(ExplorerMarketsApi.class)
                    .register(ExplorerDaoApi.class)
                    .register(ExplorerBlocksApi.class)
                    .register(ExplorerTransactionsApi.class)
                    .register(SwaggerResolution.class);
            daoNodeApplication.startServer(config.daoNodeApiUrl, config.daoNodeApiPort);
        });
    }


    @Getter
    private final RestApi restApi;
    private HttpServer httpServer;

    public RestApiMain() {
        restApi = new RestApi();
    }

    private void start(String[] args, Consumer<Config> configConsumer) {
        new Thread(() -> {
            restApi.execute(args);
            configConsumer.accept(restApi.getConfig());
            try {
                // Keep running
                Thread.currentThread().setName("daoNodeThread");
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                log.error("daoNodeThread interrupted", e);
                shutDown();
            }
        }).start();
    }

    private void startServer(String url, int port) {
        baseUrl = url + ":" + port + "/api/v1";
        try {
            httpServer = JdkHttpServerFactory.createHttpServer(URI.create(baseUrl), this);
            httpServer.createContext("/doc", new StaticFileHandler("/doc/v1/"));

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));

            log.info("Server started at {}.", baseUrl);
        } catch (Exception e1) {
            log.error("Exception at startServer:", e1);
        }
        // block and wait shut down signal, like CTRL+C
        try {
            Thread.currentThread().setName("serverThread");
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.error("serverThread interrupted", e);
            System.exit(1);
        }

        shutDown();
    }

    private void shutDown() {
        stopServer();
        restApi.gracefulShutDown();
    }

    private void stopServer() {
        if (httpServer != null) {
            httpServer.stop(1);
        }
    }
}
