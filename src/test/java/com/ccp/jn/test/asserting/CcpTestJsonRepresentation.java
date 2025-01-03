package com.ccp.jn.test.asserting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;

public class CcpTestJsonRepresentation {
	{
		CcpDependencyInjection.loadAllDependencies(new CcpGsonJsonHandler());	
	}

	//	@Test(expected = NullPointerException.class)
	@Test
	public void addToItemTest() {
		//		CcpJsonRepresentation pessoa = CcpConstants.EMPTY_JSON
		//		.addToItem("pai", "nome", "Paulo")
		//		.addToItem("pai", "idade", 55)
		//		.put("nome", "Lucas")
		//		.put("idade", 29)
		//		;

		CcpJsonRepresentation pessoa = new CcpStringDecorator("testes/ccpJsonRepresentation/addToItem.json").file().asSingleJson();

		System.out.println(pessoa);
		assertTrue(pessoa.containsAllFields("pai","nome","idade"));
		CcpJsonRepresentation pai = pessoa.getInnerJson("pai");
		System.out.println(pai);
		assertTrue(pai.containsAllFields("nome","idade"));
		String nomeDoPai = pessoa.getValueFromPath("", "pai","nome");
		assertTrue("Paulo".equals(nomeDoPai));

		//		String s = null;
		//		s.toString();



	}

	@Test
	public void getAsLongNumberTest() {
		String valorLong = "{'valor': 1}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(valorLong);
		Long asLongNumber = json.getAsLongNumber("valor");
		assertTrue(asLongNumber instanceof Long);
	}

	@Test (expected = RuntimeException.class)
	public void jsonDeSyntaxIncorretaParaConstrutor() {
		//Jason iválido
		CcpJsonRepresentation objeto = new CcpJsonRepresentation("Testando getAsLongNumberTest()");
		String texto = "Um texto qualquer";

		assertTrue(objeto.getAsLongNumber(texto) instanceof Long);
		assertTrue(objeto.getAsLongNumber(texto) instanceof Object);
	}

	@Test (expected = RuntimeException.class)
	public void obterPropriedadeQueNaoExiste() {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON;
		json.getAsLongNumber("minhaPropriedadeJson");
		//		CcpJsonRepresentation.getMap(null);
	}

	@Test
	public void getAsIntegerNumberTest() {
		String inteiro = "{'valor':1}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(inteiro);
		assertTrue(json.getAsIntegerNumber("valor") == 1);


	}

	@Test (expected = RuntimeException.class)
	public void getAsIntegerNumberErrorTest() {
		//		//Json Inválido
		//		String inteiro = "EuNãoSouUmJson";
		//		CcpJsonRepresentation json = new CcpJsonRepresentation(inteiro);
		//		assertTrue(json.getAsIntegerNumber("EuNãoSouUmJson") instanceof Integer);
		//		
		//		//Parâmetro não é um inteiro
		String x = "{'nome':'Fulano da Silva'}";
		CcpJsonRepresentation json2 = new CcpJsonRepresentation(x);
		assertTrue(json2.getAsIntegerNumber("nome") instanceof Integer);

		//Parâmetro estoura a capacidade de integer
		//		String explodeCoracao = "{'valor':"+Math.pow(2, 32)+"}";
		//		CcpJsonRepresentation json3 = new CcpJsonRepresentation(explodeCoracao);
		//		assertFalse(json3.getAsIntegerNumber("valor") instanceof Integer);
	}

	@Test
	public void getAsDoubleNumberTest() {
		String pontoFlutuante = "{'valorDouble':8.67}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(pontoFlutuante);
		assertTrue(json.getAsDoubleNumber("valorDouble") instanceof Double);
	}

	@Test (expected = RuntimeException.class)
	public void getAsDoubleNumberErrorTest() {

		//JASON NÃO RETORNA DOUBLE
		String pontoFlutuante = "{'valorQueNaoEhDouble':'Qualquer coisa'}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(pontoFlutuante);
		assertTrue(json.getAsDoubleNumber("valorQueNaoEhDouble") instanceof Double);

		//JSON INVÁLIDO
		String x = "valorQueNaoEhDouble':9.30";
		CcpJsonRepresentation json2 = new CcpJsonRepresentation(x);
		assert(json2.getAsDoubleNumber(x) instanceof Double);
	}

