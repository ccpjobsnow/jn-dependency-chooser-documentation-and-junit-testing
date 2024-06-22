package com.ccp.jn.test.pocs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpFileDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.http.CcpHttpRequester;
import com.ccp.especifications.http.CcpHttpResponse;
import com.ccp.especifications.json.CcpJsonHandler;
import com.ccp.implementations.db.crud.elasticsearch.CcpElasticSearchCrud;
import com.ccp.implementations.db.query.elasticsearch.CcpElasticSearchQueryExecutor;
import com.ccp.implementations.db.utils.elasticsearch.CcpElasticSearchDbRequest;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class Skills {

	public static void main(String[] args) {
		CcpDependencyInjection.loadAllDependencies(
				new CcpElasticSearchQueryExecutor(),
				new CcpElasticSearchDbRequest(), 
				new CcpElasticSearchCrud(),
				new CcpGsonJsonHandler(), 
				new CcpApacheMimeHttp()
				);
		retirarPalavras();
	}
	
	static void retirarPalavras() {
		Set<String> collect = new CcpStringDecorator("documentation\\skills\\excluir.txt").file().getLines().stream().collect(Collectors.toSet());
		System.out.println(collect.size());
		Set<String> duvidas = new CcpStringDecorator("documentation\\skills\\duvidas.txt").file().getLines().stream().collect(Collectors.toSet());
		ArrayList<String> arrayList = new ArrayList<String>(duvidas);
		System.out.println(arrayList.size());
		arrayList.addAll(collect);
		List<CcpJsonRepresentation> asJsonList = new CcpStringDecorator("documentation\\skills\\skills.json").file().asJsonList();
		for (String string : arrayList) {
			asJsonList = asJsonList.stream().filter(x -> x.getAsString("word").equals(string) == false).collect(Collectors.toList());
		}
		System.out.println(asJsonList.size());
		CcpFileDecorator file = new CcpStringDecorator("documentation\\skills\\synonyms.txt").file();
		Set<String> synonyms = file.getLines().stream().collect(Collectors.toSet());
		ArrayList<String> arrayList2 = new ArrayList<String>(synonyms);
		Collections.sort(arrayList2);
		file.reset();
		for (String string : arrayList2) {
			file.append(string);	
		}
	}
	
	static void contarSinonimos() {
		List<String> lines = new CcpStringDecorator("documentation\\skills\\x.json").file().getLines();
		Set<String> collect = lines.stream().map(x -> new CcpJsonRepresentation(x).getAsString("word")).collect(Collectors.toSet());
		System.out.println(collect.size());
		String extractStringContent = new CcpStringDecorator("newSynonyms.json").file().extractStringContent();
		CcpJsonRepresentation json = new CcpJsonRepresentation(extractStringContent);
		List<CcpJsonRepresentation> collect2 = json.fieldSet().stream().map(field -> json.getInnerJson(field)).collect(Collectors.toList());
		Set<String> synonyms = new HashSet<>();
		for (CcpJsonRepresentation x : collect2) {
			List<String> newSynonyms = x.getAsStringList("newSynonyms");
			synonyms.addAll(newSynonyms);
		}
		long count = collect.stream().filter(x -> synonyms.contains(x) == false).count();
		System.out.println(count);
	}
	
	static void atualizarSinonimos() {
		String newSynonymsText = new CcpStringDecorator("newSynonyms.json").file().extractStringContent();
		CcpJsonRepresentation newSynonyms = new CcpJsonRepresentation(newSynonymsText);
		Set<String> fieldSet = newSynonyms.fieldSet();
		List<CcpJsonRepresentation> skillsToUpdateSynonyms = fieldSet.stream()
		.map(x -> newSynonyms.getInnerJson(x).put("word", x))
		.filter(x -> x.getAsStringList("newSynonyms").isEmpty() == false)
		.collect(Collectors.toList());
		String skillsText = new CcpStringDecorator("documentation\\skills\\skills.json").file().extractStringContent();
		
		List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(skillsText).getAsJsonList("linhas");
		List<String> vagas = new CcpStringDecorator("C:\\jn\\vagas\\vagas.txt").file().getLines().stream()
				.map(vaga -> sanitizeWord(vaga)).collect(Collectors.toList());
		
		List<CcpJsonRepresentation> collect = skills.stream().map(oldSkill -> atualizarSinonimosDestaSkill(oldSkill, skillsToUpdateSynonyms, vagas)).collect(Collectors.toList());
		new CcpStringDecorator("documentation\\skills\\sinonimosAtualizados.json").file().append(collect.toString());
	}
	
	static CcpJsonRepresentation atualizarSinonimosDestaSkill(CcpJsonRepresentation oldSkill, List<CcpJsonRepresentation>skillsToUpdateSynonyms, List<String> vagas) {
		String word = oldSkill.getAsString("word");
		List<CcpJsonRepresentation> synonyms = new ArrayList<CcpJsonRepresentation>( oldSkill.getAsJsonList("synonyms"));
		Optional<CcpJsonRepresentation> findFirst = skillsToUpdateSynonyms.stream().filter(x -> x.getAsString("word").equals(word)).findFirst();
		boolean skillNotFound = findFirst.isPresent() == false;
		if(skillNotFound) {
			return oldSkill;
		}
		CcpJsonRepresentation skillToUpdateSynonyms = findFirst.get();
		List<String> newSynonyms = skillToUpdateSynonyms.getAsStringList("newSynonyms");
		List<CcpJsonRepresentation> collect = newSynonyms.stream().map(x -> getRelatorioDeUmaPalavra(x, vagas)).collect(Collectors.toList());
		synonyms.addAll(collect);
		CcpJsonRepresentation put = oldSkill.put("synonyms", synonyms);
		return put;
	}
	
	
	static void inserirTermosDoGeminiQueNaoTenhamSidoRegistradosComoSinonimos() {
		CcpFileDecorator file = new CcpStringDecorator("documentation\\skills\\updatedSkillList.json").file();
		String geminiText = file.extractStringContent();
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<Map<String, Object>> list = jsonHandler.fromJson(geminiText);
		CcpFileDecorator skillFile = new CcpStringDecorator("documentation\\skills\\skills.json").file();
		String skillsText = skillFile.extractStringContent();
		Set<String> allSynonyms = getAllSynonymsFromList(skillsText);
		 
		List<CcpJsonRepresentation> newSkills = list.stream()
				.map(x -> new CcpJsonRepresentation(x))
				.filter(x -> x.getAsString("word").trim().length() > 1)
				.filter(x -> x.getAsBoolean("gemini"))
				.filter(x -> x.getAsIntegerNumber("vagas") > 1)
				
				.filter(x -> allSynonyms.contains(x.getAsString("word")) == false)
				.map(x -> x.renameField("vagas", "somatoria"))
				.collect(Collectors.toList());
		 
		List<Map<String, Object>> lista = jsonHandler.fromJson(skillsText);
		List<CcpJsonRepresentation> oldSkills = lista.stream().map(x -> new CcpJsonRepresentation(x)).collect(Collectors.toList());
	
		List<CcpJsonRepresentation> allSkills = new ArrayList<>(oldSkills);
		allSkills.addAll(newSkills);
		
		skillFile.reset().append(allSkills.toString());
		
	}
	static void loadErrors() {
		String extractStringContent = new CcpStringDecorator("erros.txt").file().extractStringContent();
		List<String> collect = new CcpJsonRepresentation(extractStringContent).getAsJsonList("list")
		.stream()
		.map(x -> x.getInnerJson("response"))
		.map(x -> x.getAsJsonList("candidates").get(0))
		.map(x -> x.getInnerJson("content"))
		.map(x -> x.getAsJsonList("parts").get(0))
		.map(x -> x.getAsString("text"))
		.map(x -> x.replace("`", ""))
		.map(x -> x.replace("-", ""))
		.map(x -> x.replace("'", ""))
		.map(x -> x.replace("(", ","))
		.map(x -> x.replace(")", ","))
		.map(x -> x.replace("[", ""))
		.map(x -> x.replace("]", ""))
		.map(x -> x.replace("\n", ","))
		.map(x -> x.replace("\r", ","))
		.map(x -> x.replace("\t", ","))
		.map(x -> x.trim().toUpperCase())
		.filter(x -> x.length() > 2)
		.collect(Collectors.toList());
		String str = "";
		for (String string : collect) {
			str += string;
		}
		String[] split = str.split(",");
		Set<String> skillsFromErrors = Arrays.asList(split).stream().map(x -> x.trim()).filter(x -> x.length() > 2).collect(Collectors.toSet());

		CcpFileDecorator file = new CcpStringDecorator("documentation\\skills\\updatedSkillList.json").file();
		String skillsText = file.extractStringContent();

		Set<String> allSynonyms = getAllSynonyms(skillsText);
		Set<String> newSkills = skillsFromErrors.stream()
				.filter(x -> allSynonyms.contains(x) == false)
				.collect(Collectors.toSet());
		
		List<String> vagas = new CcpStringDecorator("C:\\jn\\vagas\\vagas.txt").file().getLines().stream()
				.map(vaga -> sanitizeWord(vaga)).collect(Collectors.toList());
	
		List<CcpJsonRepresentation> newList = newSkills.stream().map(x -> getRelatorioDeUmaPalavra(x, vagas).put("gemini", true).renameField("skill", "word"))
		.collect(Collectors.toList());
		String updatedSkillList = file.extractStringContent();
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<Map<String, Object>> list = jsonHandler.fromJson(updatedSkillList);
		List<CcpJsonRepresentation> oldList = list.stream().map(x -> new CcpJsonRepresentation(x)).collect(Collectors.toList());
		List<CcpJsonRepresentation> listToSave = new ArrayList<>(oldList);
		listToSave.addAll(newList);
		file.reset().append(listToSave.toString());
	}

	static Set<String> getAllSynonyms(String skillsText) {
		List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(skillsText).getAsJsonList("linhas");
		Set<String> allSynonyms = getAllSynonyms(skills);
		return allSynonyms;
	}

	static Set<String> getAllSynonymsFromList(String skillsText) {
		String extractStringContent = new CcpStringDecorator("documentation\\skills\\skills.json").file().extractStringContent();
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<Map<String, Object>> list = jsonHandler.fromJson(extractStringContent);
		
		List<CcpJsonRepresentation> skills = list.stream().map(x -> new CcpJsonRepresentation(x)).collect(Collectors.toList());
		Set<String> allSynonyms = getAllSynonyms(skills);
		return allSynonyms;
	}

	private static Set<String> getAllSynonyms(List<CcpJsonRepresentation> skills) {
		Set<String> allSynonyms = new HashSet<>();
		for (CcpJsonRepresentation skill : skills) {
			String word = skill.getAsString("word");
			allSynonyms.add(word);
			boolean gemini = skill.getAsBoolean("gemini");
			
			if(gemini) {
				continue;
			}
			Set<String> synonyms = skill.getAsJsonList("synonyms").stream().map(x -> x.getAsString("skill")).collect(Collectors.toSet());
			allSynonyms.addAll(synonyms);
		}
		return allSynonyms;
	}
	
	static void showGeminiSkills() {
		String extractStringContent = new CcpStringDecorator("documentation\\skills\\updatedSkillList.json").file().extractStringContent();
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<Map<String, Object>> list = jsonHandler.fromJson(extractStringContent);
		Set<String> collect = list.stream()
				.map(x -> new CcpJsonRepresentation(x))
				.filter(x -> x.getAsBoolean("gemini"))
				.filter(x -> x.getAsIntegerNumber("vagas") > 1)
				.map(x -> x.getAsString("word"))
				.collect(Collectors.toSet());
		
		for (String string : collect) {
			System.out.println(string);
		}
		System.out.println(collect.size());
	}
	static void loadSynonymsMatch() {
		String extractStringContent = new CcpStringDecorator("documentation\\skills\\updatedSkillList.json").file().extractStringContent();
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<Map<String, Object>> list = jsonHandler.fromJson(extractStringContent);
		List<CcpJsonRepresentation> SkillsGemini = list.stream()
				.map(x -> new CcpJsonRepresentation(x))
				.filter(x -> x.getAsBoolean("gemini"))
				.filter(x -> x.getAsIntegerNumber("vagas") >= 2)
				.collect(Collectors.toList());
		
		List<CcpJsonRepresentation> skillsNonGemini = list.stream()
				.map(x -> new CcpJsonRepresentation(x))
				.filter(x -> x.getAsBoolean("gemini") == false)
				.filter(x -> x.containsAllFields("somatoria"))
				.filter(x -> x.getAsIntegerNumber("somatoria") >= 2)
				.collect(Collectors.toList());
		
		CcpJsonRepresentation newSynonyms = CcpConstants.EMPTY_JSON;
		
		for (CcpJsonRepresentation skillNonGemini : skillsNonGemini) {
			for (CcpJsonRepresentation skillGemini : SkillsGemini) {
				boolean skip = isSynonym(skillGemini, skillNonGemini) == false;
				if(skip) {
					continue;
				}
				List<String> synonyms = skillNonGemini.getAsJsonList("synonyms").stream().map(x -> x.getAsString("skill")).collect(Collectors.toList());
				String newSynonym = skillGemini.getAsString("word");
				String word = skillNonGemini.getAsString("word");
				
				CcpJsonRepresentation skill = newSynonyms.getInnerJson(word);
				CcpJsonRepresentation modified = skill
						.addToList("newSynonyms", newSynonym)
						.put("synonyms", synonyms)
						;
				newSynonyms = newSynonyms.put(word, modified);
			}
		}
		new CcpStringDecorator("newSynonyms.json").file().reset().append(newSynonyms.toString());
	}
	
	static boolean isSynonym(String w1, String w2) {
		boolean notContains = w1.contains(w2)== false;
		if(notContains) {
			return false;
		}
		
		String[] split1 = w1.replace("-", " ").replace(".", " ").split(" ");
		String[] split2 = w2.replace("-", " ").replace(".", " ").split(" ");
		int k = 0;
		for (String s2 : split2) {
			String s1 = split1[k++];
			boolean mismatch = s1.equals(s2) == false;
			if(mismatch) {
				return false;
			}
		}
		return true;
	}
	
	static boolean isSynonym(CcpJsonRepresentation skillGemini, CcpJsonRepresentation skillNonGemini) {
		String newSkill = skillGemini.getAsString("word");
		if(newSkill.trim().length() < 3) {
			return false;
		}
		String word = skillNonGemini.getAsString("word");
		boolean c1 = isSynonym(word, newSkill);
		if(c1) {
			return true;
		}
		boolean c2 = isSynonym(newSkill, word);
		if(c2) {
			return true;
		}
		
		List<CcpJsonRepresentation> synonyms = skillNonGemini.getAsJsonList("synonyms");
		for (CcpJsonRepresentation syn : synonyms) {
			String synonym = syn.getAsString("skill");
			boolean c3 = isSynonym(newSkill, synonym);
			if(c3) {
				return true;
			}
			boolean c4 = isSynonym(synonym, newSkill);
			if(c4) {
				return true;
			}
		}
		return false;
	}
	
	static boolean isSynonym(CcpJsonRepresentation skillGemini, List<CcpJsonRepresentation> skillsNonGemini) {
        skillsNonGemini.stream().filter(skillNonGemini -> isSynonym(skillGemini, skillNonGemini));
		return false;
	}
	static void loadNewSkillsFromGemini() {
		List<String> vagas = new CcpStringDecorator("C:\\jn\\vagas\\vagas.txt").file().getLines().stream()
				.map(vaga -> sanitizeWord(vaga)).collect(Collectors.toList());

		String extractStringContent = new CcpStringDecorator("documentation\\skills\\skills.json").file()
				.extractStringContent();

		List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(extractStringContent).getAsJsonList("linhas");
		CcpFileDecorator file = new CcpStringDecorator("documentation\\skills\\classificacao.txt").file();
		List<String> lines = file.getLines();
		ArrayList<CcpJsonRepresentation> skillsCopy = new ArrayList<CcpJsonRepresentation>(skills);
		List<String> words = lines.stream().filter(x -> x.startsWith("(1)"))
				.map(x -> x.split("\\)")[1].split(":")[0].trim()).map(ranking -> filtrarSkill(skillsCopy, ranking))
				.map(x -> getWord(x)).filter(x -> x.trim().length() > 2).collect(Collectors.toList());
		;

		CcpTimeDecorator ctd = new CcpTimeDecorator();
		Set<String> allSynonyms = new HashSet<>();
		for (CcpJsonRepresentation skill : skills) {
			boolean gemini = skill.getAsBoolean("gemini");
			
			if(gemini) {
				continue;
			}
			Set<String> collect = skill.getAsJsonList("synonyms").stream().map(x -> x.getAsString("skill")).collect(Collectors.toSet());
			allSynonyms.addAll(collect);
		}
		for (String word : words) {
			long started = System.currentTimeMillis();
			skills = answerToGemini(
					"Using the information technology context I ask you all similar skills like '{skill}'? Answer only these skills (without explanations) and return these skill list in programming syntax list, eg: ['requiredSkill1', 'requiredSkill2'] without break line",
					word, "similar", vagas, skills, allSynonyms);
			skills = answerToGemini(
					"Using the information technology context I ask you all required skills it is needed indeed to know to have proficiency in '{skill}'? Answer only these skills (without explanations and without optional skills) and return these skill list in programming syntax list, eg: ['requiredSkill1', 'requiredSkill2'] without break line",
					word, "prerequisites", vagas, skills, allSynonyms);
			skills = answerToGemini(
					"Using the information technology context I ask you all similar skills like '{skill}'? Answer only these skills (without explanations) and return these skill list in programming syntax list, eg: ['requiredSkill1', 'requiredSkill2'] without break line",
					word, "similar", vagas, skills, allSynonyms);
			long finished = System.currentTimeMillis();
			long enlapsed = finished - started;
			long remainingTimeToWait = 15000 - enlapsed;
			ctd.sleep((int) remainingTimeToWait);
		}
	}
	
	static List<CcpJsonRepresentation> answerToGemini(String phrase, String skill, String jsonKeyToPut,  List<String> vagas, List<CcpJsonRepresentation> skills, Set<String> allSynonyms) {
		
		
		String answer = phrase.replace("{skill}", skill);
		
		String request = ("{\r\n"
				+ "    \"contents\": [\r\n"
				+ "        {\r\n"
				+ "            \"parts\": [\r\n"
				+ "                {\r\n"
				+ "                    \"text\": \"{answer} \""
				+ "                }\r\n"
				+ "            ]\r\n"
				+ "        }\r\n"
				+ "    ]\r\n"
				+ "}").replace("{answer}", answer)
				;
		System.out.println(skill);
		CcpHttpRequester ccpHttp = CcpDependencyInjection.getDependency(CcpHttpRequester.class);
		CcpHttpResponse executeHttpRequest = ccpHttp.executeHttpRequest("https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=AIzaSyAZ2ARz1sOqBk7V41_gUTh_qapRKw1vxdM", 
				"POST", 
				CcpConstants.EMPTY_JSON, 
				request, 
				200);
		
		
		CcpJsonRepresentation geminiResponse = executeHttpRequest.asSingleJson();

		List<CcpJsonRepresentation> candidates = geminiResponse.getAsJsonList("candidates");
		boolean invalidGeminiResponse = candidates.isEmpty();
		
		if(invalidGeminiResponse) {
			logGeminiResponse(request, geminiResponse, "invalidGeminiResponse");
			return skills;
		}
		
		CcpJsonRepresentation candidate = candidates.get(0);
		CcpJsonRepresentation content = candidate.getInnerJson("content");
		List<CcpJsonRepresentation> parts = content.getAsJsonList("parts");
		boolean missingParts = parts.isEmpty();
		if(missingParts) {
			logGeminiResponse(request, geminiResponse, "missingParts");
			return skills;
		}
		CcpJsonRepresentation part = parts.get(0);
		String text = part.getAsString("text");
		
		Set<String> oldSkills = skills.stream().map(x -> x.getAsString("word")).collect(Collectors.toSet());
		boolean skillNotFound = oldSkills.contains(skill) == false;

		if(skillNotFound) {
			logGeminiResponse(request, geminiResponse, "skillNotFound");
			return skills;
		}
		
		CcpJsonRepresentation skillToUpdate = skills.stream().filter(x -> x.getAsString("word").equals(skill)).findFirst().get();

		try {
			CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			List<String> skillsFromGemini = jsonHandler.fromJson(text);
			
			Set<String> onlyNewSkills = skillsFromGemini.stream()
					.map(x -> x.toUpperCase().trim())
					.filter(x -> allSynonyms.contains(x) == false)
					.filter(x -> oldSkills.contains(x) == false)
					.collect(Collectors.toSet())
					;
			
			List<CcpJsonRepresentation> newSkills = onlyNewSkills.stream().map(x -> getRelatorioDeUmaPalavra(x, vagas).put("gemini", true).renameField("skill", "word")).collect(Collectors.toList());
			skills.addAll(newSkills);
			List<CcpJsonRepresentation> items = skillToUpdate.getAsJsonList(jsonKeyToPut);
			items.addAll(newSkills);
			skillToUpdate = skillToUpdate.put(jsonKeyToPut, items);
		} catch (Exception e) {
			logGeminiResponse(request, geminiResponse, e.getClass().getName());
		}
		List<CcpJsonRepresentation> listWithoutSkill = skills.stream().filter(x -> x.getAsString("word").equals(skill) == false).collect(Collectors.toList());
		ArrayList<CcpJsonRepresentation> updatedSkillList = new ArrayList<CcpJsonRepresentation>(listWithoutSkill);
		updatedSkillList.add(skillToUpdate);
		
		new CcpStringDecorator("documentation\\skills\\updatedSkillList.json").file().append(updatedSkillList.toString());
		return updatedSkillList;
	}
	
	static void logGeminiResponse(String request, CcpJsonRepresentation geminiResponse, String content) {
		String asUgglyJson = CcpConstants.EMPTY_JSON
				.put("response", geminiResponse)
				.put("request", request)
				.asUgglyJson()
				;
		new CcpStringDecorator(content).file().append(asUgglyJson);
	}
	
	static long getPositionsCount(List<String> positions, String newSkill) {
		
		long count = positions.stream().map(x -> x.toUpperCase()).filter(x -> x.contains(newSkill)).count();
		
		return count;
	}
	
	static CcpJsonRepresentation filtrarSkill(List<CcpJsonRepresentation> skills, String ranking) {
		Optional<CcpJsonRepresentation> findFirst = skills.stream().filter(skill -> skillRankingMatches(ranking, skill)).findFirst();
		boolean notFound = findFirst.isPresent() == false;
	
		if(notFound) {
			return CcpConstants.EMPTY_JSON;
		}
		CcpJsonRepresentation json = findFirst.get();
		return json;
	}
	
	private static boolean skillRankingMatches(String ranking, CcpJsonRepresentation skill) {
		Integer asIntegerNumber = skill.getAsIntegerNumber("ranking");
		Integer valueOf = Integer.valueOf(ranking) + 1;
		boolean equals = asIntegerNumber.equals(valueOf);
		return equals;
	}
	static String getWord(CcpJsonRepresentation json) { 
		
		String word = json.getAsString("word");
		
		
		boolean sap = word.toUpperCase().startsWith("SAP");
		
		if(sap) {
			return word;
		}
		
		List<CcpJsonRepresentation> asJsonList = json.getAsJsonList("synonyms");
		boolean noSynonyms = asJsonList.isEmpty();
		
		if(noSynonyms) {
			return word;
		}
		
		List<CcpJsonRepresentation> synonyms = new ArrayList<>(asJsonList);
		CcpJsonRepresentation skill = json.getInnerJson("skill");
		synonyms.add(skill);
		
		synonyms.sort((s1, s2) -> ordenarSinonimos(s1, s2));
		CcpJsonRepresentation synonym = synonyms.get(0);
		String skillName = synonym.getAsString("skill");
		return skillName;
	}
	
	static int ordenarSinonimos(CcpJsonRepresentation s1, CcpJsonRepresentation s2) {
		
		if(s1.isEmpty()) {
			return 0;
		}
		
		if(s2.isEmpty()) {
			return 0;
		}
		
		Integer v1 = s1.getAsIntegerNumber("vagas");
		Integer v2 = s2.getAsIntegerNumber("vagas");
	
		int vagaDiff = v2 - v1;
		
		if(vagaDiff != 0) {
			return vagaDiff;
		}

		String w1 = s1.getAsString("skill");
		String w2 = s2.getAsString("skill");
		
		int length1 = w1.length();
		int length2 = w2.length();
		
		int lengthDiff = length2 - length1;
		
		if(lengthDiff != 0) {
			return lengthDiff;
		}
		
		int compareTo = w1.compareTo(w2);
		return compareTo;
	}
	
	static void levantarSkillsAvulsas(String... palavras) {
		String extractStringContent = new CcpStringDecorator("documentation\\skills\\skills.json").file().extractStringContent();
		List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(extractStringContent).getAsJsonList("linhas");
		for (CcpJsonRepresentation skill : skills) {
			boolean incorrectJson = skill.containsAllFields("somatoria") == false;
			if(incorrectJson) {
				continue;
			}
			Integer somatoria = skill.getAsIntegerNumber("somatoria");
			Integer ranking = skill.getAsIntegerNumber("ranking") - 1;
			String word = skill.getAsString("word");
			System.out.println(ranking + ": " + word + " = " + somatoria);
		}
	}
	static void criarArquivosFinais() {
		//		criarRelatoriosDosArquivos();
				String extractStringContent = new CcpStringDecorator("documentation\\skills\\classificacao\\skills.json").file().extractStringContent();
				List<CcpJsonRepresentation> skills = new CcpJsonRepresentation(extractStringContent).getAsJsonList("linhas");
				List<String> classificados = new CcpStringDecorator("documentation\\skills\\classificacao\\perguntar.txt").file().getLines();
				List<String> perguntasParaGemini = classificados.stream().filter(x -> x.trim().endsWith("(1)")).map(x -> Integer.valueOf(x.split(":")[0]))
				.map(x -> skills.get(x)).map(x -> {
					String word = x.getAsString("word");
					Set<String> set = x.getAsJsonList("synonyms").stream().map(y -> y.getAsString("skill")).collect(Collectors.toSet());
					ArrayList<String> arrayList = new ArrayList<String>(set);
					arrayList.add(word);
					return arrayList.toString().replace("[", "").replace("]", "");
				}).collect(Collectors.toList());
				
				
				CcpFileDecorator arquivoDePerguntasParaGemini = new CcpStringDecorator("documentation\\skills\\classificacao\\arquivoDePerguntasParaGemini.txt").file().reset();
					
				for (String perguntaParaGemini : perguntasParaGemini) {
					arquivoDePerguntasParaGemini.append(perguntaParaGemini);
				}
				
				List<CcpJsonRepresentation> skillsQueNaoSaoDeTecnologiaParaPopularTabelaDeSkills = classificados.stream().filter(x -> x.trim().endsWith("(1)") == false).map(x -> Integer.valueOf(x.split(":")[0]))
				.map(x -> skills.get(x)).collect(Collectors.toList());
				String extractStringContent2 = new CcpStringDecorator("documentation\\skills\\classificacao\\profissoes.json").file().extractStringContent();
				List<CcpJsonRepresentation> profissoesParaPopularTabelaDeSkills = new CcpJsonRepresentation(extractStringContent2).getAsJsonList("linhas").subList(0, 151);
				
				ArrayList<CcpJsonRepresentation> primeirosRegistrosDaTabelaDeSkills = new ArrayList<>(profissoesParaPopularTabelaDeSkills);
				primeirosRegistrosDaTabelaDeSkills.addAll(skillsQueNaoSaoDeTecnologiaParaPopularTabelaDeSkills);
				
				CcpFileDecorator arquivoComPrimeirosRegistrosDaTabelaDeSkills = new CcpStringDecorator("documentation\\skills\\classificacao\\listSkills.json").file().reset();
				arquivoComPrimeirosRegistrosDaTabelaDeSkills.append(primeirosRegistrosDaTabelaDeSkills.toString());
	}
	

	static void criarRelatoriosDosArquivos() {
		List<String> vagas = new CcpStringDecorator("C:\\jn\\vagas\\vagas.txt")
				.file().getLines().stream().map(vaga -> sanitizeWord(vaga)).collect(Collectors.toList());
		
		criarRelatorioDeUmArquivo("documentation\\skills\\classificacao\\gemini.txt", "documentation\\skills\\classificacao\\skills.json", vagas);
		criarRelatorioDeUmArquivo("documentation\\skills\\classificacao\\profissoes.txt", "documentation\\skills\\classificacao\\profissoes.json", vagas);
	}

	static void criarRelatorioDeUmArquivo(String arquivoParaLer, String arquivoParaEscrever, List<String> vagas) {
	
		CcpFileDecorator fileToRead = new CcpStringDecorator(arquivoParaLer).file();
		
		List<String> lines2 = fileToRead.getLines();
		
		Collection<String> lines = new HashSet<>(lines2);
		
		List<CcpJsonRepresentation> relatorios = lines.stream().map(line -> getRelatorioDeUmaLinha(line, vagas)).collect(Collectors.toList());
		
		long mediaGeral = 0;
		long totalGeralDeSkills = 0;
		long totalGeralDeVagasEncontradas = 0;
		int k = 1;
		for (CcpJsonRepresentation relatorio : relatorios) {
			System.out.println("lendo a linha " + k++);
			boolean isIncompleteProcess = relatorio.containsAllFields("somatoria", "listSize") == false;
			if(isIncompleteProcess) {
				continue;
			}
			Integer somatoria = relatorio.getAsIntegerNumber("somatoria");
			Integer listSize = relatorio.getAsIntegerNumber("listSize");
			totalGeralDeVagasEncontradas += somatoria;
			totalGeralDeSkills += listSize;
		}
		mediaGeral = totalGeralDeVagasEncontradas / totalGeralDeSkills;
		relatorios.sort((a, b) -> ordernarLinhasNoArquivo(a, b));
		
		int ranking = 1;
		List<CcpJsonRepresentation> linhas = new ArrayList<>();
		
		for (CcpJsonRepresentation relatorio : relatorios) {
			String word = relatorio.getValueFromPath("", "skill", "skill"); 
			CcpJsonRepresentation putAll = CcpConstants.EMPTY_JSON
					.put("ranking", ranking++)
					.put("word", word)
					.putAll(relatorio)
					;
			linhas.add(putAll);
		}
		
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
		.put("linhas", linhas)
		.put("mediaGeral", mediaGeral)
		.put("totalGeralDeSkills", totalGeralDeSkills)
		.put("totalGeralDeVagasEncontradas", totalGeralDeVagasEncontradas);
		
		CcpFileDecorator fileToWrite = new CcpStringDecorator(arquivoParaEscrever).file().reset();
		fileToWrite.append(put.toString());
	}
	
	static CcpJsonRepresentation getRelatorioDeUmaLinha(String linha, List<String> vagas) {
		String[] split = linha.split(",");
		List<String> asList = Arrays.asList(split);
		Set<String> collect = asList.stream()
		.map(x -> sanitizeWord(x)).filter(x -> x.trim().length() >= 2)
		.collect(Collectors.toSet());
		
		boolean hasNoWords = collect.isEmpty();
		if(hasNoWords) {
			return CcpConstants.EMPTY_JSON;
		}
		
		List<CcpJsonRepresentation> jsons = collect.stream().map(skill -> getRelatorioDeUmaPalavra(skill, vagas)).collect(Collectors.toList());
		
		int somatoria = 0;
		for (CcpJsonRepresentation json : jsons) {
			Integer count = json.getAsIntegerNumber("vagas");
			somatoria += count;
		}
		int listSize = jsons.size();
		int media = somatoria / listSize;
		ArrayList<CcpJsonRepresentation> synonyms = new ArrayList<>(jsons);
		synonyms.sort((a, b) -> ordenarSkillsNaLinha(a, b));
		CcpJsonRepresentation skill = synonyms.remove(0);
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("somatoria", somatoria)
				.put("listSize", listSize)
				.put("media", media)
				.put("skill", skill)
				.put("synonyms", synonyms)
				;
		
		return put;
	}
	
	static int ordernarLinhasNoArquivo(CcpJsonRepresentation a, CcpJsonRepresentation b) {
		boolean m1 = a.containsAllFields("somatoria", "listSize", "media", "skill") == false;
		if(m1) {
			return 0;
		}
		
		boolean m2 = b.containsAllFields("somatoria", "listSize", "media", "skill") == false;
		if(m2) {
			return 0;
		}
		
		Integer somatoria1 = a.getAsIntegerNumber("somatoria");
		Integer somatoria2 = b.getAsIntegerNumber("somatoria");
		
		if(somatoria1 != somatoria2) {
			int subtracao = somatoria2 - somatoria1;
			return subtracao;
		}

		Long count1 = a.getValueFromPath(0L, "skill", "vagas");
		Long count2 = b.getValueFromPath(0l, "skill", "vagas");
		
		if(count2 != count1) {
			int subtracao = (int)(count2 - count1);
			return subtracao;
		}

		Integer media1 = a.getAsIntegerNumber("media");
		Integer media2 = b.getAsIntegerNumber("media");
		
		if(media1 != media2) {
			int subtracao = media2 - media1;
			return subtracao;
		}

		String skill1 = a.getValueFromPath("", "skill", "skill");
		String skill2 = b.getValueFromPath("", "skill", "skill");
		
		int compareTo = skill1.compareTo(skill2);
		return compareTo;
	}
	
	static int ordenarSkillsNaLinha(CcpJsonRepresentation a, CcpJsonRepresentation b) {
		
		Integer count1 = a.getAsIntegerNumber("vagas");
		Integer count2 = b.getAsIntegerNumber("vagas");
		
		if(count1 != count2) {
			int sub = count2 - count1;
			return sub;
		}
		
		String skill1 = a.getAsString("skill");
		String skill2 = b.getAsString("skill");
		int compareTo = skill1.compareTo(skill2);
		return compareTo;
	}
	
	static CcpJsonRepresentation getRelatorioDeUmaPalavra(String palavra, List<String> vagas) {
		
		long count = vagas.stream().filter(vaga -> contains(vaga, palavra)).count();
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("skill", palavra).put("vagas", count);
	
		return put;
	}
	
	static void gravarArquivo(
			List<String> lines,
			String nomeDoArquivoOndeGravar,
			String sigla
			) {
			
			List<String> palavrasClassificadas = lines.stream().filter(line -> line.startsWith(sigla)).map(line -> new CcpJsonRepresentation(line.substring(1)).getAsString("skill")).collect(Collectors.toList());
			TreeSet<String> classifiedWords = new TreeSet<>(palavrasClassificadas);
			CcpFileDecorator reset = new CcpStringDecorator(nomeDoArquivoOndeGravar).file();
			List<String> palavrasQueJaEstavam = reset.getLines();
			LinkedHashSet<String> set = new LinkedHashSet<>();
			for (String string : palavrasQueJaEstavam) {
				String[] split = string.split(",");
				for (String word : split) {
					set.add(word.trim().toUpperCase());
				}
			}
			int k = 1;
			for (String word : classifiedWords) {
				String upperCase = word.trim().toUpperCase();
				boolean add = set.add(upperCase);
				if(add) {
					reset.append(upperCase);
					System.out.println(k++);
				}
			}
			
			
	}

	static void levantarSkills() {
		Set<String> novasPalavras = new HashSet<>();
		
		Set<String> sinonimos_frases = readSkills("documentation\\skills\\sinonimos_frases.txt");
		
		Set<String> sinonimos = readSkills("documentation\\skills\\sinonimos.txt");

		Set<String> levantadas = read("documentation\\skills\\levantadas.json");


		Set<String> antigos_termos = readTermosEstudaveis("documentation\\skills\\antigos_termos.json");

		Set<String> profissoes = readSkills("documentation\\skills\\profissoes.txt");

		novasPalavras.addAll(sinonimos_frases);
		novasPalavras.addAll(antigos_termos);
		novasPalavras.addAll(levantadas);
		novasPalavras.addAll(profissoes);
		novasPalavras.addAll(sinonimos);
		System.out.println(novasPalavras.size());
		Set<String> listaNegra = readSkills("documentation\\skills\\listaNegra.txt");
		Set<String> readFolder = readFolder();
		listaNegra.addAll(readFolder);
		for (String string : listaNegra) {
			novasPalavras.remove(string);
		}
		System.out.println(novasPalavras.size());
		List<String> vagas = new CcpStringDecorator("documentation\\skills\\vagas.txt")
				.file().getLines().stream().map(vaga -> sanitizeWord(vaga)).collect(Collectors.toList());
		
		List<CcpJsonRepresentation> ocorrencias = new ArrayList<>();
		
		int k = 0;
		
		for (String skill : novasPalavras) {
			long count = vagas.stream().filter(vaga -> contains(vaga, skill)).count();
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("skill", skill).put("vagas", count);
			ocorrencias.add(put);
			System.out.println(k++ + " = " + put);
		}
		ocorrencias = ocorrencias.stream().filter(x -> x.getAsIntegerNumber("vagas") >= 2).collect(Collectors.toList());
		List<String> skills = ocorrencias.stream().map(x -> x.getAsString("skill")).collect(Collectors.toList());
		Set<String> set = new HashSet<>();
		for (CcpJsonRepresentation ocorrencia : ocorrencias) {
			String asString = ocorrencia.getAsString("skill");
			for (String skill : skills) {
				boolean equalsIgnoreCase = asString.equalsIgnoreCase(skill);
				
				if(equalsIgnoreCase) {
					continue;
				}
				
				String trim2 = skill.replace(" ", "");
				
				boolean differentPhrases = trim2.contains(asString) == false;
				
				if(differentPhrases) {
					continue;
				}
				
				List<String> asList = Arrays.asList(asString.split(" "));
				List<String> asList2 = Arrays.asList(skill.split(" "));
				boolean notContains = asList2.containsAll(asList) == false;
				if(notContains) {
					continue;
				}
				
				set.add(asString);
				break;
			}
			
		}	
		ocorrencias.sort((a, b) -> sort(a, b));
		CcpFileDecorator estatisticas = new CcpStringDecorator("documentation\\skills\\estatisticas.txt").file().reset();
		
		for (CcpJsonRepresentation ocorrencia : ocorrencias) {
			String skill = ocorrencia.getAsString("skill");
			boolean contains = set.contains(skill);
			if(contains) {
//				continue;
			}
			String asUgglyJson = ocorrencia.asUgglyJson();
			estatisticas.append(asUgglyJson);
		}
	}
	static boolean contains(String text, String phrase) {
		boolean notContains = text.contains(phrase) == false;
		if(notContains) {
			return false;
		}
		
		List<String> asList2 = Arrays.asList(text.split(" "));
		List<String> asList = Arrays.asList(phrase.split(" "));
		boolean containsAll = asList2.containsAll(asList);
		return containsAll;
	}
	private static int sort(CcpJsonRepresentation a, CcpJsonRepresentation b) {
		int i = a.getAsString("skill").compareTo(b.getAsString("skill"));
		return i;
	}
	

	static Set<String> readSkills(String path){
		Set<String> result = new HashSet<String>();
		new CcpStringDecorator(path).file().readLines((line, x) -> {
			Set<String> upperCase = getAllSkills(line);
			result.addAll(upperCase);
		});
		
		System.out.println(result.size());
		return result;
	}
	
	static Set<String> readTermosEstudaveis(String path) {
		Set<String> result = new HashSet<String>();
		String extractStringContent = new CcpStringDecorator(path).file().extractStringContent();
		CcpJsonRepresentation data = new CcpJsonRepresentation(extractStringContent);
		List<CcpJsonRepresentation> collect = data.getInnerJson("hits").getAsJsonList("hits");
		for (CcpJsonRepresentation json : collect) {
			String line = json.getAsString("_id");
			Set<String> upperCase = getAllSkills(line);
			result.addAll(upperCase);
		}
		System.out.println(result.size());
		return result;
	
	}	
	static Set<String> read(String path) {
		String extractStringContent = new CcpStringDecorator(path).file().extractStringContent();
		List<Map<String, Object>> list = CcpDependencyInjection.getDependency(CcpJsonHandler.class).fromJson(extractStringContent);
		List<CcpJsonRepresentation> collect = list.stream().map(x -> new CcpJsonRepresentation(x)).collect(Collectors.toList());
		Set<String> result = new HashSet<String>();
		for (CcpJsonRepresentation json : collect) {
			String line = json.getAsString("nome");
			Set<String> upperCase = getAllSkills(line);
			result.addAll(upperCase);
		}
		System.out.println(result.size());
		return result;
	}
	
	static Set<String> getAllSkills(String word) {
		String upperCase = sanitizeWord(word);
		String[] split = upperCase.replace("=>", ",").replace("(", ",").replace("/", ",").replace(")", "").split(",");
		Set<String> asList = Arrays.asList(split).stream().map(x -> sanitizeWord(x))
				.filter(x -> x.length() > 2)
				.collect(Collectors.toSet());
		
		return asList;
	}


	private static String sanitizeWord(String word) {
		CcpTextDecorator text = new CcpStringDecorator(word).text();
		String upperCase = text.stripAccents().content.trim().toUpperCase()
				.replace("+", "")
				;
			return upperCase;
	}
	
	static Set<String> readFolder(){
		Set<String> result = new HashSet<String>();
		
		new CcpStringDecorator("documentation\\skills\\classificacao")
		.folder().readFiles(file -> {
			file.readLines((line, number) -> {
				try {
					
					CcpJsonRepresentation json = new CcpJsonRepresentation(line);
					String skill = json.getAsString("skill");
					result.add(skill);
				} catch (Exception e) {
				}
			});
		});;
		
		System.out.println(result.size());
		return result;

	}
}
