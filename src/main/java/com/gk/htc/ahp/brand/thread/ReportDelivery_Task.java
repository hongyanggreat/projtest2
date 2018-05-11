/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.service.primarywork.Queue;
import com.gk.htc.ahp.brand.app.SmppSever;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.MyLog;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.ClientInfo;
import com.gk.htc.ahp.brand.entity.My_DeliveryReceipt;
import com.gk.htc.ahp.brand.entity.WaitDeliveryReceipt;
import com.gk.htc.ahp.brand.jsmpp.InvalidResponseException;
import com.gk.htc.ahp.brand.jsmpp.PDUException;
import com.gk.htc.ahp.brand.jsmpp.bean.DataCoding;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliveryReceipt;
import com.gk.htc.ahp.brand.jsmpp.bean.ESMClass;
import com.gk.htc.ahp.brand.jsmpp.bean.GSMSpecificFeature;
import com.gk.htc.ahp.brand.jsmpp.bean.MessageMode;
import com.gk.htc.ahp.brand.jsmpp.bean.MessageType;
import com.gk.htc.ahp.brand.jsmpp.bean.NumberingPlanIndicator;
import com.gk.htc.ahp.brand.jsmpp.bean.RegisteredDelivery;
import com.gk.htc.ahp.brand.jsmpp.bean.TypeOfNumber;
import com.gk.htc.ahp.brand.jsmpp.extra.NegativeResponseException;
import com.gk.htc.ahp.brand.jsmpp.extra.ResponseTimeoutException;
import com.gk.htc.ahp.brand.jsmpp.session.SMPPServerSession;
import com.gk.htc.ahp.brand.jsmpp.util.DeliveryReceiptState;
import com.gk.htc.ahp.brand.jsmpp.util.MessageId;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import com.gk.htc.ahp.brand.service.primarywork.WorkQueue;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class ReportDelivery_Task extends Thread {

    private static final Logger logger = Logger.getLogger(ReportDelivery_Task.class);
    public static final HashMap<String, WaitDeliveryReceipt> WAIT_DELIVERY = new HashMap<>();   // Cho Report check Exprire
    private static final Queue<My_DeliveryReceipt> NOTIFY_RESULT_QUEUE = new Queue("NOTIFY_RESULT_QUEUE");
    private static boolean reportDRError = false;
    // TODO Log Report when Retry over Error 10 time
    private static final HashMap<String, Integer> ERROR_COUNT = new HashMap<>();
    private final WorkQueue workQueue;

    public ReportDelivery_Task(WorkQueue workQueue) {
        this.setName("ReportDelivery:" + DateProc.createTimestamp());
        MonitorWorker.addDemonName(this.getName());
        this.workQueue = workQueue;
    }

    public static void put(My_DeliveryReceipt report) {
        NOTIFY_RESULT_QUEUE.enqueue(report);
    }

    @Override
    public void run() {
        MyLog.debug("[===>ReportDelivery Started....]");
        while (AppStart.isRuning) {
            try {
                My_DeliveryReceipt report = NOTIFY_RESULT_QUEUE.dequeue();
                if (report.isShutDown()) {

                } else {
                    // KEY CACHE on WAIT_DELIVERY
                    String keyReport = report.getTransId() + "-" + report.getReceiverPhone();
                    // Neu khong phai tin test hoac tin Report hoac alert thi xu ly report
                    WaitDeliveryReceipt rpWait = WAIT_DELIVERY.remove(keyReport);
                    if (rpWait == null) {
                        logger.error("Get WaitDeliveryReceiptTask from WAIT_DELIVERY is null keycache= [" + keyReport + "]");
                        logger.error("Not [ReportDelivery] [" + report.getTransId() + "]" + report.getReceiverPhone() + "-->Status:" + report.getStatus() + "|keyReport=" + keyReport);
                    } else {
                        // TU WaitDeliveryReceipt se lay ra Client de tra Delivery
                        ClientInfo cl = SmppSever.SYSID_ADDRANGE_CLIENT.get(rpWait.getKeyCacheClient());
                        if (cl != null) {
                            int result = 0;
                            if (report.getError().equals("0")) {
                                result = 1;
                            }
                            DeliveryReceiptTask task = new DeliveryReceiptTask(
                                    cl.getSession(),
                                    rpWait.getMessageId(),
                                    result,
                                    rpWait.getType() + "", // 0 CSKH 1 QC
                                    report.getError(),
                                    report.getMsg(),
                                    rpWait.getLabel(),
                                    report.getReceiverPhone(),
                                    0);
                            workQueue.execute(task);
                        } else {
                            logger.error("Get Client Session from SYSID_ADDRANGE_CLIENT is null by keycache= [" + keyReport + "]");
                            logger.error("Not [ReportDelivery] [" + report.getTransId() + "]" + report.getReceiverPhone() + "-->Status:" + report.getStatus() + "|errmsg=" + report.getError() + "|cpCode=" + rpWait.getAcc().getAddressRange() + "|cpUser=" + rpWait.getAcc().getUserName());
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error(Tool.getLogMessage(ex));
                counterError("ReportDelivery");
            }
        }
        MonitorWorker.removeDemonName(this.getName());
    }

    private static int counterError(String phone) {
        Integer tmp = null;
        try {
            tmp = ERROR_COUNT.get(phone);
            if (tmp != null) {
                tmp += 1;
            } else {
                tmp = 1;
            }
            ERROR_COUNT.put(phone, tmp);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return tmp;
    }

    private static int getcounterError(String phone) {
        Integer tmp = null;
        try {
            tmp = ERROR_COUNT.get(phone);
            if (tmp == null) {
                tmp = 0;
            }
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
        return tmp;
    }

    private static void removeCount(String phone) {
        try {
            ERROR_COUNT.remove(phone);
        } catch (Exception e) {
            logger.error(Tool.getLogMessage(e));
        }
    }

//    public static void countReportOverErrorCache() {
//        try {
//            if (!reportDRError) {
//                // Neu chua thong bao loi
//                // Kiem tra xem co bao nhieu loi
//                {
//                    // Lay ra So loi
//                    int countError = getcounterError("ReportDeliveryCMC");
//                    MyLog.debug("So loi hien tai:" + countError);
//                    if (countError > MyConfig.REPORT_OVER_WARNING) {
//                        // Neu so loi vuot qua canh bao
//                        String message = "";
//                        SMSUtils.SendAlert8x65(message, "84986233352");
//                        // Remove count Error
//                        removeCount("ReportDeliveryCMC");
//                        // Danh dau da thong bao
//                        reportDRError = true;
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            logger.error(ex);
//        }
//    }
    public void shutDown() {
        My_DeliveryReceipt oneReceipt = new My_DeliveryReceipt();
        oneReceipt.setShutDown(true);
        put(oneReceipt);
    }

    public class DeliveryReceiptTask implements Runnable {

        private SMPPServerSession session;
        private MessageId messageId;
        private int result;
        private String type;
        private String error;
        private String shortMsg;
        private String label;
        private String receiver;
        // optional
        private boolean shutDown;
        private int delay;

        public DeliveryReceiptTask() {
        }

        public DeliveryReceiptTask(SMPPServerSession session,
                MessageId messageId,
                int result,
                String type,
                String error,
                String shortMsg,
                String label,
                String receiver,
                int delay
        ) {
            this.session = session;
            this.messageId = messageId;
            this.result = result;
            this.type = type;
            this.error = error;
            this.shortMsg = shortMsg;
            this.label = label;
            this.receiver = receiver;
            this.delay = delay;
        }

        public boolean isShutDown() {
            return shutDown;
        }

        public void setShutDown(boolean shutDown) {
            this.shutDown = shutDown;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delay);
                reportSMPP();
            } catch (InterruptedException e) {
            }

        }

        private void reportSMPP() {
            try {
                DeliveryReceiptState _result;
                int submitted;                  // Mac dinh la chua gui
                int delivery;
                switch (result) {
                    case 1:     // Thanh Cong sau khi gui
                        _result = DeliveryReceiptState.DELIVRD;
                        submitted = 1;
                        delivery = DeliveryReceiptState.DELIVRD.value();
                        break;
                    case 0:     // That bai
                        _result = DeliveryReceiptState.UNDELIV;
                        submitted = 1;
                        delivery = DeliveryReceiptState.UNDELIV.value();
                        break;
                    default:
                        _result = DeliveryReceiptState.REJECTD;
                        submitted = DeliveryReceiptState.UNDELIV.value();
                        delivery = DeliveryReceiptState.UNDELIV.value();
                        break;
                }
                DeliveryReceipt delReceipt = new DeliveryReceipt(messageId.getValue(),
                        submitted, //submitted
                        delivery, // delivered
                        new Date(), // submitDate
                        new Date(), // doneDate
                        _result, // finalStatus
                        error, // error  Description Error
                        shortMsg); // text //  max 20 Char
                session.deliverShortMessage(
                        String.valueOf(type), // serviceType:Service brand   CSKH QC
                        TypeOfNumber.INTERNATIONAL, // sourceAddrTon
                        NumberingPlanIndicator.ISDN, // sourceAddrNpi
                        label, // sourceAddr - LABEL
                        TypeOfNumber.INTERNATIONAL, // destAddrTon
                        NumberingPlanIndicator.ISDN, // destAddrNpi
                        receiver, // destinationAddr    -- PHONE
                        new ESMClass(MessageMode.DEFAULT, MessageType.SMSC_DEL_RECEIPT, GSMSpecificFeature.DEFAULT),
                        // esmClass
                        (byte) 0, // protocoId
                        (byte) 0, // priorityFlag
                        new RegisteredDelivery(0), // registeredDelivery
                        DataCoding.newInstance(0), // dataCoding
                        delReceipt.toString().getBytes()
                );                                                              // shortMessage
//                MyLog.debug("Sending delivery reciept for message id " + messageId);
            } catch (IllegalArgumentException | PDUException | ResponseTimeoutException
                    | InvalidResponseException | NegativeResponseException | IOException e) {
                logger.error("Failed sending delivery_receipt for message id " + messageId, e);
            }
        }
        /*
         session.deliverShortMessage(
         "CMT", // serviceType
         TypeOfNumber.valueOf(submitSm.getDestAddrTon()), // sourceAddrTon
         NumberingPlanIndicator.valueOf(submitSm.getDestAddrNpi()), // sourceAddrNpi
         submitSm.getDestAddress(), // sourceAddr
         TypeOfNumber.valueOf(submitSm.getSourceAddrTon()), // destAddrTon
         NumberingPlanIndicator.valueOf(submitSm.getSourceAddrNpi()), // destAddrNpi
         submitSm.getSourceAddr(), // destinationAddr
         new ESMClass(MessageMode.DEFAULT, MessageType.SMSC_DEL_RECEIPT, GSMSpecificFeature.DEFAULT),// esmClass
         (byte) 0, // protocoId
         (byte) 0, // priorityFlag
         new RegisteredDelivery(0), // registeredDelivery
         DataCoding.newInstance(0), // dataCoding
         delReceipt.toString().getBytes()
         */
    }

}
