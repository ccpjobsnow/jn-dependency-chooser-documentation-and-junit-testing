package com.ccp.jn.test.asserting;

import org.junit.Before;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.setup.CcpDbSetupCreator;
import com.ccp.especifications.http.CcpHttpHandler;
import com.ccp.especifications.http.CcpHttpResponseTransform;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.implementations.db.dao.elasticsearch.CcpElasticSearchDao;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public abstract class TemplateDeTestes {
	protected final String ENDPOINT_URL = "http://localhost:8080/";
	protected final int caminhoFeliz = 200;

	public TemplateDeTestes() {
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDao(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());
	}

	@Before
	public void before() {
		this.resetAllData();
	}

	public synchronized void resetAllData() {
		String mainPath = "documentation\\database\\elasticsearch\\scripts\\";
		String createFolder = mainPath + "insert";

		CcpDbSetupCreator dbSetup = CcpDependencyInjection.getDependency(CcpDbSetupCreator.class);
		dbSetup.resetAllData("jn", createFolder);
	}

	protected abstract String getMethod();

	protected CcpMapDecorator getHeaders() {
		return new CcpMapDecorator();
	}

	protected CcpMapDecorator getCaminhoFeliz(String uri) {
		CcpMapDecorator testarEndpoint = this.testarEndpoint(this.caminhoFeliz, CcpConstants.EMPTY_JSON, uri,
				CcpHttpResponseType.singleRecord);
		return testarEndpoint;
	}

	protected CcpMapDecorator testarEndpoint(String uri, Integer expectedStatus) {
		CcpMapDecorator testarEndpoint = this.testarEndpoint(expectedStatus, CcpConstants.EMPTY_JSON, uri,
				CcpHttpResponseType.singleRecord);
		return testarEndpoint;
	}

	protected <V> V testarEndpoint(Integer expectedStatus, CcpMapDecorator body, String uri,
			CcpHttpResponseTransform<V> transformer) {

		String method = this.getMethod();
		CcpMapDecorator headers = this.getHeaders();

		CcpHttpHandler http = new CcpHttpHandler(expectedStatus);
		String path = this.ENDPOINT_URL + uri;
		V executeHttpRequest = http.executeHttpRequest(path, method, headers, body, transformer);

		this.logRequestAndResponse(expectedStatus, body, executeHttpRequest);

		return executeHttpRequest;
	}

	private <V> void logRequestAndResponse(Integer expectedStatus, CcpMapDecorator body, V executeHttpRequest) {
		
		CcpMapDecorator md = new CcpMapDecorator().put("x", executeHttpRequest);
		
		if(executeHttpRequest instanceof CcpMapDecorator json) {
			md = json;
		}
		CcpMapDecorator put = new CcpMapDecorator().put("request", body).put("response", md);
		String asPrettyJson = put.asPrettyJson();
		
		new CcpStringDecorator("c:\\ccp\\jn\\logs\\").folder()
				.createNewFolderIfNotExists(this.getClass().getSimpleName())
				.writeInTheFile(expectedStatus + ".json", asPrettyJson);
	}

}
