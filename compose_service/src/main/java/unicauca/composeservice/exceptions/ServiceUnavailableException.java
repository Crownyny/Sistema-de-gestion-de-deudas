package unicauca.composeservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.URI;

@Getter
public class ServiceUnavailableException extends WebClientRequestException {
    private final String serviceName;

    public ServiceUnavailableException(String serviceName, Throwable cause, HttpMethod method, URI uri) {
        super(cause, method, uri, HttpHeaders.EMPTY);
        this.serviceName = serviceName;
    }

}