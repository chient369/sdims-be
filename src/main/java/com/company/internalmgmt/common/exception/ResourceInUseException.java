package com.company.internalmgmt.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to delete a resource that is still in use
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }

    public ResourceInUseException(String resourceName, String relatedResourceName) {
        super(String.format("%s cannot be deleted because it is still used by %s", resourceName, relatedResourceName));
    }
} 
