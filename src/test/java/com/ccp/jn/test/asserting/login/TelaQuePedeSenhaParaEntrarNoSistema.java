package com.ccp.jn.test.asserting.login;

import org.junit.Test;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.jn.sync.status.login.StatusEndpointsLogin;
import com.ccp.jn.sync.status.login.StatusExecuteLogin;
import com.ccp.jn.test.asserting.TemplateDeTestes;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPasswordLocked;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginTokenLocked;

public class TelaQuePedeSenhaParaEntrarNoSistema extends TemplateDeTestes{

	@Test
	public void emailInvalido() {
		this.executarLogin(ConstantesParaTestesDeLogin.INVALID_EMAIL, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.invalidEmail);
	}

	@Test
	public void tokenBloqueado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginTokenLocked.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD , StatusExecuteLogin.lockedToken);
	}

	@Test
	public void tokenFaltando() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.missingEmail);
	}

	@Test
	public void faltandoCadastrarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginAnswers.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.missingPassword);
	}

	@Test
	public void senhaBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginPasswordLocked.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.lockedPassword);
	}

	@Test
	public void usuarioJaLogado() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		JnEntityLoginEmail.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		JnEntityLoginSessionCurrent.INSTANCE.createOrUpdate(variaveisParaTeste.TESTING_JSON);
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.loginConflict);
	}

	@Test
	public void caminhoFeliz() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		new TelaDoCadastroDeSenha().fluxoEsperado(variaveisParaTeste);;
		new CcpTimeDecorator().sleep(10000);
		JnEntityLoginSessionCurrent.INSTANCE.delete(variaveisParaTeste.TESTING_JSON);
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.expectedStatus);
	}

	@Test
	public void errarParaDepoisAcertarSenha() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		new TelaDoCadastroDeSenha().fluxoEsperado(variaveisParaTeste);
		new CcpTimeDecorator().sleep(10000);
		JnEntityLoginSessionCurrent.INSTANCE.delete(variaveisParaTeste.TESTING_JSON);
		
		for(int k = 1; k < 3; k++) {
			this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.WRONG_PASSWORD, StatusExecuteLogin.wrongPassword);
		}
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.expectedStatus);
	}
	
	@Test
	public void senhaRecemBloqueada() {
		VariaveisParaTeste variaveisParaTeste = new VariaveisParaTeste();
		new TelaDoCadastroDeSenha().fluxoEsperado(variaveisParaTeste);;
		new CcpTimeDecorator().sleep(10000);
		JnEntityLoginSessionCurrent.INSTANCE.delete(variaveisParaTeste.TESTING_JSON);
		
		for(int k = 1; k < 3; k++) {
			this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.WRONG_PASSWORD, StatusExecuteLogin.wrongPassword);
		}
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.WRONG_PASSWORD, StatusExecuteLogin.passwordLockedRecently);
		this.executarLogin(variaveisParaTeste, ConstantesParaTestesDeLogin.CORRECT_PASSWORD, StatusExecuteLogin.lockedPassword);
	}

	private void executarLogin(VariaveisParaTeste variaveisParaTeste, String senha, StatusEndpointsLogin expectedStatus) {
		this.executarLogin(variaveisParaTeste.VALID_EMAIL, senha, expectedStatus);
	}
	
	private void executarLogin(String email, String senha, StatusEndpointsLogin expectedStatus) {
		CcpJsonRepresentation body = CcpConstants.EMPTY_JSON.put("password", senha);
		String uri = "login/"
		+ email;
		this.testarEndpoint(expectedStatus, body, uri, CcpHttpResponseType.singleRecord);
	}

	
	protected String getMethod() {
		return "POST";
	}
	
}
