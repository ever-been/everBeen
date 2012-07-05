//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.05 at 10:17:24 AM CEST 
//


package cz.cuni.mff.been.jaxb.td;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import cz.cuni.mff.been.jaxb.AbstractSerializable;


/**
 * <p>Java class for TaskPropertyObject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskPropertyObject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://been.mff.cuni.cz/taskmanager/td}valGroup" minOccurs="0"/>
 *       &lt;attGroup ref="{http://been.mff.cuni.cz/taskmanager/td}taskPropertyObjectAttrGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskPropertyObject", propOrder = {
    "strVal",
    "binVal"
})
public class TaskPropertyObject
    extends AbstractSerializable
    implements Serializable
{

    private final static long serialVersionUID = -9223372036854775808L;
    @XmlElement(name = "strval")
    protected StrVal strVal;
    @XmlElement(name = "binval")
    protected byte[] binVal;
    @XmlAttribute(name = "key", required = true)
    protected String key;

    /**
     * Gets the value of the strVal property.
     * 
     * @return
     *     possible object is
     *     {@link StrVal }
     *     
     */
    public StrVal getStrVal() {
        return strVal;
    }

    /**
     * Sets the value of the strVal property.
     * 
     * @param value
     *     allowed object is
     *     {@link StrVal }
     *     
     */
    public void setStrVal(StrVal value) {
        this.strVal = value;
    }

    public boolean isSetStrVal() {
        return (this.strVal!= null);
    }

    /**
     * Gets the value of the binVal property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBinVal() {
        return binVal;
    }

    /**
     * Sets the value of the binVal property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBinVal(byte[] value) {
        this.binVal = ((byte[]) value);
    }

    public boolean isSetBinVal() {
        return (this.binVal!= null);
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

    public boolean isSetKey() {
        return (this.key!= null);
    }

}
