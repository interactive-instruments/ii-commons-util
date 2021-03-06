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

import static de.interactive_instruments.IoUtils.copy;
import static de.interactive_instruments.IoUtils.copySecure;

import java.io.*;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;

import de.interactive_instruments.container.Pair;
import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.exceptions.IOsizeLimitExceededException;
import de.interactive_instruments.io.DefaultFileIgnoreFilter;
import de.interactive_instruments.io.MultiFileFilter;
import de.interactive_instruments.io.PathFilter;
import de.interactive_instruments.jaxb.adapters.IFileXmlAdapter;
import de.interactive_instruments.properties.PropertyUtils;

/**
 * The IFile class helps to generate "usable" error messages for file operations by extending the standard Java java.io.File class with methods that simplify error handling.
 * <p>
 *
 * It also bundles functionality for frequently conducted tasks like file compressing, copying, opening a file as StringBuffer, open a file as DOM, search (recursively) in directories for files (with a regular expression), etc...
 *
 * TODO: use new JDK 1.7 link capabilities
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 * @version 2.2
 * @since 1.0
 * @see de.interactive_instruments.jaxb.adapters.IFileXmlAdapter
 */
@XmlRootElement
@XmlJavaTypeAdapter(IFileXmlAdapter.class)
public final class IFile extends File {

    private static final long serialVersionUID = 22L;

    // identifier of the file which will be output in error messages
    protected String identifier;

    // patterns for relative file detection
    private static final Pattern DIR_UP = Pattern.compile("\\.\\.");
    private static final Pattern MULTI_B_SLASH = Pattern.compile("(\\\\+)|(//+)|(\\/+)");

    // replacements for special chars in file names
    private static Pair<Pattern, String>[] REPLACEMENTS = new Pair[]{
            new Pair<>(Pattern.compile("\u00C4", Pattern.LITERAL), "Ae"),
            new Pair<>(Pattern.compile("\u00DC", Pattern.LITERAL), "Ue"),
            new Pair<>(Pattern.compile("\u00D6", Pattern.LITERAL), "Oe"),
            new Pair<>(Pattern.compile("\u00E4", Pattern.LITERAL), "ae"),
            new Pair<>(Pattern.compile("\u00FC", Pattern.LITERAL), "ue"),
            new Pair<>(Pattern.compile("\u00F6", Pattern.LITERAL), "oe"),
            new Pair<>(Pattern.compile("\u00DF", Pattern.LITERAL), "ss"),

            // LATIN SMALL LETTER E WITH GRAVE
            new Pair<>(Pattern.compile("\u00E8", Pattern.LITERAL), "e"),
            // LATIN SMALL LETTER E WITH ACUTE
            new Pair<>(Pattern.compile("\u00E9", Pattern.LITERAL), "e"),
            // LATIN SMALL LETTER E WITH CIRCUMFLEX
            new Pair<>(Pattern.compile("\u00EA", Pattern.LITERAL), "e"),

            new Pair<>(Pattern.compile(",", Pattern.LITERAL), ""),
            new Pair<>(Pattern.compile("'", Pattern.LITERAL), " ")
    };

    // removes all version information in a basename beginning with a number, followed by a dot
    private final static Pattern versionRemovePattern = Pattern.compile(
            "-(?:0|(?:[1-9]\\d*))(?:-|(?:\\.(?:0|(?:[1-9]\\d*)))).*|\\.(?:.(?!\\.))+$");

    // files that are removed on exit
    private static final LinkedBlockingDeque<String> filesToDeleteOnExit = new LinkedBlockingDeque<>();

    // Default 10 GB
    private static final long defaultMaxUnzipSize = PropertyUtils.getenvOrProperty("ii.file.max.unzip.size", 10737418240L);

    private static class DeleteFilesHook extends Thread {
        @Override
        public void run() {
            for (final String file : filesToDeleteOnExit) {
                try {
                    final IFile f = new IFile(file, "TMP_DELETE_HOOK");
                    if (f.isDirectory()) {
                        f.deleteDirectory();
                    } else {
                        Files.delete(f.toPath());
                    }
                } catch (final IOException e) {
                    ExcUtils.suppress(e);
                }
            }
        }
    }

    /**
     * Initialize file path without setting an identifier for it
     *
     * @param path
     *            File or directory path
     */
    public IFile(final String path) {
        super(path);
        this.identifier = "";
    }

    /**
     * Initialize a file path and set an identifier which is used for error messages.
     *
     * @param path
     *            File or directory path
     * @param identifier
     *            A hint to the user what this file is intended for. Note that "directory" or "file" will be added in failure messages behind the identifier, so it should be omitted in the identifier.
     */
    public IFile(final String path, final String identifier) {
        super(path);
        this.identifier = identifier;
    }

    /**
     * Copy a file path from a File-Object
     *
     * @param file
     *            File
     */
    public IFile(final File file) {
        super(file.getPath());
        this.identifier = "";
    }

    public IFile(final URI uri) {
        super(uri.isAbsolute() ? uri : Paths.get(uri).toUri());
        this.identifier = "";
    }

    public IFile(final URI uri, final String identifier) {
        super(uri);
        this.identifier = identifier;
    }

    /**
     * Initialize a file in a directory without setting a identifier for it
     *
     * @param dir
     *            IFile-Object with directory path
     * @param fileName
     *            Name of the file in the directory
     */
    public IFile(final IFile dir, final String fileName) {
        super(dir, fileName);
        this.identifier = "";
    }

