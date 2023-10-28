package com.testbni.artha.Util;

public class RegistrationException extends RuntimeException {
    private String message;

    public RegistrationException(String message){
        this.message = message;
    }

    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
