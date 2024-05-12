package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusExistsLoginEmail;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginToken;

public class TelaQuePedeEmail extends TemplateDeTestes{
	
	@Test
	public void emailInvalido() {
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.invalidEmail, ConstantesParaTestesDeLogin.INVALID_EMAIL);
	}
	
	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpEntity mirrorEntity = JnEntityLoginToken.INSTANCE.getMirrorEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.verificarExistenciaDeEmail(variaveisParaTeste, StatusExistsLoginEmail.lockedToken);
	}
	
	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.verificarExistenciaDeEmail(variaveisParaTeste, StatusExistsLoginEmail.missingEmail);		
	}
	
	@Test
	public void senhaBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		CcpEntity mirrorEntity = JnEntityLoginPassword.INSTANCE.getMirrorEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.verificarExistenciaDeEmail(variaveisParaTeste, StatusExistsLoginEmail.lockedPassword);		
	}
	
	@Test
	public void usuarioJaLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginSessionCurrent.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.verificarExistenciaDeEmail(variaveisParaTeste, StatusExistsLoginEmail.loginConflict);		
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.verificarExistenciaDeEmail(variaveisParaTeste, StatusExistsLoginEmail.missingPassword);		
	}
	
	@Test
	public void faltandoPreRegistro() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.verificarExistenciaDeEmail(variaveisParaTeste, StatusExistsLoginEmail.missingAnswers);		
	}
	
	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.verificarExistenciaDeEmail(variaveisParaTeste, StatusExistsLoginEmail.expectedStatus);		
	}
	
	

	private void verificarExistenciaDeEmail(VariaveisParaTeste variaveisParaTeste, StatusEndpointsLogin expectedStatus) {
		this.verificarExistenciaDeEmail(expectedStatus, variaveisParaTeste.VALID_EMAIL);
	}	
	
	private void verificarExistenciaDeEmail(StatusEndpointsLogin expectedStatus, String email) {
		String uri = "login/"
				+ email
				+ "/token";

		this.testarEndpoint(uri, expectedStatus);
	}

	
	protected String getMethod() {
		return "HEAD";
	}
}
