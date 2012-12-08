/*
 * Copyright (C) 2012 sourcecoding.com / Matthias Reining
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sourcecoding.multitenancy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.jboss.security.Base64Utils;

/**
 * {@link SecurityTools} provides some methods for handling passwords.
 * <p>
 * Based on https://www.owasp.org/index.php/Hashing_Java
 * </p>
 * 
 * @author matthias reining
 */
public abstract class SecurityTools {
	private static final int ITERATION_NUMBER = 1000;

	public static boolean checkPassword(String clearText, String hash) {
		if (clearText == null || hash == null || hash.length() < 30)
			return false;
		try {
			String salt = hash.substring(27);
			String digest = hash.substring(0, 27);

			byte[] bDigest = Base64Utils.fromb64(digest);
			byte[] bSalt = Base64Utils.fromb64(salt);

			byte[] proposedDigest = getHash(ITERATION_NUMBER, clearText, bSalt);

			if (Arrays.equals(proposedDigest, bDigest))
				return true;

			return false;
		} catch (IOException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String... args) {
		String hash = createHash(args[0]);

		System.out.println("pw: " + args[0]);
		System.out.println("hash: " + createHash(args[0]));
		System.out.println("validate: " + checkPassword(args[0], hash));
	}

	public static String createHash(String password) {

		if (password == null)
			throw new RuntimeException(
					"Build a hash from null is not possible!");
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			String hash = "";

			// Base64Utils.tob64 return different length (one character).
			// To split salt and hash a definied length is necessary.
			// the while loop ensure that the length is fix.
			while (hash.length() != 38) {

				// Salt generation 64 bits long
				byte[] bSalt = random.generateSeed(8);
				// Digest computation
				byte[] bDigest = getHash(ITERATION_NUMBER, password, bSalt);

				String sDigest = Base64Utils.tob64(bDigest);
				String sSalt = Base64Utils.tob64(bSalt);

				hash = sDigest + sSalt;
			}
			return hash;

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 * 
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getHash(int iterationNb, String password, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++) {
			digest.reset();
			input = digest.digest(input);
		}

		return input;
	}

}
