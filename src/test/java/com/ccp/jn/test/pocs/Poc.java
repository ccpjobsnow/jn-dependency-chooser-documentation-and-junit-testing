package com.ccp.jn.test.pocs;

public class Poc {
	public static void main(String[] args) {
		Pessoa p = new Pessoa(11, "lala2");
		System.out.println(p.hashCode());
	}
}
record Pessoa(int idade, String nome) {
	
	public void teste() {
	
	}
}