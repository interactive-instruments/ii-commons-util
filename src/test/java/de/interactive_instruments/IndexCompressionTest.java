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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test compression of index arrays
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class IndexCompressionTest {

    @Test
    public void testCompression1() {
        final int[] x = new int[8];
        x[0] = 1;

        final long idx = IndexCompression.compress(x);
        assertEquals("1", Long.toBinaryString(idx));
        assertEquals(1L, idx);
    }

    @Test
    public void testUncompress1() {
        long compressed = 1L;
        final int[] x = IndexCompression.uncompress(compressed);

        assertEquals(1, x[0]);
    }

    @Test
    public void testCompression2() {
        final int[] x = new int[2];
        x[0] = 2;

        final long idx = IndexCompression.compress(x);
        assertEquals("100", Long.toBinaryString(idx));
        assertEquals(4L, idx);
    }

    @Test
    public void testUncompress2() {
        long compressed = 4L;
        final int[] x = IndexCompression.uncompress(compressed);

        assertEquals(2, x[0]);
    }

    @Test
    public void testCompression3() {
        final int[] x = new int[5];
        x[0] = 1;
        x[1] = 23;
        x[2] = 2;
        x[3] = 1;
        x[4] = 4;

        final long idx = IndexCompression.compress(x);
        // 100 0 1 0000000010 0 0000010111 0 1
        assertEquals("1000100000000000100000000001011101", Long.toBinaryString(idx));
        assertEquals(9126936669L, idx);
    }

    @Test
    public void testUncompress3() {
        long compressed = 9126936669L;
        final int[] x = IndexCompression.uncompress(compressed);

        assertEquals(1, x[0]);
        assertEquals(23, x[1]);
        assertEquals(2, x[2]);
        assertEquals(1, x[3]);
        assertEquals(4, x[4]);
    }

    @Test
    public void testCompression4() {
        final int[] x = new int[8];
        x[0] = 13;
        x[1] = 128;
        x[2] = 4;
        x[3] = 1;
        x[4] = 1;
        x[5] = 1;
        x[6] = 1;
        x[7] = 3;

        final long idx = IndexCompression.compress(x);
        assertEquals("1101111000000000010000000010000000000000000011010", Long.toBinaryString(idx));
        assertEquals(488185314410522L, idx);
    }

    @Test
    public void testUncompress4() {
        long compressed = 488185314410522L;
        final int[] x = IndexCompression.uncompress(compressed);

        assertEquals(13, x[0]);
        assertEquals(128, x[1]);
        assertEquals(4, x[2]);
        assertEquals(1, x[3]);
        assertEquals(1, x[4]);
        assertEquals(1, x[5]);
        assertEquals(1, x[6]);
        assertEquals(3, x[7]);
    }

    @Test
    public void testExceptions() {
        {
            final int[] x = new int[1];
            x[0] = 2048;
            boolean exceptionThrown = false;
            try {
                IndexCompression.compress(x);
            } catch (IllegalStateException e) {
                exceptionThrown = true;
            }
            assertTrue(exceptionThrown);
        }
        {
            final int[] x = new int[Long.SIZE];
            for (int i = 0; i < x.length; i++) {
                x[i] = 1;
            }
            boolean exceptionThrown = false;
            try {
                IndexCompression.compress(x);
            } catch (IllegalStateException e) {
                exceptionThrown = true;
            }
            assertTrue(exceptionThrown);
        }
    }

}
