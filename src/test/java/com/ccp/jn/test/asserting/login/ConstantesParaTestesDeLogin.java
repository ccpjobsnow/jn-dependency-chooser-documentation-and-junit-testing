package com.ccp.jn.test.asserting.login;

import com.ccp.decorators.CcpMapDecorator;

public interface ConstantesParaTestesDeLogin {
	 String STRONG_PASSWORD = "Jobsnow1!";
	 String WRONG_PASSWORD = "qualquerCoisa";
	 String INCORRECT_TOKEN_TO_SAVE_PASSWORD = "qualquerCoisa";
	 String WEAK_PASSWORD = "12345678";
	 String VALID_EMAIL = "devs.jobsnow@gmail.com";
	 CcpMapDecorator TESTING_JSON = new CcpMapDecorator().put("email", VALID_EMAIL);
	String INVALID_EMAIL = "devs.jobsnowgmail.com";

}
