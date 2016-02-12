/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.io;

import de.interactive_instruments.MdUtils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class FileHashVisitor implements FileVisitor<Path> {

    private long fileCount=0;
    private long size=0;
    private final PathFilter filter;
    private final MessageDigest md;


    public FileHashVisitor(PathFilter filter) {
        if(filter==null) {
            this.filter = (p) -> true;
        }else {
            this.filter = filter;
        }
        this.md=MdUtils.getMessageDigest();
    }

    public FileHashVisitor(PathFilter filter, MessageDigest md) {
        if(filter==null) {
            this.filter = (p) -> true;
        }else {
            this.filter = filter;
        }
        this.md=md;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(!filter.accept(file)) {
            return FileVisitResult.CONTINUE;
        }
        fileCount++;
        size+=attrs.size();
        final String mod = file.getFileName() + String.valueOf(
                attrs.lastModifiedTime().toMillis() + attrs.size());
        md.update(mod.getBytes());
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

    public long getFileCount() {
        return fileCount;
    }

    public long getSize() {
        return size;
    }

    public byte[] getHash() {
        return md.digest();
    }
}