	@Test
	public void getAsBooleanTest() {
		String x = "{'valor': true}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(x);
		assertTrue(json.getAsBoolean("valor"));
	}

	@Test
	public void getAsBooleanErrorTest() {
		//		//JSON INVÁLIDO
		//		String x = "true";
		//		CcpJsonRepresentation json = new CcpJsonRepresentation(x);
		//		assertTrue(json.getAsBoolean(x));

		//VALOR JSON NÃO É BOOLEANO
		String y = "{'valor': 'abacaxi'}";
		CcpJsonRepresentation json2 = new CcpJsonRepresentation(y);
		assertFalse(json2.getAsBoolean("valor"));
	}

	/**Verificar funcionamento do decorator*/
	@Test
	public void putFilledTemplateTest() {
		String x = "{'nome':'Alice'}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(x)
				.put("saudacoes", "Olá, {nome}")
				.put("nome", "Alice");

		CcpJsonRepresentation jsonAtualizado = json.putFilledTemplate("saudacoes", "saudacoesAtualizadas");

		System.out.println(jsonAtualizado);
	}

	@Test
	public void getAsTextDecoratorTest() {
		String x = "{'nome':'Lucas'}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(x);
		System.out.println(json.getAsTextDecorator("nome"));

		assertTrue(json.getAsTextDecorator("nome") instanceof CcpTextDecorator);
	}

	@Test
	public void getAsStringTest() {

		int numero = 1;
		String x = "{'valor':"+numero+"}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(x);
		System.out.println(json.getAsString("valor"));

		assertTrue(json.getAsString("valor") instanceof String);
	}

	@Test
	public void getJsonPieceTest() {
		String json = "{'nome':'João'}";
		CcpJsonRepresentation objJson = new CcpJsonRepresentation(json);
		System.out.println(objJson.getJsonPiece("nome"));
		/**Esse método não trata erros, apenas retorna um json vazio
		 * caso uma chave inválida seja passada*/
	}

	@Test
	public void getJsonPieceVarargsTest() {
		String json = "{"
				+ "'nome':'João',"
				+ "'idade':40,"
				+ "'altura':1.85"
				+ "}";
		CcpJsonRepresentation objJson = new CcpJsonRepresentation(json);
		System.out.println(objJson.getJsonPiece("nome","idade","altura"));
		/**Esse método não trata erros, apenas retorna um json vazio
		 * caso uma chave inválida seja passada*/
	}

	@Test
	public void getOrDefaultTest() {
		String json = "{'veiculo':'carro'}";
		CcpJsonRepresentation objJson = new CcpJsonRepresentation(json);
		String resultado = objJson.getOrDefault("veiculo", "default");

		//assertEquals("caminhão", resultado);
		assertEquals("carro", resultado);
	}


	@Test (expected = RuntimeException.class)
	public void getOrDefaultErroTest() {
		String json = "{'veiculo':25}";
		CcpJsonRepresentation objJson = new CcpJsonRepresentation("");
		String resultado = objJson.getOrDefault("veiculo", "default");

		assertEquals("default", resultado);
	}

	//getgetAsUglyJsonTest -  observar caractere não convertido
	@Test
	public void getgetAsUglyJsonTest() {
		String filha = "{"
				+ "'nome':'Márcia',"
				+ "'idade':34"
				+ "}";

		String mae = "{"
				+ "'nome':'Maria',"
				+ "'idade': 58',"
				+ "'filha': "+filha
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(mae);
		System.out.println("Ugly json = "+ json.asUgglyJson());
	}

	//getAsPrettyJson - observar caractere não convertido
	@Test
	public void getgetAsPrettyJsonTest() {
		String filha = "{"
				+ "'nome':'Márcia',"
				+ "'idade':34"
				+ "}";

		String mae = "{"
				+ "'nome':'Maria',"
				+ "'idade': 58',"
				+ "'filha': "+filha
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(mae);
		System.out.println("Pretty Json = "+json.asPrettyJson());
	}

