package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class SavePreRegistration  extends TemplateDeTestes{
	private final int faltandoCadastrarSenha = 202;
	private final int usuarioNovoNoSistema = 404;
	private final int usuarioJaLogado = 409;
	private final int tokenBloqueado = 403;
	private final int senhaBloqueada = 401;
	private final int emailInvalido = 400;

	
	@Test
	public void emailInvalido() {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.INVALID_EMAIL, this.emailInvalido);
	}

	@Test
	public void usuarioNovoNoSistema() {
		this.cadastrarPreRegistration(this.usuarioNovoNoSistema);
	}
	
	@Test
	public void tokenBloqueado() {
		new UpdatePassword().tokenBloqueado();
		this.cadastrarPreRegistration(this.tokenBloqueado);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		new CreateLoginToken().faltandoCadastrarSenha();
		this.cadastrarPreRegistration(this.faltandoCadastrarSenha);
	}

	@Test
	public void senhaBloqueada() {
		new ExecuteLogin().senhaBloqueada();
		this.cadastrarPreRegistration(this.senhaBloqueada);
	}
	
	@Test
	public void usuarioJaLogado() {
		new ExecuteLogin().usuarioJaLogado();
		this.cadastrarPreRegistration(this.usuarioJaLogado);
	}
	
	
	@Test
	public void caminhoFeliz() {
		new UpdatePassword().faltandoPreRegistro();
		this.cadastrarPreRegistration(this.caminhoFeliz);
	}

	public void cadastrarPreRegistration(int expectedStatus) {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private void cadastrarPreRegistration(String email, int expectedStatus) {
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("goal", "jobs").put("channel", "linkedin");
		String uri = "login/"+ email 	+ "/pre-registration";
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	@Override
	protected String getMethod() {
		return "POST";
	}

}
