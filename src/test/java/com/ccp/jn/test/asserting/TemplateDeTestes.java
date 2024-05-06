package com.ccp.jn.test.asserting;

import java.util.HashSet;
import java.util.Set;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.setup.CcpDbSetupCreator;
import com.ccp.especifications.http.CcpHttpHandler;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.especifications.http.CcpHttpResponseTransform;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.ccp.implementations.password.mindrot.CcpMindrotPasswordHandler;
import com.ccp.process.CcpProcessStatus;

public abstract class TemplateDeTestes {
	protected final String ENDPOINT_URL = "http://localhost:8080/";
	public TemplateDeTestes() {
		String simpleName = this.getClass().getSimpleName();
		boolean repeated = set.add(simpleName) == false;
		if(repeated) {
			return;
		}
		System.out.println(simpleName);
		this.resetAllData();
	}
	
	static Set<String> set = new HashSet<>();
	static {
		CcpDependencyInjection.loadAllDependencies(
				new CcpGsonJsonHandler(), 
				new CcpElasticSearchCrud(),
				new CcpElasticSearchDbRequest(), 
				new CcpApacheMimeHttp(), 
				new CcpElasticSearchDbSetup(),
				new CcpMindrotPasswordHandler()
				);

	}
	public  void resetAllData() {
		String mainPath = "documentation\\database\\elasticsearch\\scripts\\";
		String createFolder = mainPath + "insert";

		CcpDbSetupCreator dbSetup = CcpDependencyInjection.getDependency(CcpDbSetupCreator.class);
		dbSetup.reinsertAllTables("jn", createFolder);
	}

	protected abstract String getMethod();

	protected CcpJsonRepresentation getHeaders() {
		return CcpConstants.EMPTY_JSON;
	}

	protected CcpJsonRepresentation testarEndpoint(String uri, CcpProcessStatus expectedStatus) {
		CcpJsonRepresentation testarEndpoint = this.testarEndpoint(expectedStatus, CcpConstants.EMPTY_JSON, uri,
				CcpHttpResponseType.singleRecord);
		return testarEndpoint;
	}

	protected <V> V testarEndpoint(CcpProcessStatus scenarioName, CcpJsonRepresentation body, String uri,
			CcpHttpResponseTransform<V> transformer) {

		String method = this.getMethod();
		CcpJsonRepresentation headers = this.getHeaders();

		int expectedStatus = scenarioName.status();
		CcpHttpHandler http = new CcpHttpHandler(expectedStatus, CcpConstants.DO_BY_PASS);
		String path = this.ENDPOINT_URL + uri;
		String name = this.getClass().getName();
		String asUgglyJson = body.asUgglyJson();
		
		CcpHttpResponse response = http.ccpHttp.executeHttpRequest(path, method, headers, asUgglyJson);
		
		V executeHttpRequest = http.executeHttpRequest(name, path, method, headers, asUgglyJson, transformer, response);
		
		int actualStatus = response.httpStatus;
		
		this.logRequestAndResponse(path, scenarioName, actualStatus, body, executeHttpRequest);
		
		scenarioName.verifyStatus(actualStatus);
		
		return executeHttpRequest;
	}

	private <V> void logRequestAndResponse(String url, CcpProcessStatus status, int actualStatus,  CcpJsonRepresentation body, V executeHttpRequest) {
		
		CcpJsonRepresentation md = CcpConstants.EMPTY_JSON.put("x", executeHttpRequest);
		
		if(executeHttpRequest instanceof CcpJsonRepresentation json) {
			md = json;
		}
		
		String date = new CcpTimeDecorator().getFormattedDateTime("dd/MM/yyyy HH:mm:ss");

		int expectedStatus = status.status();
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("url", url)
				.put("actualStatus", actualStatus)
				.put("expectedStatus", expectedStatus)
				.put("request", body)
				.put("response", md)
				.put("date", date)
				;
		String asPrettyJson = put.asPrettyJson();
		
		String testName = this.getClass().getSimpleName();
		new CcpStringDecorator("c:\\rh\\jn\\logs\\").folder()
				.createNewFolderIfNotExists(testName)
				.writeInTheFile(status + ".json", asPrettyJson);
	}

}
