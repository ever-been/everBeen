//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.05 at 10:17:17 AM CEST 
//


package cz.cuni.mff.been.jaxb.benchmark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import cz.cuni.mff.been.jaxb.AbstractSerializable;


/**
 * <p>Java class for Evaluators complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Evaluators">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://been.mff.cuni.cz/benchmarkmanagerng/benchmark}evaluator" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://been.mff.cuni.cz/benchmarkmanagerng/benchmark}changedAttrGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Evaluators", propOrder = {
    "evaluator"
})
public class Evaluators
    extends AbstractSerializable
    implements Serializable
{

    private final static long serialVersionUID = -9223372036854775808L;
    protected List<GenEval> evaluator;
    @XmlAttribute(name = "changed")
    protected Boolean changed;

    /**
     * Gets the value of the evaluator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the evaluator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvaluator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenEval }
     * 
     * 
     */
    public List<GenEval> getEvaluator() {
        if (evaluator == null) {
            evaluator = new ArrayList<GenEval>();
        }
        return this.evaluator;
    }

    public boolean isSetEvaluator() {
        return ((this.evaluator!= null)&&(!this.evaluator.isEmpty()));
    }

    public void unsetEvaluator() {
        this.evaluator = null;
    }

    /**
     * Gets the value of the changed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isChanged() {
        if (changed == null) {
            return true;
        } else {
            return changed;
        }
    }

    /**
     * Sets the value of the changed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setChanged(boolean value) {
        this.changed = value;
    }

    public boolean isSetChanged() {
        return (this.changed!= null);
    }

    public void unsetChanged() {
        this.changed = null;
    }

}
