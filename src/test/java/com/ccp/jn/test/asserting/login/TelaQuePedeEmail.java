package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.sync.status.login.ExistsLoginEmail;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class TelaQuePedeEmail extends TemplateDeTestes{
	
	@Test
	public void emailInvalido() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.invalidEmail, ConstantesParaTestesDeLogin.INVALID_EMAIL);
	}
	
	@Test
	public void tokenBloqueado() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.lockedToken);
	}
	
	@Test
	public void tokenFaltando() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.missingEmail);		
	}
	
	@Test
	public void senhaBloqueada() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.lockedPassword);		
	}
	
	@Test
	public void usuarioJaLogado() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.loginConflict);		
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.missingPassword);		
	}
	
	@Test
	public void faltandoPreRegistro() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.missingAnswers);		
	}
	
	@Test
	public void caminhoFeliz() {
		this.verificarExistenciaDeEmail(ExistsLoginEmail.expectedStatus);		
	}
	
	

	private void verificarExistenciaDeEmail(EndpointsLogin expectedStatus) {
		this.verificarExistenciaDeEmail(expectedStatus, ConstantesParaTestesDeLogin.VALID_EMAIL);
	}	
	
	private void verificarExistenciaDeEmail(EndpointsLogin expectedStatus, String email) {
		String uri = "login/"
				+ email
				+ "/token";

		this.testarEndpoint(uri, expectedStatus);
	}

	@Override
	protected String getMethod() {
		return "HEAD";
	}
}
