package com.gk.htc.ahp.brand.app;

import com.gk.htc.ahp.brand.thread.ReportDelivery_Task;
import com.gk.htc.ahp.brand.cache.CacheClient;
import com.gk.htc.ahp.brand.cache.CacheSmppUserKey;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.DoWork;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.SMSUtils;
import com.gk.htc.ahp.brand.common.StringUtils;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.dao.SmsQueueDao;
import com.gk.htc.ahp.brand.entity.Account;
import com.gk.htc.ahp.brand.entity.BrandLabel;
import com.gk.htc.ahp.brand.entity.ClientInfo;
import com.gk.htc.ahp.brand.entity.My_DeliveryReceipt;
import com.gk.htc.ahp.brand.entity.RouteTable;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.entity.UniqueID;
import com.gk.htc.ahp.brand.entity.WaitDeliveryReceipt;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import com.gk.htc.ahp.brand.jsmpp.PDUStringException;
import com.gk.htc.ahp.brand.jsmpp.SMPPConstant;
import com.gk.htc.ahp.brand.jsmpp.bean.Alphabet;
import com.gk.htc.ahp.brand.jsmpp.bean.CancelSm;
import com.gk.htc.ahp.brand.jsmpp.bean.DataSm;
import com.gk.htc.ahp.brand.jsmpp.bean.QuerySm;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceSm;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMulti;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMultiResult;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitSm;
import com.gk.htc.ahp.brand.jsmpp.extra.ProcessRequestException;
import com.gk.htc.ahp.brand.jsmpp.extra.SessionState;
import com.gk.htc.ahp.brand.jsmpp.session.BindRequest;
import com.gk.htc.ahp.brand.jsmpp.session.DataSmResult;
import com.gk.htc.ahp.brand.jsmpp.session.QuerySmResult;
import com.gk.htc.ahp.brand.jsmpp.session.SMPPServerSession;
import com.gk.htc.ahp.brand.jsmpp.session.SMPPServerSessionListener;
import com.gk.htc.ahp.brand.jsmpp.session.ServerMessageReceiverListener;
import com.gk.htc.ahp.brand.jsmpp.session.Session;
import com.gk.htc.ahp.brand.jsmpp.session.SessionStateListener;
import com.gk.htc.ahp.brand.jsmpp.util.DeliveryReceiptState;
import com.gk.htc.ahp.brand.jsmpp.util.MessageIDGenerator;
import com.gk.htc.ahp.brand.jsmpp.util.MessageId;
import com.gk.htc.ahp.brand.jsmpp.util.RandomMessageIDGenerator;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.ahp.brand.smpp.utils.GsmUtil;
import com.gk.htc.sendMT.service.ProcessSMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author uudashr implements Runnable thi phai goi ham run va bi treo o day neu
 * extens Thread thi start
 */
public class SmppSever extends Thread implements ServerMessageReceiverListener {

    private static final Logger logger = LoggerFactory.getLogger(SmppSever.class);

    private final int processorDegree;      // So Client toi da ket noi vao SMPP Server
    private final int port;
    private boolean stop = false;
    // Session Mapping
    /**
     * MAP SESSION_LOGIN cho doi soat
     */
    public static final CacheSmppUserKey SESSIONID_USER_KEY = new CacheSmppUserKey();
    public static final CacheClient SYSID_ADDRANGE_CLIENT = new CacheClient();
    //--
    // Chuyen tu Private thanh public
    public static ExecutorService execService;
    private static final MessageIDGenerator messageIDGenerator = new RandomMessageIDGenerator();
    private final AtomicInteger requestCounter = new AtomicInteger();
//    private final TrafficWatcherThread traffic = new TrafficWatcherThread();
    //--------------------------------
    private static final String SPACE_KEY = "-";

    public SmppSever(int port, int processorDegree) {
        this.setName("Smpp Sever IDC");
        MonitorWorker.addDemonName(this.getName());
        this.port = port;
        this.processorDegree = processorDegree;
        //--
        execService = Executors.newFixedThreadPool(processorDegree);
    }

    public static void showCLient() {
        SYSID_ADDRANGE_CLIENT.showClientCache();
    }
    SMPPServerSessionListener sessionListener;

