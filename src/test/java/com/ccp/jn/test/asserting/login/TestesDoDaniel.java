package com.ccp.jn.test.asserting.login;

import static org.junit.Assert.assertFalse;

import javax.swing.JOptionPane;

import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.jn.sync.status.login.StatusCreateLoginEmail;
import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.sync.status.login.StatusExecuteLogout;
import com.ccp.jn.sync.status.login.StatusExistsLoginEmail;
import com.ccp.jn.sync.status.login.StatusSaveAnswers;
import com.ccp.jn.sync.status.login.StatusUpdatePassword;
import com.ccp.jn.test.asserting.VariaveisParaTeste;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.status.StatusExecuteLogin;

public class TestesDoDaniel {
	
	public void _20240518() {
		String email = "onias@ccpjobsnow.com";
		VariaveisParaTeste cenarioDeTeste = new VariaveisParaTeste(email);

		TelaQuePedeEmail telaQuePedeEmail = new TelaQuePedeEmail();
		telaQuePedeEmail.execute(cenarioDeTeste, StatusExistsLoginEmail.missingEmail);
		
		TelaParaConfirmacaoDeEmail telaParaConfirmacaoDeEmail = new TelaParaConfirmacaoDeEmail();
		telaParaConfirmacaoDeEmail.execute(cenarioDeTeste, StatusCreateLoginEmail.missingPassword);
		
		TelaDoPreRegistro telaDoPreRegistro = new TelaDoPreRegistro();
		telaDoPreRegistro.execute(cenarioDeTeste, StatusSaveAnswers.missingPassword); 
		
		AoEntrarNaTelaDoCadastroDeSenha aoEntrarNaTelaDoCadastroDeSenha = new AoEntrarNaTelaDoCadastroDeSenha();
		aoEntrarNaTelaDoCadastroDeSenha.execute(cenarioDeTeste, StatusCreateLoginEmail.expectedStatus);
		
		TelaDoCadastroDeSenha telaDoCadastroDeSenha = new TelaDoCadastroDeSenha();
		String token = telaDoCadastroDeSenha.execute(cenarioDeTeste, StatusUpdatePassword.expectedStatus, 
				x -> JOptionPane.showInputDialog("Por favor digite o token enviado ao email " + email));
		
		TelaDeLogout telaDeLogout = new TelaDeLogout();
		new CcpTimeDecorator().sleep(10_000);
		telaDeLogout.execute(cenarioDeTeste, StatusExecuteLogout.expectedStatus);
		
		telaQuePedeEmail.execute(cenarioDeTeste, StatusExistsLoginEmail.expectedStatus);
		
		TelaQuePedeSenhaParaEntrarNoSistema telaQuePedeSenhaParaEntrarNoSistema = new TelaQuePedeSenhaParaEntrarNoSistema();
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.weakPassword, x -> "12345678");
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.expectedStatus, x -> VariaveisParaTeste.CORRECT_PASSWORD);
		new CcpTimeDecorator().sleep(10_000);
		telaDeLogout.execute(cenarioDeTeste, StatusExecuteLogout.expectedStatus);
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.wrongPassword, x -> VariaveisParaTeste.WRONG_PASSWORD);
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.passwordLockedRecently, x -> VariaveisParaTeste.WRONG_PASSWORD);
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.lockedPassword, x -> VariaveisParaTeste.CORRECT_PASSWORD);
		aoEntrarNaTelaDoCadastroDeSenha.execute(cenarioDeTeste, StatusCreateLoginToken.statusAlreadySentToken);
		telaDoCadastroDeSenha.execute(cenarioDeTeste, StatusUpdatePassword.expectedStatus, x -> token);
		new CcpTimeDecorator().sleep(10_000);
		telaDeLogout.execute(cenarioDeTeste, StatusExecuteLogout.expectedStatus);
		boolean isLockedPassword = JnEntityLoginPassword.INSTANCE.getMirrorEntity().exists(cenarioDeTeste.REQUEST_TO_LOGIN);
		assertFalse(isLockedPassword);
		telaQuePedeSenhaParaEntrarNoSistema.execute(cenarioDeTeste, StatusExecuteLogin.expectedStatus, x -> VariaveisParaTeste.CORRECT_PASSWORD);
	}
	
}
