/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.gk.htc.ahp.brand.jsmpp.util;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.app.SmppClient;
import com.gk.htc.ahp.brand.common.Tool;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gk.htc.ahp.brand.jsmpp.InvalidNumberOfDestinationsException;
import com.gk.htc.ahp.brand.jsmpp.PDUStringException;
import com.gk.htc.ahp.brand.jsmpp.bean.Address;
import com.gk.htc.ahp.brand.jsmpp.bean.AlertNotification;
import com.gk.htc.ahp.brand.jsmpp.bean.Bind;
import com.gk.htc.ahp.brand.jsmpp.bean.BindResp;
import com.gk.htc.ahp.brand.jsmpp.bean.CancelSm;
import com.gk.htc.ahp.brand.jsmpp.bean.CancelSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.Command;
import com.gk.htc.ahp.brand.jsmpp.bean.DataSm;
import com.gk.htc.ahp.brand.jsmpp.bean.DataSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliverSm;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliverSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliveryReceipt;
import com.gk.htc.ahp.brand.jsmpp.bean.DestinationAddress;
import com.gk.htc.ahp.brand.jsmpp.bean.DistributionList;
import com.gk.htc.ahp.brand.jsmpp.bean.EnquireLink;
import com.gk.htc.ahp.brand.jsmpp.bean.EnquireLinkResp;
import com.gk.htc.ahp.brand.jsmpp.bean.GenericNack;
import com.gk.htc.ahp.brand.jsmpp.bean.MessageState;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameters;
import com.gk.htc.ahp.brand.jsmpp.bean.Outbind;
import com.gk.htc.ahp.brand.jsmpp.bean.QuerySm;
import com.gk.htc.ahp.brand.jsmpp.bean.QuerySmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceSm;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMulti;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMultiResp;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitSm;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.Unbind;
import com.gk.htc.ahp.brand.jsmpp.bean.UnbindResp;
import com.gk.htc.ahp.brand.jsmpp.bean.UnsuccessDelivery;
import com.gk.htc.ahp.brand.jsmpp.bean.DestinationAddress.Flag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of SMPP PDU PDUDecomposer.
 *
 * @author uudashr
 * @version 1.0
 * @since 1.0
 *
 */