    void shutDown() {
        MonitorWorker.removeDemonName(this.getName());
        execService.shutdown();
        stop = true;
        if (sessionListener != null) {
            try {
                sessionListener.close();
                SYSID_ADDRANGE_CLIENT.unbindAllClient();
            } catch (IOException ex) {
                logger.error(Tool.getLogMessage(ex));
            }
        }
    }

    @Override
    public void run() {
        try {
            //---
            MyLog.debug("SmppSever Starting...");
            Tool.debug("SmppSever Starting...");
            sessionListener = new SMPPServerSessionListener(port);
            sessionListener.setSessionStateListener(new SessionStateListenerImpl());
            sessionListener.setPduProcessorDegree(processorDegree);
            // Co the bo thang nay
//            traffic.start();
            MyLog.debug("SmppSever Listening on port {" + port + "}");
            Tool.debug("SmppSever Listening on port " + port);
            while (AppStart.isRuning && !stop) {
                if (sessionListener != null) {
                    SMPPServerSession newSession = sessionListener.accept();
                    MyLog.debug("Accepting connection for session {" + newSession.getSessionId() + "}");
                    Tool.debug("Accepting connection for session: " + newSession.getSessionId());
                    newSession.setMessageReceiverListener(this);
                    execService.execute(new WaitBindTask(newSession));
                    MyLog.debug("SmppSever Started...");
                } else {
                    Tool.debug("==> SMPP Server Session is null or Close");
                }
            }
        } catch (IOException e) {
            logger.error("IO error occured", e);
        }
    }

    @Override
    public QuerySmResult onAcceptQuerySm(QuerySm querySm, SMPPServerSession source) throws ProcessRequestException {
        throw new ProcessRequestException("Invalid System ID", SMPPConstant.STAT_ESME_RQUERYFAIL);
    }

    public static MessageId getMessageID() {
        return messageIDGenerator.newMessageId();
    }

    public static enum CODE {

        NO_DATA_HEADER(22, "Data Header Empty"),
        NO_DATA_MSG(23, "Message is Empty"),
        INVALID_DEST_ADD(24, "Invalid Dest Addr"),
        INVALID_SESSION(25, "Invalid Session"),
        UNICODE_NOT_ALLOW(26, "Unicode not Allow"),
        UNKNOW_EXCEPTION(99, "Unknow Exception"), //--
        ;
        public int val;
        public String desc;

        private CODE(int val, String desc) {
            this.val = val;
            this.desc = desc;
        }

        public String getFull() {   //  max 20 Char
            return this.val + "." + this.desc;
        }
    }
    String infoLog = "";

    void appen(String input) {
        if (Tool.checkNull(infoLog)) {
            infoLog += input;
        } else {
            infoLog += "|" + input;
        }
    }

