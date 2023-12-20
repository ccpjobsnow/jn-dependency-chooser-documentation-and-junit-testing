package com.ccp.jn.test.asserting.login;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VerificacaoDeStatusDaTarefaAssincrona;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.utils.JnTopic;

@SuppressWarnings("unchecked")
public class ConfirmacaoDeEmail  extends TemplateDeTestes{
	private static final DesbloqueioDeToken DESBLOQUEIO_DE_TOKEN = new DesbloqueioDeToken();
	private static final CadastroDeSenha CADASTRO_DE_SENHA = new CadastroDeSenha();
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenPendenteDeDesbloqueio = 420;
	private final int faltandoCadastrarSenha = 202;
	private final int faltandoPreRegistro = 201;
	private final int usuarioJaLogado = 409;
	private final int tokenBloqueado = 403;
	private final int senhaBloqueada = 401;
	private final int emailInvalido = 400;
	
	@Test
	public void emailInvalido() {
		this.confirmarEmail(ConstantesParaTestesDeLogin.INVALID_EMAIL, this.emailInvalido);
	}
	
	@Test
	public void tokenBloqueado() {
		CADASTRO_DE_SENHA.tokenBloqueado();
		this.confirmarEmail(this.tokenBloqueado);
	}

	@Test
	public void faltandoPreRegistro() {
		CADASTRO_DE_SENHA.faltandoPreRegistro();
		this.confirmarEmail(this.faltandoPreRegistro);

	}

	@Test
	public void faltandoCadastrarSenha() {
	
		CcpMapDecorator json = this.confirmarEmail(this.faltandoCadastrarSenha);
		
		this.verificarEnvioDoToken(json);
	}
	
	@Test
	public void senhaBloqueada() {
		new ExecucaoDeLogin().senhaBloqueada();
		this.confirmarEmail(this.senhaBloqueada);
	}

	@Test
	public void usuarioJaLogado() {
		new ExecucaoDeLogin().caminhoFeliz();
		this.confirmarEmail(this.usuarioJaLogado);
	}

	@Test
	public void tokenPendenteDeDesbloqueio() {
		new SolicitacaoDeDesbloqueioDeToken().caminhoFeliz();
		this.confirmarEmail(this.tokenPendenteDeDesbloqueio);
	}

	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.confirmarEmail(this.senhaDeDesbloqueioDeTokenEstaBloqueada);
	}
	
	@Test
	public void caminhoFeliz() {
		new Logout().caminhoFeliz();
		this.confirmarEmail(this.caminhoFeliz);
	}

	private void verificarEnvioDoToken(CcpMapDecorator json) {
		String asyncTaskId = json.getAsString("asyncTaskId");
		assertTrue(asyncTaskId.trim().isEmpty() == false);
		CcpMapDecorator x = ConstantesParaTestesDeLogin.TESTING_JSON.put("subjectType" ,JnTopic.sendUserToken.name());
		VerificacaoDeStatusDaTarefaAssincrona verificacaoDeStatusDaTarefaAssincrona = new VerificacaoDeStatusDaTarefaAssincrona();
		verificacaoDeStatusDaTarefaAssincrona.getCaminhoFeliz(asyncTaskId, x, jsonToTest -> new JnEntityEmailMessageSent().exists(jsonToTest));
	}

	private CcpMapDecorator confirmarEmail(int expectedStatus) {
		return this.confirmarEmail(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private CcpMapDecorator confirmarEmail(String email, int expectedStatus) {
		
		String uri = "/login/"
		+ email
		+ "/token/language/portuguese";
		CcpMapDecorator asSingleJson = this.testarEndpoint(uri, expectedStatus);
		return asSingleJson;
	}


	@Override
	protected String getMethod() {
		return "POST";
	}

}