    /**
     * Initialize a file in a directory without setting a identifier for it
     *
     * @param dir
     *            IFile-Object with directory path
     * @param fileName
     *            Name of the file in the directory
     */
    public IFile(final File dir, final String fileName) {
        super(dir, fileName);
        this.identifier = "";
    }

    /**
     * Returns a new IFile with the expanded path
     *
     * @param path
     * @return
     */
    public IFile expandPath(final String path) {
        final IFile tmp = new IFile(this, path);
        tmp.setIdentifier(this.identifier + " " + path);
        return tmp;
    }

    /**
     * Ensures that a path inside this directory is returned.
     *
     * If the path is an absolute path, it must start with this directory as parent.
     *
     * @param path
     *            relative path in this directory or an absolute path starting with this directory.
     * @return
     */
    public IFile secureExpandPathDown(final String path) {
        final Path p;
        try {
            p = Paths.get(
                    MULTI_B_SLASH.matcher(
                            DIR_UP.matcher(path).replaceAll(""))
                            .replaceAll(Matcher.quoteReplacement(File.separator)))
                    .normalize();
        } catch (InvalidPathException e) {
            throw new SecurityException("Invalid path: " + path, e);
        }
        if ((p.isAbsolute() && (p.startsWith(this.toPath())) || (
        // compare without driver letter
        SystemUtils.IS_OS_WINDOWS && p.startsWith(File.separator + FilenameUtils.getPath(
                this.toPath().toString() + File.separator))))) {
            return new IFile(p.toFile());
        }
        final IFile tmp = new IFile(this, p.toString());
        tmp.setIdentifier(this.identifier + path);
        return tmp;
    }

    /**
     * Sets the identifier of the file that will be used in error messages
     *
     * @param identifier
     *            A hint to the user what this file is intended for. Note that "directory" or "file" will be added in failure messages behind the identifier, so it should be omitted in the identifier.
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Return file or directory size
     *
     * @return file size, 0 may indicate a failure
     */
    @Override
    public long length() {
        if (isDirectory()) {
            long size = 0;
            try {
                for (final IFile file : getFilesInDirRecursive(null, 15, false)) {
                    try {
                        if (!FileUtils.isSymlink(file)) {
                            size += file.length();
                        }
                    } catch (final IOException ign) {
                        // Ignore exceptions caught when asking if a File is a symlink.
                    }
                }
            } catch (IOException e) {
                return 0L;
            }
            return size;
        } else {
            return super.length();
        }
    }

    /**
     * Returns the canonical path if possible or just the abstract path
     */
    public String getCanonicalOrSimplePath() {
        String path;
        try {
            path = this.getCanonicalPath();
        } catch (final IOException e) {
            path = this.getPath();
        }
        return path;
    }

    /**
     * Expect that a file or directory is writable
     *
     * @throws IOException
     */
    public void expectIsWritable() throws IOException {
        if (!this.canWrite()) {
            throw new IOException("Perrmission to write " + this.identifier +
                    " \"" +
                    getCanonicalOrSimplePath() +
                    "\" denied or file/directory does not exist");
        }
    }

    /**
     * Expect that a file or directory is readable
     *
     * @throws IOException
     */
    public void expectIsReadable() throws IOException {
        if (!this.exists()) {
            throw new FileNotFoundException("Unable to read " + this.identifier +
                    " \"" +
                    getCanonicalOrSimplePath() +
                    "\" : file/directory does not exist");
        }
        if (!this.canRead()) {
            throw new IOException("Perrmission to read " + this.identifier +
                    " \"" +
                    getCanonicalOrSimplePath() + "\" denied");
        }
    }

    /**
     * Expect that a file or directory is read and writable
     *
     * @throws IOException
     */
    public void expectIsReadAndWritable() throws IOException {
        this.expectIsReadable();
        this.expectIsWritable();
    }

    /**
     * Expect that this IS NOT a directory
     *
     * @throws IOException
     */
    private void expectNotADirectory() throws IOException {
        if (this.isDirectory()) {
            throw new IOException("Path to directory given \"" +
                    getCanonicalOrSimplePath() + "\" but expected path to " +
                    this.identifier + " file");
        }
    }

    /**
     * Workaround for unix systems: alternatively check if file is symbolic link, because exists() will always return false in that case
     */
    @Override
    public boolean exists() {
        return super.exists() || Files.isSymbolicLink(this.toPath());
    }

    /**
     * Expect that this IS a directory
     *
     * @throws IOException
     */
    private void expectIsADirectory() throws IOException {
        if (!this.isDirectory()) {
            throw new IOException("Path to " + this.identifier +
                    " directory expected, but got \"" +
                    getCanonicalOrSimplePath() + "\" which is not a directory");
        }
    }

    /**
     * Creates a new file if the file does not exists and checks if the file is writable.
     */
    public void expectFileIsWritable() throws IOException {
        if (!this.exists()) {
            boolean creationFailed;
            try {
                creationFailed = !this.createNewFile();

            } catch (IOException e) {
                creationFailed = true;
            }
            if (creationFailed) {
                throw new IOException("Could not create " + this.identifier +
                        " file \"" +
                        getCanonicalOrSimplePath() +
                        "\" due to security or right restrictions");
            }
        }
        this.expectIsWritable();
    }

