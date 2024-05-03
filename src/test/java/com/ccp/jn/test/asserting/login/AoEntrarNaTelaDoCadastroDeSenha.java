package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.CreateLoginToken;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLockedToken;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityPreRegistration;

public class AoEntrarNaTelaDoCadastroDeSenha extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.criarTokenDeLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, CreateLoginToken.invalidEmail);
	}
	
	@Test
	public void tokenBloqueado() { 
		JnEntityLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.criarTokenDeLogin(CreateLoginToken.lockedToken);
	}
	
	@Test
	public void tokenFaltando() {
		this.criarTokenDeLogin(CreateLoginToken.missingEmail);
	}
	
	@Test
	public void faltandoPreRegistro() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.criarTokenDeLogin(CreateLoginToken.missingAnswers);
	}
	
	@Test
	public void caminhoFeliz() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityPreRegistration.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.criarTokenDeLogin(CreateLoginToken.expectedStatus);
	}
	
	public void criarTokenDeLogin(EndpointsLogin expectedStatus) {
		this.criarTokenDeLogin(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private void criarTokenDeLogin(String email, EndpointsLogin expectedStatus) {
		this.testarEndpoint("login/"
				+ email
				+ "/token/language/portugese", expectedStatus);
	}
	
	@Override
	protected String getMethod() {
		return "POST";
	}

}
