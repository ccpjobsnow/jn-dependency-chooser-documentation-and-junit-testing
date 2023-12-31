package com.ccp.jn.test.asserting.login;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public interface ConstantesParaTestesDeLogin {
	 String STRONG_PASSWORD = "Jobsnow1!";
	 String WRONG_PASSWORD = "qualquerCoisa";
	 String INCORRECT_TOKEN_TO_SAVE_PASSWORD = "qualquerCoisa";
	 String WEAK_PASSWORD = "12345678";
	 String VALID_EMAIL = "devs.jobsnow@gmail.com";
	 CcpJsonRepresentation TESTING_JSON = CcpConstants.EMPTY_JSON.put("email", VALID_EMAIL);
	String INVALID_EMAIL = "devs.jobsnowgmail.com";

}
