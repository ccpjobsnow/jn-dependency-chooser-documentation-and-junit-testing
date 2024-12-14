package com.ccp.jn.test.pocs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutor;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityField;
import com.ccp.especifications.db.utils.decorators.CcpEntityFactory;
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.ccp.local.testings.implementations.cache.CcpLocalCacheInstances;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.utils.JnDeleteKeysFromCache;


public class Poc {
	static{
		CcpDependencyInjection.loadAllDependencies(
				new CcpElasticSearchQueryExecutor(),
				new CcpElasticSearchDbRequest(), 
				CcpLocalCacheInstances.mock,
				new CcpElasticSearchCrud(),
				new CcpGsonJsonHandler(), 
				new CcpApacheMimeHttp()
				);
	}
	
	
	public static void main(String[] args) throws Exception {
		CcpEntity ent = new CcpEntityFactory(JnEntityLoginPassword.class).entityInstance;
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("email", "xxx");
		CcpEntity twinEntity = ent.getTwinEntity();
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpEntity[] thisEntityAndHisTwinEntity = ent.getThisEntityAndHisTwinEntity();
		CcpSelectUnionAll unionAll = crud.unionAll(put, JnDeleteKeysFromCache.INSTANCE, thisEntityAndHisTwinEntity);
		twinEntity.isPresentInThisUnionAll(unionAll, put);
		CcpEntity t2 = twinEntity.getTwinEntity();
		t2.isPresentInThisUnionAll(unionAll, put);
		ent.isPresentInThisUnionAll(unionAll, put);
	}

