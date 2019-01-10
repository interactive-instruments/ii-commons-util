/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