	@Test
	public void toStringTest() {
		String filha = "{"
				+ "'nome':'Márcia',"
				+ "'idade':34"
				+ "}";

		String mae = "{"
				+ "'nome':'Maria',"
				+ "'idade': 58',"
				+ "'filha': "+filha
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(mae);
		System.out.println("toString = "+ json.toString());
	}

	@Test
	public void fieldSetTest() {
		String filha = "{"
				+ "'nome':'Márcia',"
				+ "'idade':34"
				+ "}";

		String mae = "{"
				+ "'nome':'Maria',"
				+ "'idade': 58',"
				+ "'filha': "+filha
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(mae);
		System.out.println("Retorno de fieldSetTest() = "+ json.fieldSet());
	}

	@Test
	public void putTest() {
		String valor = "{'frutas':''}";
		String frutas = "{'banana','laranja','maçã','tangerina','uva'}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(valor);

		System.out.println("Retorno putTest()= "+json.put("frutas", frutas));

	}

	@Test
	public void putMultiTest() {

		CcpJsonRepresentation pessoa1 = new CcpJsonRepresentation("{'nome':'João','idade':49}");
		CcpJsonRepresentation pessoa2 = new CcpJsonRepresentation("{'nome':'Maria','idade':51}");
		CcpJsonRepresentation pessoa3 = new CcpJsonRepresentation("{'nome':'José','idade':11}");

		List<CcpJsonRepresentation> listaDePessoas = Arrays.asList(pessoa1, pessoa2, pessoa3);

		// Criação de um objeto JSON vazio para receber o array
		CcpJsonRepresentation json = new CcpJsonRepresentation("{}");

		CcpJsonRepresentation resultado = json.put("arrayJson", listaDePessoas);

		// Exibe o resultado final
		System.out.println("putMultiTest() = "+resultado);
	}

	@Test
	public void duplicateValueFromFieldTest() {
		String variavel = "{"
				+ "'nome':'Pedro',"
				+ "'nome_copiado':''"
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(variavel);

		System.out.println("duplicateValueFromFieldTest() = "+
				json.duplicateValueFromField("nome","nome_copiado"));
	}

	@Test
	//Se o campo não existir ele cria e copia o valor para ele
	public void duplicateValueFromFieldErrorTest() {
		String variavel = "{"
				+ "'nome':'Pedro',"
				+ "'nome_copiado':''"
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(variavel);

		System.out.println("duplicateValueFromFieldTestError() = "+
				json.duplicateValueFromField("nome","nome_da_vó"));
	}

	@Test
	public void renameFieldTest() {
		String pessoa = ""
				+ "    	   { "
				+ "        'nome': 'Alexandre Thomas Figueiredo', "
				+ "        'idade': 19, "
				+ "        'cpf': '09051710984', "
				+ "        'rg': '237564956', "
				+ "        'data_nasc': '01/05/2005', "
				+ "        'sexo': 'Masculino', "
				+ "        'signo': 'Touro', "
				+ "        'mae': 'Melissa Tatiane', "
				+ "        'pai': 'Yuri Murilo Figueiredo', "
				+ "        'email': 'alexandrethomasfigueiredo@ferplast.com.br', "
				+ "        'senha': 'PUjKB9KD9f', "
				+ "        'cep': '84185970', "
				+ "        'endereco': 'Rodovia do Cerne km 90', "
				+ "        'numero': 177, "
				+ "        'bairro': 'Centro', "
				+ "        'cidade': 'Abapã', "
				+ "        'estado': 'PR', "
				+ "        'telefone_fixo': '4235499785', "
				+ "        'celular': '42995209139', "
				+ "        'altura': '1,82', "
				+ "        'peso': 108, "
				+ "        'tipo_sanguineo': 'AB+', "
				+ "        'cor': 'vermelho' "
				+ "    } ";

		//Se o campo não existir ele ignora
		CcpJsonRepresentation json = new CcpJsonRepresentation(pessoa);
		System.out.println("renameFieldTest() = "+json.renameField("cor","carro"));

	}

