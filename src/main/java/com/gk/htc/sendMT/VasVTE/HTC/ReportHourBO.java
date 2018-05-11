
package com.gk.htc.sendMT.VasVTE.HTC;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportHourBO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reportHourBO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cpAlias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cpCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cpId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mtError" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mtErrorAlias" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mtFailure" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mtSent" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mtSentOut" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mtSentVt" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="reportHour" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reportHourId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="result" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="smsSent" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="smsSentOut" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="smsSentVt" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="sumMT" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reportHourBO", propOrder = {
    "cpAlias",
    "cpCode",
    "cpId",
    "message",
    "mtError",
    "mtErrorAlias",
    "mtFailure",
    "mtSent",
    "mtSentOut",
    "mtSentVt",
    "reportHour",
    "reportHourId",
    "result",
    "smsSent",
    "smsSentOut",
    "smsSentVt",
    "sumMT"
})
public class ReportHourBO {

    protected String cpAlias;
    protected String cpCode;
    protected Long cpId;
    protected String message;
    protected Long mtError;
    protected Long mtErrorAlias;
    protected Long mtFailure;
    protected Long mtSent;
    protected Long mtSentOut;
    protected Long mtSentVt;
    protected String reportHour;
    protected Long reportHourId;
    protected Long result;
    protected Long smsSent;
    protected Long smsSentOut;
    protected Long smsSentVt;
    protected Long sumMT;

    /**
     * Gets the value of the cpAlias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpAlias() {
        return cpAlias;
    }

    /**
     * Sets the value of the cpAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpAlias(String value) {
        this.cpAlias = value;
    }

    /**
     * Gets the value of the cpCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpCode() {
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
    public void setCpCode(String value) {
        this.cpCode = value;
    }

    /**
     * Gets the value of the cpId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCpId() {
        return cpId;
    }

    /**
     * Sets the value of the cpId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCpId(Long value) {
        this.cpId = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the mtError property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMtError() {
        return mtError;
    }

    /**
     * Sets the value of the mtError property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMtError(Long value) {
        this.mtError = value;
    }

    /**
     * Gets the value of the mtErrorAlias property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMtErrorAlias() {
        return mtErrorAlias;
    }

    /**
     * Sets the value of the mtErrorAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMtErrorAlias(Long value) {
        this.mtErrorAlias = value;
    }

    /**
     * Gets the value of the mtFailure property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMtFailure() {
        return mtFailure;
    }

    /**
     * Sets the value of the mtFailure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMtFailure(Long value) {
        this.mtFailure = value;
    }

    /**
     * Gets the value of the mtSent property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMtSent() {
        return mtSent;
    }

    /**
     * Sets the value of the mtSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMtSent(Long value) {
        this.mtSent = value;
    }

    /**
     * Gets the value of the mtSentOut property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMtSentOut() {
        return mtSentOut;
    }

    /**
     * Sets the value of the mtSentOut property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMtSentOut(Long value) {
        this.mtSentOut = value;
    }

    /**
     * Gets the value of the mtSentVt property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMtSentVt() {
        return mtSentVt;
    }

    /**
     * Sets the value of the mtSentVt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMtSentVt(Long value) {
        this.mtSentVt = value;
    }

    /**
     * Gets the value of the reportHour property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportHour() {
        return reportHour;
    }

    /**
     * Sets the value of the reportHour property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportHour(String value) {
        this.reportHour = value;
    }

    /**
     * Gets the value of the reportHourId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getReportHourId() {
        return reportHourId;
    }

    /**
     * Sets the value of the reportHourId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setReportHourId(Long value) {
        this.reportHourId = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setResult(Long value) {
        this.result = value;
    }

    /**
     * Gets the value of the smsSent property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSmsSent() {
        return smsSent;
    }

    /**
     * Sets the value of the smsSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSmsSent(Long value) {
        this.smsSent = value;
    }

    /**
     * Gets the value of the smsSentOut property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSmsSentOut() {
        return smsSentOut;
    }

    /**
     * Sets the value of the smsSentOut property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSmsSentOut(Long value) {
        this.smsSentOut = value;
    }

    /**
     * Gets the value of the smsSentVt property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSmsSentVt() {
        return smsSentVt;
    }

    /**
     * Sets the value of the smsSentVt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSmsSentVt(Long value) {
        this.smsSentVt = value;
    }

    /**
     * Gets the value of the sumMT property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSumMT() {
        return sumMT;
    }

    /**
     * Sets the value of the sumMT property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSumMT(Long value) {
        this.sumMT = value;
    }

}
