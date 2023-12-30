package com.ccp.jn.test.asserting.login;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VerificacaoDeStatusDaTarefaAssincrona;
import com.jn.commons.entities.JnEntityRequestUnlockToken;

public class RequestUnlockToken  extends TemplateDeTestes{
	
	private static final UnlockToken DESBLOQUEIO_DE_TOKEN = new UnlockToken();
	private static final UpdatePassword CADASTRO_DE_SENHA = new UpdatePassword();
	private final int solicitacaoDeDesbloqueioDeTokenJaFeita = 409;
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenNaoBloqueado = 404;///TODO token existente
	private final int emailInvalido = 400;

	@Test
	public void emailInvalido() {
		this.solicitarDesbloqueioDeToken(ConstantesParaTestesDeLogin.INVALID_EMAIL, this.emailInvalido);
	}

	@Test
	public void tokenNaoBloqueado() {
		CADASTRO_DE_SENHA.caminhoFeliz();
		this.solicitarDesbloqueioDeToken(this.tokenNaoBloqueado);
	}

	@Test
	public void solicitacaoDeDesbloqueioDeTokenJaFeita() {
		this.caminhoFeliz();
		this.solicitarDesbloqueioDeToken(this.solicitacaoDeDesbloqueioDeTokenJaFeita);

	}

	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.solicitarDesbloqueioDeToken(this.senhaDeDesbloqueioDeTokenEstaBloqueada);
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void caminhoFeliz() {
		CADASTRO_DE_SENHA.tokenBloqueado();
		CcpMapDecorator solicitarDesbloqueioDeToken = this.solicitarDesbloqueioDeToken(this.caminhoFeliz);
		String asyncTaskId = solicitarDesbloqueioDeToken.getAsString("asyncTaskId");
		assertFalse(asyncTaskId.trim().isEmpty());
		VerificacaoDeStatusDaTarefaAssincrona verificacaoDeStatusDaTarefaAssincrona = new VerificacaoDeStatusDaTarefaAssincrona();
		verificacaoDeStatusDaTarefaAssincrona.getCaminhoFeliz(asyncTaskId, ConstantesParaTestesDeLogin.TESTING_JSON, jsonToTest -> new JnEntityRequestUnlockToken().exists(jsonToTest));
	}
	
	private CcpMapDecorator solicitarDesbloqueioDeToken(int expectedStatus) {
		CcpMapDecorator solicitarDesbloqueioDeToken = this.solicitarDesbloqueioDeToken(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
		return solicitarDesbloqueioDeToken;
	}
	
	private CcpMapDecorator solicitarDesbloqueioDeToken(String email, int expectedStatus) {
		CcpMapDecorator testarEndpoint = this.testarEndpoint("/login/" + email + "/token/language/portuguese/unlocking", expectedStatus);
		return testarEndpoint;
	}
	
	@Override
	protected String getMethod() {
		return "POST";
	}

}