	@Test
	public void removeFieldTest() {
		String pessoa = ""
				+ "    	   { "
				+ "        'nome': 'Alexandre Thomas Figueiredo', "
				+ "        'idade': 19, "
				+ "        'cpf': '09051710984', "
				+ "        'rg': '237564956', "
				+ "        'data_nasc': '01/05/2005', "
				+ "        'sexo': 'Masculino', "
				+ "        'signo': 'Touro', "
				+ "        'mae': 'Melissa Tatiane', "
				+ "        'pai': 'Yuri Murilo Figueiredo', "
				+ "        'email': 'alexandrethomasfigueiredo@ferplast.com.br', "
				+ "        'senha': 'PUjKB9KD9f', "
				+ "        'cep': '84185970', "
				+ "        'endereco': 'Rodovia do Cerne km 90', "
				+ "        'numero': 177, "
				+ "        'bairro': 'Centro', "
				+ "        'cidade': 'Abapã', "
				+ "        'estado': 'PR', "
				+ "        'telefone_fixo': '4235499785', "
				+ "        'celular': '42995209139', "
				+ "        'altura': '1,82', "
				+ "        'peso': 108, "
				+ "        'tipo_sanguineo': 'AB+', "
				+ "        'cor': 'vermelho' "
				+ "    } ";
		
		//SE O CAMPO NÃO EXISTIR ELE IGNORA
		CcpJsonRepresentation json = new CcpJsonRepresentation(pessoa);
		System.out.println("removeFieldTest() = "+json.removeField("tipo_sanguineo"));
	}

	@Test
	public void removeFieldsTest() {
		String pessoa = ""
				+ "    	   { "
				+ "        'nome': 'Alexandre Thomas Figueiredo', "
				+ "        'idade': 19, "
				+ "        'cpf': '09051710984', "
				+ "        'rg': '237564956', "
				+ "        'data_nasc': '01/05/2005', "
				+ "        'sexo': 'Masculino', "
				+ "        'signo': 'Touro', "
				+ "        'mae': 'Melissa Tatiane', "
				+ "        'pai': 'Yuri Murilo Figueiredo', "
				+ "        'email': 'alexandrethomasfigueiredo@ferplast.com.br', "
				+ "        'senha': 'PUjKB9KD9f', "
				+ "        'cep': '84185970', "
				+ "        'endereco': 'Rodovia do Cerne km 90', "
				+ "        'numero': 177, "
				+ "        'bairro': 'Centro', "
				+ "        'cidade': 'Abapã', "
				+ "        'estado': 'PR', "
				+ "        'telefone_fixo': '4235499785', "
				+ "        'celular': '42995209139', "
				+ "        'altura': '1,82', "
				+ "        'peso': 108, "
				+ "        'tipo_sanguineo': 'AB+', "
				+ "        'cor': 'vermelho' "
				+ "    } ";
		
		//SE O CAMPO NÃO EXISTIR ELE IGNORA
		CcpJsonRepresentation json = new CcpJsonRepresentation(pessoa);
		System.out.println("removeFieldsTest() = "+
				json.removeFields(
						"tipo_sanguineo",
						"cor",
						"cpf"
						));
	}
	
	@Test
	public void getContentTest() {
		String pessoa = "{'nome':'fulano','idade':30}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(pessoa);
		System.out.println("getContentTest() = "+json.getContent()+"\n");
	}
	
	@Test
	public void copyTest() {
		String pessoa = "{'nome':'fulano','idade':30}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(pessoa);
		CcpJsonRepresentation jsonCopia = json.copy();
		
		assertEquals(json, jsonCopia);
	}
	
