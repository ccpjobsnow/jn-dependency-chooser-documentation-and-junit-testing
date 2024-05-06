package com.ccp.jn.test.asserting;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.utils.JnGenerateRandomToken;

public class VariaveisParaTeste {
	
	public final String[] emails = new String[] {
			"jassonguimaraes@gmail.com",
			"jasson@noxxonsat.com.br",
			"similili@bol.com.br",
			"onias@noxxonsat.com.br",
			"onias@ccpjobsnow.com",
			"onias85@gmail.com",
			"devs.jobsnow@gmail.com",
			"ccpjobsnow@gmail.com",
	};

	public VariaveisParaTeste() {
		this.VALID_EMAIL = this.emails[contador++ % this.emails.length];
		this.TESTING_JSON = this.TESTING_JSON.put("email", this.VALID_EMAIL);
		this.REQUEST_TO_LOGIN = this.REQUEST_TO_LOGIN.put("email", this.VALID_EMAIL);
	}
	
			
	public final String SESSION_TOKEN = CcpConstants.EMPTY_JSON.getTransformed(new JnGenerateRandomToken(8, "token")).getAsString("token");
	public CcpJsonRepresentation REQUEST_TO_LOGIN = CcpConstants.EMPTY_JSON
			.put("userAgent", "Apache-HttpClient/4.5.4 (Java/17.0.9)")
			.put("ip", "localhost:8080")
			.put("sessionToken", SESSION_TOKEN)
			;
	private static int contador;
	public CcpJsonRepresentation TESTING_JSON = CcpConstants.EMPTY_JSON;
	public String VALID_EMAIL;
	

}
