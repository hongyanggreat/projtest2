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
package com.gk.htc.ahp.brand.app;

import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.jsmpp.bean.AlertNotification;
import com.gk.htc.ahp.brand.jsmpp.bean.Alphabet;
import com.gk.htc.ahp.brand.jsmpp.bean.BindType;
import com.gk.htc.ahp.brand.jsmpp.bean.DataCoding;
import com.gk.htc.ahp.brand.jsmpp.bean.DataSm;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliverSm;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliveryReceipt;
import com.gk.htc.ahp.brand.jsmpp.bean.ESMClass;
import com.gk.htc.ahp.brand.jsmpp.bean.MessageType;
import com.gk.htc.ahp.brand.jsmpp.bean.NumberingPlanIndicator;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameters;
import com.gk.htc.ahp.brand.jsmpp.bean.RegisteredDelivery;
import com.gk.htc.ahp.brand.jsmpp.bean.TypeOfNumber;
import com.gk.htc.ahp.brand.jsmpp.extra.NegativeResponseException;
import com.gk.htc.ahp.brand.jsmpp.extra.ProcessRequestException;
import com.gk.htc.ahp.brand.jsmpp.extra.ResponseTimeoutException;
import com.gk.htc.ahp.brand.jsmpp.extra.SessionState;
import com.gk.htc.ahp.brand.jsmpp.session.DataSmResult;
import com.gk.htc.ahp.brand.jsmpp.session.MessageReceiverListener;
import com.gk.htc.ahp.brand.jsmpp.session.SMPPSession;
import com.gk.htc.ahp.brand.jsmpp.session.Session;
import com.gk.htc.ahp.brand.jsmpp.session.SessionStateListener;
import com.gk.htc.ahp.brand.jsmpp.util.InvalidDeliveryReceiptException;
import com.gk.htc.ahp.brand.jsmpp.InvalidResponseException;
import com.gk.htc.ahp.brand.jsmpp.PDUException;
import com.gk.htc.ahp.brand.jsmpp.SMPPConstant;
import com.gk.htc.ahp.brand.jsmpp.util.OctetUtil;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import static com.gk.htc.sendMT.service.thread._AbstractThreadSend2Provider.EXCEPTION;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import static com.gk.htc.sendMT.service.thread._AbstractThreadSend2Provider.SUCCESS_INT;
import static com.gk.htc.sendMT.service.thread._AbstractThreadSend2Provider.SUCCESS_MSG;

/**
 * This stress client is an example of submit bulk messages asynchronously.
 *
 * <table border="1">
 * <tr><td><strong>Name</strong></td><td><strong>Description</strong></td><td><strong>Default
 * value</strong></td></tr>
 * <tr><td>jsmpp.client.host</td><td>Server host
 * address</td><td>localhost</td></tr>
 * <tr><td>jsmpp.client.port</td><td>Server port</td><td>7774</td></tr>
 * <tr><td>jsmpp.client.systemId</td><td>System Identifier</td><td>j</td></tr>
 * <tr><td>jsmpp.client.password</td><td>Password</td><td>jpwd</td></tr>
 * <tr><td>jsmpp.client.sourceAddr</td><td>Submit Source
 * Address</td><td>1616</td></tr>
 * <tr><td>jsmpp.client.destinationAddr</td><td>Submit Destination
 * Address</td><td>62161616</td>
 * <tr><td>jsmpp.client.transactionTimer</td><td>Transaction
 * timer</td><td>2000</td></tr>
 * <tr><td>jsmpp.client.bulkSize</td><td>Amount of bulk
 * messages</td><td>100000</td></tr>
 * <tr><td>jsmpp.client.procDegree</td><td>Max parallel processor for PDU
 * reading</td><td>3</td></tr>
 * <tr><td>jsmpp.client.maxOutstanding</td><td>Maximum outstanding
 * messages</td><td>10</td></tr>
 * <tr><td>jsmpp.client.log4jPath</td><td>Log4j
 * configuration</td><td>conf/client-log4j.properties</td></tr>
 * </table>
 *
 * @author uudashr
 *
 */
