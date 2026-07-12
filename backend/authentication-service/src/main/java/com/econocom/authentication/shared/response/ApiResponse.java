package com.econocom.authentication.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Indica si la operación se ha realizado correctamente.
     */
    private boolean success;

    /**
     * Código HTTP de la respuesta.
     */
    private int status;

    /**
     * Código de error
     */
    private String code;

    /**
     * Mensaje descriptivo.
     */
    private String message;

    /**
     * Datos devueltos por la operación.
     */
    private T data;

    /**
     * Fecha y hora de la respuesta.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

}
