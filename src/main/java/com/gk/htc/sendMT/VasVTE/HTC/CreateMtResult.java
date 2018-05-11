
package com.gk.htc.sendMT.VasVTE.HTC;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createMtResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createMtResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errCode" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="errDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responseMt" type="{http://impl.bulkSms.ws/}responseMt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createMtResult", propOrder = {
    "errCode",
    "errDesc",
    "responseMt"
})
public class CreateMtResult {

    protected long errCode;
    protected String errDesc;
    protected List<ResponseMt> responseMt;

    /**
     * Gets the value of the errCode property.
     * 
     */
    public long getErrCode() {
        return errCode;
    }

    /**
     * Sets the value of the errCode property.
     * 
     */
    public void setErrCode(long value) {
        this.errCode = value;
    }

    /**
     * Gets the value of the errDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrDesc() {
        return errDesc;
    }

    /**
     * Sets the value of the errDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrDesc(String value) {
        this.errDesc = value;
    }

    /**
     * Gets the value of the responseMt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the responseMt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResponseMt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResponseMt }
     * 
     * 
     */
    public List<ResponseMt> getResponseMt() {
        if (responseMt == null) {
            responseMt = new ArrayList<ResponseMt>();
        }
        return this.responseMt;
    }

}
