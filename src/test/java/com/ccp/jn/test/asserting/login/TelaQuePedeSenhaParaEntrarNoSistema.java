package com.ccp.jn.test.asserting.login;

import java.util.function.Function;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusExistsLoginEmail;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.status.StatusExecuteLogin;

public class TelaQuePedeSenhaParaEntrarNoSistema extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.executarLogin(VariaveisParaTeste.INVALID_EMAIL, VariaveisParaTeste.CORRECT_PASSWORD, StatusExecuteLogin.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		CcpEntity mirrorEntity = JnEntityLoginToken.INSTANCE.getMirrorEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusExecuteLogin.lockedToken, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}

	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.execute(variaveisParaTeste, StatusExecuteLogin.missingEmail, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}

	@Test
	public void faltandoCadastrarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.ANSWERS_JSON);
		this.execute(variaveisParaTeste, StatusExecuteLogin.missingPassword, x-> VariaveisParaTeste.CORRECT_PASSWORD);
	}

	@Test
	public void senhaBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		CcpEntity mirrorEntity = JnEntityLoginPassword.INSTANCE.getMirrorEntity();
		mirrorEntity.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusExecuteLogin.lockedPassword, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}

	@Test
	public void usuarioJaLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		JnEntityLoginSessionCurrent.INSTANCE.createOrUpdate(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusExecuteLogin.loginConflict, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}

	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		new TelaDoCadastroDeSenha().fluxoEsperado(variaveisParaTeste);;
		new CcpTimeDecorator().sleep(10000);
		JnEntityLoginSessionCurrent.INSTANCE.delete(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusExecuteLogin.expectedStatus, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}

	
	@Test
	public void bloquearSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste("onias@ccpjobsnow.com");
		new TelaDoCadastroDeSenha().fluxoEsperado(variaveisParaTeste);
		new CcpTimeDecorator().sleep(10000);
		JnEntityLoginSessionCurrent.INSTANCE.delete(variaveisParaTeste.REQUEST_TO_LOGIN);
		this.execute(variaveisParaTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		this.execute(variaveisParaTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		this.execute(variaveisParaTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		this.execute(variaveisParaTeste, StatusExecuteLogin.passwordLockedRecently, x -> VariaveisParaTeste.WRONG_PASSWORD);
		this.execute(variaveisParaTeste, StatusExecuteLogin.lockedPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		new CcpTimeDecorator().sleep(10_000);
		new TelaQuePedeEmail().execute(variaveisParaTeste, StatusExistsLoginEmail.lockedPassword);
	}
	
	@Test
	public void errarParaDepoisAcertarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		new TelaDoCadastroDeSenha().fluxoEsperado(variaveisParaTeste);
		new CcpTimeDecorator().sleep(10000);
		JnEntityLoginSessionCurrent.INSTANCE.delete(variaveisParaTeste.REQUEST_TO_LOGIN);
		
		for(int k = 1; k < 3; k++) {
			System.out.println(k);
			this.execute(variaveisParaTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		}
		this.execute(variaveisParaTeste, StatusExecuteLogin.expectedStatus, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}
	
	@Test
	public void senhaRecemBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		new TelaDoCadastroDeSenha().fluxoEsperado(variaveisParaTeste);;
		new CcpTimeDecorator().sleep(10000);
		JnEntityLoginSessionCurrent.INSTANCE.delete(variaveisParaTeste.REQUEST_TO_LOGIN);
		
		for(int k = 1; k <= 3; k++) {
			this.execute(variaveisParaTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		}
		this.execute(variaveisParaTeste, StatusExecuteLogin.passwordLockedRecently, x -> VariaveisParaTeste.WRONG_PASSWORD);
		this.execute(variaveisParaTeste, StatusExecuteLogin.lockedPassword, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}

	public String execute(VariaveisParaTeste variaveisParaTeste, CcpProcessStatus expectedStatus, Function<VariaveisParaTeste, String> producer) {
		String senha = producer.apply(variaveisParaTeste);
		this.executarLogin(variaveisParaTeste.VALID_EMAIL, senha, expectedStatus);
		String apply = producer.apply(variaveisParaTeste);
		return apply;
	}
	
	private void executarLogin(String email, String senha, CcpProcessStatus expectedStatus) {
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("password", senha);
		String uri = "login/"
		+ email;
		this.testarEndpoint(expectedStatus, body, uri, CcpHttpResponseType.singleRecord);
	}

	
	protected String getMethod() {
		return "POST";
	}
	
}
