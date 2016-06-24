/**
 * Copyright 2010-2016 interactive instruments GmbH
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
