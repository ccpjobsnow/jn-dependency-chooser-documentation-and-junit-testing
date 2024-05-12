package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;

public class AoEntrarNaTelaDoCadastroDeSenha extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.criarTokenDeLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, StatusCreateLoginToken.statusInvalidEmail);
	}
	
	@Test
	public void tokenBloqueado() { 
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpEntity mirrorEntity = JnEntityLoginToken.INSTANCE.getMirrorEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.criarTokenDeLogin(StatusCreateLoginToken.statusLockedToken, variaveisParaTeste);
	}
	
	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.criarTokenDeLogin(StatusCreateLoginToken.statusMissingEmail, variaveisParaTeste);
	}
	
	@Test
	public void faltandoPreRegistro() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.criarTokenDeLogin(StatusCreateLoginToken.missingAnswers, variaveisParaTeste);
	}
	
	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		this.criarTokenDeLogin(StatusCreateLoginToken.statusExpectedStatus, variaveisParaTeste);
	}
	
	private void criarTokenDeLogin(StatusEndpointsLogin expectedStatus, VariaveisParaTeste variaveisParaTeste) {
		this.criarTokenDeLogin(variaveisParaTeste.VALID_EMAIL, expectedStatus);
	}
	
	private void criarTokenDeLogin(String email, StatusEndpointsLogin expectedStatus) {
		String uri = "login/"
				+ email
				+ "/token/language/portuguese";
		this.testarEndpoint(uri, expectedStatus);
	}
	
	
	protected String getMethod() {
		return "POST";
	}

}