	static void criarArquivoDeVagas() {
		CcpDbQueryOptions queryMatchAll = 
				CcpDbQueryOptions.INSTANCE
					.matchAll()
				;
		queryMatchAll.startAggregations().startBucket("x", null, 1).startAggregations().addAvgAggregation(null, null);
		Set<Object> emailsDasVisualizacoes = getEmails(queryMatchAll, "visualizacao_de_curriculo", "email");
		Set<Object> emailsDasVagas = getEmails(queryMatchAll, "vagas", "email", "mail");
		
		List<Object> intersectList = new CcpCollectionDecorator(emailsDasVisualizacoes).getIntersectList(emailsDasVagas);
		
		CcpJsonRepresentation mgetJson = CcpConstants.EMPTY_JSON;
		CcpEntityField idField = new CcpEntityField() {
			public String name() {
				return "email";
			}
			public boolean isPrimaryKey() {
				return false;
			}
		};
	
		CcpDbQueryOptions queryToSearchViews = CcpDbQueryOptions.INSTANCE.startSimplifiedQuery().terms(idField, intersectList).endSimplifiedQueryAndBackToRequest();
		Set<Object> candidatos = getEmails(queryToSearchViews, "visualizacao_de_curriculo", "candidato", "candidate");
		CcpJsonRepresentation template = new CcpJsonRepresentation("{\r\n"
				+ "    \"_index\": \"profissionais2\",\r\n"
				+ "    \"_source\": {\r\n"
				+ "        \"include\": [\r\n"
				+ "            \"curriculo.arquivo\",\r\n"
				+ "            \"pretensaoPj\",\r\n"
				+ "            \"pretensaoClt\",\r\n"
				+ "            \"pcd\",\r\n"
				+ "            \"mudanca\",\r\n"
				+ "            \"homeoffice\",\r\n"
				+ "            \"experiencia\",\r\n"
				+ "            \"disponibilidade\",\r\n"
				+ "            \"ddd\",\r\n"
				+ "            \"bitcoin\"\r\n"
				+ "        ]\r\n"
				+ "    }\r\n"
				+ "}");
		for (Object candidato : candidatos) {
			CcpJsonRepresentation doc = template.put("_id", candidato);
			mgetJson = mgetJson.addToList("docs", doc);
		}
		
		CcpHttpResponse executeHttpRequest = CcpDependencyInjection.getDependency(CcpHttpRequester.class).executeHttpRequest("http://localhost:9200/_mget", "POST", CcpConstants.EMPTY_JSON, mgetJson.asUgglyJson());
		CcpJsonRepresentation asSingleJson = executeHttpRequest.asSingleJson();
		
		List<CcpJsonRepresentation> collect = asSingleJson.getAsJsonList("docs").stream().map(json -> {
			String id = json.getAsString("_id");
			CcpJsonRepresentation source = json.getInnerJson("_source");
			CcpJsonRepresentation put = source.put("id", id);
			return put;
		}).collect(Collectors.toList());
		
		CcpJsonRepresentation resumes = CcpConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation curriculo : collect) {
			String id = curriculo.getAsString("id");
			resumes = resumes.put(id, curriculo);
		}
		
		
		CcpJsonRepresentation candidatosAgrupadosPorRecrutadores = getCandidatosAgrupadosPorRecrutadores(queryMatchAll);
		CcpJsonRepresentation vagasAgrupadosPorRecrutadores = getVagasAgrupadosPorRecrutadores(intersectList);
		Set<String> recrutadores = vagasAgrupadosPorRecrutadores.fieldSet();
		List<CcpJsonRepresentation> todasAsVagas = new ArrayList<>();
		CcpJsonRepresentation res = new CcpJsonRepresentation(resumes.content);
		for (String recrutador : recrutadores) {
			List<CcpJsonRepresentation> curriculos = candidatosAgrupadosPorRecrutadores.getAsStringList(recrutador)
					.stream().map(x -> res.getInnerJson(x)).collect(Collectors.toList())
					;

			List<CcpJsonRepresentation> vagas = vagasAgrupadosPorRecrutadores.getAsJsonList(recrutador);

			int k = 0;
			for (CcpJsonRepresentation curriculo : curriculos) {
				CcpJsonRepresentation vaga = vagas.get(k++ % vagas.size());
				CcpJsonRepresentation putAll = vaga.putAll(curriculo);
				todasAsVagas.add(putAll);
			}
		}
		CcpFileDecorator vagasFile = new CcpStringDecorator("c:\\logs\\vagas.json").file().reset();
		vagasFile.append(todasAsVagas.toString());
	}
	
	static class AgruparVagasPorRecrutadores implements java.util.function.Consumer<CcpJsonRepresentation>{
		
		CcpJsonRepresentation vagasAgrupadasPorRecrutadores = CcpConstants.EMPTY_JSON;
		
		public void accept(CcpJsonRepresentation json) {
			
			String recrutador = json.getAsObject("mail");
			String contato = json.getAsString("contato");
			String texto = json.getAsString("vaga");
			String contactChannel =  new CcpStringDecorator(contato.trim()).email().isValid() ? "email" : "link";
			
			CcpJsonRepresentation vaga = 
			CcpConstants.EMPTY_JSON
			.put("channel", contato)
			.put("email", recrutador)
			.put("description", texto)
			.put("contactChannel", contactChannel);
			
			this.vagasAgrupadasPorRecrutadores = this.vagasAgrupadasPorRecrutadores.addToList(recrutador, vaga);
			
		}
		
	}
	
	static class AgruparCandidatosPorRecrutadores implements java.util.function.Consumer<CcpJsonRepresentation>{
		
		CcpJsonRepresentation candidatosAgrupadosPorRecrutadores = CcpConstants.EMPTY_JSON;
		
		public void accept(CcpJsonRepresentation json) {
			String candidato = json.getAsObject("candidate", "candidato");
			String recrutador = json.getAsString("email");
			this.candidatosAgrupadosPorRecrutadores = this.candidatosAgrupadosPorRecrutadores.addToList(recrutador, candidato);
			
		}
		
	}

	static CcpJsonRepresentation getVagasAgrupadosPorRecrutadores(List<Object> intersectList) {
		CcpEntityField idField = new CcpEntityField() {
			public String name() {
				return "mail";
			}
			public boolean isPrimaryKey() {
				return false;
			}
		};
		CcpDbQueryOptions query = 
				CcpDbQueryOptions.INSTANCE
						.startSimplifiedQuery()
								.terms(idField, intersectList)
						.endSimplifiedQueryAndBackToRequest();

		String[] resourcesNames = new String[] {"vagas"};
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		AgruparVagasPorRecrutadores consumer = new AgruparVagasPorRecrutadores();
		queryExecutor.consumeQueryResult(
				query, 
				resourcesNames, 
				"10s", 
				10000, 
				consumer, "contato", "vaga", "mail");
		return consumer.vagasAgrupadasPorRecrutadores;
	}

	static CcpJsonRepresentation getCandidatosAgrupadosPorRecrutadores(CcpDbQueryOptions query) {
		
		String[] resourcesNames = new String[] {"visualizacao_de_curriculo"};
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		
		AgruparCandidatosPorRecrutadores consumer = new AgruparCandidatosPorRecrutadores();
		queryExecutor.consumeQueryResult(
				query, 
				resourcesNames, 
				"10s", 
				10000, 
				consumer, "candidate", "candidato", "email");
		return consumer.candidatosAgrupadosPorRecrutadores;
	}
	
	
	static Set<Object> getEmails(CcpDbQueryOptions query, String tabela, String... fields) {
		String[] resourcesNames = new String[] {tabela};
		CcpQueryExecutor queryExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		Set<Object> set = new HashSet<>();
		queryExecutor.consumeQueryResult(
				query, 
				resourcesNames, 
				"10s", 
				10000, 
				json -> {
					for (String field : fields) {
						
						String value = json.getAsTextDecorator(field).content.trim().toLowerCase();
						
						if(value.isEmpty()) {
							continue;
						}
						set.add(value);
					}
					
				}, fields);
		return set;
	}

	static void excluirCurriculo() {
		CcpHttpResponse executeHttpRequest = CcpDependencyInjection.getDependency(CcpHttpRequester.class).executeHttpRequest("http://localhost:9200/profissionais2/_doc/lucascavalcantedeo@gmail.com", "DELETE", CcpConstants.EMPTY_JSON, "");
		System.out.println(executeHttpRequest);
	}

	static void testarTempo() {
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("type", "teste");
		JnEntityJobsnowError.ENTITY.delete(put);
		JnEntityJobsnowError.ENTITY.create(put);
		while(true) {
			boolean x = JnEntityJobsnowError.ENTITY.exists(put);
			if(!x) {
				JnEntityJobsnowError.ENTITY.create(put);
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
				, JnDeleteKeysFromCache.INSTANCE
				, JnEntityInstantMessengerParametersToSend.ENTITY
				, JnEntityEmailParametersToSend.ENTITY
				);
		
		CcpTimeDecorator.log(JnEntityInstantMessengerParametersToSend.ENTITY.isPresentInThisUnionAll(unionAll, json));
		CcpTimeDecorator.log(JnEntityEmailParametersToSend.ENTITY.isPresentInThisUnionAll(unionAll, json));
	}

	static void testarDisposable() {
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("type", "teste");
		CcpEntity instance = JnEntityJobsnowError.ENTITY;
//		instance.delete(put);
		instance.create(put);
		CcpCrud dependency = CcpDependencyInjection.getDependency(CcpCrud.class);
		while(true) {
			CcpTimeDecorator ccpTimeDecorator = new CcpTimeDecorator();
			CcpSelectUnionAll unionAll = dependency.unionAll(put, JnDeleteKeysFromCache.INSTANCE, instance);
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
//		CcpTimeDecorator.log("A frequência de expurgo da entidade " + JnEntityContactUs.INSTANCE + " é " + JnEntityContactUs.INSTANCE.timeOption );
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("subjectType", "teste").put("email", "teste");
		JnEntityContactUs.ENTITY.create(put);
		CcpJsonRepresentation oneById = JnEntityContactUs.ENTITY.getOneById(put);
		CcpTimeDecorator.log(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById);
		new CcpTimeDecorator().sleep(500);
		CcpJsonRepresentation oneById1 = JnEntityContactUs.ENTITY.getOneById(put);
		CcpTimeDecorator.log(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById1);
		new CcpTimeDecorator().sleep(400);
		CcpJsonRepresentation oneById2 = JnEntityContactUs.ENTITY.getOneById(put, x -> CcpConstants.EMPTY_JSON.put("msg", "Registro já obsoleto no banco de dados, não será mais listado"));
		CcpTimeDecorator.log(new CcpTimeDecorator().getFormattedDateTime("HH:mm:ss.SSS") + ". Veio: " + oneById2);
	}

	static void diposableEntity() {
		RuntimeException e = new RuntimeException("erro de teste");
		CcpJsonRepresentation json = new CcpJsonRepresentation(e);
		JnEntityJobsnowError.ENTITY.create(json);
		String id = JnEntityJobsnowError.ENTITY.calculateId(json);
		CcpJsonRepresentation value1 = JnEntityJobsnowError.ENTITY.getOneById(id);
		CcpTimeDecorator.log(value1);
//		CcpJsonRepresentation copyIdToSearch = JnEntityJobsnowError.INSTANCE.getCopyIdToSearch(value1);
//		CcpJsonRepresentation value2 = JnEntityDisposableRecord.INSTANCE.getOneById(copyIdToSearch);
//		CcpTimeDecorator.log(value2);
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
				"10s", 
				10000, 
				vaga -> {
					String texto = vaga.getAsString("vaga").replace("\n", "").trim();
					String completeLeft = new CcpStringDecorator("" + ++counter).text().completeLeft('0', 6).content;
					file.append(completeLeft + ": " + texto);
					CcpTimeDecorator.log(counter);
				}, "vaga");
	}
}