	@Test
	public void getInnerJsonFromPathTest() {
	    String pessoa = ""
	        + "       { "
	        + "        'nome': 'Alexandre Thomas Figueiredo', "
	        + "        'idade': 19, "
	        + "        'cpf': '09051710984', "
	        + "        'rg': '237564956', "
	        + "        'data_nasc': '01/05/2005', "
	        + "        'sexo': 'Masculino', "
	        + "        'signo': 'Touro', "
	        + "        'mae': 'Melissa Tatiane', "
	        + "        'pai': 'Yuri Murilo Figueiredo', "
	        + "        'email': 'alexandrethomasfigueiredo@ferplast.com.br', "
	        + "        'senha': 'PUjKB9KD9f', "
	        + "        'endereco': { "
	        + "            'rua': 'Rodovia do Cerne km 90', "
	        + "            'numero': 177, "
	        + "            'bairro': 'Centro', "
	        + "            'cidade': 'Abapã', "
	        + "            'estado': 'PR', "
	        + "            'cep': '84185970' "
	        + "        }, "
	        + "        'telefone_fixo': '4235499785', "
	        + "        'celular': '42995209139', "
	        + "        'altura': '1,82', "
	        + "        'peso': 108, "
	        + "        'tipo_sanguineo': 'AB+', "
	        + "        'cor': 'vermelho' "
	        + "    } ";
	    
	    CcpJsonRepresentation json = new CcpJsonRepresentation(pessoa);
	    CcpJsonRepresentation enderecoDoJson = json.getInnerJsonFromPath("endereco");
	    
	    System.out.println("getInnerJsonFromPathTest() " + enderecoDoJson);
	}
	
	@Test (expected = ClassCastException.class)
	public void getInnerJsonFromPathTestError() {
				String pessoa = ""
				+ "    	   { "
				+ "        'nome': 'Alexandre Thomas Figueiredo', "
				+ "        'idade': 19, "
				+ "        'cpf': '09051710984', "
				+ "        'rg': '237564956', "
				+ "        'data_nasc': '01/05/2005', "
				+ "        'sexo': 'Masculino', "
				+ "        'signo': 'Touro', "
				+ "        'mae': 'Melissa Tatiane', "
				+ "        'pai': 'Yuri Murilo Figueiredo', "
				+ "        'email': 'alexandrethomasfigueiredo@ferplast.com.br', "
				+ "        'senha': 'PUjKB9KD9f', "
				+ "        'cep': '84185970', "
				+ "        'endereco': 'Rodovia do Cerne km 90', "
				+ "        'numero': 177, "
				+ "        'bairro': 'Centro', "
				+ "        'cidade': 'Abapã', "
				+ "        'estado': 'PR', "
				+ "        'telefone_fixo': '4235499785', "
				+ "        'celular': '42995209139', "
				+ "        'altura': '1,82', "
				+ "        'peso': 108, "
				+ "        'tipo_sanguineo': 'AB+', "
				+ "        'cor': 'vermelho' "
				+ "    } ";
		
		 CcpJsonRepresentation json = new CcpJsonRepresentation(pessoa);
		 CcpJsonRepresentation enderecoDoJson = json.getInnerJsonFromPath("endereco");
		    
		 System.out.println("getInnerJsonFromPathTestError() " + enderecoDoJson);
	
	}
	
	@Test
	//Esse método está retornando o default ou o próprio path ao invés do value
	public void getValueFromPathTest() {
		String cadastro =  ""
				+ "{ "
				+ "  'nome': 'João', "
				+ "  'idade': 30, "
				+ "  'endereço': { "
				+ "    'rua': 'Av. Paulista', "
				+ "    'cidade': 'São Paulo', "
				+ "    'estado': 'SP' "
				+ "  }, "
				+ "  'contatos': [ "
				+ "    { "
				+ "      'tipo': 'telefone', "
				+ "      'numero': '1234-5678' "
				+ "    }, "
				+ "    { "
				+ "      'tipo': 'email', "
				+ "      'endereco': 'joao@example.com' "
				+ "    } "
				+ "  ] "
				+ "}";
		
		CcpJsonRepresentation addToItem = CcpOtherConstants.EMPTY_JSON.addToItem("filho", "pai", "abacaxi");
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(cadastro);
		String valueFromPath = addToItem.getValueFromPath("-","filho","pai");
		System.out.println("getValueFromPathTest() = "+ valueFromPath);
	}
	
	@Test
	public void getAsJsonListTest() {
		String dados = "{"
			    + "	 'nomes': ["
			    + "  {'nome': 'Alice Monteiro'}, "
			    + "  {'nome': 'Bruno Silva'}, "
			    + "  {'nome': 'Carla Souza'}, "
			    + "  {'nome': 'Diego Almeida'}, "
			    + "  {'nome': 'Eduarda Ferreira'}, "
			    + "  {'nome': 'Felipe Costa'}, "
			    + "  {'nome': 'Gabriela Santos'}, "
			    + "  {'nome': 'Hugo Pereira'}, "
			    + "  {'nome': 'Isabela Rocha'}, "
			    + "  {'nome': 'João Oliveira'}, "
			    + "  {'nome': 'Lara Ramos'}"
			    + "]"
			    + "}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(dados);
		System.out.println("getAsJsonListTest() = "+json.getAsJsonList("nomes"));
	}
	
