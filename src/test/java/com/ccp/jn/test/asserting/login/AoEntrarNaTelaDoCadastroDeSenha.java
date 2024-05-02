package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.CreateLoginToken;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class AoEntrarNaTelaDoCadastroDeSenha extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.criarTokenDeLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, CreateLoginToken.emailInvalido);
	}
	
	@Test
	public void tokenBloqueado() {
		this.criarTokenDeLogin(CreateLoginToken.tokenBloqueado);
	}
	
	@Test
	public void tokenFaltando() {
		this.criarTokenDeLogin(CreateLoginToken.tokenFaltando);
	}
	
	@Test
	public void faltandoPreRegistro() {
		this.criarTokenDeLogin(CreateLoginToken.faltandoPreRegistration);
	}
	
	@Test
	public void caminhoFeliz() {
		this.criarTokenDeLogin(CreateLoginToken.caminhoFeliz);
	}
	
	private void criarTokenDeLogin(EndpointsLogin expectedStatus) {
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
