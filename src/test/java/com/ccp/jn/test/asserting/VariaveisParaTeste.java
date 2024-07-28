package com.ccp.jn.test.asserting;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

public class VariaveisParaTeste {
	public final static String CORRECT_PASSWORD = "Jobsnow1!";
	public final static String WRONG_PASSWORD = "Novasenha1!";
	public final static String INCORRECT_TOKEN_TO_SAVE_PASSWORD = "qualquerCoisa";
	public final static String INVALID_EMAIL = "devs.jobsnowgmail.com";

	public final static String SESSION_TOKEN = CcpConstants.EMPTY_JSON.putRandomToken(8, "token").getAsString("token");
	
	public CcpJsonRepresentation REQUEST_TO_LOGIN = CcpConstants.EMPTY_JSON
			.put("userAgent", "Apache-HttpClient/4.5.4 (Java/17.0.9)")
			.put("sessionToken", SESSION_TOKEN)
			.put("ip", "localhost:8080")
			;
			
	public final String VALID_EMAIL;

	public final CcpJsonRepresentation ANSWERS_JSON;
	


	public VariaveisParaTeste() {
		this(new CcpStringDecorator("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toLowerCase()).text().generateToken(8).content + "@teste.com");
	}
	public VariaveisParaTeste(String email) {
	
		this.REQUEST_TO_LOGIN = this.REQUEST_TO_LOGIN.put("email", email).putEmailHash("SHA1");
		this.VALID_EMAIL = email;
		this.ANSWERS_JSON = this.REQUEST_TO_LOGIN.put("goal", "jobs").put("channel", "linkedin");
	}	
}
