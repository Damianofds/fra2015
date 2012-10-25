/*
 * ====================================================================
 *
 * Copyright (C) 2007 - 2012 GeoSolutions S.A.S.
 * http://www.geo-solutions.it
 *
 * GPLv3 + Classpath exception
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. 
 *
 * ====================================================================
 *
 * This software consists of voluntary contributions made by developers
 * of GeoSolutions.  For more information on GeoSolutions, please see
 * <http://www.geo-solutions.it/>.
 *
 */
package it.geosolutions.geostore.core.model;

import it.geosolutions.geostore.core.model.enums.DataType;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

/**
 * Class Attribute.
 *
 * @author Tobia di Pisa (tobia.dipisa at geo-solutions.it)
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Entity(name = "Attribute")
@Table(name = "gs_attribute",
uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "resource_id"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "gs_attribute")
@XmlRootElement(name = "Attribute")
public class Attribute implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -1298676702253831972L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "name", nullable = false, updatable = true)
    @Index(name = "idx_attribute_name")
    private String name;
    @Column(name = "string", nullable = true, updatable = true)
    @Index(name = "idx_attribute_text")
    private String textValue;
    @Column(name = "number", nullable = true, updatable = true)
    @Index(name = "idx_attribute_number")
    private Double numberValue;
    @Column(name = "date", nullable = true, updatable = true)
    @Index(name = "idx_attribute_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateValue;
    @Column(name = "type", nullable = false, updatable = false)
    @Index(name = "idx_attribute_type")
    @Enumerated(EnumType.STRING)
    private DataType type;
    @ManyToOne(optional = false)
    @Index(name = "idx_attribute_resource")
    @ForeignKey(name = "fk_attribute_resource")
    private Resource resource;
    /**
     * Only used for XML un/marshalling
     */
    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * @throws Exception
     */
    @PreUpdate
    @PrePersist
    public void onPreUpdate() throws Exception {
        if (textValue == null && numberValue == null && dateValue == null) {
            throw new NullPointerException("Null value not allowed in attribute: " + this.toString());

        } else if ((this.textValue == null && (this.numberValue != null ^ this.dateValue != null))
                || (this.numberValue == null && (this.textValue != null ^ this.dateValue != null))
                || (this.dateValue == null && (this.textValue != null ^ this.numberValue != null))) {
            this.type = this.textValue != null ? DataType.STRING : (this.numberValue != null ? DataType.NUMBER : DataType.DATE);

        } else {
            throw new Exception("Only one DataType can be not-null inside the Attribute entity: " + this.toString());

        }
    }

    /**
     * @return the id
     */
    @XmlTransient
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the attribute to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the textValue
     */
    @XmlTransient
    public String getTextValue() {
        return textValue;
    }

    /**
     * @param textValue the textValue to set
     */
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    /**
     * @return the numberValue
     */
    @XmlTransient
    public Double getNumberValue() {
        return numberValue;
    }

    /**
     * @param numberValue the numberValue to set
     */
    public void setNumberValue(Double numberValue) {
        this.numberValue = numberValue;
    }

    /**
     * @return the dateValue
     */
    @XmlTransient
    public Date getDateValue() {
        return dateValue;
    }

    /**
     * @param dateValue the dateValue to set
     */
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * Only used for XML marshalling
     */
    @Transient
    @XmlElement
    public String getValue() {

        switch (type) {
            case DATE:
                return DATE_FORMAT.format(dateValue);
            case NUMBER:
                return numberValue.toString();
            case STRING:
                return textValue.toString();
            default:
                throw new IllegalStateException("Unknown type " + type);
        }
    }

    /**
     * Only used for XML unmarshalling
     */
    protected void setValue(String text) {
        if (type != null) {
            setValue(text, type);
        } else {
            throw new IllegalStateException("Setting value with no type selected");
        }
    }

    protected void setValue(String text, DataType type) {
        switch (type) {
            case DATE:
                try {
                    dateValue = DATE_FORMAT.parse(text);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Can't parse date [" + text + "]", e);
                }
                break;
            case NUMBER:
                try {
                    numberValue = Double.valueOf(text);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Can't parse double [" + text + "]", e);
                }
                break;
            case STRING:
                textValue = text;
                break;
            default:
                throw new IllegalStateException("Unknown type " + type);
        }


    }

    /**
     * THe XMLAttribute annotation is to make sure that type will be
     * unmarshalled before value.
     *
     * @return the type
     */
    @XmlAttribute
    public DataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(DataType type) {
        this.type = type;
    }

    /**
     * @return the resource
     */
    @XmlTransient
    public Resource getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append('[');

        if (id != null) {
            builder.append("id=").append(id);
        } else {
            builder.append("id is null");
        }

        if (name != null) {
            builder.append(", name=").append(name);
        }

        if (textValue != null) {
            builder.append(", textValue=").append(textValue);
        }

        if (numberValue != null) {
            builder.append(", numberValue=").append(numberValue);
        }

        if (dateValue != null) {
            builder.append(", dateValue=").append(dateValue);
        }

        if (textValue == null && numberValue == null && dateValue == null) {
            builder.append(", value is null");
        }

        if (type != null) {
            builder.append(", type=").append(type);
        }

        builder.append(']');
        return builder.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.textValue != null ? this.textValue.hashCode() : 0);
        hash = 53 * hash + (this.numberValue != null ? this.numberValue.hashCode() : 0);
        hash = 53 * hash + (this.dateValue != null ? this.dateValue.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 53 * hash + (this.resource != null ? this.resource.hashCode() : 0);
        return hash;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.textValue == null) ? (other.textValue != null) : !this.textValue.equals(other.textValue)) {
            return false;
        }
        if (this.numberValue != other.numberValue && (this.numberValue == null || !this.numberValue.equals(other.numberValue))) {
            return false;
        }
        if (this.dateValue != other.dateValue && (this.dateValue == null || !this.dateValue.equals(other.dateValue))) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.resource != other.resource && (this.resource == null || !this.resource.equals(other.resource))) {
            return false;
        }
        return true;
    }
}
