package com.ccp.jn.test.asserting;

import org.junit.Before;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.setup.CcpDbSetupCreator;
import com.ccp.especifications.http.CcpHttpHandler;
import com.ccp.especifications.http.CcpHttpResponseTransform;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.implementations.db.setup.elasticsearch.CcpElasticSearchDbSetup;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public abstract class TemplateDeTestes {
	protected final String ENDPOINT_URL = "http://localhost:8080/";
	protected final int caminhoFeliz = 200;
	
	public TemplateDeTestes() {
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpElasticSearchDbSetup());
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
		CcpMapDecorator testarEndpoint = this.testarEndpoint(this.caminhoFeliz, CcpConstants.EMPTY_JSON, uri, CcpHttpResponseType.singleRecord);
		return testarEndpoint;
	}

	
	protected CcpMapDecorator testarEndpoint(String uri, Integer expectedStatus) {
		CcpMapDecorator testarEndpoint = this.testarEndpoint(expectedStatus, CcpConstants.EMPTY_JSON, uri, CcpHttpResponseType.singleRecord);
		return testarEndpoint;
	}
	
	protected<V> V testarEndpoint(Integer expectedStatus, CcpMapDecorator body, String uri, CcpHttpResponseTransform<V> transformer) {
		
		String method = this.getMethod();
		CcpMapDecorator headers = this.getHeaders();
		
		CcpHttpHandler http = new CcpHttpHandler(expectedStatus);
		String path = this.ENDPOINT_URL + uri;
		V executeHttpRequest = http.executeHttpRequest(path, method, headers, body, transformer);
		System.out.println("Executando a url " + uri + " usando o metodo " + method );
		return executeHttpRequest;
	}


}
