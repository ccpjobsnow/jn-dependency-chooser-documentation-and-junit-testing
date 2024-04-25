package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.utils.JnConstants;

public class UpdatePassword extends TemplateDeTestes{

	private static final UnlockToken DESBLOQUEIO_DE_TOKEN = new UnlockToken();
	private static final RequestUnlockToken SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN = new RequestUnlockToken();
//	private static final DesbloqueioDeToken DESBLOQUEIO_DE_TOKEN = new DesbloqueioDeToken();
	private static final CreateLoginToken CONFIRMACAO_DE_EMAIL = new CreateLoginToken();
	private static final SavePreRegistration PRE_REGISTRO = new SavePreRegistration();
	private final int senhaDeDesbloqueioDeTokenEstaBloqueada = 421;
	private final int tokenDigitadoIncorretamente = 401;
	private final int tokenPendenteDeDesbloqueio = 420;
	private final int senhaAvaliadaComoFraca = 423;
	private final int usuarioNovoNoSistema = 404;
	private final int faltandoPreRegistro = 201;
	private final int tokenBloqueado = 403;
	private final int emailInvalido = 400;


	@Test
	public void emailInvalido() {
		this.cadastrarSenha(
				ConstantesParaTestesDeLogin.INVALID_EMAIL, 
				ConstantesParaTestesDeLogin.INCORRECT_TOKEN_TO_SAVE_PASSWORD, 
				ConstantesParaTestesDeLogin.STRONG_PASSWORD, this.emailInvalido);
	}

	@Test
	public void tokenDigitadoIncorretamente() {
		CONFIRMACAO_DE_EMAIL.faltandoCadastrarSenha();
		this.cadastrarSenhaForteComTokenIncorreto(this.tokenDigitadoIncorretamente);
	}
	
	
	@Test
	public void senhaAvaliadaComoFraca() {
		CONFIRMACAO_DE_EMAIL.faltandoCadastrarSenha();
		String tokenToValidateLogin = this.getTokenToValidateLogin();
		this.cadastrarSenhaFracaComTokenCorreto(tokenToValidateLogin, this.senhaAvaliadaComoFraca);
	}
	
	@Test
	public void usuarioNovoNoSistema() {
		this.cadastrarSenhaForteComTokenIncorreto(this.usuarioNovoNoSistema);
	}

	@Test
	public void tokenBloqueado() {
		CONFIRMACAO_DE_EMAIL.faltandoCadastrarSenha();

		for(int k = 0; k < (JnConstants.maxTries); k++) {
			this.cadastrarSenhaForteComTokenIncorreto(this.tokenDigitadoIncorretamente);
		}
		this.cadastrarSenhaForteComTokenIncorreto(this.tokenBloqueado);
	}
	
	@Test
	public void tokenPendenteDeDesbloqueio() {
		SOLICITACAO_DE_DESBLOQUEIO_DE_TOKEN.caminhoFeliz();
		this.cadastrarSenhaFracaComTokenIncorreto(this.tokenPendenteDeDesbloqueio );
	}

	@Test
	public void senhaDeDesbloqueioDeTokenEstaBloqueada() {
		DESBLOQUEIO_DE_TOKEN.senhaDeDesbloqueioDeTokenEstaBloqueada();
		this.cadastrarSenhaFracaComTokenIncorreto(this.senhaDeDesbloqueioDeTokenEstaBloqueada);
		
	}

	@Test
	public void caminhoFeliz() {
		CONFIRMACAO_DE_EMAIL.faltandoCadastrarSenha();
		String tokenToValidateLogin = this.getTokenToValidateLogin();
		this.cadastrarSenhaForteComTokenCorreto(tokenToValidateLogin, this.faltandoPreRegistro);
		PRE_REGISTRO.cadastrarPreRegistration(this.caminhoFeliz);
		this.cadastrarSenhaForteComTokenCorreto(tokenToValidateLogin, this.caminhoFeliz);
		
	}

	@Test
	public void faltandoPreRegistro() {
		CONFIRMACAO_DE_EMAIL.faltandoCadastrarSenha();
		String tokenToValidateLogin = this.getTokenToValidateLogin();
		this.cadastrarSenhaForteComTokenCorreto(tokenToValidateLogin, this.faltandoPreRegistro);
	}

	private void cadastrarSenhaFracaComTokenIncorreto(int expectedStatus) {
		this.cadastrarSenha(ConstantesParaTestesDeLogin.INCORRECT_TOKEN_TO_SAVE_PASSWORD, ConstantesParaTestesDeLogin.WEAK_PASSWORD,
				expectedStatus);
	}
	private void cadastrarSenhaFracaComTokenCorreto(String tokenToValidateLogin, int expectedStatus) {
		this.cadastrarSenha(tokenToValidateLogin, ConstantesParaTestesDeLogin.WEAK_PASSWORD,
				expectedStatus);
	}
	private void cadastrarSenhaForteComTokenIncorreto(int expectedStatus) {
		this.cadastrarSenha(ConstantesParaTestesDeLogin.INCORRECT_TOKEN_TO_SAVE_PASSWORD, ConstantesParaTestesDeLogin.STRONG_PASSWORD, expectedStatus);
	}
	private void cadastrarSenhaForteComTokenCorreto(String tokenToValidateLogin, int expectedStatus) {
		this.cadastrarSenha(tokenToValidateLogin, ConstantesParaTestesDeLogin.STRONG_PASSWORD, expectedStatus);
	}
	
	private void cadastrarSenha(String tokenToValidateLogin, String password, int expectedStatus) {
		this.cadastrarSenha(ConstantesParaTestesDeLogin.VALID_EMAIL, tokenToValidateLogin, password, expectedStatus);
	}
	
	private void cadastrarSenha(String email, String tokenToValidateLogin, String password, int expectedStatus) {
		String uri = "login/"
		+ email
		+ "/password";
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("password", password).put("token", tokenToValidateLogin);
		this.testarEndpoint(expectedStatus, body, uri,  CcpHttpResponseType.singleRecord);
	}

	private String getTokenToValidateLogin() {
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("email", ConstantesParaTestesDeLogin.VALID_EMAIL);
		CcpJsonRepresentation data = JnEntityLoginToken.INSTANCE.getOneById(put);
		String token = data.getAsString("token");
		return token;

	}
	@Override
	protected String getMethod() {
		return "POST";
	}


}