    /**
     * Expect this is a file which is readable
     */
    public void expectFileIsReadable() throws IOException {
        this.expectIsReadable();
        this.expectNotADirectory();
    }

    /**
     * Expect this is a file which is read- and writable
     */
    public void expectFileIsReadAndWritable() throws IOException {
        this.expectFileIsReadable();
        this.expectFileIsWritable();
    }

    /**
     * Expect this is a directory which is readable
     */
    public void expectDirIsReadable() throws IOException {
        this.expectIsReadable();
        this.expectIsADirectory();
    }

    /**
     * Expect this is a directory which is writable
     */
    public void expectDirIsWritable() throws IOException {
        this.expectIsWritable();
        this.expectIsADirectory();
    }

    /**
     * Expect this directory already exists
     *
     * @throws IOException
     */
    public void expectDirExists() throws IOException {
        if (!this.exists()) {
            throw new FileNotFoundException("Directory " + this.identifier + "\"" +
                    getCanonicalOrSimplePath() + "\" does not exist");
        }
        this.expectIsADirectory();
    }

    public List<IFile> listDirs() {
        final File[] files = this.listFiles((FileFilter) DefaultFileIgnoreFilter.getInstance());
        final List<IFile> dirs = new ArrayList<>(files.length);
        for (final File file : files) {
            if (file.isDirectory()) {
                dirs.add(new IFile(file));
            }
        }
        return dirs;
    }

    public IFile[] listIFiles() {
        return listIFiles(DefaultFileIgnoreFilter.getInstance());
    }

    public IFile[] listIFiles(final MultiFileFilter filter) {
        final String[] ss = list(filter);
        if (ss == null)
            return null;
        final int n = ss.length;
        final IFile[] fs = new IFile[n];
        for (int i = 0; i < n; i++) {
            fs[i] = new IFile(this, ss[i]);
        }
        Arrays.sort(fs);
        return fs;
    }

    /**
     * Return all files in the current directory that match the regular expression or null if no matched files are found.
     *
     * @param regExp
     * @return Selected files or null
     * @throws IOException
     */
    public List<IFile> getFilesInDirByRegex(final Pattern regExp,
            final boolean sort) throws IOException {
        this.expectDirIsReadable();
        final File[] filesInDir = this.listFiles();
        if (filesInDir == null) {
            return null;
        }

        final List<IFile> appFiles = new ArrayList<>();
        for (final File file : filesInDir) {
            if (file.isFile()
                    && DefaultFileIgnoreFilter.acceptFile(file)
                    && regExp.matcher(file.getName()).matches()) {
                appFiles.add(new IFile(file));
            }
        }

        if (appFiles.isEmpty()) {
            return null;
        } else if (sort) {
            Collections.sort(appFiles);
        }

        return appFiles;
    }

    /**
     * Return all files in this directory recursively that match the regular expression or null if no matched files are found.
     *
     * @param regExp
     *            Compiled versionRemovePattern
     * @return Selected files or null
     * @throws IOException
     */
    public List<IFile> getFilesInDirRecursiveByRegex(final Pattern regExp)
            throws IOException {
        return getFilesInDirRecursiveByRegex(regExp, 15, true);
    }

    /**
     * Return all files in this directory recursively that match the regular expression or null if no matched files are found.
     *
     * @param regExp
     *            Compiled versionRemovePattern
     * @param maxDepth
     *            Max depth for subdirs
     * @param sort
     *            The output is unsorted in Unix OS! Force sorting.
     * @return Selected files or null
     * @throws IOException
     */
    public List<IFile> getFilesInDirRecursiveByRegex(final Pattern regExp,
            final int maxDepth, final boolean sort) throws IOException {
        this.expectDirIsReadable();
        final List<IFile> appFiles = new ArrayList<>();

        final File[] filesInDir = this.listFiles();
        if (filesInDir != null) {
            for (final File file : filesInDir) {
                if (DefaultFileIgnoreFilter.acceptFile(file)) {
                    if (file.isFile() && regExp.matcher(file.getName()).matches()) {
                        appFiles.add(new IFile(file));
                    } else if (maxDepth >= 1 && file.isDirectory()) {
                        final List<IFile> subDirFiles = new IFile(file).getFilesInDirRecursiveByRegex(regExp, maxDepth - 1,
                                false);
                        if (subDirFiles != null) {
                            appFiles.addAll(subDirFiles);
                        }
                    }
                }
            }
        }

        if (appFiles.isEmpty()) {
            return null;
        } else if (sort) {
            Collections.sort(appFiles);
        }

        return appFiles;
    }

    public List<IFile> getFilesInDirRecursive()
            throws IOException {
        return getFilesInDirRecursive(5, true);
    }

    public List<IFile> getFilesInDirRecursive(final int maxDepth, final boolean sort) throws IOException {
        return getFilesInDirRecursive(DefaultFileIgnoreFilter.getInstance(), maxDepth, sort);
    }

