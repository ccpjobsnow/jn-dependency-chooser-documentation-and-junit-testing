package com.ccp.jn.test.pocs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpFolderDecorator;
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
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.http.CcpHttpMethods;
import com.ccp.implementations.db.bulk.elasticsearch.CcpElasticSerchDbBulk;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.ccp.implementations.password.mindrot.CcpMindrotPasswordHandler;
import com.ccp.jn.async.business.factory.CcpJnAsyncBusinessFactory;
import com.ccp.jn.async.business.login.JnAsyncBusinessExecuteLogout;
import com.ccp.local.testings.implementations.CcpLocalInstances;
import com.ccp.local.testings.implementations.cache.CcpLocalCacheInstances;
import com.ccp.validation.CcpJsonInvalid;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginSessionValidation;
import com.jn.commons.utils.JnDeleteKeysFromCache;
import com.jn.sync.mensageria.JnSyncMensageriaSender;
import com.vis.commons.utils.VisAsyncBusiness;


public class JnRandomTests {
	static{ 
		CcpDependencyInjection.loadAllDependencies(
				CcpLocalInstances.mensageriaSender.getLocalImplementation(new CcpJnAsyncBusinessFactory()),
				new CcpElasticSearchQueryExecutor(),
				new CcpElasticSearchDbRequest(), 
				new CcpMindrotPasswordHandler(),
				new CcpElasticSerchDbBulk(),
				CcpLocalCacheInstances.map,
				new CcpElasticSearchCrud(),
				new CcpGsonJsonHandler(), 
				new CcpApacheMimeHttp()
				);
	}
	
	public static void main(String[] args) throws Exception {
		CcpJsonRepresentation json = new CcpJsonRepresentation("{\r\n"
				+ "  \"_entities\": {\r\n"
				+ "    \"login_session_conflict\": {\r\n"
				+ "      \"email\": \"376e139c1a4b8ae34a3efe9bf12623a2fbdd3622\"\r\n"
				+ "    },\r\n"
				+ "    \"login_session_validation\": {\r\n"
				+ "      \"userAgent\": \"teste\",\r\n"
				+ "      \"email\": \"376e139c1a4b8ae34a3efe9bf12623a2fbdd3622\",\r\n"
				+ "      \"ip\": \"teste\",\r\n"
				+ "      \"token\": \"6bb3f103b0b1c2851ea1e6cb2312a2a0406df2e5\"\r\n"
				+ "    }\r\n"
				+ "  },\r\n"
				+ "  \"data\": \"24032025 01:23:08\",\r\n"
				+ "  \"email\": \"onias85@gmail.com\",\r\n"
				+ "  \"ip\": \"teste\",\r\n"
				+ "  \"messageId\": \"d7b026ac-471d-4cdc-976e-28cd580082fc\",\r\n"
				+ "  \"request\": {\r\n"
				+ "    \"userAgent\": \"teste\",\r\n"
				+ "    \"email\": \"onias85@gmail.com\",\r\n"
				+ "    \"ip\": \"teste\",\r\n"
				+ "    \"token\": \"CO8HTJU0\",\r\n"
				+ "    \"_entities\": {\r\n"
				+ "      \"login_session_conflict\": {\r\n"
				+ "        \"email\": \"376e139c1a4b8ae34a3efe9bf12623a2fbdd3622\"\r\n"
				+ "      },\r\n"
				+ "      \"login_session_validation\": {\r\n"
				+ "        \"userAgent\": \"teste\",\r\n"
				+ "        \"email\": \"376e139c1a4b8ae34a3efe9bf12623a2fbdd3622\",\r\n"
				+ "        \"ip\": \"teste\",\r\n"
				+ "        \"token\": \"6bb3f103b0b1c2851ea1e6cb2312a2a0406df2e5\"\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    \"topic\": \"executeLogout\"\r\n"
				+ "  },\r\n"
				+ "  \"started\": 1742790188666,\r\n"
				+ "  \"token\": \"CO8HTJU0\",\r\n"
				+ "  \"topic\": \"executeLogout\",\r\n"
				+ "  \"userAgent\": \"teste\"\r\n"
				+ "}");
		boolean exists = JnEntityLoginSessionValidation.ENTITY.exists(json);
		System.out.println(exists);
	}