public class SmppClient extends Thread implements MessageReceiverListener {

    final Logger logger = Logger.getLogger(SmppClient.class);
    // Thoi gian doi cua 1 giao dich
    private static final Long DEFAULT_TRANSACTIONTIMER = 15000L;
    private static final Integer DEFAULT_PROCESSOR_DEGREE = 3;
    private final long reconnectInterval = 5L; // 5 seconds
    // For Sever Remote
    private SMPPSession clientSession = null;
    private boolean connected = false;
    //-----
    private final String user;
    private final String password;
    private final String addressRange;
    private final String host;
    private final int port;

    void shutdown() {
        if (clientSession != null && !clientSession.getSessionState().equals(SessionState.CLOSED)) {
            clientSession.unbindAndClose();
            if (clientSession != null) {
                clientSession = null;
            }
        }
    }

    public boolean isConnect() {
        return connected && clientSession != null;
    }

    //---
    public SmppClient(String host, int port, String user, String password, String addressRange) {
        this.setName("SmppClient[" + addressRange + "]");
        MonitorWorker.addDemonName(this.getName());
        this.user = user;
        this.password = password;
        this.addressRange = addressRange;
        this.port = port;
        this.host = host;
    }

    @Override
    public void run() {
        MyLog.debug("=>Starting conect SmppClient...");
        try {
            clientSession = newSession();
        } catch (IOException e) {
            reconnectAfter(reconnectInterval);
            logger.error("Failed initialize connection or bind:" + Tool.getLogMessage(e));
        }
    }

