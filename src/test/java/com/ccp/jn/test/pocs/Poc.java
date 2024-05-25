package com.ccp.jn.test.pocs;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.entities.JnEntityDisposableRecords;
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
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("type", "org.springframework.web.HttpRequestMethodNotSupportedException");
		boolean exists = JnEntityJobsnowError.INSTANCE.exists(put);
		System.out.println(exists);
	}

	static void extracted() {
		System.out.println("A frequência de expurgo da entidade " + JnEntityContactUs.INSTANCE + " é " + JnEntityContactUs.INSTANCE.timeOption );
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("subjectType", "teste").put("email", "teste");
		JnEntityContactUs.INSTANCE.create(put);
		CcpJsonRepresentation oneById = JnEntityContactUs.INSTANCE.getOneById(put);
		System.out.println(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById);
		new CcpTimeDecorator().sleep(500);
		CcpJsonRepresentation oneById1 = JnEntityContactUs.INSTANCE.getOneById(put);
		System.out.println(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById1);
		new CcpTimeDecorator().sleep(400);
		CcpJsonRepresentation oneById2 = JnEntityContactUs.INSTANCE.getOneById(put, x -> CcpConstants.EMPTY_JSON.put("msg", "Registro já obsoleto no banco de dados, não será mais listado"));
		System.out.println(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById2);
	}

	static void diposableEntity() {
		RuntimeException e = new RuntimeException("erro de teste");
		CcpJsonRepresentation values = new CcpJsonRepresentation(e);
		JnEntityJobsnowError.INSTANCE.create(values);
		String id = JnEntityJobsnowError.INSTANCE.getId(values);
		CcpJsonRepresentation value1 = JnEntityJobsnowError.INSTANCE.getOneById(id);
		System.out.println(value1);
		CcpJsonRepresentation copyIdToSearch = JnEntityJobsnowError.INSTANCE.getCopyIdToSearch(value1);
		CcpJsonRepresentation value2 = JnEntityDisposableRecords.INSTANCE.getOneById(copyIdToSearch);
		System.out.println(value2);
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
					System.out.println(counter);
				}, "vaga");
	}
}