	static void testarExpurgable2() {
		CcpJsonRepresentation json = new CcpJsonRepresentation("{\r\n"
				+ "  \"email\": \"onias85@gmail.com\",\r\n"
				+ "  \"ip\": \"localhost\",\r\n"
				+ "  \"password\": \"Jobsnow1!\",\r\n"
				+ "  \"token\": \"M6ZRDQ83\",\r\n"
				+ "  \"originalToken\": \"M6ZRDQ83\",\r\n"
				+ "  \"userAgent\": \"Apache-HttpClient/4.5.4 (Java/17.0.9)\"\r\n"
				+ "}");
		CcpEntity entity = JnEntityLoginSessionValidation.ENTITY;
		testarExpurgable(json, entity);
	}

	static void testarExpurgable() {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON.put("type", "teste").put("stackTrace", "teste").put("cause", "teste");
		CcpEntity entity = JnEntityJobsnowError.ENTITY;
		testarExpurgable(json, entity);
	}

	private static void testarExpurgable(CcpJsonRepresentation json, CcpEntity entity) {
		entity.createOrUpdate(json);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		for(int k = 0; k < 65; k++) {
			CcpTimeDecorator ctd = new CcpTimeDecorator();
			System.out.println("Teste1: " + ctd.getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS") + " = " + entity.exists(json));
			CcpSelectUnionAll unionAll = crud.unionAll(new CcpJsonRepresentation[] {json}, JnDeleteKeysFromCache.INSTANCE, entity);
			System.out.println("unionAll1: " + ctd.getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS") + " = " + entity.isPresentInThisUnionAll(unionAll, json));
			ctd.sleep(1000);
		}
		
		testarExpurgable(json, entity);
	}

	static void testarSalvamentoDeSenha() {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON
				.put(JnEntityLoginPassword.Fields.password.name(), "123456")
				.put("email", "onias85@gmail.com")
				;
		JnEntityLoginPassword.ENTITY.createOrUpdate(json);

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpSelectUnionAll unionAll = crud.unionAll(new CcpJsonRepresentation[] {json}, JnDeleteKeysFromCache.INSTANCE, JnEntityLoginPassword.ENTITY);
		System.out.println(unionAll);
		
		System.out.println(com.ccp.dependency.injection.CcpDependencyInjection
				.getDependency(com.ccp.especifications.password.CcpPasswordHandler.class)
				.matches("123456", "$2a$12$mjfndltYxA2TsM9Eo8rnSOaNCr3QTTerfoVcj5ucAGO5C/vavQofC"));
		System.out.println(com.ccp.dependency.injection.CcpDependencyInjection
				.getDependency(com.ccp.especifications.password.CcpPasswordHandler.class)
				.matches("123456", "$2a$12$FYwjF4ysRKHCwg9cp1H/meLBRLeevbDlT5ZQvoSGQX6D1osAtWVde"));
	}

	static void extracted() {
		CcpFolderDecorator folder = new CcpStringDecorator("documentation/database/elasticsearch/scripts/entities/create").folder();
		Map<String, List<String>> map = new TreeMap<>();
		folder.readFiles(file -> {
			String fileName = file.getName();
			String javaClassName = "JnEntity" + new CcpStringDecorator(fileName).text().toCamelCase().toString();
			CcpJsonRepresentation json = file.asSingleJson().getInnerJsonFromPath("mappings", "properties");
			Set<String> fields = json.fieldSet();
			for (String field : fields) {
				List<String> javaClassesName = map.getOrDefault(field, new ArrayList<>());
				javaClassesName.add(javaClassName);
				map.put(field, javaClassesName);
			}
		});
		CcpFileDecorator reset = new CcpStringDecorator("c:\\logs\\fields.txt").file().reset();
		Set<String> keySet = map.keySet();
		for (String string : keySet) {
			List<String> list = map.get(string);
			reset.append(string + " = " + list);
		}
	}

	static void testarValidacoes() {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON
				.put("name", "L")
				.put("ddd", 20)
				;
		try {
			
			new JnSyncMensageriaSender(VisAsyncBusiness.resume).apply(json);
		} catch (CcpJsonInvalid e) {
			new CcpStringDecorator("C:\\RH\\errosDeCurriculo.json")
			.file()
			.reset()
			.append(e.result.asPrettyJson())
			;
		}
	}

	static void metodoDoLucas() {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON
				.put("email", "onias85@gmail.com")
				
				;
		List<CcpJsonRepresentation> parametersToSearch = JnEntityLoginSessionValidation.ENTITY.getParametersToSearch(json);
		
		System.out.println(parametersToSearch);
		
		JnEntityLoginSessionValidation.ENTITY.getTwinEntity().createOrUpdate(json);
		JnAsyncBusinessExecuteLogout.INSTANCE.apply(json);
	}

	static void testarExpurgableEntity() {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON
				.put("cause", new CcpJsonRepresentation("{'nome':'onias'}"))
				.put("stackTrace", "{'nome':'vieira'}")
				.put("type", "any")
				;
		CcpJsonRepresentation oneById = JnEntityJobsnowError.ENTITY.getOneById(json, CcpOtherConstants.RETURNS_EMPTY_JSON);
		System.out.println(oneById);
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
		
		CcpJsonRepresentation mgetJson = CcpOtherConstants.EMPTY_JSON;
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
		
		CcpHttpResponse executeHttpRequest = CcpDependencyInjection.getDependency(CcpHttpRequester.class).executeHttpRequest("http://localhost:9200/_mget", CcpHttpMethods.POST, CcpOtherConstants.EMPTY_JSON, mgetJson.asUgglyJson());
		CcpJsonRepresentation asSingleJson = executeHttpRequest.asSingleJson();
		
		List<CcpJsonRepresentation> collect = asSingleJson.getAsJsonList("docs").stream().map(json -> {
			String id = json.getAsString("_id");
			CcpJsonRepresentation source = json.getInnerJson("_source");
			CcpJsonRepresentation put = source.put("id", id);
			return put;
		}).collect(Collectors.toList());
		
		CcpJsonRepresentation resumes = CcpOtherConstants.EMPTY_JSON;
		
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
		
		CcpJsonRepresentation vagasAgrupadasPorRecrutadores = CcpOtherConstants.EMPTY_JSON;
		
		public void accept(CcpJsonRepresentation json) {
			
			String recrutador = json.getAsObject("mail");
			String contato = json.getAsString("contato");
			String texto = json.getAsString("vaga");
			String contactChannel =  new CcpStringDecorator(contato.trim()).email().isValid() ? "email" : "link";
			
			CcpJsonRepresentation vaga = 
			CcpOtherConstants.EMPTY_JSON
			.put("channel", contato)
			.put("email", recrutador)
			.put("description", texto)
			.put("contactChannel", contactChannel);
			
			this.vagasAgrupadasPorRecrutadores = this.vagasAgrupadasPorRecrutadores.addToList(recrutador, vaga);
			
		}
		
	}
	
	static class AgruparCandidatosPorRecrutadores implements java.util.function.Consumer<CcpJsonRepresentation>{
		
		CcpJsonRepresentation candidatosAgrupadosPorRecrutadores = CcpOtherConstants.EMPTY_JSON;
		
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
		CcpHttpResponse executeHttpRequest = CcpDependencyInjection.getDependency(CcpHttpRequester.class).executeHttpRequest("http://localhost:9200/profissionais2/_doc/lucascavalcantedeo@gmail.com", CcpHttpMethods.DELETE, CcpOtherConstants.EMPTY_JSON, "");
		System.out.println(executeHttpRequest);
	}

	static void testarTempo() {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON.
				put("cause", new CcpJsonRepresentation("{'nome':'onias'}"))
				.put("stackTrace", "{'nome':'vieira'}")
				.put("type", "any")
				;
		JnEntityJobsnowError.ENTITY.delete(json);
		JnEntityJobsnowError.ENTITY.createOrUpdate(json);
		while(true) {
			boolean exists = JnEntityJobsnowError.ENTITY.exists(json);
			if(exists == false) {
				JnEntityJobsnowError.ENTITY.createOrUpdate(json);
				System.out.println(new CcpTimeDecorator().getFormattedDateTime("dd/MM/yyyy HH:mm:ss.SSS"));
			}
			new CcpTimeDecorator().sleep(1000);
			
		}
	}

	static void errarInfinitamente() {
		CcpTimeDecorator ccpTimeDecorator = new CcpTimeDecorator();
		CcpHttpRequester dependency = CcpDependencyInjection.getDependency(CcpHttpRequester.class);

		while(true) {
			dependency.executeHttpRequest("http://localhost:8080/login/r066u1bd@teste.com", CcpHttpMethods.GET, CcpOtherConstants.EMPTY_JSON, "");
			ccpTimeDecorator.sleep(60_000);
		}
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
//					CcpTimeDecorator.appendLog(counter);
				}, "vaga");
	}
}
