package com.ccp.jn.test.asserting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityPassword;
import com.jn.commons.entities.JnEntityWeakPassword;
import com.jn.commons.utils.JnTopic;

public class LoginTesting {

	private static final String STRONG_PASSWORD = "Jobsnow1!";
	private static final String INCORRECT_TOKEN_TO_SAVE_PASSWORD = "qualquerCoisa";
	private static final String WEAK_PASSWORD = "12345678";
	private static final String TESTING_EMAIL = "devs.jobsnow@gmail.com";
	private static final CcpMapDecorator TESTING_JSON = new CcpMapDecorator().put("email", TESTING_EMAIL);
	private static final String BASE_URL = "http://localhost:8080/";
	private final CcpHttpRequester ccpHttp;
	
	public LoginTesting() {
		CcpDependencyInjection.loadAllDependencies(
				new CcpGsonJsonHandler()
				,new CcpElasticSearchDbRequest()
				,new CcpApacheMimeHttp()
				);
		this.ccpHttp = CcpDependencyInjection.getDependency(CcpHttpRequester.class);
	}

	@Test
	public void testing() {
//		DropAndRecreateDatabase.main(null);
		this.ourUserIsAccessingJobsNowByTheFirstTime();
		String asyncTaskId = this.soOurUserRequestsToJobsNowToSendsLoginToken();
		this.soOurUserWaitsJobsNowToSendHisLoginTokenByEmail(asyncTaskId);
		this.createPreRegistration();
		this.soOurUserEntersTokenIncorrectly();
		String tokenToSavePassword = this.soOurUserOpenHisMailMessageAndGetsHisTokenThatJobsNowHasSentToHim();
		this.soOurUserTypesWeakPassword(tokenToSavePassword);
		String tokenToValidateLogin = this.soOurUserConfirmesWeakPassword(tokenToSavePassword);
		this.validateCorrectLoginToken(tokenToValidateLogin);
		tokenToValidateLogin = this.soOurUserSavesStrongPassword(tokenToSavePassword);
		this.validateCorrectLoginToken(tokenToValidateLogin);
		this.soOurUserConfirmesWeakPassword(tokenToSavePassword);
		this.soOurUserSavesStrongPassword(tokenToSavePassword);
		tokenToValidateLogin = this.soOurUserSavesPassword(tokenToSavePassword);
		this.validateCorrectLoginToken(tokenToValidateLogin);
		this.soOurUserExecuteLoginConflict();
		this.soOurUserExecuteLogout();
		this.soOurUserExecuteLogoutWithoutLogin();
		this.soOurUserExecuteCorrectLoginWithStrongPassword();
		this.soOurUserConfirmesWeakPassword(tokenToSavePassword);
		this.soOurUserExecuteLoginConflict();
		this.soOurUserExecuteLogout();
		this.soOurUserExecuteCorrectLoginWithWeakPassword();
	}

