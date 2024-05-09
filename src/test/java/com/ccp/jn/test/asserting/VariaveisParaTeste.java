package com.ccp.jn.test.asserting;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.utils.JnGenerateRandomToken;

public class VariaveisParaTeste {
	
	public final static String SESSION_TOKEN = CcpConstants.EMPTY_JSON.getTransformed(
			new JnGenerateRandomToken(8, "token")).getAsString("token");
	
	public CcpJsonRepresentation REQUEST_TO_LOGIN = CcpConstants.EMPTY_JSON
			.put("userAgent", "Apache-HttpClient/4.5.4 (Java/17.0.9)")
			.put("sessionToken", SESSION_TOKEN)
			.put("ip", "localhost:8080")
			;
			
	public CcpJsonRepresentation TESTING_JSON = CcpConstants.EMPTY_JSON;
	private static int contador;
	public String VALID_EMAIL;
	

	private static String getEmailFicticio() {
		String zeroEsquerda = contador < 10 ? "0": "";
		String emailFicticio = String.format("teste%s%s@jn.com", zeroEsquerda, contador++);
		return emailFicticio;
	}

	public VariaveisParaTeste() {
		this.VALID_EMAIL = getEmailFicticio();
		this.TESTING_JSON = this.TESTING_JSON.put("email", this.VALID_EMAIL);
		this.REQUEST_TO_LOGIN = this.REQUEST_TO_LOGIN.put("email", this.VALID_EMAIL);
	}
	
			
	

}
