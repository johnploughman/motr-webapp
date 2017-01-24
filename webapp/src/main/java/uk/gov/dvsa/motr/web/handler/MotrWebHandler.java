package uk.gov.dvsa.motr.web.handler;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import uk.gov.dvsa.motr.web.config.Config;
import uk.gov.dvsa.motr.web.system.MotrWebApplication;

import static uk.gov.dvsa.motr.web.accesslog.AccessLogger.logAccess;
import static uk.gov.dvsa.motr.web.system.LogConfigurator.configureLogging;
import static uk.gov.dvsa.motr.web.system.LogConfigurator.setRequestContext;


/**
 * Entry point for Lambda
 */
public class MotrWebHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    private final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    /**
     * Executes once per container instance
     */
    public MotrWebHandler() {

        MotrWebApplication application = new MotrWebApplication();
        handler = JerseyLambdaContainerHandler.getAwsProxyHandler(application);
        Config config = handler.getApplicationHandler().getServiceLocator().getService(Config.class);
        configureLogging(config);
    }

    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {

        setRequestContext(context);
        return logAccess(request, context, handler::proxy);
    }
}