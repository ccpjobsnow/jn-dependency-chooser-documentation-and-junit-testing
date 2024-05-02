package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.EndpointsLogin;
import com.ccp.jn.sync.status.login.SavePreRegistration;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class TelaDoPreRegistro  extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.INVALID_EMAIL, SavePreRegistration.emailInvalido);
	}

	@Test
	public void tokenBloqueado() {
		this.cadastrarPreRegistration(SavePreRegistration.tokenBloqueado);
	}

	@Test
	public void tokenFaltando() {
		this.cadastrarPreRegistration(SavePreRegistration.tokenFaltando);
	}

	@Test
	public void usuarioJaLogado() {
		this.cadastrarPreRegistration(SavePreRegistration.usuarioJaLogado);
	}

	@Test
	public void faltandoCadastrarSenha() {
		this.cadastrarPreRegistration(SavePreRegistration.faltandoCadastrarSenha);
	}

	@Test
	public void senhaBloqueada() {
		this.cadastrarPreRegistration(SavePreRegistration.senhaBloqueada);
	}

	@Test
	public void caminhoFeliz() {
		this.cadastrarPreRegistration(SavePreRegistration.caminhoFeliz);
	}

	public void cadastrarPreRegistration(EndpointsLogin expectedStatus) {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private void cadastrarPreRegistration(String email, EndpointsLogin expectedStatus) {
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("goal", "jobs").put("channel", "linkedin");
		String uri = "login/"+ email 	+ "/pre-registration";
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	@Override
	protected String getMethod() {
		return "POST";
	}

}
