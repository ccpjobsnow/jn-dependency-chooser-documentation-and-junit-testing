package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginAnswers;

public class AoEntrarNaTelaDoCadastroDeSenha extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.criarTokenDeLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, StatusCreateLoginToken.statusInvalidEmail);
	}
	
	@Test
	public void tokenBloqueado() { 
		JnEntityLoginLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.criarTokenDeLogin(StatusCreateLoginToken.statusLockedToken);
	}
	
	@Test
	public void tokenFaltando() {
		this.criarTokenDeLogin(StatusCreateLoginToken.statusMissingEmail);
	}
	
	@Test
	public void faltandoPreRegistro() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.criarTokenDeLogin(StatusCreateLoginToken.missingAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.criarTokenDeLogin(StatusCreateLoginToken.statusExpectedStatus);
	}
	
	public void criarTokenDeLogin(StatusEndpointsLogin expectedStatus) {
		this.criarTokenDeLogin(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private void criarTokenDeLogin(String email, StatusEndpointsLogin expectedStatus) {
		this.testarEndpoint("login/"
				+ email
				+ "/token/language/portugese", expectedStatus);
	}
	
	
	protected String getMethod() {
		return "POST";
	}

}
