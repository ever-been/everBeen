//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.05 at 10:17:21 AM CEST 
//


package cz.cuni.mff.been.jaxb.group;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import cz.cuni.mff.been.common.rsl.Condition;

public class Adapter1
    extends XmlAdapter<String, Condition>
{


    public Condition unmarshal(String value) {
        return (cz.cuni.mff.been.jaxb.Convertor.parseRSL(value));
    }

    public String marshal(Condition value) {
        return (String.valueOf(value));
    }

}
