package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusExistsLoginEmail;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginLockedPassword;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginAnswers;

public class TelaQuePedeEmail extends TemplateDeTestes{
	
	@Test
	public void emailInvalido() {
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.invalidEmail, ConstantesParaTestesDeLogin.INVALID_EMAIL);
	}
	
	@Test
	public void tokenBloqueado() {
		JnEntityLoginLockedToken.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.lockedToken);
	}
	
	@Test
	public void tokenFaltando() {
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.missingEmail);		
	}
	
	@Test
	public void senhaBloqueada() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginLockedPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.lockedPassword);		
	}
	
	@Test
	public void usuarioJaLogado() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLogin.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.loginConflict);		
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.missingPassword);		
	}
	
	@Test
	public void faltandoPreRegistro() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.missingAnswers);		
	}
	
	@Test
	public void caminhoFeliz() {
		JnEntityLoginEmail.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		JnEntityLoginPassword.INSTANCE.createOrUpdate(ConstantesParaTestesDeLogin.TESTING_JSON);
		this.verificarExistenciaDeEmail(StatusExistsLoginEmail.expectedStatus);		
	}
	
	

	private void verificarExistenciaDeEmail(StatusEndpointsLogin expectedStatus) {
		this.verificarExistenciaDeEmail(expectedStatus, ConstantesParaTestesDeLogin.VALID_EMAIL);
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
