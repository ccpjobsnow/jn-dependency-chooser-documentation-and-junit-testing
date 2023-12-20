package com.ccp.jn.test.asserting;

import org.junit.Before;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpFolderDecorator;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.http.CcpHttpHandler;
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.especifications.http.CcpHttpResponseTransform;
import com.ccp.especifications.http.CcpHttpResponseType;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.jn.commons.utils.JnConstants;

public abstract class TemplateDeTestes {
	protected final String ENDPOINT_URL = "http://localhost:8080/";
	protected final int caminhoFeliz = 200;
	
	public TemplateDeTestes() {
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp());
	}
	
	@Before
	public void before() {
		this.resetAllData();
	}
	
	public synchronized void resetAllData() {
		
		CcpHttpRequester ccpHttp = CcpDependencyInjection.getDependency(CcpHttpRequester.class);
		CcpDbRequester dbRequester = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		CcpMapDecorator connectionDetails = dbRequester.getConnectionDetails();
		String DATABASE_URL = connectionDetails.getAsString("DB_URL");
		
		String url = DATABASE_URL + "/_all/_delete_by_query";
		
		String matchAll = CcpConstants.EMPTY_JSON
						.putSubKey("query", "match_all", CcpConstants.EMPTY_JSON)
						.asJson();
		
		CcpHttpResponse endpointResponse = ccpHttp.executeHttpRequest(url, "POST", connectionDetails, matchAll);

		if (endpointResponse.isClientError()) {
			new CcpTimeDecorator().sleep(1000);
			this.resetAllData();
			return;
		}

		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(JnConstants.DATABASE_SCRIPTS_FOLDER);
		CcpFolderDecorator file = ccpStringDecorator.folder();
//		ScriptsConsumer consumer = new ScriptsConsumer(FilesOptions.insert);
//		file.readFiles(consumer);
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
