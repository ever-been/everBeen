//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.05 at 10:17:25 AM CEST 
//


package cz.cuni.mff.been.jaxb.tuplit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the cz.cuni.mff.been.jaxb.tuplit package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Row_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "row");
    private final static QName _Float_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "float");
    private final static QName _Long_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "long");
    private final static QName _Double_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "double");
    private final static QName _Serializable_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "serializable");
    private final static QName _Uuid_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "uuid");
    private final static QName _String_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "string");
    private final static QName _Int_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "int");
    private final static QName _File_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "file");
    private final static QName _Binary_QNAME = new QName("http://been.mff.cuni.cz/resultsrepositoryng/tuplit", "binary");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cz.cuni.mff.been.jaxb.tuplit
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NamedFloat }
     * 
     */
    public NamedFloat createNamedFloat() {
        return new NamedFloat();
    }

    /**
     * Create an instance of {@link NamedDouble }
     * 
     */
    public NamedDouble createNamedDouble() {
        return new NamedDouble();
    }

    /**
     * Create an instance of {@link NamedString }
     * 
     */
    public NamedString createNamedString() {
        return new NamedString();
    }

    /**
     * Create an instance of {@link NamedUUID }
     * 
     */
    public NamedUUID createNamedUUID() {
        return new NamedUUID();
    }

    /**
     * Create an instance of {@link NamedLong }
     * 
     */
    public NamedLong createNamedLong() {
        return new NamedLong();
    }

    /**
     * Create an instance of {@link TupLit }
     * 
     */
    public TupLit createTupLit() {
        return new TupLit();
    }

    /**
     * Create an instance of {@link NamedBinary }
     * 
     */
    public NamedBinary createNamedBinary() {
        return new NamedBinary();
    }

    /**
     * Create an instance of {@link NamedInt }
     * 
     */
    public NamedInt createNamedInt() {
        return new NamedInt();
    }

    /**
     * Create an instance of {@link Row }
     * 
     */
    public Row createRow() {
        return new Row();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Row }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "row")
    public JAXBElement<Row> createRow(Row value) {
        return new JAXBElement<Row>(_Row_QNAME, Row.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedFloat }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "float")
    public JAXBElement<NamedFloat> createFloat(NamedFloat value) {
        return new JAXBElement<NamedFloat>(_Float_QNAME, NamedFloat.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedLong }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "long")
    public JAXBElement<NamedLong> createLong(NamedLong value) {
        return new JAXBElement<NamedLong>(_Long_QNAME, NamedLong.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedDouble }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "double")
    public JAXBElement<NamedDouble> createDouble(NamedDouble value) {
        return new JAXBElement<NamedDouble>(_Double_QNAME, NamedDouble.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedBinary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "serializable")
    public JAXBElement<NamedBinary> createSerializable(NamedBinary value) {
        return new JAXBElement<NamedBinary>(_Serializable_QNAME, NamedBinary.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedUUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "uuid")
    public JAXBElement<NamedUUID> createUuid(NamedUUID value) {
        return new JAXBElement<NamedUUID>(_Uuid_QNAME, NamedUUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "string")
    public JAXBElement<NamedString> createString(NamedString value) {
        return new JAXBElement<NamedString>(_String_QNAME, NamedString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedInt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "int")
    public JAXBElement<NamedInt> createInt(NamedInt value) {
        return new JAXBElement<NamedInt>(_Int_QNAME, NamedInt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedUUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "file")
    public JAXBElement<NamedUUID> createFile(NamedUUID value) {
        return new JAXBElement<NamedUUID>(_File_QNAME, NamedUUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedBinary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://been.mff.cuni.cz/resultsrepositoryng/tuplit", name = "binary")
    public JAXBElement<NamedBinary> createBinary(NamedBinary value) {
        return new JAXBElement<NamedBinary>(_Binary_QNAME, NamedBinary.class, null, value);
    }

}
