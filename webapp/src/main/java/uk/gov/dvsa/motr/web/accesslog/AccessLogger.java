package uk.gov.dvsa.motr.web.accesslog;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Logs request/response information about handling proxy event
 */
public class AccessLogger {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogger.class.getSimpleName());

    public static AwsProxyResponse logAccess(
            AwsProxyRequest request,
            Context ctx,
            BiFunction<AwsProxyRequest, Context, AwsProxyResponse> handler
    ) {

        Map<String, Object> fields = new HashMap<>();

        fields.put("request.method", request.getHttpMethod());
        fields.put("request.path", request.getPath());
        fields.put("request.queryString", request.getQueryString());

        try {

            AwsProxyResponse response = handler.apply(request, ctx);
            fields.put("response.statusCode", String.valueOf(response.getStatusCode()));
            fields.put("response.body.length", response.getBody().length());

            fields.forEach((key, val) -> MDC.put(key, val.toString()));

            logger.info("Access event");

            return response;
        } catch (Exception e) {

            logger.error("Access error", e);
            throw e;
        } finally {

            fields.forEach((key, val) -> MDC.remove(key));
        }
    }
}
