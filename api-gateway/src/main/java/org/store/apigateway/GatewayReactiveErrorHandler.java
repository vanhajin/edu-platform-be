package org.store.apigateway;

import com.eduplatform.common.dtos.web.ReponseUtil;
import com.eduplatform.common.utils.ExceptionMessageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;

@Component
@Order(-2)
public class GatewayReactiveErrorHandler implements WebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
        if (exchange.getResponse().isCommitted()) return Mono.error(exception);
        HttpStatus status = exception instanceof ResponseStatusException responseStatusException
                ? HttpStatus.valueOf(responseStatusException.getStatusCode().value()) : HttpStatus.INTERNAL_SERVER_ERROR;
        String code = status.is4xxClientError() ? "GATEWAY_REQUEST_ERROR" : "GATEWAY_ERROR";
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(ReponseUtil.error(code, status.getReasonPhrase(), ExceptionMessageUtil.getDetail("api-gateway", exception)));
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception serializationException) {
            return exchange.getResponse().setComplete();
        }
    }
}