	@Test
	/*Aparentemente a classe CcpCollectionDecorator precisa de um toString()*/
	public void getAsCollectionDecoratorTest() {
		String dados = "{"
			    + "	 'nomes': ["
			    + "  {'nome': 'Alice Monteiro'}, "
			    + "  {'nome': 'Bruno Silva'}, "
			    + "  {'nome': 'Carla Souza'}, "
			    + "  {'nome': 'Diego Almeida'}, "
			    + "  {'nome': 'Eduarda Ferreira'}, "
			    + "  {'nome': 'Felipe Costa'}, "
			    + "  {'nome': 'Gabriela Santos'}, "
			    + "  {'nome': 'Hugo Pereira'}, "
			    + "  {'nome': 'Isabela Rocha'}, "
			    + "  {'nome': 'João Oliveira'}, "
			    + "  {'nome': 'Lara Ramos'}"
			    + "]"
			    + "}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(dados);
		System.out.println("getAsCollectionDecorator() = "+json.getAsCollectionDecorator("nomes"));
		
	}
	
	@Test
	public void getAsStringListTest() {
		String nomes = "{"
					 + "'nomes'"
					 + ":['Pedro',"
					 + "'João',"
					 + "'Tiago']"
					 + "}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(nomes);
		System.out.println("getAsStringListTest() = "+json.getAsStringList("nomes"));
		
	}
	
	@Test
	public void getAsStringListWithAlternativeFieldTest() {
		String nomes = "{"
                + "'nomes': [],"
                + "'sobrenome': 'Silva'"
                + "}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(nomes);
		System.out.println("\ngetAsStringListWithAlternativeFieldTest() = "+json.getAsStringList("nomes","sobrenome"));
		
	}
	
	@Test
	public void getAsObjectListTeste() {
		String keys = "{"
					+ "'nome': 'Lucas',"
					+ "'pais': 'Brasil'"
					+ "}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(keys);
		System.out.println("\ngetAsObjectListTeste() = "+json.getAsObjectList("x"));
	}
	
	@Test
	public void putAllTest() {
		String keys = "{'pais':'Brasil',"
					+ "'cidade':'São Paulo',"
					+ "'Estado':'SP'}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(keys);
		CcpJsonRepresentation putAll = json.putAll(json);
		System.out.println("\nputAllTest() = "+ putAll);
	}
	
	@Test
	public void containsFieldTest() {
		String campos = "{"
					  + "'nome':'Chico',"
					  + "'idade':50,"
					  + "'peso':62.3,"
					  + "'nacionalidade':'brasileiro'"
					  + "}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(campos);
		System.out.println("\ncontainsFieldTest() "+json.containsField("nome"));
		System.out.println("containsFieldTest() "+json.containsField("paçoca"));
		System.out.println("containsFieldTest() "+json.containsField("nacionalidade")+"\n");
	}
	
	@Test
	public void containsAllFieldsJsonTest() {
		String campos = "{"
					  + "'nome':'Chico',"
					  + "'idade':50,"
					  + "'peso':62.3,"
					  + "'nacionalidade':'brasileiro'"
					  + "}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(campos);

	    Collection<String> fields = Arrays.asList("nome", "idade", "peso");

	    boolean result = json.containsAllFields(fields);
	    
	    System.out.println("\ncontainsAllFieldsJsonTest() " + result+"\n");
	    
	}
	
	@Test
	public void containsAllFieldsTest() {
		String campos = "{"
				+ "'nome':'Chico',"
				+ "'idade':50,"
				+ "'peso':62.3,"
				+ "'nacionalidade':'brasileiro'"
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(campos);

		Collection<String> fields = Arrays.asList("nome", "idade", "peso");

		boolean result = json.containsAllFields(fields);

		System.out.println("\ncontainsAllFieldsTest() " + result+"\n");
	}
	
