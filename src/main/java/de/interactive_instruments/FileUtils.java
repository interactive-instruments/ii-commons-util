/*
 * Copyright 2010-2019 interactive instruments GmbH
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

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Locale;


public class FileUtils {

    private FileUtils() {
    }

    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a kilobyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a megabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The file copy buffer size (30 MB)
     */
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The number of bytes in a gigabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = ONE_KB * ONE_GB;

    /**
     * The number of bytes in a terabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = ONE_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    //-----------------------------------------------------------------------
    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is rounded by places.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @param places rounded decimal places
     * @param locale decimal separator locale
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplayRoundedSize(final BigInteger size, final int places, final Locale locale) {
        String displaySize;

        final long sizeInLong = size.longValue();

        final String formatPattern = "%." + places + "f";

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.format(locale, formatPattern, sizeInLong / ONE_EB_BI.doubleValue()) + " EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.format(locale, formatPattern, sizeInLong / ONE_PB_BI.doubleValue()) + " PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.format(locale, formatPattern, sizeInLong / ONE_TB_BI.doubleValue()) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.format(locale, formatPattern, sizeInLong / ONE_GB_BI.doubleValue()) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.format(locale, formatPattern, sizeInLong / ONE_MB_BI.doubleValue()) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.format(locale, formatPattern, sizeInLong / ONE_KB_BI.doubleValue()) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }

        final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        return displaySize.replaceFirst(new StringBuilder("[")
                .append(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator())
                .append("]")
                .append(new String(new char[places]).replace('\0', '0'))
                .append(" ")
                .toString()
            ," ");
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is rounded by places.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @param places rounded decimal places
     * @param locale decimal separator locale
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    public static String byteCountToDisplayRoundedSize(final long size, final int places, final Locale locale) {
        return byteCountToDisplayRoundedSize(BigInteger.valueOf(size), places, locale);
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     *     Same as {@link #byteCountToDisplayRoundedSize(BigInteger, int, Locale)}, but locale is {@link Locale#getDefault()}.
     * </p>
     * @param size the number of bytes
     * @param places rounded decimal places
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    public static String byteCountToDisplayRoundedSize(final BigInteger size, final int places) {
        return byteCountToDisplayRoundedSize(size, places, Locale.getDefault());
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     *     Same as {@link #byteCountToDisplayRoundedSize(BigInteger, int, Locale)}, but locale is {@link Locale#getDefault()}.
     * </p>
     * @param size the number of bytes
     * @param places rounded decimal places
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    public static String byteCountToDisplayRoundedSize(final long size, final int places) {
        return byteCountToDisplayRoundedSize(size, places, Locale.getDefault());
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     *     Same as {@link #byteCountToDisplayRoundedSize(BigInteger, int)}, but places is 0.
     * </p>
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    public static String byteCountToDisplayRoundedSize(final BigInteger size) {
        return byteCountToDisplayRoundedSize(size, 0);
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     *     Same as {@link #byteCountToDisplayRoundedSize(BigInteger, int)}, but places is 0.
     * </p>
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    public static String byteCountToDisplayRoundedSize(final long size) {
        return byteCountToDisplayRoundedSize(size, 0);
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the
     * nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     * @since 2.4
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplaySize(final BigInteger size) {
        String displaySize;

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_EB_BI)) + " EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_PB_BI)) + " PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_TB_BI)) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_GB_BI)) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_MB_BI)) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_KB_BI)) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the
     * nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplaySize(final long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }
}