    public List<IFile> getFilesInDirRecursive(final PathFilter filter, int maxDepth, final boolean sort) throws IOException {
        this.expectDirIsReadable();
        final List<IFile> appFiles = new ArrayList<>();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(toPath())) {
            for (final Path file : stream) {
                if (maxDepth >= 1 && Files.isDirectory(file)) {
                    final List<IFile> subDirFiles = new IFile(file.toFile()).getFilesInDirRecursive(
                            filter, maxDepth - 1, false);
                    if (subDirFiles != null) {
                        appFiles.addAll(subDirFiles);
                    }
                } else if (filter == null || filter.accept(file)) {
                    appFiles.add(new IFile(file.toFile()));
                }
            }
        }
        if (appFiles.isEmpty()) {
            return null;
        } else if (sort) {
            Collections.sort(appFiles);
        }
        return appFiles;
    }

    /**
     * Returns a compiled Pattern for matching file extensions
     *
     * @param ext
     *            File extension String without "."
     * @return Compiled Pattern
     */
    public static Pattern getRegexForExtension(final String ext) {
        return Pattern.compile("([^\\s]+(\\.(?i)(" + ext + "))$)");
    }

    /**
     * Ensure that all necessary parent directories are available
     *
     * @return the directory
     * @throws IOException
     */
    public IFile ensureDir() throws IOException {
        this.mkdirs();
        if (!this.exists()) {
            throw new IOException("Unable to create "
                    + this.identifier + " directory path \"" +
                    getCanonicalOrSimplePath() +
                    "\" due to security or right restrictions");
        }
        return this;
    }

    /**
     * Returns all files in a directory which were modified before the given time.
     *
     * @param time
     *            in milliseconds
     * @return a List of files
     * @throws IOException
     */
    public List<IFile> getModifiedFilesInDirBeforeTime(final long time)
            throws IOException {
        this.expectDirIsReadable();
        final File[] files = this.listFiles();
        final List<IFile> filesModifiedBefore = new ArrayList<IFile>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].lastModified() < time) {
                filesModifiedBefore.add(new IFile(files[i]));
            }
        }
        return filesModifiedBefore;
    }

    /**
     * Deletes a directory recursively and returns true on success
     *
     * @return true on success
     * @throws IOException
     */
    public boolean deleteDirectory() throws IOException {
        if (this.exists()) {
            final File[] files = this.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        new IFile(files[i]).deleteDirectory();
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return this.delete();
    }

    /**
     * Unconditionally close a closeable object and ignore errors. ! Use with care !
     */
    @Deprecated
    public static void closeQuietly(final Closeable closeable) {
        IoUtils.closeQuietly(closeable);
    }

    /**
     * Compresses a file or directory as GZIP
     *
     * @param destPath
     *            Output path for the compressed data
     * @throws IOException
     */
    public void compressTo(final String destPath) throws IOException {
        final IFile targetFile = new IFile(destPath);
        OutputStream ostream = null;
        InputStream istream = null;
        try {
            targetFile.expectFileIsWritable();
            ostream = new GZIPOutputStream(new FileOutputStream(targetFile));
            istream = new FileInputStream(this);
            copy(istream, ostream, 8192);
        } catch (final IOException e) {
            this.expectIsReadable();
            throw new IOException("Compression of " + this.identifier +
                    " \"" + getCanonicalOrSimplePath() +
                    "\" failed: " + e.getMessage());
        } finally {
            // Close opened streams and ignore errors
            IoUtils.closeQuietly(istream);
            IoUtils.closeQuietly(ostream);
        }
    }

    /**
     * Compresses a file or directory as GZIP
     *
     * @param outputStream
     *            Output stream for the compressed data
     * @throws IOException
     */
    public void compressTo(final OutputStream outputStream) throws IOException {
        final ZipOutputStream wrappedOut = new ZipOutputStream(outputStream);
        final byte[] buffer = new byte[8192];

        final List<IFile> files;
        if (this.isDirectory()) {
            files = this.getFilesInDirRecursive();
        } else {
            files = Collections.singletonList(this);
        }
        try {
            compressAndStream(wrappedOut, buffer, files);
        } catch (final IOException e) {
            this.expectIsReadable();
            throw new IOException("Compression of " + this.identifier +
                    " \"" + getCanonicalOrSimplePath() +
                    "\" failed: " + e.getMessage());
        } finally {
            IoUtils.closeQuietly(wrappedOut);
        }
    }

    /**
     * Compresses a file or directory as GZIP
     *
     * @param outputStream
     *            Output stream for the compressed data
     * @throws IOException
     */
    public static void compressTo(final List<IFile> files, final OutputStream outputStream) throws IOException {
        final ZipOutputStream wrappedOut = new ZipOutputStream(outputStream);
        final byte[] buffer = new byte[8192];

        final Set<IFile> allFiles = new HashSet<>();
        for (final IFile file : files) {
            allFiles.addAll(file.getFilesInDirRecursive());
        }
        try {
            compressAndStream(wrappedOut, buffer, allFiles);
        } catch (final IOException e) {
            throw new IOException("Compression failed: " + e.getMessage());
        } finally {
            IoUtils.closeQuietly(wrappedOut);
        }
    }

    private static void compressAndStream(final ZipOutputStream wrappedOut, final byte[] buffer, final Collection<IFile> files)
            throws IOException {
        for (final IFile file : files) {
            final FileInputStream in = new FileInputStream(file);
            final ZipEntry entry = new ZipEntry(file.getPath());
            wrappedOut.putNextEntry(entry);
            copy(in, wrappedOut, buffer);
            in.close();
        }
    }

    /**
     * Fast file copy via Java FileChannel
     *
     * @throws IOException
     */
    public IFile copyTo(final String destPath) throws IOException {
        final IFile targetFile = new IFile(destPath);
        targetFile.expectFileIsWritable();
        if (!Files.isSameFile(this.toPath(), targetFile.toPath())) {
            final FileInputStream fileInputStream = new FileInputStream(this);
            final FileChannel inChannel = fileInputStream.getChannel();
            final FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            final FileChannel outChannel = fileOutputStream.getChannel();

            try {
                inChannel.transferTo(0, inChannel.size(),
                        outChannel);
            } catch (final IOException e) {
                this.expectIsReadable();
                throw new IOException("Copying of " + this.identifier +
                        " \"" + getCanonicalOrSimplePath() +
                        " \" to \"" + destPath +
                        "\" failed: " + e.getMessage());
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
        }
        return targetFile;
    }

    /**
     * Moves a file
     *
     * @param destPath
     *            destination path
     * @throws IOException
     */
    public void moveTo(final String destPath) throws IOException {
        moveTo(destPath, false);
    }

    /**
     * Moves a file
     *
     * @param destPath
     *            destination path
     * @param overwrite
     *            overwrite an existing file
     * @throws IOException
     */
    public void moveTo(final String destPath, boolean overwrite) throws IOException {
        final IFile targetFile = new IFile(destPath, "MOVE_TARGET");
        if (targetFile.exists()) {
            if (Files.isSameFile(this.toPath(), targetFile.toPath())) {
                return;
            } else if (overwrite) {
                targetFile.expectNotADirectory();
                targetFile.delete();
            } else {
                throw new IOException("Moving of " + this.identifier +
                        " \"" + getCanonicalOrSimplePath() +
                        " \" to \"" + destPath +
                        "\" failed: destination file already exists");
            }
        }
        targetFile.expectFileIsWritable();
        if (!this.renameTo(targetFile)) {
            // Renaming failed. This might happen if the file is moved
            // from one filesystem to another. Workaround: copy and delete
            this.copyTo(destPath);
            this.delete();
        }
    }

    /**
     * Create a temporary copy that will be deleted on exit
     */
    public IFile createTempCopy(final String tmpPrefix, final String tmpSuffix)
            throws IOException {
        final IFile tmpFile = IFile.createTempFile(tmpPrefix, tmpSuffix);
        tmpFile.deleteOnExit();
        tmpFile.setIdentifier(this.identifier + " (temporary copy of \"" +
                this.getCanonicalOrSimplePath() + "\")");
        this.copyTo(tmpFile.getPath());
        return tmpFile;
    }

    /**
     * Create a temporary copy in a specified dir that will be deleted on exit
     */
    public IFile createTempCopy(
            final String tmpPrefix, final String tmpSuffix, final IFile dir)
            throws IOException {
        dir.expectDirIsWritable();
        final IFile tmpFile = new IFile(
                File.createTempFile(tmpPrefix + "_", tmpSuffix, dir));
        tmpFile.deleteOnExit();
        tmpFile.setIdentifier(this.identifier + " (temporary copy of \"" +
                this.getCanonicalOrSimplePath() + "\")");
        this.copyTo(tmpFile.getPath());
        return tmpFile;
    }

    @Deprecated
    public String getNameWithoutExt() throws IOException {
        return this.getFilenameWithoutExt();
    }

    /**
     * Return the name of the file without the extension
     *
     * @return The filename without extension after the last dot
     * @throws IOException
     */
    public String getFilenameWithoutExt() throws IOException {
        this.expectNotADirectory();
        String fileNameWithoutExt = this.getName();
        final int dotPos = fileNameWithoutExt.lastIndexOf('.');
        if (dotPos != -1) {
            fileNameWithoutExt = fileNameWithoutExt.substring(0, dotPos);
        }
        return fileNameWithoutExt;
    }

    /**
     * Return the extension including a dot character
     *
     * @return The filename extension after the last dot
     * @throws IOException
     */
    public String getFileExtension() throws IOException {
        this.expectNotADirectory();
        String extension = "";
        final int dotPos = this.getName().lastIndexOf('.');
        if (dotPos != -1) {
            return this.getName().substring(dotPos);
        }
        return extension;
    }

    private static String getBasename(final String path) {
        if (SUtils.isNullOrEmpty(path)) {
            return null;
        }
        final int lastUnixPos = path.lastIndexOf('/');
        final int lastWindowsPos = path.lastIndexOf('\\');
        final int lastSlashPos = Math.max(lastUnixPos, lastWindowsPos);
        if (lastSlashPos == -1) {
            return path;
        }
        return path.substring(lastSlashPos + 1);
    }

    /**
     * Return the name of the file without the extension and version
     *
     * @return The filename without extension after the last dot
     * @throws IOException
     */
    public static String getFilenameWithoutExtAndVersion(final String path) {
        return versionRemovePattern.matcher(getBasename(path)).replaceAll("");
    }

    public static class VersionedFileList extends ArrayList<IFile> {

        private static Pattern fileNameVersionExt = Pattern.compile(
                "(.*)-((\\d+)\\.(\\d+)(\\.\\d+)?(\\.\\d+)?(-\\w+)?)(\\.\\w+)?");

        private final Map<String, Pair<Version, IFile>> latestVersionedFiles = new LinkedHashMap<>();

        public VersionedFileList(final IFile[] files) {
            super(Arrays.asList(files));
            for (final IFile file : files) {
                final Matcher matcher = fileNameVersionExt.matcher(file.getName());
                if (matcher.matches()) {
                    final String ext = matcher.group(8);
                    final String basename = matcher.group(1) + (ext != null ? ext : "");
                    final Version version;
                    try {
                        version = new Version(matcher.group(2));
                    } catch (IllegalArgumentException e) {
                        ExcUtils.suppress(e);
                        continue;
                    }
                    final Pair<Version, IFile> existing = this.latestVersionedFiles.get(basename);
                    if (existing != null && existing.getKey().compareTo(version) > 0) {
                        continue;
                    }
                    this.latestVersionedFiles.put(basename, new Pair<>(version, file));
                } else {
                    this.latestVersionedFiles.put(file.getName(), new Pair<>(null, file));
                }
            }
        }

        /**
         * If multiple file with different versions exist, only the latest version is returned
         *
         * @return latest versions
         */
        public List<IFile> latest() {
            return latestVersionedFiles.values().stream().map(Pair::getValue).collect(Collectors.toList());
        }

        /**
         * Returns true if the file is newer than the file in the versioned list or if the file does not exist in the list, false otherwise
         *
         * @param fileName
         *            file
         * @return true if the file is newer or does not exist in list, false otherwise
         */
        public boolean isNewer(final String fileName) {
            final String basename = getBasename(fileName);
            final Matcher matcher = fileNameVersionExt.matcher(basename);
            if (matcher.matches()) {
                final Version version;
                try {
                    version = new Version(matcher.group(2));
                } catch (IllegalArgumentException e) {
                    return !this.latestVersionedFiles.containsKey(basename);
                }
                final String ext = matcher.group(8);
                final Pair<Version, IFile> existing = this.latestVersionedFiles.get(
                        matcher.group(1) + (ext != null ? ext : ""));
                if (existing != null && existing.getKey() != null) {
                    return existing.getKey().compareTo(version) < 0;
                } else {
                    return true;
                }
            }
            return !this.latestVersionedFiles.containsKey(basename);
        }

        /**
         * Returns true if the file with or without a version exists
         *
         * @param fileName
         * @return
         */
        public boolean anyVersionExists(final String fileName) {
            final String basename = getBasename(fileName);
            final Matcher matcher = fileNameVersionExt.matcher(basename);
            if (matcher.matches()) {
                final String ext = matcher.group(8);
                return this.latestVersionedFiles.containsKey(
                        matcher.group(1) + (ext != null ? ext : ""));
            }
            return this.latestVersionedFiles.containsKey(basename);
        }

        /**
         * Returns true if the file is newer than the file in the versioned list or if the file does not exist in the list
         *
         * @param file
         *            file
         * @return true if the file is newer or does not exist in list, false otherwise
         */
        public boolean isNewer(final IFile file) {
            return isNewer(file.getPath());
        }
    }

    /**
     * Returns all files in a directory with respect to its versions
     *
     * The key of the Map is the basename of the files.
     *
     * @see #getFilenameWithoutExt()
     * @throws IOException
     *             if this is not a directory or does not exist
     * @return VersionedFileList
     */
    public VersionedFileList getVersionedFilesInDir(final MultiFileFilter filter) throws IOException {
        expectDirExists();
        return new VersionedFileList(this.listIFiles(filter));
    }

    /**
     * Returns all files in a directory with respect to its versions
     *
     * The key of the Map is the basename of the files.
     *
     * @see #getFilenameWithoutExt()
     * @throws IOException
     *             if this is not a directory or does not exist
     * @return VersionedFileList
     */
    public VersionedFileList getVersionedFilesInDir() throws IOException {
        return getVersionedFilesInDir(DefaultFileIgnoreFilter.getInstance());
    }

    /**
     * Unzips a zip file to a destination directory
     *
     * @param destDir
     * @throws IOException
     */
    public void unzipTo(final IFile destDir) throws IOException {
        unzipTo(destDir, DefaultFileIgnoreFilter.getInstance(), defaultMaxUnzipSize);
    }

    public void unzipTo(final IFile destDir, final FileFilter filter) throws IOException {
        unzipTo(destDir, filter, defaultMaxUnzipSize);
    }

    /**
     * Unzips a zip file to a destination directory
     *
     * @param destDir
     * @param filter
     * @throws IOException
     */
    public void unzipTo(final IFile destDir, final FileFilter filter, final long maxSize) throws IOException {
        // Unzip
        ZipFile zipFile = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            zipFile = new ZipFile(this);
            final Enumeration<? extends ZipEntry> enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                final ZipEntry zipEntry = enu.nextElement();
                final IFile destFile = new IFile(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    destFile.ensureDir();
                    continue;
                } else {
                    if (filter != null && !(filter.accept(destFile) ||
                            filter.accept(destFile.getParentFile()))) {
                        continue;
                    }
                    new IFile(destFile.getParent()).ensureDir();
                }
                is = zipFile.getInputStream(zipEntry);
                fos = new FileOutputStream(destFile);
                copySecure(is, fos, 4096, maxSize);
            }
        } finally {
            IoUtils.closeQuietly(fos);
            IoUtils.closeQuietly(is);
            IoUtils.closeQuietly(zipFile);
        }
    }

    public void gunzipTo(final IFile destFile) throws IOException {
        destFile.expectFileIsWritable();
        final FileOutputStream fos = new FileOutputStream(destFile);
        gunzipTo(fos, defaultMaxUnzipSize);
    }

    public void gunzipTo(final OutputStream outputStream) throws IOException {
        gunzipTo(outputStream, defaultMaxUnzipSize);
    }

    public void gunzipTo(final OutputStream outputStream, final long maxSize) throws IOException {
        GZIPInputStream gzis = null;
        try {
            gzis = new GZIPInputStream(new FileInputStream(this), 1024);
            copySecure(gzis, outputStream, 4096, maxSize);
        } finally {
            IoUtils.closeQuietly(outputStream);
            IoUtils.closeQuietly(gzis);
        }
    }

    public boolean isGZipped() {
        int magic = 0;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this, "r");
            magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
        } catch (IOException e) {
            ExcUtils.suppress(e);
        } finally {
            IoUtils.closeQuietly(raf);
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }

    /**
     * Creates a symbolic link of this file Only supports Linux yet with bad performance
     *
     * @deprecated Use Files.createSymbolicLink();
     *
     * @param destPath
     *            path where the link to this file or directory is stored
     */
    @Deprecated
    public void createLink(final String destPath)
            throws IOException {
        if (SystemUtils.IS_OS_LINUX) {
            final Process process = Runtime.getRuntime().exec(
                    new String[]{"/bin/ln", "-sfn",
                            this.getAbsolutePath(),
                            destPath});
            try {
                final int errCode = process.waitFor();
                if (errCode != 0) {
                    throw new IOException("Link creation of " +
                            this.getCanonicalOrSimplePath() + " to target" +
                            destPath + " failed with error code " + errCode);
                }
                process.waitFor();
            } catch (InterruptedException e) {
                throw new IOException();
            }
            process.destroy();
        }
    }

    /**
     * Open the file with the system default charset and return a StringBuffer object
     *
     * @return the content of the file represented as StringBuffer object
     * @throws IOException
     */
    public StringBuffer readContent() throws IOException {
        return readContent(null);
    }

    /**
     * Open the file with the specific charset and return a StringBuffer object
     *
     * @param charset
     *            The charset used to read the file content
     * @return the content of the file represented as StringBuffer object
     * @throws IOException
     */
    public StringBuffer readContent(final String charset)
            throws IOException {
        this.expectFileIsReadable();
        final StringBuffer fileData = new StringBuffer(1024);
        BufferedReader reader = null;
        try {
            final InputStream fInput;
            if (this.isGZipped()) {
                fInput = new GZIPInputStream(new FileInputStream(this));
            } else {
                fInput = new FileInputStream(this);
            }
            final InputStreamReader fStrReader;
            if (charset != null) {
                fStrReader = new InputStreamReader(fInput, charset);
            } else {
                fStrReader = new InputStreamReader(fInput);
            }
            reader = new BufferedReader(fStrReader);
            final char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
        } catch (IOException e) {
            throw new IOException("Reading file content of " +
                    this.identifier + " \"" +
                    this.getCanonicalOrSimplePath() + "\" failed: " +
                    e.getMessage());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return fileData;
    }

    /**
     * Write content represented as StringBuffer object to the file with the system default charset
     *
     * @param content
     *            content as StringBuffer object that will be written to the file
     * @throws IOException
     */
    public void writeContent(final StringBuffer content)
            throws IOException {
        writeContent(content, "UTF-8");
    }

    /**
     * Write content represented as StringBuffer object to the file with a specific charset
     *
     * @param content
     *            content as StringBuffer object that will be written to the file
     * @param charset
     *            The charset used to write the file content
     * @throws IOException
     */
    public void writeContent(final StringBuffer content, final String charset)
            throws IOException {
        BufferedWriter writer = null;
        try {
            final FileOutputStream fOutput = new FileOutputStream(this);
            final OutputStreamWriter fStrWriter;
            if (charset != null) {
                fStrWriter = new OutputStreamWriter(fOutput, charset);
            } else {
                fStrWriter = new OutputStreamWriter(fOutput);
            }
            writer = new BufferedWriter(fStrWriter);
            writer.write(content.toString());
        } catch (IOException e) {
            throw new IOException("Writing file content to " +
                    this.identifier + " \""
                    + this.getCanonicalOrSimplePath() + "\" failed ", e);
        } finally {
            IoUtils.closeQuietly(writer);
        }
    }

    /**
     * Write from an inputStream to a File in a specific charset
     *
     * Note: Will close the inputStream afterwards!
     *
     * @param inputStream
     * @param inputCharset
     * @throws IOException
     */
    public void writeContent(final InputStream inputStream, final String inputCharset) throws IOException {
        BufferedWriter writer;
        Reader reader = null;
        try {
            final FileOutputStream fOutput = new FileOutputStream(this);
            writer = new BufferedWriter(new OutputStreamWriter(fOutput, "UTF-8"));
            if (inputCharset == null) {
                reader = new InputStreamReader(inputStream, inputCharset);
            } else {
                reader = new InputStreamReader(inputStream, "UTF-8");
            }

            int read;
            final char[] buffer = new char[1024];
            while ((read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
            }
            writer.flush();
        } catch (IOException e) {
            throw new IOException("Writing file content to " +
                    this.identifier + " \""
                    + this.getCanonicalOrSimplePath() + "\" failed", e);
        } finally {
            IoUtils.closeQuietly(inputStream);
            IoUtils.closeQuietly(reader);
        }
    }

    /**
     * Write from an inputStream to a File in a specific charset
     *
     * Note: Will close the inputStream afterwards!
     *
     * @param inputStream
     * @param inputCharset
     * @throws IOException
     */
    public void writeContentSecure(final InputStream inputStream, final String inputCharset, final long maxSize)
            throws IOException {
        BufferedWriter writer;
        Reader reader = null;
        try {
            final FileOutputStream fOutput = new FileOutputStream(this);
            writer = new BufferedWriter(new OutputStreamWriter(fOutput, "UTF-8"));
            if (inputCharset == null) {
                reader = new InputStreamReader(inputStream, inputCharset);
            } else {
                reader = new InputStreamReader(inputStream, "UTF-8");
            }

            int read;
            long charsRead = 0;
            // char = 2 bytes
            long max = maxSize / 2;
            final char[] buffer = new char[1024];
            while ((read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
                charsRead += read;
                if (charsRead > max) {
                    throw new IOsizeLimitExceededException(maxSize);
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new IOException("Writing file content to " +
                    this.identifier + " \""
                    + this.getCanonicalOrSimplePath() + "\" failed", e);
        } finally {
            IoUtils.closeQuietly(inputStream);
            IoUtils.closeQuietly(reader);
        }
    }

    /**
     * Write from an inputStream to a File
     *
     * Note: Will close the inputStream afterwards!
     *
     * @param inputStream
     * @throws IOException
     */
    public void write(final InputStream inputStream) throws IOException {
        try (final FileOutputStream fOutput = new FileOutputStream(this)) {
            copy(inputStream, fOutput, 2048);
        } catch (IOException e) {
            throw new IOException("Writing file content to " +
                    this.identifier + " \""
                    + this.getCanonicalOrSimplePath() + "\" failed", e);
        } finally {
            IoUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Write from an inputStream to a File
     *
     * Note: Will close the inputStream afterwards!
     *
     * @param inputStream
     * @throws IOException
     */
    public void writeSecure(final InputStream inputStream, final long maxSize) throws IOException {
        try (final FileOutputStream fOutput = new FileOutputStream(this)) {
            copySecure(inputStream, fOutput, 2048, maxSize);
        } catch (IOException e) {
            throw new IOException("Writing file content to " +
                    this.identifier + " \""
                    + this.getCanonicalOrSimplePath() + "\" failed", e);
        } finally {
            IoUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Creates a copy of the original file with the ending ".bak"
     *
     * @param originalFile
     * @return
     * @throws IOException
     */
    public static IFile createBackup(final IFile originalFile) throws IOException {
        final IFile bakFile = new IFile(originalFile.getAbsolutePath() + ".bak");
        if (bakFile.exists()) {
            throw new IOException("Cannot create backup, file \"" +
                    bakFile.getAbsolutePath() + "\" already exists");
        }
        originalFile.copyTo(bakFile.getAbsolutePath());
        return bakFile;
    }

    public static IFile createBackup(final File originalFile) throws IOException {
        return createBackup(new IFile(originalFile));
    }

    /**
     * @see java.io.File#createTempFile(String, String)
     * @throws IOException
     */
    public static IFile createTempFile(final String prefix, final String suffix)
            throws IOException {
        final IFile file = new IFile(File.createTempFile(prefix + "_", suffix));
        deleteOnExit(file);
        return file;
    }

    /**
     * @see java.io.File#createTempFile(String, String, File)
     * @throws IOException
     */
    public static IFile createTempFile(
            final String prefix, final String suffix, final IFile dir)
            throws IOException {
        final IFile file = new IFile(File.createTempFile(prefix + "_", suffix, dir));
        deleteOnExit(file);
        return file;
    }

    /**
     * Creates a temporary directory with a prefix and suffix
     *
     * @see java.io.File#createTempFile(String, String)
     * @throws IOException
     */
    public static IFile createTempDir(final String prefix)
            throws IOException {
        final IFile dir = new IFile(Files.createTempDirectory(prefix).toString(), "tmp");
        deleteOnExit(dir);
        return dir;
    }

    public static void deleteOnExit(final File file) {
        if (filesToDeleteOnExit.isEmpty()) {
            Runtime.getRuntime().addShutdownHook(new DeleteFilesHook());
        }
        filesToDeleteOnExit.add(file.getAbsolutePath());
    }

    private static String replaceSpecialChars(String str) {
        for (int i = 0; i < REPLACEMENTS.length; i++) {
            str = REPLACEMENTS[i].getLeft().matcher(str).replaceAll(
                    Matcher.quoteReplacement(REPLACEMENTS[i].getRight()));
        }
        return str.trim();
    }

    /**
     * Returns a string usable as filename
     */
    public static String sanitize(final String name) {
        if (null == name) {
            return "";
        }

        if (SystemUtils.IS_OS_UNIX) {
            return replaceSpecialChars(name.replaceAll("/+", ""));
        }

        return replaceSpecialChars(
                name.replaceAll("[\u0001-\u001f<>:\"/\\\\|?*\u007f]+", ""));
    }

    public InputStream getInputStream() throws IOException {
        if (isGZipped()) {
            return new GZIPInputStream(new FileInputStream(this), 1024);
        } else {
            return new FileInputStream(this);
        }
    }
}