	@Test
	//Retorna false apenas de conter o campo "nome"
	public void containsAnyFieldsTest() {
		String campos = "{"
				+ "'nome':'Chico',"
				+ "'idade':50,"
				+ "'peso':62.3,"
				+ "'nacionalidade':'brasileiro'"
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(campos);

		Collection<String> fields = Arrays.asList("nome", "sobrenome", "carro");

		boolean result = json.containsAllFields(fields);

		System.out.println("\ncontainsAnyFieldsTest() " + result+"\n");
	}
	
	@Test
	public void containsAnyFieldsJsonTest() {
		String campos = "{"
				+ "'nome':'Chico',"
				+ "'idade':50,"
				+ "'peso':62.3,"
				+ "'nacionalidade':'brasileiro'"
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(campos);

		System.out.println("\ncontainsAnyFieldsJsonTest() " + json.containsAnyFields("peso","carro","idade")+"\n");
	}
	
	@Test
	public void getTest() {
		String campos = "{"
				+ "'nome':'Chico',"
				+ "'idade':50,"
				+ "'peso':62.3,"
				+ "'nacionalidade':'brasileiro'"
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(campos);
		
		Double valorConvertidoEmObjeto = (double) json.get("peso");
		
		System.out.println("\ngetTest() "+ (valorConvertidoEmObjeto+1) + "\n");
	}
	
	@Test
	public void getAsObjectTest() {
		String campos = "{"
				+ "'nome':'Chico',"
				+ "'idade':50,"
				+ "'peso':62.3,"
				+ "'nacionalidade':'brasileiro'"
				+ "}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(campos);
		
		//Muda para qualquer tipo sem a necessidade de fazer o casting
		Double valorConvertidoEmObjeto =  json.getAsObject("peso");
		
		assertTrue(valorConvertidoEmObjeto instanceof Double);
	}
	
	@Test
	public void isEmptyTest() {
		String vazio = "{}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(vazio);
		
		System.out.println(json.isEmpty());
	}
	
	@Test
	public void addToListTest() {
		String jsonQueSeraPreenchido = "{}";
		String jsonJaPreenchido = "{'idade':29}";
		
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(jsonQueSeraPreenchido);
		System.out.println(json.addToList("nomes", "Jesus","Maria","José"));
		System.out.println("\n"+json.addToList("campo",jsonJaPreenchido));
	}
	
	@Test
	public void whenHasFieldTest() {
		String elemento = "{'nome':'Lucas'}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(elemento);
		
		json.whenHasField("nome", funcao ->{
			System.out.println("Encontrei o field");
			return funcao;
		});
	}
	
	@Test
	public void whenHasNotFieldTest() {
		String elemento = "{'campoQualquer': 'Lucas'}";
		CcpJsonRepresentation json = new CcpJsonRepresentation(elemento);
		
		json.whenHasNotField("nome", funcao ->{
			CcpJsonRepresentation jsonComCampoNome = new CcpJsonRepresentation("{'nome' : "+json.getAsString("campoQualquer")+"}");
			System.out.println(jsonComCampoNome);
			return funcao;
		});
	}
	
	
	//VERIFICAR SE MÉTODO ESTÁ FUNCIONANDO
	@Test
	public void copyIfNotContainsTest() {
		String json = "{'nome':'Lucas'," + "'nomeCopiado':''}";

		CcpJsonRepresentation objJson = new CcpJsonRepresentation(json).removeField("nomeCopiado");
		assertFalse(objJson.containsAllFields("nomeCopiado"));
		CcpJsonRepresentation copyIfNotContains = objJson.copyIfNotContains("nome", "nomeCopiado");
		assertTrue(copyIfNotContains.containsAllFields("nomeCopiado"));
		String nomeCopiado = copyIfNotContains.getAsString("nomeCopiado");
		String nome = copyIfNotContains.getAsString("nome");
		assertEquals(nome, nomeCopiado);
		
		System.out.println(copyIfNotContains);// O valor do campo nome não foi colado em nomeCopiado
	}
	
