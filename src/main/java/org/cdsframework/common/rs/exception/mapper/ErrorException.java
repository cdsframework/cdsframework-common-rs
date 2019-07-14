/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.common.rs.exception.mapper;

/**
 *
 * @author HLN Consulting, LLC
 */
public class ErrorException extends Exception {
    private ErrorMessage errorMessage;

    public ErrorException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
    
}
