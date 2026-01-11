package com.ahmad.resourcehub.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorDTO(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String errorCode,
        Instant timestamp,
        String traceId,
        Map<String, Object> details,
        List<FieldErrorDTO> fieldErrors
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type = "about:blank";
        private String title;
        private int status;
        private String detail;
        private String instance;
        private String errorCode;
        private Instant timestamp = Instant.now();
        private String traceId;
        private Map<String, Object> details;
        private List<FieldErrorDTO> fieldErrors;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder status(HttpStatus status) {
            this.status = status.value();
            this.title = status.getReasonPhrase();
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }

        public Builder fieldErrors(List<FieldErrorDTO> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public ApiErrorDTO build() {
            return new ApiErrorDTO(
                    type, title, status, detail, instance,
                    errorCode, timestamp, traceId, details, fieldErrors
            );
        }
    }
}