    /**
     * Reconnect session after specified interval.
     *
     * @param timeSecond is the interval.
     */
    private synchronized void reconnectAfter(final long timeSecond) {
        new Thread() {
            @Override
            public void run() {
                MyLog.debug("Schedule reconnect [" + addressRange + "] after " + timeSecond + " Second");
                Tool.debug("Schedule reconnect [" + addressRange + "] after " + timeSecond + " Second");
                try {
                    Thread.sleep(timeSecond * 1000);
                } catch (InterruptedException e) {
                }
                int attempt = 0;
                while (AppStart.isRuning && (clientSession == null || clientSession.getSessionState().equals(SessionState.CLOSED))) {
                    try {
                        MyLog.debug("Reconnecting [" + addressRange + "] attempt #" + (++attempt) + " affter " + timeSecond + " Second...");
                        Tool.debug("Reconnecting [" + addressRange + "] attempt #" + (++attempt) + " affter " + timeSecond + " Second...");
                        clientSession = newSession();
                    } catch (IOException e) {
                        MyLog.debug("Failed connection and bind to " + host + ":" + port + ":" + addressRange + ":" + Tool.getLogMessage(e));
                        Tool.debug("Failed connection and bind to " + host + ":" + port + ":" + addressRange + ":" + Tool.getLogMessage(e));
                        // wait for a second
                        try {
                            Thread.sleep(timeSecond * 1000);
                        } catch (InterruptedException ee) {
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
            // delivery receipt From HTC
            try {
//                Tool.debug("onAcceptDeliverSm from:" + host + ":port=" + port);
                DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                String messageId = delReceipt.getId();
                logger.debug("MessageId lay ve:" + messageId);
                if (StringUtils.isNumeric(delReceipt.getId())) {
                    long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                    messageId = Long.toString(id, 16);
                    logger.debug("messageId Sau Long [" + id + "] cua Server Tra Ve:" + messageId);
                }
                String info = "messageId:" + messageId
                        + "|id:" + delReceipt.getId()
                        + "|phone:" + deliverSm.getSourceAddr()
                        + "|Submitted:" + delReceipt.getSubmitted()
                        + "|dlvrd:" + delReceipt.getDelivered()
                        + "|status:" + delReceipt.getFinalStatus()
                        + "|error:" + delReceipt.getError()
                        + "|msg:" + delReceipt.getText();
                // -- logDRMessage
//                MsgBrand_SMPP_DR dr = new MsgBrand_SMPP_DR();
//                dr.setDlvrd(delReceipt.getDelivered() + "");
//                dr.setError(delReceipt.getError());
//                dr.setLbNode(MyConfig.LB_NODE);
//                dr.setMessage(delReceipt.getText());
//                dr.setMessageId(messageId);
//                dr.setOper(SMSUtils.buildMobileOperator(deliverSm.getSourceAddr()));
//                dr.setPhone(deliverSm.getSourceAddr());
//                dr.setRequestTime(DateProc.createTimestamp());
//                dr.setSendTo(addressRange);
//                dr.setStatus(delReceipt.getFinalStatus().toString());
//                dr.setSubmitted(delReceipt.getSubmitted() + "");
//                dr.setTranId(info);
//                AppStart.logDRReport.addToqueue(dr);
                // Log DRSMPP Vas File
//                MyLog.logDRSMPPVAS_VTE(info);
            } catch (InvalidDeliveryReceiptException e) {
                logger.error("Failed getting delivery receipt:" + Tool.getLogMessage(e));
            }
        } else {
            // (MO se ve o day) regular short message
            Tool.debug("Receiving message : " + new String(deliverSm.getShortMessage()));
        }
    }

    @Override
    public void onAcceptAlertNotification(AlertNotification alertNotification) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This class will receive the notification from {@link SMPPSession} for the
     * state changes. It will schedule to re-initialize session.
     *
     * @author uudashr
     *
     */
    private boolean ALERT_LOSE = false;

    private class SessionStateListenerImpl implements SessionStateListener {

        @Override
        public void onStateChange(SessionState newState, SessionState oldState, Object source) {
//            Tool.debug("SmppClient[" + addressRange + "] onStateChange...");
            if (newState.equals(SessionState.CLOSED)) {
                connected = false;
                if (AppStart.isRuning && MyConfig.SV_ALERT && !ALERT_LOSE) {
                    String message = "[" + MyConfig.LB_NODE + "-FPT] Lose Connect [" + addressRange + "] IP:" + host + ":" + port;
                    SMSUtils.SendAlert8x65(message, "84888157922");
                    ALERT_LOSE = true;
                }
                // Reconnect
                if (AppStart.isRuning) {
                    reconnectAfter(reconnectInterval);
                } else {
                    MonitorWorker.removeDemonName("SmppClient[" + addressRange + "]");
                    Tool.debug("SmppClient[" + addressRange + "] Not Reconect and Shutdown...");
                }
            }
        }
    }

    protected void logData(SmsBrandQueue brQueue) {
        AppStart.log_submitTask.addToqueue(brQueue);
        MyLog.logSubmit(SmsQueueDao.toStringJson(brQueue));
    }

    public void doSendMT(WorkQueue executeThreaPool, final SmsBrandQueue mt) {
        executeThreaPool.execute(sendMTASCII(mt));
    }

    private Runnable sendMTASCII(final SmsBrandQueue brQueue) {
        return new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                brQueue.setTimeSend(DateProc.createTimestamp());

                try {
                    String responseMsgId = clientSession.submitShortMessage(
                            "SMPP",
                            TypeOfNumber.INTERNATIONAL,
                            NumberingPlanIndicator.UNKNOWN,
                            brQueue.getLabel(), // BRAND NAME
                            TypeOfNumber.INTERNATIONAL,
                            NumberingPlanIndicator.UNKNOWN,
                            brQueue.getPhone(), // So nhan tin
                            new ESMClass(),
                            (byte) 0,
                            (byte) 0,
                            null,
                            null,
                            new RegisteredDelivery(1), // Defaul register Delivery
                            (byte) 0,
                            DataCoding.newInstance(Alphabet.ALPHA_DEFAULT.value()),
                            (byte) 0, // smDefaultMsgId
                            brQueue.getMessage().getBytes()
                    );
                    brQueue.setMessageId(responseMsgId);           // transaction ID
                    brQueue.setResult(SUCCESS_INT);
                    brQueue.setErrorInfo(SUCCESS_MSG);
                    //--
                    long delay = System.currentTimeMillis() - startTime;
                    brQueue.setProcessTime(delay);
                    //--###-- Nếu Exception thi không xuống được đến đây
                    logData(brQueue);
                    //--###-- log SUBMIT 
                    // TODO MyLog.logSubmitBR(info);
                } catch (PDUException e) {
                    brQueue.setErrorInfo("PDUException to [" + user + "|" + host + "|" + port + "|" + addressRange + "]:" + e.getMessage());
                    brQueue.setResult(EXCEPTION);
                    logData(brQueue);
                    logger.error("sendMTASCII.PDUException:" + Tool.getLogMessage(e));
                } catch (ResponseTimeoutException e) {
                    brQueue.setErrorInfo("ResponseTimeoutException to [" + user + "|" + host + "|" + port + "|" + addressRange + "]:" + e.getMessage());
                    brQueue.setResult(EXCEPTION);
                    logData(brQueue);
                    logger.error("sendMTASCII.ResponseTimeoutException:" + Tool.getLogMessage(e));
                } catch (InvalidResponseException | NegativeResponseException | IOException e) {
                    logger.error("sendMTASCII.InvalidResponseException:" + Tool.getLogMessage(e));
                    if (e instanceof NegativeResponseException) {
                        NegativeResponseException ex = (NegativeResponseException) e;
                        int status = ex.getCommandStatus();
                        brQueue.setResult(status);
                        brQueue.setErrorInfo(ex.getMessage());
                        logData(brQueue);
                    } else {
                        brQueue.setRetry();
                        brQueue.setResult(EXCEPTION);
                        brQueue.setErrorInfo("InvalidResponseException | NegativeResponseException | IOException to [" + user + "|" + host + "|" + port + "|" + addressRange + "]" + e.getMessage());
                        logData(brQueue);
                    }
                }
            }
        };
    }
    static final byte UDHIE_HEADER_LENGTH = 0x05;
    static final byte UDHIE_IDENTIFIER_SAR = 0x00;
    static final byte UDHIE_SAR_LENGTH = 0x03;

    public void doSendLongMT(WorkQueue executeThreaPool, final ArrayList<SmsBrandQueue> arrLong) {
        executeThreaPool.execute(sendLong_MTASCII(arrLong));
    }

    private static byte[] buildUDH_LongMsg(byte[] aMessage, byte refer, int segmentSeqnum, int totalSegments) {

        int lengthOfData = aMessage.length;
        byte[] segments = new byte[6 + lengthOfData];
        // UDH header
        // doesn't include itself, its header length
        segments[0] = UDHIE_HEADER_LENGTH;
        // SAR identifier
        segments[1] = UDHIE_IDENTIFIER_SAR;
        // SAR length
        segments[2] = UDHIE_SAR_LENGTH;
        // reference number (same for all messages)
        segments[3] = refer;
        // total number of segments
        segments[4] = (byte) totalSegments;
        // segment number
        segments[5] = (byte) segmentSeqnum;

        // copy the data into the array
        System.arraycopy(aMessage, 0, segments, 6, lengthOfData);
        return segments;
    }

    private Runnable sendLong_MTASCII(final ArrayList<SmsBrandQueue> arrLong) {
        return new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                byte[] referenceNumber = new byte[1];
                new Random().nextBytes(referenceNumber);
                // REFER
                int refer = OctetUtil.bytesToInt(referenceNumber);
                OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum(refer);
                OptionalParameter sarTotalSegments = OptionalParameters.newSarTotalSegments(arrLong.size());
                for (SmsBrandQueue brQueue : arrLong) {
                    try {
                        brQueue.setSarMsgRefNum(refer);
                        OptionalParameter sarSegmentSeqnum = OptionalParameters.newSarSegmentSeqnum(brQueue.getSarSegmentSeqnum());
                        //--
                        byte[] msgByte = buildUDH_LongMsg(brQueue.getMessage().getBytes(), referenceNumber[0], brQueue.getSarSegmentSeqnum(), arrLong.size());
                        // 05 00 03 123 02 01 xx
                        brQueue.setTimeSend(DateProc.createTimestamp());
                        String responseMsgId = clientSession.submitShortMessage(
                                "BRAND",
                                TypeOfNumber.INTERNATIONAL,
                                NumberingPlanIndicator.ISDN,
                                brQueue.getLabel(), // NHAN BRAND
                                TypeOfNumber.INTERNATIONAL,
                                NumberingPlanIndicator.ISDN,
                                brQueue.getPhone(), // So nhan tin
                                new ESMClass(SMPPConstant.ESMCLS_UDHI_INDICATOR_SET), // 
                                (byte) 0,
                                (byte) 0,
                                null,
                                null,
                                new RegisteredDelivery(1), // Defaul register Delivery
                                (byte) 0,
                                DataCoding.newInstance(Alphabet.ALPHA_DEFAULT.value()),
                                (byte) 0, // smDefaultMsgId
                                msgByte,
                                sarMsgRefNum,
                                sarSegmentSeqnum,
                                sarTotalSegments // SarTotalSegments
                        );
                        brQueue.setMessageId(responseMsgId);           // transaction ID
                        long delay = System.currentTimeMillis() - startTime;
                        brQueue.setProcessTime(delay);
                        brQueue.setResult(SUCCESS_INT);
                        brQueue.setErrorInfo(SUCCESS_MSG);
                        //--###--
                        logData(brQueue);
                        //--###--
                    } catch (PDUException e) {
                        brQueue.setErrorInfo("PDUException to [" + user + "|" + host + "|" + port + "|" + addressRange + "]:" + e.getMessage());
                        brQueue.setResult(EXCEPTION);
                        logData(brQueue);
                        // TODO Send fail ko Retry
                        logger.error("sendLong_MTASCII:" + Tool.getLogMessage(e));
                    } catch (ResponseTimeoutException e) {
                        brQueue.setErrorInfo("ResponseTimeoutException to [" + user + "|" + host + "|" + port + "|" + addressRange + "]:" + e.getMessage());
                        brQueue.setResult(EXCEPTION);
                        logData(brQueue);
                        logger.error("ResponseTimeoutException:" + Tool.getLogMessage(e));
                    } catch (InvalidResponseException | NegativeResponseException | IOException e) {
                        logger.error("sendLong_MTASCII.InvalidResponseException:" + Tool.getLogMessage(e));
                        if (e instanceof NegativeResponseException) {
                            NegativeResponseException ex = (NegativeResponseException) e;
                            int status = ex.getCommandStatus();
                            brQueue.setResult(status);
                            brQueue.setErrorInfo(ex.getMessage());
                            logData(brQueue);
                        } else {
                            brQueue.setRetry();
                            brQueue.setResult(EXCEPTION);
                            brQueue.setErrorInfo("InvalidResponseException | NegativeResponseException | IOException to [" + user + "|" + host + "|" + port + "|" + addressRange + "]" + e.getMessage());
                            logData(brQueue);
                        }
                    }
                }
            }
        };
    }

