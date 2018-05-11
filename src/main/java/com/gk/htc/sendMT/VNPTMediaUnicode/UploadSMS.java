
package com.gk.htc.sendMT.VNPTMediaUnicode;

import com.gk.htc.sendMT.VNPTMediaUnicode.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


//<xs:complexType name="uploadSMS">
//<xs:sequence>
//<xs:element name="username" type="xs:string" minOccurs="0"/>
//<xs:element name="password" type="xs:string" minOccurs="0"/>
//<xs:element name="serviceId" type="xs:string" minOccurs="0"/>
//<xs:element name="userId" type="xs:string" minOccurs="0"/>
//<xs:element name="contentType" type="xs:string" minOccurs="0"/>
//<xs:element name="serviceKind" type="xs:string" minOccurs="0"/>
//<xs:element name="infor" type="xs:string" minOccurs="0"/>
//<xs:element name="dataCoding" type="xs:int"/>
//</xs:sequence>
//</xs:complexType>

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadSMS", propOrder = {
    "username",
    "password",
    "serviceId",
    "userId",
    "contentType",
    "serviceKind",
    "infor",
    "dataCoding"
})
public class UploadSMS {

    protected String username;
    protected String password;
    protected String serviceId;
    protected String userId;
    protected String contentType;
    protected String serviceKind;
    protected String infor;
    protected int dataCoding;

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
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
     * Gets the value of the serviceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Sets the value of the serviceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceId(String value) {
        this.serviceId = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

    /**
     * Gets the value of the serviceKind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceKind() {
        return serviceKind;
    }

    /**
     * Sets the value of the serviceKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceKind(String value) {
        this.serviceKind = value;
    }

    /**
     * Gets the value of the infor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfor() {
        return infor;
    }

    /**
     * Sets the value of the infor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfor(String value) {
        this.infor = value;
    }

    public int getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(int dataCoding) {
        this.dataCoding = dataCoding;
    }
    
    

}
