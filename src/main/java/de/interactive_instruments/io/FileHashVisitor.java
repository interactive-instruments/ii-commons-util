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
package de.interactive_instruments.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.Set;

import de.interactive_instruments.MdUtils;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class FileHashVisitor implements FileVisitor<Path> {

    private long size = 0;
    private long emptyFiles = 0;
    private long skippedFiles = 0;
    private final Set<byte[]> files = new LinkedHashSet<>();
    private long byteLength = 0;
    private final PathFilter filter;
    private final MdUtils.FnvChecksum checksum;
    private String checksumStr = null;

    public FileHashVisitor(final PathFilter filter, final MdUtils.FnvChecksum checksum) {
        this.filter = filter;
        this.checksum = checksum;
    }

    public FileHashVisitor(final PathFilter filter) {
        this(filter, new MdUtils.FnvChecksum());

    }

    public FileHashVisitor() {
        this(null, new MdUtils.FnvChecksum());
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        final String mod = file.getFileName() + String.valueOf(
                attrs.lastModifiedTime().toMillis() + attrs.size());
        if (filter == null || filter.accept(file)) {
            synchronized (this) {
                files.add(mod.getBytes());
                byteLength += mod.length();
                if (attrs.size() == 0) {
                    this.emptyFiles++;
                } else {
                    size += attrs.size();
                }
            }
        } else {
            skippedFiles++;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public String getHash() {
        if (checksumStr == null) {
            if (this.byteLength < Integer.MAX_VALUE) {
                final byte[] bytes = new byte[(int) this.byteLength];
                int i = 0;
                for (final byte[] fileBytes : files) {
                    for (int j = 0, fileBytesLength = fileBytes.length; j < fileBytesLength; j++) {
                        bytes[i++] = fileBytes[j];
                    }
                }
                this.checksum.update(bytes);
            } else {
                for (final byte[] fileBytes : files) {
                    this.checksum.update(fileBytes);
                }
            }
            checksumStr = this.checksum.toString();
        }
        return checksumStr;
    }

    public long getFileCount() {
        return files.size();
    }

    public long getSize() {
        return size;
    }

    public long getEmptyFiles() {
        return emptyFiles;
    }

    public long getSkippedFiles() {
        return skippedFiles;
    }
}