    /**
     * Nhận một bản tin
     *
     * @param submitSm
     * @param smppSession
     * @return
     * @throws com.gk.htc.ahp.brand.jsmpp.extra.ProcessRequestException
     */
    @Override
    public MessageId onAcceptSubmitSm(SubmitSm submitSm, SMPPServerSession smppSession) throws ProcessRequestException {

        // Generate MessageId de ty tra Dr
        MessageId messageId = getMessageID();
        /**
         * Create CP Queue
         */
        DoWork dowork = new DoWork();
        long delay = 0;
        SmsBrandQueue oneMsg = new SmsBrandQueue();
        oneMsg.setNode(MyConfig.LB_NODE);
        try {
            String label = submitSm.getSourceAddr();            // BRAND NAME
            appen("label:" + label);
            oneMsg.setLabel(label);
            String phone = submitSm.getDestAddress();           // Gui den so
            appen("SourcePhone:" + phone);
            phone = SMSUtils.PhoneTo84(phone);              // Format Phone to 84
            appen("phoneFM84:" + phone);
            oneMsg.setPhone(phone);
            byte dataEncode = submitSm.getDataCoding();        // DataEncode
            appen("dataEncode:" + dataEncode);
            String svType = submitSm.getServiceType();
            appen("svType:" + svType);                          // CSKH or QC
            oneMsg.setSystemId(UniqueID.getId(phone));     // Mac dinh sinh ra boi SYSTEM SystemId
            //--End Parter Info
            oneMsg.setRequestTime(DateProc.createTimestamp());
            appen("requestTime:" + DateProc.createTimestamp());
            String keycacheClient = SESSIONID_USER_KEY.get(smppSession.getSessionId());    // userCp for CDR
            String[] arr = keycacheClient.split(SPACE_KEY);
            String systemId = arr[0];
            appen("systemId:" + systemId);
            appen("addRessRange:" + arr[1]);
            Account acc = Account.getAccount(systemId);
            if (acc == null) {
                oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(CODE.INVALID_SESSION.getFull());
                appen("errorInfo:" + oneMsg.getErrorInfo() + "[" + "Invalid System ID:" + SMPPConstant.STAT_ESME_RINVSYSID + "]");
                throw new ProcessRequestException("Invalid System ID", SMPPConstant.STAT_ESME_RINVSYSID);
            } else if (acc.getStatus() != Account.STATUS.ACTIVE.val) {
                oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                oneMsg.setUserSender(acc.getUserName());
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(CODE.INVALID_SESSION.getFull());
                appen("errorInfo:" + oneMsg.getErrorInfo() + "[" + "Loked System ID:" + SMPPConstant.STAT_ESME_RINVSYSID + "]");
                throw new ProcessRequestException("Loked System ID", SMPPConstant.STAT_ESME_RINVSYSID);
            }
            oneMsg.setUserSender(acc.getUserName());
            oneMsg.setCpCode(acc.getCpCode());                              // CP_CODE for One Partner has many Account
            String oper = SMSUtils.buildMobileOperator(phone);              // OPERATOR
            appen("oper:" + oper);
            oneMsg.setOper(oper);
            int type = BrandLabel.TYPE.CSKH.val;                            // Defaul Type CSKH
            if (Tool.checkNull(svType)) {
                type = BrandLabel.TYPE.CSKH.val;
                appen(".CSKH");
            } else if (svType.equals(String.valueOf(BrandLabel.TYPE.QC.val))) {
                appen(".QC");
                type = BrandLabel.TYPE.QC.val;
            } else {
                appen(".CSKH");
            }
            oneMsg.setType(type);                           // CSKH/QC
            boolean phoneValid = SMSUtils.validPhoneVN(phone);
            if (!phoneValid) {
                oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(CODE.INVALID_DEST_ADD.getFull());
                appen("errorInfo:" + oneMsg.getErrorInfo() + "[" + "Invalid Dest Addr:" + SMPPConstant.STAT_ESME_RINVDSTADR + "]");
                throw new ProcessRequestException("Invalid Dest Addr", SMPPConstant.STAT_ESME_RINVDSTADR);
            }
            //-----------------//
            byte emsClass = submitSm.getEsmClass();
            byte[] userData = submitSm.getShortMessage();                   // Data Mac Dinh Co the Long co the khong
            byte[] userDataHeader;
            //--
//            Tool.debug(emsClass);
            if (emsClass == SMPPConstant.ESMCLS_UDHI_INDICATOR_SET) {
//                Tool.debug("|==> Tin nhan den vao Long SMS");
                // LongSMS emsClass =64 Cho nay phai Xu ly Message
                userData = GsmUtil.getShortMessageUserData(submitSm.getShortMessage());
                userDataHeader = GsmUtil.getShortMessageUserDataHeader(submitSm.getShortMessage());
//                Tool.printByte(userDataHeader);
                if (userDataHeader == null) {
                    oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                    appen("result:" + oneMsg.getResult());
                    oneMsg.setErrorInfo(CODE.NO_DATA_HEADER.getFull());
                    appen("errorInfo:" + oneMsg.getErrorInfo() + "[" + "Expected Optional Parameter missing:" + SMPPConstant.STAT_ESME_RMISSINGTLV + "]");
                    throw new ProcessRequestException("Expected Optional Parameter missing.", SMPPConstant.STAT_ESME_RMISSINGTLV);
                } else if (userData == null) {
                    oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                    appen("result:" + oneMsg.getResult());
                    oneMsg.setErrorInfo(CODE.NO_DATA_MSG.getFull());
                    appen("errorInfo:" + oneMsg.getErrorInfo() + "[Predefined Message Invalid or Not Found:" + SMPPConstant.STAT_ESME_RINVDFTMSGID + "]");
                    throw new ProcessRequestException("Predefined Message Invalid or Not Found", SMPPConstant.STAT_ESME_RINVDFTMSGID);
                } else {
                    // ca userHeader va userData ok
                    try {
                        int referNum = userDataHeader[3] & 0xff;                // Range 0 to 255, not -128 to 127; // Reference
                        int totalMessages = userDataHeader[4] & 0xff;           // Range 0 to 255, not -128 to 127;
                        int currentMessageNum = userDataHeader[5] & 0xff;       // Range 0 to 255, not -128 to 127;
//                        Tool.debug("|==> referNum:" + referNum + "|totalMessages:" + totalMessages + "|sequenceMsg:" + currentMessageNum);
                        oneMsg.setSarMsgRefNum(referNum);
                        oneMsg.setSarTotalSegments(totalMessages);
                        oneMsg.setSarSegmentSeqnum(currentMessageNum);
                    } catch (Exception e) {
                        appen("errorInfo:" + e.getMessage() + "[Invalid userDataHeader:" + SMPPConstant.STAT_ESME_RMISSINGTLV + "]");
                        throw new ProcessRequestException("Invalid userDataHeader.", SMPPConstant.STAT_ESME_RMISSINGTLV);
                    }
                }
            } else {
                // VAO SMS THUONG LUON
                oneMsg.setSarTotalSegments(1);
                oneMsg.setSarSegmentSeqnum(1);
            }
            appen("SarMsgRefNum:" + oneMsg.getSarMsgRefNum());
            appen("SarTotalSegment:" + oneMsg.getSarTotalSegments());
            appen("SarSegmentSeqnum:" + oneMsg.getSarSegmentSeqnum());
            // Message Reciver String
            if (userData == null) {
                oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(CODE.NO_DATA_MSG.getFull());
                appen("errorInfo:" + oneMsg.getErrorInfo() + "[Predefined Message Invalid or Not Found:" + SMPPConstant.STAT_ESME_RINVDFTMSGID + "]");
                throw new ProcessRequestException("Predefined Message Invalid or Not Found", SMPPConstant.STAT_ESME_RINVDFTMSGID);
            }
            String message;                          // Defaul Message
            if (dataEncode == Alphabet.ALPHA_UCS2.value()) {
//                try {
//                    message = new String(userData, "UTF-16BE");
//                } catch (Exception e) {
//                    logger.error(Tool.getLogMessage(e));
//                    message = new String(userData);
//                }
                oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(CODE.UNICODE_NOT_ALLOW.getFull());
                appen("errorInfo:" + oneMsg.getErrorInfo() + "[Message dataEncode[8] not Allow:" + SMPPConstant.STAT_ESME_RX_P_APPN + "]");
                throw new ProcessRequestException("Message dataEncode[8] not Allow", SMPPConstant.STAT_ESME_RX_P_APPN);
                // ISO 8859-1 | ISO 8859-8 | ISO 8859-5 | IA5
            } else if (dataEncode == Alphabet.ALPHA_8_BIT.value()) {
//                try {
//                    MyLog.debug("User use ALPHA_8_BIT:" + messageId.getValue());
//                    message = new String(userData, "ISO8859-1");
//                } catch (Exception e) {
//                    logger.error(Tool.getLogMessage(e));
//                    message = new String(submitSm.getShortMessage());
//                }
                oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(CODE.UNICODE_NOT_ALLOW.getFull());
                appen("errorInfo:" + oneMsg.getErrorInfo() + "[Message dataEncode[4] not Allow:" + SMPPConstant.STAT_ESME_RX_P_APPN + "]");
                throw new ProcessRequestException("Message dataEncode[4] not Allow", SMPPConstant.STAT_ESME_RX_P_APPN);
            } else {
                // La noi Dung USerData Ban Dau
                message = new String(userData);
            }
            BrandLabel brand = BrandLabel.findFromCache(acc.getUserName(), label);
            if (brand == null) {
                oneMsg.setResult(ProcessSMS.CODE.BRAND_NOT_AVTIVE.val);
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(ProcessSMS.CODE.BRAND_NOT_AVTIVE.mess);
                appen("errorInfo:" + oneMsg.getErrorInfo() + "[Invalid Source Address:" + SMPPConstant.STAT_ESME_RINVSRCADR + "]");
                throw new ProcessRequestException("Invalid Source Address", SMPPConstant.STAT_ESME_RINVSRCADR);
            }
            RouteTable route = brand.getRoute();

            oneMsg.setTotalSms(1);                      // SMPP thi chi di duoc 1 Message 1
            String group = route.getGroup(oper);
            oneMsg.setBrGroup(group);
            appen("brGroup:" + group);
            appen("totalsms:1");                        // Voi smpp 1 lan chi gui duoc 1 Message
//--    

            boolean operApproved = route.checkRole(oper, BrandLabel.TYPE.CSKH.val);
            if (!operApproved) {
                oneMsg.setResult(ProcessSMS.CODE.SMS_TELCO_NOT_ALLOW.val);
                appen("result:" + oneMsg.getResult());
                oneMsg.setErrorInfo(ProcessSMS.CODE.SMS_TELCO_NOT_ALLOW.mess);
                appen("errorInfo:" + oneMsg.getErrorInfo() + "ESME Receiver Reject Message Error Code[8]" + SMPPConstant.STAT_ESME_RX_R_APPN + "]");
                throw new ProcessRequestException("ESME Receiver Reject Message Error Code[8]", SMPPConstant.STAT_ESME_RX_R_APPN);
            }
            message = StringUtils.replaceMultiWhiteSpace(message);
            oneMsg.setMessage(message);
            appen("message:" + message);
            oneMsg.setResult(ProcessSMS.CODE.RECEIVED.val);
            appen("Result:" + ProcessSMS.CODE.RECEIVED.val);
            oneMsg.setErrorInfo(ProcessSMS.CODE.RECEIVED.mess);
            oneMsg.setTranId(messageId.getValue());
            appen("TranId:" + messageId.getValue());
            oneMsg.setDataEncode(dataEncode);
            appen("setDataEndCode:" + dataEncode);
            //--
            String sendTo = route.getSendTo(oper, BrandLabel.TYPE.CSKH.val);  // TO GATEWAY
            oneMsg.setSendTo(sendTo);
            appen("sendTo:" + sendTo);
            //********* BO LOG DB CHUYEN NGAY VAO QUEUE GUI LOG SAU
            oneMsg.setStartTime(System.currentTimeMillis());
            if (emsClass == SMPPConstant.ESMCLS_UDHI_INDICATOR_SET) {
                // Long MT
                AppStart.concatLong_Task.putLongMT(oneMsg);
            } else {
                AppStart.sendPrimaryTask.addToqueue(oneMsg);
            }
            Tool.debug(DateProc.Timestamp2HHMMSS(1) + ": FROM [" + acc.getUserName() + "] brand[" + brand.getBrandLabel() + "] to GW=" + oneMsg.getSendTo());
            if (submitSm.getRegisteredDelivery() == 1) {
                //==> TODO Mac dinh de Nhan DR ??
                WaitDeliveryReceipt waitDLV_RT = new WaitDeliveryReceipt();
                waitDLV_RT.setMessageId(messageId);
                waitDLV_RT.setAcc(acc);
                waitDLV_RT.setType(type);
                waitDLV_RT.setLabel(label);
                waitDLV_RT.setReqTime(System.currentTimeMillis());              // Dung cho Expire
                waitDLV_RT.setKeyCacheClient(keycacheClient);
                String keyReport = messageId.getValue() + "-" + phone;
                ReportDelivery_Task.WAIT_DELIVERY.put(keyReport, waitDLV_RT);
                // TODO
                My_DeliveryReceipt dr = new My_DeliveryReceipt(messageId.getValue(),
                        oneMsg.getPhone(),
                        1, // Submited
                        DeliveryReceiptState.DELIVRD.value(), // dlvrd
                        DeliveryReceiptState.DELIVRD, // status
                        "0", // error
                        oneMsg.getMessage().length() > 20 ? oneMsg.getMessage().substring(0, 20) : oneMsg.getMessage());
                ReportDelivery_Task.put(dr);
            } else {
                logger.warn("PartNer [" + systemId + "] not RegisteredDelivery [1] for Message ID:" + messageId.getValue() + "-[" + phone + "]");
            }
            logger.debug("Receiving submit_sm: {} | and return message id: {}", new String(submitSm.getShortMessage()), messageId.getValue());
            // Tang request len
            requestCounter.incrementAndGet();
        } catch (IllegalArgumentException e) {
            logger.error(Tool.getLogMessage(e));
            oneMsg.setResult(ProcessSMS.CODE.REJECT.val);    // Error
            oneMsg.setErrorInfo(CODE.UNKNOW_EXCEPTION.getFull());
            appen("errorInfo:" + oneMsg.getErrorInfo() + "[System Error" + SMPPConstant.STAT_ESME_RSYSERR + "]");
            throw new ProcessRequestException("System Error", SMPPConstant.STAT_ESME_RSYSERR);
        } finally {
            delay = dowork.done();
            appen("delay:" + delay);
            oneMsg.setProcessTime(delay);
            oneMsg.setStartTime(System.currentTimeMillis());
            // LUON LUON LOG INCOME MESSAGE
            AppStart.log_incomeTask.addToqueue(oneMsg);
            MyLog.logIncome(SmsQueueDao.toStringJson(oneMsg));
            infoLog = "";
        }
        // Tra DR ve cho KH NHAN THANH CONG
        return messageId;
    }

