package com.ccp.jn.test.pocs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Poc {
	public static void main(String[] args) {
		List<String> arrayList = Arrays.asList();
		Stream<String> stream = arrayList.stream();
		List<String> collect = stream.collect(Collectors.toList());
		List<String> collect2 = stream.collect(Collectors.toList());
	}
}
record Pessoa(int idade, String nome) {
	
	public void teste() {
	
	}
}