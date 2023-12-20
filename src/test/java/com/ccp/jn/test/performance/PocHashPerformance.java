package com.ccp.jn.test.performance;

import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class PocHashPerformance {
	static{
		CcpDependencyInjection.loadAllDependencies(new CcpApacheMimeHttp(), new CcpGsonJsonHandler(), new CcpElasticSearchDbRequest());
	}
	public static void main(String[] args) {
		String s = "teste";
		String x = s + "zzzzzzzz";
		System.out.println(x);
	}
	
	static void x() {
		CcpHttpRequester dependency = CcpDependencyInjection.getDependency(CcpHttpRequester.class);
		CcpDbRequester dependency2 = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		CcpMapDecorator connectionDetails = dependency2.getConnectionDetails();
		String databaseUrl = connectionDetails.getAsString("DB_URL");
		CcpHttpResponse executeHttpRequest = dependency.executeHttpRequest(databaseUrl, "GET", connectionDetails, "", 200);
		System.out.println(executeHttpRequest);
	}

	static void testeHash() {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXZ01234567890!@#$%&*()_+=";
		
		CcpStringDecorator csd = new CcpStringDecorator(str);
		String generateToken = csd.text().generateToken(50);
		
		CcpHashDecorator hash = new CcpStringDecorator(generateToken).hash();

		long currentTimeMillis = System.currentTimeMillis();
		int limite = 1000000;
		for(int k = 0; k < limite; k++) {
			hash.asString("MD5");
		}
		long diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		System.out.println(diff);
		for(int k = 0; k < limite; k++) {
			hash.asString("SHA1");
		}
		diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		System.out.println(diff);
		for(int k = 0; k < limite; k++) {
			hash.asString("SHA-256");
		}
		diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		System.out.println(diff);
		for(int k = 0; k < limite; k++) {
			hash.asString("SHA-512");
		}
		diff = System.currentTimeMillis() - currentTimeMillis;
		currentTimeMillis = System.currentTimeMillis();
		System.out.println(diff);
	}
}
