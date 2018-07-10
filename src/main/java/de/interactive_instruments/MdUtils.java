/**
 * Copyright 2017-2018 European Union, interactive instruments GmbH
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * This work was supported by the EU Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: A Reusable INSPIRE Reference Platform (ARE3NA).
 */
package de.interactive_instruments;

import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The MdUtils provide two hash functions. The Fowler–Noll–Vo hash is for fast checksum use but
 * not cryptographic hashing. For cryptographic hashing, use the {@link #getMessageDigest()}
 * method, which will return either SHA-256 if available or SHA-1 otherwise.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class MdUtils {

	private static final long FNV_64_INIT = 0xcbf29ce484222325L;
	private static final long FNV_64_PRIME = 0x100000001b3L;

	/**
	 * Fowler–Noll–Vo hash function, 1a 64 bit version.
	 *
	 * FNV hash is designed for fast hash table and checksum use, not cryptography!
	 *
	 * @param data input data to hash
	 * @return hash as BigInteger
	 */
	public static long checksum(final byte[] data) {
		// n = 64
		// prime = 1099511628211
		// offset basis = 0xcbf29ce484222325
		long hash = FNV_64_INIT;
		for (int i = 0; i < data.length; i++) {
			hash ^= data[i];
			hash *= FNV_64_PRIME;
		}
		return hash;
	}

	public static class FnvChecksum {
		private long hash = FNV_64_INIT;

		/**
		 * Fowler–Noll–Vo hash function, 1a 64 bit version.
		 *
		 * FNV hash is designed for fast hash table and checksum use, not cryptography!
		 *
		 * @param data input data to hash
		 */
		public void update(final byte[] data) {
			// n = 64
			// prime = 1099511628211
			for (int i = 0; i < data.length; i++) {
				hash ^= data[i];
				hash *= FNV_64_PRIME;
			}
		}

		public long getHash() {
			return hash;
		}

		public byte[] getBytes() {
			try {
				return Long.toHexString(hash).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				return Long.toHexString(hash).getBytes();
			}
		}

		@Override
		public String toString() {
			return h64ToString(hash);
		}
	}

	/**
	 * Fowler–Noll–Vo hash function, 1a 64 bit version.
	 *
	 * FNV hash is designed for fast hash table and checksum use, not cryptography!
	 *
	 * @param data input data to hash
	 * @return hash as 64 bit string
	 */
	public static String checksumAsHexStr(final byte[] data) {
		return h64ToString(checksum(data));
	}

	/**
	 * 64 bit hash to string
	 *
	 * @param hash hash to convert
	 * @return hash as 64 bit string
	 */
	public static String h64ToString(final long hash) {
		// return Long.toHexString(hash);
		return String.format("%016X", hash);
	}

	public static MessageDigest getMessageDigest() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			try {
				md = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e1) {
				throw new InvalidParameterException("SHA-256 and SHA-1 are not available");
			}
		}
		md.reset();
		return md;
	}
}
