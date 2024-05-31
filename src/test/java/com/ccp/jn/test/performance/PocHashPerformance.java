package com.ccp.jn.test.performance;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.implementations.db.bulk.elasticsearch.CcpElasticSerchDbBulk;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.ccp.implementations.password.mindrot.CcpMindrotPasswordHandler;
import com.jn.commons.entities.JnEntityJobsnowError;

public class PocHashPerformance {
	static{
		CcpDependencyInjection.loadAllDependencies(new CcpApacheMimeHttp(), new CcpGsonJsonHandler(), new CcpElasticSearchDbRequest());
	}
	public static void main(String[] args) {
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchCrud(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpMindrotPasswordHandler(),
				new CcpElasticSerchDbBulk());

		Object exists = JnEntityJobsnowError.INSTANCE.calculateId(CcpConstants.EMPTY_JSON.put("type", "org.springframework.web.HttpRequestMethodNotSupportedException"));
		System.out.println("" + exists);
		
	}
	
	static void x() {
		CcpHttpRequester dependency = CcpDependencyInjection.getDependency(CcpHttpRequester.class);
		CcpDbRequester dependency2 = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		CcpJsonRepresentation connectionDetails = dependency2.getConnectionDetails();
		String databaseUrl = connectionDetails.getAsString("DB_URL");
		CcpHttpResponse executeHttpRequest = dependency.executeHttpRequest(databaseUrl, "GET", connectionDetails, "", 200);
		CcpTimeDecorator.log(executeHttpRequest);
	}

	static void testeHash() {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXZ01234567890!@#$%&*()_+=";
		
		CcpStringDecorator csd = new CcpStringDecorator(str);
		String generateToken = csd.text().generateToken(50).content;
		
		CcpHashDecorator hash = new CcpStringDecorator(generateToken).hash();

		long currentTimeMillis = System.currentTimeMillis();
		int limite = 1000000;
		for(int k = 0; k < limite; k++) {
			hash.asString("MD5");
		}
		long diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		CcpTimeDecorator.log(diff);
		for(int k = 0; k < limite; k++) {
			hash.asString("SHA1");
		}
		diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		CcpTimeDecorator.log(diff);
		for(int k = 0; k < limite; k++) {
			hash.asString("SHA-256");
		}
		diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		CcpTimeDecorator.log(diff);
		for(int k = 0; k < limite; k++) {
			hash.asString("SHA-512");
		}
		diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		CcpTimeDecorator.log(diff);
	}
}
