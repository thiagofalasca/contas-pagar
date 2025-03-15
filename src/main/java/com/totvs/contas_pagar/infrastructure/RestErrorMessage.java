package com.totvs.contas_pagar.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
public class RestErrorMessage {
    private String message;
    private HttpStatusCode statusCode;
    private String details;
}