public class DefaultDecomposer implements PDUDecomposer {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultDecomposer.class);
    private static final PDUDecomposer instance = new DefaultDecomposer();

    public static final PDUDecomposer getInstance() {
        return instance;
    }

    /**
     * Default constructor.
     */
    public DefaultDecomposer() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#header(byte[])
     */
    public Command header(byte[] b) {
        Command pdu = new Command();
        assignHeader(pdu, b);
        return pdu;
    }

    // BIND OPERATION
    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#bind(byte[])
     */
    public Bind bind(byte[] b) throws PDUStringException {
        Bind req = new Bind();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(req, reader);
        req.setSystemId(reader.readCString());
        StringValidator.validateString(req.getSystemId(),
                StringParameter.SYSTEM_ID);
        req.setPassword(reader.readCString());
        StringValidator.validateString(req.getPassword(),
                StringParameter.PASSWORD);
        req.setSystemType(reader.readCString());
        StringValidator.validateString(req.getSystemType(),
                StringParameter.SYSTEM_TYPE);
        req.setInterfaceVersion(reader.readByte());
        req.setAddrTon(reader.readByte());
        req.setAddrNpi(reader.readByte());
        req.setAddressRange(reader.readCString());
        StringValidator.validateString(req.getAddressRange(),
                StringParameter.ADDRESS_RANGE);
        return req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#bindResp(byte[])
     */
    public BindResp bindResp(byte[] b) throws PDUStringException {
        BindResp resp = new BindResp();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(resp, reader);
        if (resp.getCommandLength() > 16 && resp.getCommandStatus() == 0) {
            resp.setSystemId(reader.readCString());
            StringValidator.validateString(resp.getSystemId(),
                    StringParameter.SYSTEM_ID);

            resp.setOptionalParameters(readOptionalParameters(reader));
        }
        return resp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#unbind(byte[])
     */
    public Unbind unbind(byte[] b) {
        Unbind req = new Unbind();
        assignHeader(req, b);
        return req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#unbindResp(byte[])
     */
    public UnbindResp unbindResp(byte[] b) {
        UnbindResp resp = new UnbindResp();
        assignHeader(resp, b);
        return resp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#outbind(byte[])
     */
    public Outbind outbind(byte[] b) throws PDUStringException {
        Outbind req = new Outbind();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(req, reader);
        req.setSystemId(reader.readCString());
        StringValidator.validateString(req.getSystemId(),
                StringParameter.SYSTEM_ID);
        req.setPassword(reader.readCString());
        StringValidator.validateString(req.getPassword(),
                StringParameter.PASSWORD);
        return req;
    }

    // ENQUIRE_LINK OPERATION
    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#enquireLink(byte[])
     */
    public EnquireLink enquireLink(byte[] b) {
        EnquireLink req = new EnquireLink();
        assignHeader(req, b);
        return req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#enquireLinkResp(byte[])
     */
    public EnquireLinkResp enquireLinkResp(byte[] b) {
        EnquireLinkResp resp = new EnquireLinkResp();
        assignHeader(resp, b);
        return resp;
    }

    // GENERICK_NACK OPERATION
    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#genericNack(byte[])
     */
    public GenericNack genericNack(byte[] b) {
        GenericNack req = new GenericNack();
        assignHeader(req, b);
        return req;
    }

    // SUBMIT_SM OPERATION
    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#submitSm(byte[])
     */
    public SubmitSm submitSm(byte[] b) throws PDUStringException {
        SubmitSm req = new SubmitSm();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(req, reader);
        req.setServiceType(reader.readCString());
        StringValidator.validateString(req.getServiceType(),
                StringParameter.SERVICE_TYPE);

        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(),
                StringParameter.SOURCE_ADDR);

        req.setDestAddrTon(reader.readByte());
        req.setDestAddrNpi(reader.readByte());
        req.setDestAddress(reader.readCString());
        StringValidator.validateString(req.getDestAddress(),
                StringParameter.DESTINATION_ADDR);

        req.setEsmClass(reader.readByte());
        req.setProtocolId(reader.readByte());
        req.setPriorityFlag(reader.readByte());
        req.setScheduleDeliveryTime(reader.readCString());
        StringValidator.validateString(req.getScheduleDeliveryTime(),
                StringParameter.SCHEDULE_DELIVERY_TIME);
        req.setValidityPeriod(reader.readCString());
        StringValidator.validateString(req.getValidityPeriod(),
                StringParameter.VALIDITY_PERIOD);
        req.setRegisteredDelivery(reader.readByte());
        req.setReplaceIfPresent(reader.readByte());
        req.setDataCoding(reader.readByte());
        req.setSmDefaultMsgId(reader.readByte());
        byte smLength = reader.readByte();
        // req.setShortMessage(reader.readString(req.getSmLength()));
        req.setShortMessage(reader.readBytes(smLength));
        StringValidator.validateString(req.getShortMessage(),
                StringParameter.SHORT_MESSAGE);
        req.setOptionalParametes(readOptionalParameters(reader));
        return req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#submitSmResp(byte[])
     */
    public SubmitSmResp submitSmResp(byte[] b) throws PDUStringException {
        SubmitSmResp resp = new SubmitSmResp();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(resp, reader);
        if (resp.getCommandLength() > 16 && resp.getCommandStatus() == 0) {
            resp.setMessageId(reader.readCString());
            StringValidator.validateString(resp.getMessageId(),
                    StringParameter.MESSAGE_ID);
        }
        return resp;
    }

    // QUERY_SM OPERATION
    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#querySm(byte[])
     */
    public QuerySm querySm(byte[] b) throws PDUStringException {
        QuerySm req = new QuerySm();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(req, reader);
        req.setMessageId(reader.readCString());
        StringValidator.validateString(req.getMessageId(),
                StringParameter.MESSAGE_ID);
        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(),
                StringParameter.SOURCE_ADDR);

        return req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#querySmResp(byte[])
     */
    public QuerySmResp querySmResp(byte[] b) throws PDUStringException {
        QuerySmResp resp = new QuerySmResp();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(resp, reader);
        if (resp.getCommandLength() > 16 && resp.getCommandStatus() == 0) {
            resp.setMessageId(reader.readCString());
            StringValidator.validateString(resp.getMessageId(),
                    StringParameter.MESSAGE_ID);
            resp.setFinalDate(reader.readCString());
            StringValidator.validateString(resp.getFinalDate(),
                    StringParameter.FINAL_DATE);
            resp.setMessageState(MessageState.valueOf(reader.readByte()));
            resp.setErrorCode(reader.readByte());
        }
        return resp;
    }

    // DELIVER_SM OPERATION
    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#deliverSm(byte[])
     */
    public DeliverSm deliverSm(byte[] b) throws PDUStringException {
        DeliverSm req = new DeliverSm();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(req, reader);
        req.setServiceType(reader.readCString());
        StringValidator.validateString(req.getServiceType(),
                StringParameter.SERVICE_TYPE);

        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(),
                StringParameter.SOURCE_ADDR);

        req.setDestAddrTon(reader.readByte());
        req.setDestAddrNpi(reader.readByte());
        req.setDestAddress(reader.readCString());
        StringValidator.validateString(req.getDestAddress(),
                StringParameter.DESTINATION_ADDR);

        req.setEsmClass(reader.readByte());
        req.setProtocolId(reader.readByte());
        req.setPriorityFlag(reader.readByte());
        // scheduleDeliveryTime should be null of c-octet string
        req.setScheduleDeliveryTime(reader.readCString());
        StringValidator.validateString(req.getScheduleDeliveryTime(),
                StringParameter.SCHEDULE_DELIVERY_TIME);
        // validityPeriod should be null of c-octet string
        req.setValidityPeriod(reader.readCString());
        StringValidator.validateString(req.getValidityPeriod(),
                StringParameter.VALIDITY_PERIOD);
        req.setRegisteredDelivery(reader.readByte());
        // replaceIfPresent should be null
        req.setReplaceIfPresent(reader.readByte());
        req.setDataCoding(reader.readByte());
        // smDefaultMsgId should be null
        req.setSmDefaultMsgId(reader.readByte());
        byte smLength = reader.readByte();
        req.setShortMessage(reader.readBytes(smLength));
        StringValidator.validateString(req.getShortMessage(),
                StringParameter.SHORT_MESSAGE);
        req.setOptionalParametes(readOptionalParameters(reader));
        return req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#deliverSmResp(byte[])
     */
    public DeliverSmResp deliverSmResp(byte[] b) {
        DeliverSmResp resp = new DeliverSmResp();
        SequentialBytesReader reader = new SequentialBytesReader(b);
        assignHeader(resp, reader);
        // ignore the message_id, because it unused.
        return resp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#deliveryReceipt(java.lang.String)
     */
    public DeliveryReceipt deliveryReceipt(String data)
            throws InvalidDeliveryReceiptException {
        /*
         * id:IIIIIIIIII sub:SSS dlvrd:DDD submit date:YYMMDDhhmm done
         * date:YYMMDDhhmm stat:DDDDDDD err:E Text: ..........
         */
//        Tool.debug("deliveryReceipt data: " + data);
        try {
            DeliveryReceipt delRec = new DeliveryReceipt();
            delRec.setId(getDeliveryReceiptValue(DeliveryReceipt.DELREC_ID,
                    data));
            delRec.setSubmitted(Integer.parseInt(getDeliveryReceiptValue(
                    DeliveryReceipt.DELREC_SUB, data)));
            delRec.setDelivered(Integer.parseInt(getDeliveryReceiptValue(
                    DeliveryReceipt.DELREC_DLVRD, data)));
            delRec.setSubmitDate(string2Date(getDeliveryReceiptValue(
                    DeliveryReceipt.DELREC_SUBMIT_DATE, data)));
            delRec.setDoneDate(string2Date(getDeliveryReceiptValue(
                    DeliveryReceipt.DELREC_DONE_DATE, data)));
            delRec.setFinalStatus(DeliveryReceiptState
                    .getByName(getDeliveryReceiptValue(
                            DeliveryReceipt.DELREC_STAT, data)));
            delRec.setError(getDeliveryReceiptValue(DeliveryReceipt.DELREC_ERR,
                    data));
            delRec.setText(getDeliveryReceiptTextValue(data));
            return delRec;
        } catch (Exception e) {
            throw new InvalidDeliveryReceiptException(
                    "There is an error found when parsing delivery receipt", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsmpp.util.PDUDecomposer#deliveryReceipt(byte[])
     */
    public DeliveryReceipt deliveryReceipt(byte[] data)
            throws InvalidDeliveryReceiptException {
        return deliveryReceipt(new String(data));
    }

    public DataSm dataSm(byte[] data) throws PDUStringException {
        DataSm req = new DataSm();
        SequentialBytesReader reader = new SequentialBytesReader(data);
        assignHeader(req, reader);
        req.setServiceType(reader.readCString());
        StringValidator.validateString(req.getServiceType(),
                StringParameter.SERVICE_TYPE);

        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(),
                StringParameter.SOURCE_ADDR);

        req.setDestAddrTon(reader.readByte());
        req.setDestAddrNpi(reader.readByte());
        req.setDestAddress(reader.readCString());
        StringValidator.validateString(req.getDestAddress(),
                StringParameter.DESTINATION_ADDR);

        req.setEsmClass(reader.readByte());
        req.setRegisteredDelivery(reader.readByte());
        req.setDataCoding(reader.readByte());
        req.setOptionalParametes(readOptionalParameters(reader));
        return req;
    }

    public DataSmResp dataSmResp(byte[] data) throws PDUStringException {
        DataSmResp resp = new DataSmResp();
        SequentialBytesReader reader = new SequentialBytesReader(data);
        assignHeader(resp, reader);
        if (resp.getCommandLength() > 16 && resp.getCommandStatus() == 0) {
            resp.setMessageId(reader.readCString());
            StringValidator.validateString(resp.getMessageId(),
                    StringParameter.MESSAGE_ID);
        }

        return resp;
    }

    public CancelSm cancelSm(byte[] data) throws PDUStringException {
        CancelSm req = new CancelSm();
        SequentialBytesReader reader = new SequentialBytesReader(data);
        assignHeader(req, reader);
        req.setServiceType(reader.readCString());
        StringValidator.validateString(req.getServiceType(),
                StringParameter.SERVICE_TYPE);

        req.setMessageId(reader.readCString());
        StringValidator.validateString(req.getMessageId(),
                StringParameter.MESSAGE_ID);

        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(),
                StringParameter.SOURCE_ADDR);

        req.setDestAddrTon(reader.readByte());
        req.setDestAddrNpi(reader.readByte());
        req.setDestinationAddress(reader.readCString());
        StringValidator.validateString(req.getDestinationAddress(),
                StringParameter.DESTINATION_ADDR);

        return req;
    }

    public CancelSmResp cancelSmResp(byte[] data) {
        CancelSmResp resp = new CancelSmResp();
        assignHeader(resp, data);
        return resp;
    }

    public SubmitMulti submitMulti(byte[] data) throws PDUStringException,
            InvalidNumberOfDestinationsException {
        SubmitMulti req = new SubmitMulti();
        SequentialBytesReader reader = new SequentialBytesReader(data);
        assignHeader(req, reader);
        req.setServiceType(reader.readCString());
        StringValidator.validateString(req.getServiceType(),
                StringParameter.SERVICE_TYPE);

        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(),
                StringParameter.SOURCE_ADDR);

        int totalDest = 0xff & reader.readByte();
        if (totalDest > 255) {
            throw new InvalidNumberOfDestinationsException(
                    "Number of destination is invalid. Should be no more than 255. Actual number is "
                    + totalDest, totalDest);
        }

        DestinationAddress[] destAddresses = new DestinationAddress[totalDest];
        for (int i = 0; i < totalDest; i++) {
            byte flag = reader.readByte();
            if (flag == Flag.SME_ADDRESS.getValue()) {
                byte ton = reader.readByte();
                byte npi = reader.readByte();
                String addr = reader.readCString();
                StringValidator.validateString(addr,
                        StringParameter.DESTINATION_ADDR);
                Address destAddr = new Address(ton, npi, addr);
                destAddresses[i] = destAddr;
            } else if (flag == Flag.DISTRIBUTION_LIST.getValue()) {
                destAddresses[i] = new DistributionList(reader.readCString());
            } else {
                logger.warn("Unknown destination address flag: " + flag);
            }
        }
        req.setDestAddresses(destAddresses);

        req.setEsmClass(reader.readByte());
        req.setProtocolId(reader.readByte());
        req.setPriorityFlag(reader.readByte());
        req.setScheduleDeliveryTime(reader.readCString());
        StringValidator.validateString(req.getScheduleDeliveryTime(),
                StringParameter.SCHEDULE_DELIVERY_TIME);
        req.setValidityPeriod(reader.readCString());
        StringValidator.validateString(req.getValidityPeriod(),
                StringParameter.VALIDITY_PERIOD);
        req.setRegisteredDelivery(reader.readByte());
        req.setReplaceIfPresentFlag(reader.readByte());
        req.setDataCoding(reader.readByte());
        req.setSmDefaultMsgId(reader.readByte());
        byte smLength = reader.readByte();
        req.setShortMessage(reader.readBytes(smLength));
        StringValidator.validateString(req.getShortMessage(),
                StringParameter.SHORT_MESSAGE);
        req.setOptionalParameters(readOptionalParameters(reader));
        return req;
    }

    public SubmitMultiResp submitMultiResp(byte[] data)
            throws PDUStringException {
        SubmitMultiResp resp = new SubmitMultiResp();
        SequentialBytesReader reader = new SequentialBytesReader(data);
        assignHeader(resp, reader);
        resp.setMessageId(reader.readCString());
        StringValidator.validateString(resp.getMessageId(),
                StringParameter.MESSAGE_ID);

        int noUnsuccess = 0xff & reader.readByte();
        UnsuccessDelivery[] unsuccessSmes = new UnsuccessDelivery[noUnsuccess];
        for (int i = 0; i < noUnsuccess; i++) {
            byte ton = reader.readByte();
            byte npi = reader.readByte();
            String addr = reader.readCString();
            StringValidator.validateString(addr,
                    StringParameter.DESTINATION_ADDR);
            int errorStatusCode = reader.readInt();
            unsuccessSmes[i] = new UnsuccessDelivery(ton, npi, addr,
                    errorStatusCode);
        }
        resp.setUnsuccessSmes(unsuccessSmes);
        return resp;
    }

    public ReplaceSm replaceSm(byte[] data) throws PDUStringException {
        ReplaceSm req = new ReplaceSm();
        SequentialBytesReader reader = new SequentialBytesReader(data);
        assignHeader(req, reader);
        req.setMessageId(reader.readCString());
        StringValidator.validateString(req.getMessageId(),
                StringParameter.MESSAGE_ID);
        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(),
                StringParameter.SOURCE_ADDR);
        req.setScheduleDeliveryTime(reader.readCString());
        StringValidator.validateString(req.getScheduleDeliveryTime(),
                StringParameter.SCHEDULE_DELIVERY_TIME);
        req.setValidityPeriod(reader.readCString());
        StringValidator.validateString(req.getValidityPeriod(),
                StringParameter.VALIDITY_PERIOD);
        req.setSmDefaultMsgId(reader.readByte());
        byte smLength = reader.readByte();
        req.setShortMessage(reader.readBytes(smLength));
        StringValidator.validateString(req.getShortMessage(),
                StringParameter.SHORT_MESSAGE);
        return req;
    }

    public ReplaceSmResp replaceSmResp(byte[] data) {
        ReplaceSmResp resp = new ReplaceSmResp();
        assignHeader(resp, data);
        return resp;
    }

    public AlertNotification alertNotification(byte[] data) throws PDUStringException {
        AlertNotification req = new AlertNotification();
        SequentialBytesReader reader = new SequentialBytesReader(data);
        assignHeader(req, reader);
        req.setSourceAddrTon(reader.readByte());
        req.setSourceAddrNpi(reader.readByte());
        req.setSourceAddr(reader.readCString());
        StringValidator.validateString(req.getSourceAddr(), StringParameter.SOURCE_ADDR);
        req.setEsmeAddrTon(reader.readByte());
        req.setEsmeAddrNpi(reader.readByte());
        /*
         * No validation on esme_addr.
         * There is no response to alert_notificaion command, so error will be 
         * ignored.
         */
        req.setEsmeAddr(reader.readCString());
        req.setOptionalParameters(readOptionalParameters(reader));
        return req;
    }

    private OptionalParameter[] readOptionalParameters(
            SequentialBytesReader reader) {
        if (!reader.hasMoreBytes()) {
            return null;
        }
        List<OptionalParameter> params = new ArrayList<OptionalParameter>();
        while (reader.hasMoreBytes()) {
            short tag = reader.readShort();
            short length = reader.readShort();
            byte[] content = reader.readBytes(length);
            params.add(OptionalParameters.deserialize(tag, content));
        }
        return params.toArray(new OptionalParameter[params.size()]);
    }

    /**
     * YYMMDDhhmm where:
     * <ul>
     * <li>YY = last two digits of the year (00-99)</li>
     * <li>MM = month (01-12)</li>
     * <li>DD = day (01-31)</li>
     * <li>hh = hour (00-23)</li>
     * <li>mm = minute (00-59)</li>
     * </ul>
     *
     * Java format is (yyMMddHHmm).
     *
     * @param date in <tt>String</tt> format.
     * @return
     * @throws NumberFormatException if there is contains non number on
     * <code>date</code> parameter.
     * @throws IndexOutOfBoundsException if the date length in <tt>String</tt>
     * format is less than 10.
     */
    private static Date string2Date(String date) {

        int year = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(2, 4));
        int day = Integer.parseInt(date.substring(4, 6));
        int hour = Integer.parseInt(date.substring(6, 8));
        int minute = Integer.parseInt(date.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(convertTwoDigitYear(year), month - 1, day, hour, minute, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static int convertTwoDigitYear(int year) {
        if (year >= 0 && year <= 37) {
            return 2000 + year;
        } else if (year >= 38 && year <= 99) {
            return 1900 + year;
        } else {
            // should never happen
            return year;
        }
    }

    /**
     * Get the delivery receipt attribute value.
     *
     * @param attrName is the attribute name.
     * @param source the original source id:IIIIIIIIII sub:SSS dlvrd:DDD submit
     * date:YYMMDDhhmm done date:YYMMDDhhmm stat:DDDDDDD err:E
     * Text:....................
     * @return the value of specified attribute.
     * @throws IndexOutOfBoundsException
     */
    private static String getDeliveryReceiptValue(String attrName, String source)
            throws IndexOutOfBoundsException {
        String tmpAttr = attrName + ":";
        int startIndex = source.indexOf(tmpAttr);
        if (startIndex < 0) {
            return null;
        }
        startIndex = startIndex + tmpAttr.length();
        int endIndex = source.indexOf(" ", startIndex);
        if (endIndex > 0) {
            return source.substring(startIndex, endIndex);
        }
        return source.substring(startIndex);
    }

    /**
     * @param source
     * @return
     * @throws IndexOutOfBoundsException
     */
    private static String getDeliveryReceiptTextValue(String source) {
        String tmpAttr = DeliveryReceipt.DELREC_TEXT + ":";
        int startIndex = source.indexOf(tmpAttr);
        if (startIndex < 0) {
            tmpAttr = DeliveryReceipt.DELREC_TEXT.toLowerCase() + ":";
            startIndex = source.indexOf(tmpAttr);
        }
        if (startIndex < 0) {
            return null;
        }
        startIndex = startIndex + tmpAttr.length();
        return source.substring(startIndex);
    }

    private static void assignHeader(Command pdu,
            SequentialBytesReader seqBytesReader) {
        int commandLength = seqBytesReader.readInt();
        if (seqBytesReader.getBytes().length != commandLength) {
            logger.error("SYSTEM BUGS, the command_length (" + commandLength
                    + ") not equals with the byte array length ("
                    + seqBytesReader.getBytes().length + ")");
        }
        pdu.setCommandLength(commandLength);
        pdu.setCommandId(seqBytesReader.readInt());
        pdu.setCommandStatus(seqBytesReader.readInt());
        pdu.setSequenceNumber(seqBytesReader.readInt());
    }

    private static void assignHeader(Command pdu, byte[] bytes) {
        assignHeader(pdu, new SequentialBytesReader(bytes));
    }
}
