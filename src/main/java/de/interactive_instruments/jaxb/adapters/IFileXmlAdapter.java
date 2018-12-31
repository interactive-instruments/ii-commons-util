/**
 * Copyright 2017-2018 European Union, interactive instruments GmbH
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
