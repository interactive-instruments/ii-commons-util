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
package de.interactive_instruments.jaxb.adapters;

import java.io.File;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.interactive_instruments.IFile;

/**
 * Adapter for serializing the IFile class.
 *
 * @author Jon Herrmann (herrmann@interactive-instruments.de)
 * @version 1.0
 * @since 1.0
 * @see de.interactive_instruments.IFile
 */
public final class IFileXmlAdapter extends XmlAdapter<File, IFile> {
	@Override
	public IFile unmarshal(final File file) {
		return new IFile(file.getPath());
	}

	@Override
	public File marshal(final IFile file) {
		return new File(file.getPath());
	}
}
