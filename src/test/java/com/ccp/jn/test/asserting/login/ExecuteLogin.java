package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityPassword;
import com.jn.commons.utils.JnConstants;

public class ExecuteLogin extends TemplateDeTestes{

	private static final UnlockToken DESBLOQUEIO_DE_TOKEN = new UnlockToken();
	private static final UpdatePassword CADASTRO_DE_SENHA = new UpdatePassword();
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenPendenteDeDesbloqueio = 420;

	private final int faltandoCadastrarSenha = 202;
	private final int usuarioNovoNoSistema = 404;
	private final int faltandoPreRegistro = 201;
	private final int usuarioJaLogado = 409;
	private final int senhaBloqueada = 429;
	private final int senhaIncorreta = 422;
	private final int tokenBloqueado = 403;
	private final int emailInvalido = 400;
	
	@Test
	public void emailInvalido() {
		this.executarLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, this.emailInvalido, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
	}

	@Test
	public void usuarioNovoNoSistema() {
		this.executarLogin(this.usuarioNovoNoSistema, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
	}
	
	@Test
	public void tokenBloqueado() {
		new UpdatePassword().tokenBloqueado();
		this.executarLogin(this.tokenBloqueado, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
	}
	
	@Test
	public void faltandoPreRegistro() {
		new UpdatePassword().faltandoPreRegistro();
		this.executarLogin(this.faltandoPreRegistro, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		new UpdatePassword().caminhoFeliz();
		new JnEntityPassword().delete(new CcpMapDecorator().put("email", ConstantesParaTestesDeLogin.VALID_EMAIL));
		this.executarLogin(this.faltandoCadastrarSenha, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
	}

	
	@Test
	public void senhaBloqueada() {
		new ExecuteLogout().caminhoFeliz();
		for(int k = 0; k < (JnConstants.maxTries); k++) {
			this.executarLogin(this.senhaIncorreta, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
		}
		this.executarLogin(this.senhaBloqueada, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
	}

	@Test
	public void senhaIncorreta() {
		new UpdatePassword().caminhoFeliz();
		this.executarLogin(this.senhaIncorreta, ConstantesParaTestesDeLogin.WRONG_PASSWORD);
	}

	
	@Test
	public void usuarioJaLogado() {
		this.caminhoFeliz();
		this.executarLogin(this.usuarioJaLogado, ConstantesParaTestesDeLogin.STRONG_PASSWORD);
	}
	@Test
	public void tokenPendenteDeDesbloqueio() {
		new RequestUnlockToken().caminhoFeliz();
		this.executarLogin(this.tokenPendenteDeDesbloqueio, ConstantesParaTestesDeLogin.STRONG_PASSWORD);
	}
	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.executarLogin(this.senhaDeDesbloqueioDeTokenEstaBloqueada, ConstantesParaTestesDeLogin.STRONG_PASSWORD);
	}

	@Test
	public void caminhoFeliz() {
		CADASTRO_DE_SENHA.caminhoFeliz();
		this.executarLogin(this.caminhoFeliz, ConstantesParaTestesDeLogin.STRONG_PASSWORD);
	}

	private void executarLogin(int expectedStatus, String senha) {
		this.executarLogin(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus, senha);
	}
	
	private void executarLogin(String email, int expectedStatus, String senha) {
		CcpMapDecorator body = new CcpMapDecorator().put("password", senha);
		String uri = "login/"
		+ email;
		this.testarEndpoint(expectedStatus, body, uri, CcpHttpResponseType.singleRecord);
	}

	@Override
	protected String getMethod() {
		return "POST";
	}
	
}
