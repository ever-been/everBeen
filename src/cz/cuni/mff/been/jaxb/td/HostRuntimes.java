//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.05 at 10:17:24 AM CEST 
//


package cz.cuni.mff.been.jaxb.td;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.jaxb.AbstractSerializable;


/**
 * <p>Java class for HostRuntimes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HostRuntimes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://been.mff.cuni.cz/taskmanager/td}asTask" minOccurs="0"/>
 *         &lt;element ref="{http://been.mff.cuni.cz/taskmanager/td}rsl" minOccurs="0"/>
 *         &lt;element ref="{http://been.mff.cuni.cz/taskmanager/td}name" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HostRuntimes", propOrder = {
    "asTask",
    "rsl",
    "name"
})
public class HostRuntimes
    extends AbstractSerializable
    implements Serializable
{

    private final static long serialVersionUID = -9223372036854775808L;
    protected String asTask;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected Condition rsl;
    protected List<String> name;

    /**
     * Gets the value of the asTask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsTask() {
        return asTask;
    }

    /**
     * Sets the value of the asTask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsTask(String value) {
        this.asTask = value;
    }

    public boolean isSetAsTask() {
        return (this.asTask!= null);
    }

    /**
     * Gets the value of the rsl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Condition getRSL() {
        return rsl;
    }

    /**
     * Sets the value of the rsl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRSL(Condition value) {
        this.rsl = value;
    }

    public boolean isSetRSL() {
        return (this.rsl!= null);
    }

    /**
     * Gets the value of the name property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the name property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getName() {
        if (name == null) {
            name = new ArrayList<String>();
        }
        return this.name;
    }

    public boolean isSetName() {
        return ((this.name!= null)&&(!this.name.isEmpty()));
    }

    public void unsetName() {
        this.name = null;
    }

}
