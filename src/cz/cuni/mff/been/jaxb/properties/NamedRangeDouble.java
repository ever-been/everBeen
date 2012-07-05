//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.05 at 10:17:23 AM CEST 
//


package cz.cuni.mff.been.jaxb.properties;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import cz.cuni.mff.been.jaxb.AbstractSerializable;


/**
 * <p>Java class for NamedRangeDouble complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NamedRangeDouble">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="lbound" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *                 &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}boundAttrGroup"/>
 *                 &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}unitAttrGroup"/>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ubound" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *                 &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}unitAttrGroup"/>
 *                 &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}boundAttrGroup"/>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *       &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}nameAttrGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamedRangeDouble", propOrder = {

})
public class NamedRangeDouble
    extends AbstractSerializable
    implements Serializable
{

    private final static long serialVersionUID = -9223372036854775808L;
    @XmlElement(name = "lbound")
    protected NamedRangeDouble.LBound lBound;
    @XmlElement(name = "ubound")
    protected NamedRangeDouble.UBound uBound;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the lBound property.
     * 
     * @return
     *     possible object is
     *     {@link NamedRangeDouble.LBound }
     *     
     */
    public NamedRangeDouble.LBound getLBound() {
        return lBound;
    }

    /**
     * Sets the value of the lBound property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedRangeDouble.LBound }
     *     
     */
    public void setLBound(NamedRangeDouble.LBound value) {
        this.lBound = value;
    }

    public boolean isSetLBound() {
        return (this.lBound!= null);
    }

    /**
     * Gets the value of the uBound property.
     * 
     * @return
     *     possible object is
     *     {@link NamedRangeDouble.UBound }
     *     
     */
    public NamedRangeDouble.UBound getUBound() {
        return uBound;
    }

    /**
     * Sets the value of the uBound property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedRangeDouble.UBound }
     *     
     */
    public void setUBound(NamedRangeDouble.UBound value) {
        this.uBound = value;
    }

    public boolean isSetUBound() {
        return (this.uBound!= null);
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    public boolean isSetName() {
        return (this.name!= null);
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
     *       &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}boundAttrGroup"/>
     *       &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}unitAttrGroup"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class LBound
        extends AbstractSerializable
        implements Serializable
    {

        private final static long serialVersionUID = -9223372036854775808L;
        @XmlValue
        @XmlJavaTypeAdapter(Adapter4 .class)
        @XmlSchemaType(name = "double")
        protected Double value;
        @XmlAttribute(name = "open")
        protected Boolean open;
        @XmlAttribute(name = "unit")
        protected String unit;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public Double getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(Double value) {
            this.value = value;
        }

        public boolean isSetValue() {
            return (this.value!= null);
        }

        /**
         * Gets the value of the open property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isOpen() {
            if (open == null) {
                return false;
            } else {
                return open;
            }
        }

        /**
         * Sets the value of the open property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setOpen(boolean value) {
            this.open = value;
        }

        public boolean isSetOpen() {
            return (this.open!= null);
        }

        public void unsetOpen() {
            this.open = null;
        }

        /**
         * Gets the value of the unit property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUnit() {
            return unit;
        }

        /**
         * Sets the value of the unit property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUnit(String value) {
            this.unit = value;
        }

        public boolean isSetUnit() {
            return (this.unit!= null);
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
     *       &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}unitAttrGroup"/>
     *       &lt;attGroup ref="{http://been.mff.cuni.cz/hostmanager/properties}boundAttrGroup"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class UBound
        extends AbstractSerializable
        implements Serializable
    {

        private final static long serialVersionUID = -9223372036854775808L;
        @XmlValue
        @XmlJavaTypeAdapter(Adapter3 .class)
        @XmlSchemaType(name = "double")
        protected Double value;
        @XmlAttribute(name = "unit")
        protected String unit;
        @XmlAttribute(name = "open")
        protected Boolean open;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public Double getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(Double value) {
            this.value = value;
        }

        public boolean isSetValue() {
            return (this.value!= null);
        }

        /**
         * Gets the value of the unit property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUnit() {
            return unit;
        }

        /**
         * Sets the value of the unit property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUnit(String value) {
            this.unit = value;
        }

        public boolean isSetUnit() {
            return (this.unit!= null);
        }

        /**
         * Gets the value of the open property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isOpen() {
            if (open == null) {
                return false;
            } else {
                return open;
            }
        }

        /**
         * Sets the value of the open property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setOpen(boolean value) {
            this.open = value;
        }

        public boolean isSetOpen() {
            return (this.open!= null);
        }

        public void unsetOpen() {
            this.open = null;
        }

    }

}