    private synchronized SMPPSession newSession() throws IOException {
        SMPPSession tmpSession = new SMPPSession();
        tmpSession.setPduProcessorDegree(DEFAULT_PROCESSOR_DEGREE);
        tmpSession.setTransactionTimer(DEFAULT_TRANSACTIONTIMER);
        tmpSession.connectAndBind(
                host,
                port,
                BindType.BIND_TRX,
                user,
                password,
                "SMPP", // SystemType   is OPERATOR
                TypeOfNumber.INTERNATIONAL,
                NumberingPlanIndicator.ISDN,
                addressRange);
        Tool.debug("[===SMPP [" + addressRange + "]===> Connect to:" + host + ":" + port + "[" + user + "/" + password + "] SUCCESS...");
        // 
        tmpSession.addSessionStateListener(new SessionStateListenerImpl());
        // Set listener to receive deliver_sm
        tmpSession.setMessageReceiverListener(this);
        // End add NEW tuanpla
        if (ALERT_LOSE) {
            ALERT_LOSE = false;
            String message = "[" + MyConfig.LB_NODE + "-FPT] ReConnect [SUCCESS] SMPP VTE [" + addressRange + "] IP:" + host + ":" + port;
            SMSUtils.SendAlert8x65(message, "84888157922");
        }
        connected = true;
        return tmpSession;
    }

    //****************
}