	//VERIFICAR SE MÉTODO ESTÁ FUNCIONANDO
	@Test
	public void putIfNotContainsTest() {
		String json = "{}";// TALVEZ ELE IDENTIFIQUE O CAMPO EM BRANCO COMO ALGO, COLOCAR A PALAVRA
							// 'undefined' TAMBÉM NÃO FUNCIONPU

		CcpJsonRepresentation objJson = new CcpJsonRepresentation(json);
		assertFalse(objJson.containsAllFields("valor"));
		CcpJsonRepresentation putIfNotContains = objJson.putIfNotContains("valor", "teste");
		assertTrue(putIfNotContains.containsAllFields("valor"));
		System.out.println(putIfNotContains);

	}	
	
	@Test
	public void getAsArrayMetadataTest() {
		String json = "{'field':'value'}";
		
		CcpJsonRepresentation objJson = new CcpJsonRepresentation(json);
		
		//Ao tentar imprimir o decorator recebi o endereço de memória talvez esteja faltando o toString()
		assertTrue(objJson.getAsArrayMetadata("field") instanceof CcpCollectionDecorator);
	}
	
	@Test
	public void ItIsTrueThatTheFollowingFieldsTest() {
		String json = "{'field':'value'}";
		
		CcpJsonRepresentation objJson = new CcpJsonRepresentation(json);
		
		objJson.itIsTrueThatTheFollowingFields("nome");
	}
	
	@Test
	public void getMissingFieldsTest() {
		String registro = "{'nome':'Alice',"
						+ "'sobrenome':'',"
						+ "'idade':12}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(registro);
		
		List<String> campos = Arrays.asList("nome", "sobrenome", "idade");
		
		Set<String> camposFaltantes = json.getMissingFields(campos);

	    
	    assertEquals(1, camposFaltantes.size()); // Apenas "sobrenome" está vazio
		
		
	}
	
	
	//Funcionou mas não entendi o retorno
	@Test
	public void toInputStreamTest() {
		String registro = "{'nome':'Alice',"
				+ "'sobrenome':'',"
				+ "'idade':12}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(registro);
		
		InputStream inputStream = json.toInputStream();
		System.out.println(inputStream);
	}
	
	@Test
	public void hashCodeTest() {
		String registro = "{'valor':25}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(registro);
		
		System.out.println(json.hashCode());
	}
	
	@Test
	//Equals não retornou o resultado esperado
	public void equalsTest() {
		String registro = "{'valor':25}";
		String registro2 ="{'valor':25}";

		CcpJsonRepresentation json = new CcpJsonRepresentation(registro);
		CcpJsonRepresentation json2 = new CcpJsonRepresentation(registro2);
		
		
		System.out.println(json.equals(json2));
	}
	
	@Test
	public void getAsTextDecoratorListTest() {
		String registro = ""
				+ "{ "
				+ "    'nome': 'André Gabriel Diogo Araújo', "
				+ "    'idade': 21, "
				+ "    'cpf': '61170540503', "
				+ "    'rg': '486855004', "
				+ "    'data_nasc': '01/02/2003', "
				+ "    'sexo': 'Masculino', "
				+ "    'signo': 'Aquário', "
				+ "    'mae': 'Rebeca Sophia Rayssa', "
				+ "    'pai': 'Davi Joaquim Araújo', "
				+ "    'email': 'andre_gabriel_araujo@agnet.com.br', "
				+ "    'senha': 'f7KbmxukNj', "
				+ "    'cep': '49072540', "
				+ "    'endereco': 'Rua Coronel Elias Gonzaga', "
				+ "    'numero': 378, "
				+ "    'bairro': 'Dezoito do Forte', "
				+ "    'cidade': 'Aracaju', "
				+ "    'estado': 'SE', "
				+ "    'telefone_fixo': '7935018009', "
				+ "    'celular': '79992335171', "
				+ "    'altura': '1,88', "
				+ "    'peso': 72, "
				+ "    'tipo_sanguineo': 'AB+', "
				+ "    'cor': 'roxo' "
				+ "}";
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(registro);
		
		//No debug é possível constatar a lista
		List<CcpTextDecorator> asTextDecoratorList = json.getAsTextDecoratorList(registro);
		System.out.println(asTextDecoratorList.toString());
		System.out.println("***");
		
	}
	
	
	
	
	
	
	
	
	

	
	


	
	

	







}
