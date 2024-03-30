package com.ccp.jn.test.pocs;

import com.ccp.fields.validations.enums.ArrayNumbersValidations;

public class Poc {

	public static void main(String[] args) {
		Class<? extends ArrayNumbersValidations> class1 = ArrayNumbersValidations.equalsOrGreaterThan.getClass();
		System.out.println(class1);
	}
}
