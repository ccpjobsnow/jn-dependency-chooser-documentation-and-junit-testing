package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.test.asserting.TemplateDeTestes;

public class ExecuteLogout  extends TemplateDeTestes{

	private static final ExecuteLogin EXECUCAO_DE_LOGIN = new ExecuteLogin();
	private final int usuarioNaoLogado = 404;
	private final int emailInvalido = 400;
	
	@Test
	public void emailInvalido() {
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.INVALID_EMAIL, this.emailInvalido);
	}

	
	@Test
	public void usuarioNaoLogado() {
		this.caminhoFeliz();
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.VALID_EMAIL, this.usuarioNaoLogado);
		//TODO registrar logout sem login, fazer teste tambem???
	}
	
	@Test
	public void caminhoFeliz() {
		EXECUCAO_DE_LOGIN.caminhoFeliz();
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.VALID_EMAIL, this.caminhoFeliz);
	}

	@Override
	protected String getMethod() {
		return "DELETE";
	}

}