	private void soOurUserExecuteLogout() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL,
				"DELETE", 
				CcpConstants.EMPTY_JSON, 
				new CcpMapDecorator().put("password", STRONG_PASSWORD).asUgglyJson(), 200);
		
	}

	private void soOurUserExecuteLogoutWithoutLogin() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL,
				"DELETE", 
				CcpConstants.EMPTY_JSON, 
				new CcpMapDecorator().put("password", STRONG_PASSWORD).asUgglyJson(), 404);
		
	
	}

	
	private void soOurUserExecuteLoginConflict() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL,
				"POST", 
				CcpConstants.EMPTY_JSON, 
				new CcpMapDecorator().put("password", STRONG_PASSWORD).asUgglyJson(), 409);
		
	}


	private void soOurUserExecuteCorrectLoginWithStrongPassword() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL,
				"POST", 
				CcpConstants.EMPTY_JSON, 
				new CcpMapDecorator().put("password", STRONG_PASSWORD).asUgglyJson(), 200);
	}

	private void soOurUserExecuteCorrectLoginWithWeakPassword() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL,
				"POST", 
				CcpConstants.EMPTY_JSON, 
				new CcpMapDecorator().put("password", WEAK_PASSWORD).asUgglyJson(), 200);
		
	}

	private String soOurUserSavesPassword(String tokenToValidateLogin) {
		CcpHttpResponse endpointResponse = this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL
				+ "/password", 
				"POST", 
				CcpConstants.EMPTY_JSON, 
				new CcpMapDecorator().put("password", STRONG_PASSWORD).put("token", tokenToValidateLogin).asUgglyJson()
				,200);
		
		
		boolean weakPasswordHasBeenCreated = new JnEntityWeakPassword().exists(TESTING_JSON);

		assertFalse(weakPasswordHasBeenCreated);
		
		boolean passwordHasBeenCreated = new JnEntityPassword().exists(TESTING_JSON);
		
		assertTrue(passwordHasBeenCreated);

		CcpMapDecorator asSingleJson = endpointResponse.asSingleJson();
		
		String tokenToSavePassword = asSingleJson.getAsString("token");
		
		boolean sameTokens = tokenToSavePassword.equals(tokenToValidateLogin);
		
		assertFalse(sameTokens);
		
		return tokenToSavePassword;
	}
	
	private String soOurUserSavesStrongPassword(String token) {
		CcpHttpResponse endpointResponse = this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL
				+ "/password/weak", "POST", 
				new CcpMapDecorator(), 
				new CcpMapDecorator().put("password", STRONG_PASSWORD).put("token", token).asUgglyJson(), 200);
		
		
		boolean weakPasswordHasBeenCreated = new JnEntityWeakPassword().exists(TESTING_JSON);

		assertFalse(weakPasswordHasBeenCreated);
		
		boolean passwordHasBeenCreated = new JnEntityPassword().exists(TESTING_JSON);
		
		assertTrue(passwordHasBeenCreated);

		CcpMapDecorator asSingleJson = endpointResponse.asSingleJson();
		
		String loginToken = asSingleJson.getAsString("token");
		
		return loginToken;
	}


	private void soOurUserEntersTokenIncorrectly() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL
				+ "/password", "POST", 
				new CcpMapDecorator(), 
				new CcpMapDecorator().put("password", WEAK_PASSWORD).put("token", INCORRECT_TOKEN_TO_SAVE_PASSWORD).asUgglyJson()
				,401);
		
	}


	private void createPreRegistration() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL
				+ "/pre-registration", "POST", 
				new CcpMapDecorator(), 
				new CcpMapDecorator().put("goal", "jobs").put("channel", "linkedin").asUgglyJson()
				,200);
		
	}


	private void validateCorrectLoginToken(String loginTokenExpected) {
		CcpMapDecorator jsonLoginToken =new JnEntityLoginToken().getOneById(TESTING_JSON);
		String loginToken = jsonLoginToken.getAsString("token");
		assertFalse(loginToken.equals(loginTokenExpected));
		
		CcpMapDecorator login = new JnEntityLogin().getOneById(TESTING_JSON);
		String tokenFromLogin = login.getAsString("token");
		assertTrue(loginTokenExpected.equals(tokenFromLogin));
		
		
	}


	private void soOurUserTypesWeakPassword(String token) {
		
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL
				+ "/password", "POST", 
				new CcpMapDecorator(), 
				new CcpMapDecorator().put("password", WEAK_PASSWORD).put("token", token).asUgglyJson()
				,422);
		
		
		boolean weakPasswordHasBeenCreated = new JnEntityWeakPassword().exists(TESTING_JSON);

		assertFalse(weakPasswordHasBeenCreated);
		
		boolean loginHasBeenCreated = new JnEntityLogin().exists(TESTING_JSON);
		
		assertFalse(loginHasBeenCreated);
	}

	private String soOurUserConfirmesWeakPassword(String token) {
		
		CcpHttpResponse endpointResponse = this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL
				+ "/password/weak", "POST", 
				new CcpMapDecorator(), 
				new CcpMapDecorator().put("password", WEAK_PASSWORD).put("token", token).asUgglyJson()
				,200);
		
		
		boolean weakPasswordHasBeenCreated = new JnEntityWeakPassword().exists(TESTING_JSON
				);

		assertTrue(weakPasswordHasBeenCreated);
		
		boolean passwordHasBeenCreated = new JnEntityPassword().exists(TESTING_JSON);
		
		assertTrue(passwordHasBeenCreated);

		CcpMapDecorator asSingleJson = endpointResponse.asSingleJson();
		
		String loginToken = asSingleJson.getAsString("token");
		
		boolean loginTokenHasBeenGenerated = loginToken.equals(token);
		
		assertFalse(loginTokenHasBeenGenerated);
		
		return loginToken;
	}


	private String soOurUserOpenHisMailMessageAndGetsHisTokenThatJobsNowHasSentToHim() {
	
		CcpMapDecorator data = new JnEntityLoginToken().getOneById(TESTING_JSON);
		String token = data.getAsString("token");
		return token;
		
	}


	private void soOurUserWaitsJobsNowToSendHisLoginTokenByEmail(String asyncTaskId) {
		
		for(int k = 0; k < 10; k++) {
			CcpHttpResponse endpointResponse = this.ccpHttp.executeHttpRequest(BASE_URL
					+ "async/task/" + asyncTaskId, "GET", 
					new CcpMapDecorator(), new CcpMapDecorator().asUgglyJson(),200);

			
			CcpMapDecorator json = endpointResponse.asSingleJson();
			new CcpTimeDecorator().sleep(1000);
			
			boolean stillIsRunning = json.containsKey("finished") == false;
			
			if(stillIsRunning) {
				continue;
			}
			
			boolean success = json.getAsBoolean("success");
			assertTrue(success);
			
			boolean mailMessageHasBeenSent = new JnEntityEmailMessageSent().exists(TESTING_JSON.put("subjectType" ,JnTopic.sendUserToken.name()));
			
			assertTrue(mailMessageHasBeenSent);
			System.out.println("Tentativa " + k);
			return;
		}
		assertTrue(false);
	}


	private String soOurUserRequestsToJobsNowToSendsLoginToken() {
		CcpHttpResponse endpointResponse = this.ccpHttp.executeHttpRequest(BASE_URL
				+ "/login/"
				+ TESTING_EMAIL
				+ "/token/language/portuguese", "POST", new CcpMapDecorator(), 
				new CcpMapDecorator().asUgglyJson(), 200);
		
		String asyncTaskId = new CcpMapDecorator(endpointResponse.httpResponse).getAsString("asyncTaskId");

		assertTrue(asyncTaskId.trim().isEmpty() == false);
		return asyncTaskId;
	}


	private void ourUserIsAccessingJobsNowByTheFirstTime() {
		this.ccpHttp.executeHttpRequest(BASE_URL
				+ "login/"
				+ TESTING_EMAIL
				+ "/token", "HEAD", new CcpMapDecorator(), new CcpMapDecorator().asUgglyJson(), 404);
	
	}
}

