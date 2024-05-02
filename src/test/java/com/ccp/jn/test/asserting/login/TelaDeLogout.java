package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.jn.sync.status.login.ExecuteLogout;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class TelaDeLogout extends TemplateDeTestes {

	@Test
	public void emailInvalido() {
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.INVALID_EMAIL, ExecuteLogout.emailInvalido);
	}

	@Test
	public void usuarioNaoLogado() {
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.VALID_EMAIL, ExecuteLogout.usuarioNaoLogado);
	}

	@Test
	public void caminhoFeliz() {
		this.testarEndpoint("/login/" + ConstantesParaTestesDeLogin.VALID_EMAIL, ExecuteLogout.caminhoFeliz);
	}

	@Override
	protected String getMethod() {
		return "DELETE";
	}

}
