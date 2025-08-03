package com.example.demo.model.enums.response;
import com.example.demo.model.enums.response.ResponseStatus;
import com.example.demo.exception.StatusCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Response<T> {
    private T data;
    private Pagination pagination;
    private ResponseStatus status;


    public Response(T data, Pagination pagination, ResponseStatus status) {
        this.data = data;
        this.pagination = pagination;
        this.status = status;
    }


    public Response(T data) {
        this(data, null, new ResponseStatus(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMessage()));
    }
}

