/**
 * Copyright 2010-2017 interactive instruments GmbH
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
package de.interactive_instruments;

import java.util.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class Sample {

	public static <T> List<T> normalDistributed(final List<T> input, final double desiredSubsetSizeInPercent) {
		return normalDistributed(input, (int) (input.size() / 100 * desiredSubsetSizeInPercent));
	}

	public static <T> List<T> normalDistributed(final List<T> input, final int desiredSubsetSize) {
		final int inputSize = input.size();

		final int subsetSize = Math.min(inputSize, desiredSubsetSize < 0 ? 1 : desiredSubsetSize);
		if (subsetSize >= inputSize) {
			return input;
		}
		final int distribution = Math.min(subsetSize, (int) Math.ceil(inputSize / subsetSize));
		final int distributionClassSize = inputSize / distribution;
		final int samplesPerDistribution = subsetSize / distribution;

		final Random random = new Random();
		final List<T> samples = new ArrayList<>(subsetSize);
		final Set<Integer> allIndices = new TreeSet<>();
		// iterate over distribution classes
		for (int d = 0; d < distribution; d++) {
			// pick random elements from distribution class
			final int dStart = d * distributionClassSize;
			final Set<Integer> selectedIndices = new TreeSet<>();
			while (selectedIndices.size() < samplesPerDistribution) {
				selectedIndices.add(dStart + random.nextInt(distributionClassSize));
			}
			allIndices.addAll(selectedIndices);
		}
		// pick random from somewhere
		while (allIndices.size() < desiredSubsetSize) {
			allIndices.add(random.nextInt(inputSize));
		}
		for (final Integer selectedIndex : allIndices) {
			samples.add(input.get(selectedIndex));
		}

		return samples;
	}
}
