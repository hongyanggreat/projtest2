
package com.gk.htc.sendMT.VasVTE.HTC;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsCpBatchMt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsCpBatchMt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CPCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommandCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestMt" type="{http://impl.bulkSms.ws/}requestMt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsCpBatchMt", propOrder = {
    "user",
    "password",
    "cpCode",
    "commandCode",
    "requestMt"
})
public class WsCpBatchMt {

    @XmlElement(name = "User")
    protected String user;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "CPCode")
    protected String cpCode;
    @XmlElement(name = "CommandCode")
    protected String commandCode;
    protected List<RequestMt> requestMt;

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the cpCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCPCode() {
        return cpCode;
    }

    /**
     * Sets the value of the cpCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCPCode(String value) {
        this.cpCode = value;
    }

    /**
     * Gets the value of the commandCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommandCode() {
        return commandCode;
    }

    /**
     * Sets the value of the commandCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommandCode(String value) {
        this.commandCode = value;
    }

    /**
     * Gets the value of the requestMt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestMt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestMt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestMt }
     * 
     * 
     */
    public List<RequestMt> getRequestMt() {
        if (requestMt == null) {
            requestMt = new ArrayList<RequestMt>();
        }
        return this.requestMt;
    }

}