    private class SessionStateListenerImpl implements SessionStateListener {

        @Override
        public void onStateChange(SessionState newState, SessionState oldState, Object source) {
            SMPPServerSession session = (SMPPServerSession) source;
            if (newState == SessionState.CLOSED) {
                try {
                    // Remove StstemId login In Maping
                    String keycacheClient = SESSIONID_USER_KEY.remove(session.getSessionId());
                    // Remove Client from Client queue
                    SYSID_ADDRANGE_CLIENT.remove(keycacheClient);
                    // Delete Clientinfo
                    ClientInfo.delbySessionId(session.getSessionId());
                } catch (Exception e) {
                    logger.error(Tool.getLogMessage(e));
                }
            }
            MyLog.debug("New state of " + session.getSessionId() + " is " + newState);
        }
    }

    /**
     * WaitBindTask Thread Acept Login SMPP
     */
    private class WaitBindTask implements Runnable {

        private final SMPPServerSession _clientSession;

        public WaitBindTask(SMPPServerSession serverSession) {
            this._clientSession = serverSession;
        }

        @Override
        public void run() {
            try {
                SocketAddress clAdd = _clientSession.getClientAdd();
                String[] clientIPPort = processClientAdd(clAdd);
                // TODO chua check IP Allow
                MyLog.debug("SmppSever.class: Request From Client Address:" + clAdd.toString());
                Tool.debug("SmppSever.class: Request From Client Address:" + clAdd.toString());
                BindRequest bindRequest = _clientSession.waitForBind(10000);
                try {
                    debugRequest(logger, bindRequest);
                    String connectAddressRange = bindRequest.getAddressRange();
                    String keycacheClient = bindRequest.getSystemId() + SPACE_KEY + connectAddressRange;
//                    Tool.debug("WaitBindTask.keycacheClient:" + keycacheClient);
                    // Check username pass
                    Account accDao = new Account();
                    Account accLogin = accDao.checkLogin(bindRequest.getSystemId(), bindRequest.getPassword());
                    if (accLogin != null) {
                        String allowAddressRange = accLogin.getAddressRange();
                        // Check IP
                        if (!validAddressRange(allowAddressRange, connectAddressRange)) {
                            logger.error("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "-" + connectAddressRange + "] by Error[Invalid AddressRange]");
                            Tool.debug("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "-" + connectAddressRange + "] by Error[Invalid AddressRange]");
                            bindRequest.reject(SMPPConstant.STAT_ESME_RBINDFAIL);
                        } else if (accLogin.validIP(clientIPPort[0])) {
                            // Check AddressRange
                            if (SYSID_ADDRANGE_CLIENT.get(keycacheClient) != null) {
                                logger.error("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "] by Error[Same AddressRange]");
                                Tool.debug("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "] by Error[Same AddressRange]");
                                bindRequest.reject(SMPPConstant.STAT_ESME_RBINDFAIL);
                            } else {
                                // accepting request and send bind response immediately
                                bindRequest.accept(bindRequest.getSystemId());
                                MyLog.debug("Accepting bind request from [" + clAdd.toString() + " -user:" + bindRequest.getSystemId() + "] session {" + _clientSession.getSessionId() + "}");
                                // Create Client
                                ClientInfo cl = new ClientInfo();
                                cl.setIp(clientIPPort[0]);
                                cl.setPort(Tool.getInt(clientIPPort[1]));   // Port Client
                                cl.setClname(bindRequest.getSystemId());
                                cl.setAddressRange(bindRequest.getAddressRange());
                                cl.setOper("CP-BRAND");                                  // For CP
                                cl.setSession(_clientSession);
                                // Maping Session ID with SystemId
                                SESSIONID_USER_KEY.put(_clientSession.getSessionId(), keycacheClient);
                                SYSID_ADDRANGE_CLIENT.put(keycacheClient, cl);
                                // Map User tuong ung Session ID
                                cl.setSessionId(_clientSession.getSessionId());
                                // Add Client Real Time to DB
                                int idCl = cl.checkExist();
                                if (idCl > 0) {
                                    cl.setClid(idCl);
                                    cl.updateClient(cl);
                                } else {
                                    cl.addClient(cl);
                                }
                            }
                        } else {
                            logger.error("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "] by Error[Invalid IP:" + clientIPPort[0] + "]");
                            Tool.debug("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "] by Error[Invalid IP:" + clientIPPort[0] + "]");
                            bindRequest.reject(SMPPConstant.STAT_ESME_RINVPASWD);
                        }
                    } else {
                        logger.error("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "] by Error[Login Fail]");
                        Tool.debug("Rejecting bind request from [" + clAdd.toString() + "-" + bindRequest.getSystemId() + "-" + bindRequest.getPassword() + "] by Error[Login Fail]");
                        bindRequest.reject(SMPPConstant.STAT_ESME_RINVPASWD);
                    }
                } catch (PDUStringException e) {
                    logger.error("PDUStringException Invalid system id", e);
                    bindRequest.reject(SMPPConstant.STAT_ESME_RSYSERR);
                }
            } catch (IllegalStateException e) {
                logger.error("IllegalStateException System error", e);
            } catch (TimeoutException e) {
                logger.warn("TimeoutException Wait for bind has reach timeout", e);
            } catch (IOException e) {
                logger.error("IOException Failed accepting bind request for session {}", _clientSession.getSessionId());
            }
        }
    }

