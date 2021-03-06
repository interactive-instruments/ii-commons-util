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
package de.interactive_instruments.model.std;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import de.interactive_instruments.II_Constants;
import de.interactive_instruments.exceptions.ExcUtils;

/**
 * The default identifier implementation for domain model items.
 *
 * Serializable.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ID", namespace = II_Constants.XML_SCHEMA_NS_URI)
public class DefaultId implements Id {

    @XmlValue
    private String id;

    /**
     * Private C'tor
     */
    private DefaultId() {}

    /**
     * Creates an Id object from an string
     *
     * @param id
     *            string
     */
    DefaultId(final String id) {
        this.id = id;
    }

    /**
     * Creates an Id object from an UUID
     *
     * @param uuid
     *            UUID
     */
    DefaultId(final UUID uuid) {
        this.id = uuid.toString();
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the internal Id as string without generating an UUID!
     *
     * @param id
     *            as string
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the Id as string
     *
     * @return A string or an UUID as string
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * Returns the id in UUID representation if possible or returns a generated UUID hash
     *
     * @return UUID from string or UUID hash from string
     */
    public UUID toUuid() {
        try {
            if (id.length() == 36) {
                return UUID.fromString(id);
            }
        } catch (IllegalArgumentException e) {
            ExcUtils.suppress(e);
        }
        return UUID.nameUUIDFromBytes(id.getBytes());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Id) {
            return id.compareTo(((Id) o).getId());
        } else {
            return o.toString().compareTo(o.toString());
        }
    }

    /**
     * Compare an EID, a String or an UUID against an object
     *
     * @param obj
     *            the object to compare this {@code EID} against
     * @return {@code true} if the given object represents a {@code EID}, {@code String} or {@code UUID} equivalent to this Id, {@code false} otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Id) && !(obj instanceof String)) {
            return obj instanceof UUID && this.toUuid().equals(obj);
        }
        return this.id.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
