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
import javax.xml.bind.annotation.XmlType;
import cz.cuni.mff.been.jaxb.AbstractSerializable;


/**
 * <p>Java class for FailurePolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FailurePolicy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://been.mff.cuni.cz/taskmanager/td}failurePolicyAttrGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FailurePolicy")
public class FailurePolicy
    extends AbstractSerializable
    implements Serializable
{

    private final static long serialVersionUID = -9223372036854775808L;
    @XmlAttribute(name = "restartMax")
    protected Integer restartMax;
    @XmlAttribute(name = "timeoutRun")
    protected Long timeoutRun;

    /**
     * Gets the value of the restartMax property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getRestartMax() {
        if (restartMax == null) {
            return  0;
        } else {
            return restartMax;
        }
    }

    /**
     * Sets the value of the restartMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRestartMax(int value) {
        this.restartMax = value;
    }

    public boolean isSetRestartMax() {
        return (this.restartMax!= null);
    }

    public void unsetRestartMax() {
        this.restartMax = null;
    }

    /**
     * Gets the value of the timeoutRun property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public long getTimeoutRun() {
        if (timeoutRun == null) {
            return  0L;
        } else {
            return timeoutRun;
        }
    }

    /**
     * Sets the value of the timeoutRun property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTimeoutRun(long value) {
        this.timeoutRun = value;
    }

    public boolean isSetTimeoutRun() {
        return (this.timeoutRun!= null);
    }

    public void unsetTimeoutRun() {
        this.timeoutRun = null;
    }

}
