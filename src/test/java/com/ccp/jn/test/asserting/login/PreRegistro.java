package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.test.asserting.TemplateDeTestes;

public class PreRegistro  extends TemplateDeTestes{
	private static final DesbloqueioDeToken DESBLOQUEIO_DE_TOKEN = new DesbloqueioDeToken();
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenPendenteDeDesbloqueio = 420;
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
		new CadastroDeSenha().tokenBloqueado();
		this.cadastrarPreRegistration(this.tokenBloqueado);
	}
	
	@Test
	public void faltandoCadastrarSenha() {
		new ConfirmacaoDeEmail().faltandoCadastrarSenha();
		this.cadastrarPreRegistration(this.faltandoCadastrarSenha);
	}

	@Test
	public void senhaBloqueada() {
		new ExecucaoDeLogin().senhaBloqueada();
		this.cadastrarPreRegistration(this.senhaBloqueada);
	}
	
	@Test
	public void usuarioJaLogado() {
		new ExecucaoDeLogin().usuarioJaLogado();
		this.cadastrarPreRegistration(this.usuarioJaLogado);
	}
	
	@Test
	public void tokenPendenteDeDesbloqueio() {
		new SolicitacaoDeDesbloqueioDeToken().caminhoFeliz();
		this.cadastrarPreRegistration(this.tokenPendenteDeDesbloqueio);
	}
	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.cadastrarPreRegistration(this.senhaDeDesbloqueioDeTokenEstaBloqueada);
	}
	
	@Test
	public void caminhoFeliz() {
		new CadastroDeSenha().faltandoPreRegistro();
		this.cadastrarPreRegistration(this.caminhoFeliz);
	}

	public void cadastrarPreRegistration(int expectedStatus) {
		this.cadastrarPreRegistration(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private void cadastrarPreRegistration(String email, int expectedStatus) {
		CcpMapDecorator body = new CcpMapDecorator().put("goal", "jobs").put("channel", "linkedin");
		String uri = "login/"+ email 	+ "/pre-registration";
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	@Override
	protected String getMethod() {
		return "POST";
	}

}
