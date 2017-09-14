/**
 * Copyright 2017 European Union, interactive instruments GmbH
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
package de.interactive_instruments.container;

import java.util.Map;

public class Pair<L, R> implements Map.Entry<L, R> {

	// aka key
	private final L left;

	// aka value
	private R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int hashCode() {
		return left.hashCode() ^ right.hashCode();
	}

	public L getLeft() {
		return left;
	}

	@Override
	public L getKey() {
		return getLeft();
	}

	public R getRight() {
		return right;
	}

	@Override
	public R getValue() {
		return getRight();
	}

	@Override
	public R setValue(R value) {
		final R oldValue = right;
		right = value;
		return oldValue;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || (!(o instanceof Map.Entry))) {
			return false;
		}
		final Map.Entry<?, ?> p = (Pair<?, ?>) o;
		return this.left.equals(p.getKey()) &&
				this.right.equals(p.getValue());
	}
}
