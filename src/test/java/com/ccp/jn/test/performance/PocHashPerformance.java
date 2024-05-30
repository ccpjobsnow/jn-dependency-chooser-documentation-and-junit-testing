package com.ccp.jn.test.performance;

import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
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
import com.jn.commons.entities.JnEntityInstantMessengerMessageSent;

public class PocHashPerformance {
	static{
		CcpDependencyInjection.loadAllDependencies(new CcpApacheMimeHttp(), new CcpGsonJsonHandler(), new CcpElasticSearchDbRequest());
	}
	public static void main(String[] args) {
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler(), new CcpElasticSearchCrud(),
				new CcpElasticSearchDbRequest(), new CcpApacheMimeHttp(), new CcpMindrotPasswordHandler(),
				new CcpElasticSerchDbBulk());

		boolean exists = JnEntityInstantMessengerMessageSent.INSTANCE.exists(new CcpJsonRepresentation("{\r\n"
				+ "                        \"recipient\": 751717896,\r\n"
				+ "                        \"interval\": 24805,\r\n"
				+ "                        \"message\": \"org.springframework.web.HttpRequestMethodNotSupportedException\\n\\n[RequestMappingInfoHandlerMapping.handleNoMatch:265\\n, AbstractHandlerMethodMapping.lookupHandlerMethod:441\\n, AbstractHandlerMethodMapping.getHandlerInternal:382\\n, RequestMappingInfoHandlerMapping.getHandlerInternal:126\\n, RequestMappingInfoHandlerMapping.getHandlerInternal:68\\n, AbstractHandlerMapping.getHandler:507\\n, DispatcherServlet.getHandler:1283\\n, DispatcherServlet.doDispatch:1065\\n, DispatcherServlet.doService:979\\n, FrameworkServlet.processRequest:1014\\n, FrameworkServlet.doGet:903\\n, HttpServlet.service:527\\n, FrameworkServlet.service:885\\n, HttpServlet.service:614\\n, ServletHolder.handle:736\\n, ServletHandler.doFilter:1614\\n, WebSocketUpgradeFilter.doFilter:195\\n, FilterHolder.doFilter:205\\n, ServletHandler.doFilter:1586\\n, CcpPutSessionValuesAndExecuteTaskFilter.doFilter:52\\n, FilterHolder.doFilter:205\\n, ServletHandler.doFilter:1586\\n, CcpValidEmailFilter.doFilter:54\\n, FilterHolder.doFilter:205\\n, ServletHandler.doFilter:1586\\n, ServerHttpObservationFilter.doFilterInternal:109\\n, OncePerRequestFilter.doFilter:116\\n, FilterHolder.doFilter:205\\n, ServletHandler.doFilter:1586\\n, CharacterEncodingFilter.doFilterInternal:201\\n, OncePerRequestFilter.doFilter:116\\n, FilterHolder.doFilter:205\\n, ServletHandler.doFilter:1586\\n, ServletHandler.handle:1547\\n, ServletChannel.dispatch:797\\n, ServletChannel.handle:428\\n, ServletHandler.handle:464\\n, SecurityHandler.handle:571\\n, SessionHandler.handle:703\\n, ContextHandler.handle:761\\n, Server.handle:179\\n, HttpChannelState.run:594\\n, HttpConnection.onFillable:424\\n, AbstractConnection.succeeded:322\\n, FillInterest.fillable:99\\n, SelectableChannelEndPoint.run:53\\n, AdaptiveExecutionStrategy.runTask:478\\n, AdaptiveExecutionStrategy.consumeTask:441\\n, AdaptiveExecutionStrategy.tryProduce:293\\n, AdaptiveExecutionStrategy.produce:195\\n, QueuedThreadPool.runJob:971\\n, QueuedThreadPool.doRunJob:1201\\n, QueuedThreadPool.run:1156\\n, Thread.run:842\\n]\\n\\n\\nRequest method 'GET' is not supported\\n\\nCaused by:\\n{}\",\r\n"
				+ "                        \"subjectType\": \"notifyError\",\r\n"
				+ "                        \"token\": \"1122898762:AAGE69KRQ0P0TBZNPXgAMDqNaJCd7jFP_G0\"\r\n"
				+ "                    }"));
		System.out.println(exists);
		
	}
	
	static void x() {
		CcpHttpRequester dependency = CcpDependencyInjection.getDependency(CcpHttpRequester.class);
		CcpDbRequester dependency2 = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		CcpJsonRepresentation connectionDetails = dependency2.getConnectionDetails();
		String databaseUrl = connectionDetails.getAsString("DB_URL");
		CcpHttpResponse executeHttpRequest = dependency.executeHttpRequest(databaseUrl, "GET", connectionDetails, "", 200);
		System.out.println(executeHttpRequest);
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
