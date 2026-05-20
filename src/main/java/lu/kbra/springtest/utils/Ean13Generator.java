package lu.kbra.springtest.utils;

import java.security.SecureRandom;

public final class Ean13Generator {

	private static final SecureRandom RANDOM = new SecureRandom();

	private Ean13Generator() {
	}

	public static String generate() {
		final StringBuilder first12 = new StringBuilder(12);
		for (int i = 0; i < 12; i++) {
			first12.append(Ean13Generator.RANDOM.nextInt(10));
		}
		final int checkDigit = Ean13Generator.calculateCheckDigit(first12.toString());
		return first12.append(checkDigit).toString();
	}

	public static int calculateCheckDigit(final String first12Digits) {
		if (!first12Digits.matches("\\d{12}")) {
			throw new IllegalArgumentException("Input must contain exactly 12 digits");
		}

		int sum = 0;
		for (int i = 0; i < 12; i++) {
			final int digit = first12Digits.charAt(i) - '0';

			// EAN-13 weighting:
			// odd positions = 1
			// even positions = 3

			sum += i % 2 == 0 ? digit : digit * 3;
		}

		return (10 - sum % 10) % 10;
	}

	public static boolean isValid(final String ean13) {
		if (ean13 == null || !ean13.matches("\\d{13}")) {
			return false;
		}

		final String first12 = ean13.substring(0, 12);
		final int expected = Ean13Generator.calculateCheckDigit(first12);
		final int actual = ean13.charAt(12) - '0';
		return expected == actual;
	}
}