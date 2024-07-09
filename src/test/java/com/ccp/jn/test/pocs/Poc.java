package com.ccp.jn.test.pocs;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.entities.JnEntityDisposableRecord;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.jn.commons.entities.JnEntityJobsnowError;

public class Poc {
	static{
		CcpDependencyInjection.loadAllDependencies(
				new CcpElasticSearchQueryExecutor(),
				new CcpElasticSearchDbRequest(), 
				new CcpElasticSearchCrud(),
				new CcpGsonJsonHandler(), 
				new CcpApacheMimeHttp()
				);
	}
	
	public static void main(String[] args) {
		String s = "onias";
		StringBuilder sb = new StringBuilder(s);
		sb.deleteCharAt(1);
		System.out.println(sb);
	}

	static void testarTempo() {
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("type", "teste");
		JnEntityJobsnowError.INSTANCE.delete(put);
		JnEntityJobsnowError.INSTANCE.create(put);
		while(true) {
			boolean x = JnEntityJobsnowError.INSTANCE.exists(put);
			if(!x) {
				JnEntityJobsnowError.INSTANCE.create(put);
				System.out.println();
			}
			System.out.println(new CcpTimeDecorator().getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS") + " = " + x);
			new CcpTimeDecorator().sleep(1000);
			
		}
	}

	static void testarNotifyError() {
		CcpCrud dependency = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpJsonRepresentation json = new CcpJsonRepresentation("{\r\n"
				+ "  \"templateId\": \"notifyError\"\r\n"
				+ "} ");
		
		CcpSelectUnionAll unionAll = dependency.unionAll(json
				, JnEntityInstantMessengerParametersToSend.INSTANCE
				, JnEntityEmailParametersToSend.INSTANCE
				);
		
		CcpTimeDecorator.log(JnEntityInstantMessengerParametersToSend.INSTANCE.isPresentInThisUnionAll(unionAll, json));
		CcpTimeDecorator.log(JnEntityEmailParametersToSend.INSTANCE.isPresentInThisUnionAll(unionAll, json));
	}

	static void testarDisposable() {
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("type", "teste");
		JnEntityJobsnowError instance = JnEntityJobsnowError.INSTANCE;
//		instance.delete(put);
		instance.create(put);
		CcpCrud dependency = CcpDependencyInjection.getDependency(CcpCrud.class);
		while(true) {
			CcpTimeDecorator ccpTimeDecorator = new CcpTimeDecorator();
			CcpSelectUnionAll unionAll = dependency.unionAll(put, instance);
			boolean presentInThisUnionAll = instance.isPresentInThisUnionAll(unionAll, put);
			String formattedDateTime = ccpTimeDecorator.getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS");
			CcpTimeDecorator.log(presentInThisUnionAll + " - " + formattedDateTime);
			if(presentInThisUnionAll == false) {
				instance.create(put);
			}
			ccpTimeDecorator.sleep(10_000);
		}
	}

	static void errarInfinitamente() {
		CcpTimeDecorator ccpTimeDecorator = new CcpTimeDecorator();
		CcpHttpRequester dependency = CcpDependencyInjection.getDependency(CcpHttpRequester.class);

		while(true) {
			dependency.executeHttpRequest("http://localhost:8080/login/r066u1bd@teste.com", "GET", CcpConstants.EMPTY_JSON, "");
			ccpTimeDecorator.sleep(60_000);
		}
	}

	static void extracted() {
		CcpTimeDecorator.log("A frequência de expurgo da entidade " + JnEntityContactUs.INSTANCE + " é " + JnEntityContactUs.INSTANCE.timeOption );
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("subjectType", "teste").put("email", "teste");
		JnEntityContactUs.INSTANCE.create(put);
		CcpJsonRepresentation oneById = JnEntityContactUs.INSTANCE.getOneById(put);
		CcpTimeDecorator.log(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById);
		new CcpTimeDecorator().sleep(500);
		CcpJsonRepresentation oneById1 = JnEntityContactUs.INSTANCE.getOneById(put);
		CcpTimeDecorator.log(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById1);
		new CcpTimeDecorator().sleep(400);
		CcpJsonRepresentation oneById2 = JnEntityContactUs.INSTANCE.getOneById(put, x -> CcpConstants.EMPTY_JSON.put("msg", "Registro já obsoleto no banco de dados, não será mais listado"));
		CcpTimeDecorator.log(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById2);
	}

	static void diposableEntity() {
		RuntimeException e = new RuntimeException("erro de teste");
		CcpJsonRepresentation json = new CcpJsonRepresentation(e);
		JnEntityJobsnowError.INSTANCE.create(json);
		String id = JnEntityJobsnowError.INSTANCE.calculateId(json);
		CcpJsonRepresentation value1 = JnEntityJobsnowError.INSTANCE.getOneById(id);
		CcpTimeDecorator.log(value1);
		CcpJsonRepresentation copyIdToSearch = JnEntityJobsnowError.INSTANCE.getCopyIdToSearch(value1);
		CcpJsonRepresentation value2 = JnEntityDisposableRecord.INSTANCE.getOneById(copyIdToSearch);
		CcpTimeDecorator.log(value2);
	}

	static int counter;
	
	static void salvarVagaDoJobsNowAntigo() {
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		CcpDbQueryOptions queryToSearchLastUpdatedResumes = 
				CcpDbQueryOptions.INSTANCE
					.matchAll()
				;
		CcpFileDecorator file = new CcpStringDecorator("vagas.txt").file();
		String[] resourcesNames = new String[] {"vagas"};
		queryExecutor.consumeQueryResult(
				queryToSearchLastUpdatedResumes, 
				resourcesNames, 
				"10m", 
				10000, 
				vaga -> {
					String texto = vaga.getAsString("vaga").replace("\n", "").trim();
					String completeLeft = new CcpStringDecorator("" + ++counter).text().completeLeft('0', 6).content;
					file.append(completeLeft + ": " + texto);
					CcpTimeDecorator.log(counter);
				}, "vaga");
	}
}