    private boolean validAddressRange(String allowRange, String requestRange) {
        boolean result = false;
        if (allowRange != null) {
//            Tool.debug("validAddressRange.allow:" + allow + "|request:" + request);
            String[] arr = allowRange.split(",");
            if (arr != null && arr.length > 0) {
                for (String one : arr) {
                    if (one.equals(requestRange)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void debugRequest(Logger logger, BindRequest bindRequest) {
        MyLog.debug("getSystemId:" + bindRequest.getSystemId());                // (Var. max 16 =15)
        MyLog.debug("getPassword:" + bindRequest.getPassword());                // (Var. max 9 =8)
        // getAddressRange  (Var. max 41 =40) Dung De Phan Biet Client Voi Nhieu Client Tu 1 IP
        MyLog.debug("getAddressRange:" + bindRequest.getAddressRange());
        MyLog.debug("getSystemType:" + bindRequest.getSystemType());            // Oper for BankSim         (Var. 13 = 12)
        MyLog.debug("getAddrTon:" + bindRequest.getAddrTon());
        MyLog.debug("getBindType:" + bindRequest.getBindType());
        MyLog.debug("getAddrNpi:" + bindRequest.getAddrNpi().toString());
    }

    private String[] processClientAdd(SocketAddress clAdd) {
        String[] result = {"0", "0"};
        if (clAdd != null) {
            String tmp = clAdd.toString();
            tmp = tmp.replaceAll("/", "");
            String[] arr = tmp.split("[:]");
            if (arr != null && arr.length == 2) {
                result[0] = arr[0];
                result[1] = arr[1];
            }
        }
        return result;
    }

    /**
     * Nhận nhiều bản tin cùng nội dung
     *
     * @param submitMulti
     * @param source
     * @return
     * @throws ProcessRequestException
     */
    @Override
    public SubmitMultiResult onAcceptSubmitMulti(SubmitMulti submitMulti, SMPPServerSession source) throws ProcessRequestException {
        throw new ProcessRequestException("Invalid System ID", SMPPConstant.STAT_ESME_RSUBMITFAIL);
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
        throw new ProcessRequestException("Invalid System ID", SMPPConstant.STAT_ESME_RSUBMITFAIL);
    }

    @Override
    public void onAcceptCancelSm(CancelSm cancelSm, SMPPServerSession source) throws ProcessRequestException {
        throw new ProcessRequestException("Yêu cầu ứng dụng không gửi bản tin cancel_sm.", SMPPConstant.STAT_ESME_RCANCELFAIL);
    }

    @Override
    public void onAcceptReplaceSm(ReplaceSm replaceSm, SMPPServerSession source) throws ProcessRequestException {
        throw new ProcessRequestException("Yêu cầu ứng dụng không gửi bản tin replace_sm", SMPPConstant.STAT_ESME_RREPLACEFAIL);
    }
}
