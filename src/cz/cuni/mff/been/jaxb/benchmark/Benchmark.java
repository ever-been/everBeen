//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.05 at 10:17:17 AM CEST 
//


package cz.cuni.mff.been.jaxb.benchmark;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import cz.cuni.mff.been.jaxb.AbstractSerializable;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://been.mff.cuni.cz/benchmarkmanagerng/benchmark}benchmarkGroup"/>
 *       &lt;attGroup ref="{http://been.mff.cuni.cz/benchmarkmanagerng/benchmark}nameAttrGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "desc",
    "rsl",
    "period",
    "generator",
    "evaluators"
})
@XmlRootElement(name = "benchmark")
public class Benchmark
    extends AbstractSerializable
    implements Serializable
{

    private final static long serialVersionUID = -9223372036854775808L;
    protected Desc desc;
    @XmlElement(required = true)
    protected RSL rsl;
    protected Period period;
    @XmlElement(required = true)
    protected Generator generator;
    protected Evaluators evaluators;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link Desc }
     *     
     */
    public Desc getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Desc }
     *     
     */
    public void setDesc(Desc value) {
        this.desc = value;
    }

    public boolean isSetDesc() {
        return (this.desc!= null);
    }

    /**
     * Gets the value of the rsl property.
     * 
     * @return
     *     possible object is
     *     {@link RSL }
     *     
     */
    public RSL getRSL() {
        return rsl;
    }

    /**
     * Sets the value of the rsl property.
     * 
     * @param value
     *     allowed object is
     *     {@link RSL }
     *     
     */
    public void setRSL(RSL value) {
        this.rsl = value;
    }

    public boolean isSetRSL() {
        return (this.rsl!= null);
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setPeriod(Period value) {
        this.period = value;
    }

    public boolean isSetPeriod() {
        return (this.period!= null);
    }

    /**
     * Gets the value of the generator property.
     * 
     * @return
     *     possible object is
     *     {@link Generator }
     *     
     */
    public Generator getGenerator() {
        return generator;
    }

    /**
     * Sets the value of the generator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Generator }
     *     
     */
    public void setGenerator(Generator value) {
        this.generator = value;
    }

    public boolean isSetGenerator() {
        return (this.generator!= null);
    }

    /**
     * Gets the value of the evaluators property.
     * 
     * @return
     *     possible object is
     *     {@link Evaluators }
     *     
     */
    public Evaluators getEvaluators() {
        return evaluators;
    }

    /**
     * Sets the value of the evaluators property.
     * 
     * @param value
     *     allowed object is
     *     {@link Evaluators }
     *     
     */
    public void setEvaluators(Evaluators value) {
        this.evaluators = value;
    }

    public boolean isSetEvaluators() {
        return (this.evaluators!= null);
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

}
