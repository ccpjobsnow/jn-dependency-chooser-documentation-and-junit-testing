package com.ccp.jn.test.asserting.login;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VerificacaoDeStatusDaTarefaAssincrona;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.utils.JnAsyncBusiness;

@SuppressWarnings("unchecked")
public class CreateLoginToken  extends TemplateDeTestes{
	private static final ExecuteLogout EXECUTE_LOGOUT = new ExecuteLogout();
	private static final UnlockToken DESBLOQUEIO_DE_TOKEN = new UnlockToken();
	private static final UpdatePassword CADASTRO_DE_SENHA = new UpdatePassword();
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
		this.resetAllData();
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
		this.resetAllData();
	
		CcpJsonRepresentation json = this.confirmarEmail(this.faltandoCadastrarSenha);
		
		this.verificarEnvioDoToken(json);
	}
	
	@Test
	public void senhaBloqueada() {
		new ExecuteLogin().senhaBloqueada();
		this.confirmarEmail(this.senhaBloqueada);
	}

	@Test
	public void usuarioJaLogado() {
		new ExecuteLogin().caminhoFeliz();
		this.confirmarEmail(this.usuarioJaLogado);
	}

	@Test
	public void tokenPendenteDeDesbloqueio() {
		this.resetAllData();
		new RequestUnlockToken().caminhoFeliz();
		this.confirmarEmail(this.tokenPendenteDeDesbloqueio);
	}

	
	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		this.resetAllData();
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.confirmarEmail(this.senhaDeDesbloqueioDeTokenEstaBloqueada);
	}
	
	@Test
	public void caminhoFeliz() {
		this.resetAllData();
		EXECUTE_LOGOUT.caminhoFeliz();
		this.confirmarEmail(this.caminhoFeliz);
	}

	private void verificarEnvioDoToken(CcpJsonRepresentation json) {
		String asyncTaskId = json.getAsString("asyncTaskId");
		assertTrue(asyncTaskId.trim().isEmpty() == false);
		CcpJsonRepresentation x = ConstantesParaTestesDeLogin.TESTING_JSON.put("subjectType" ,JnAsyncBusiness.sendUserToken);
		VerificacaoDeStatusDaTarefaAssincrona verificacaoDeStatusDaTarefaAssincrona = new VerificacaoDeStatusDaTarefaAssincrona();
		verificacaoDeStatusDaTarefaAssincrona.getCaminhoFeliz(asyncTaskId, x, jsonToTest -> JnEntityEmailMessageSent.INSTANCE.exists(jsonToTest));
	}

	private CcpJsonRepresentation confirmarEmail(int expectedStatus) {
		return this.confirmarEmail(ConstantesParaTestesDeLogin.VALID_EMAIL, expectedStatus);
	}
	
	private CcpJsonRepresentation confirmarEmail(String email, int expectedStatus) {
		
		String uri = "/login/"
		+ email
		+ "/token/language/portuguese";
		CcpJsonRepresentation asSingleJson = this.testarEndpoint(uri, expectedStatus);
		return asSingleJson;
	}


	@Override
	protected String getMethod() {
		return "POST";
	}

}
