package com.ccp.jn.test.asserting;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
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
	public String VALID_EMAIL;

	public CcpJsonRepresentation ANSWERS_JSON = CcpConstants.EMPTY_JSON;
	


	public VariaveisParaTeste() {
		String nome = new CcpStringDecorator("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toLowerCase()).text().generateToken(8).content;
		this.VALID_EMAIL = nome + "@teste.com";
		this.TESTING_JSON = this.TESTING_JSON.put("email", this.VALID_EMAIL);
		this.REQUEST_TO_LOGIN = this.REQUEST_TO_LOGIN.put("email", this.VALID_EMAIL);
		this.ANSWERS_JSON = this.TESTING_JSON.put("goal", "jobs").put("channel", "linkedin");
	}
	
			
	

}
