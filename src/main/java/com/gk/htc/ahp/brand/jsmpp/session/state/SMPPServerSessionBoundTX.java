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
package com.gk.htc.ahp.brand.jsmpp.session.state;

import java.io.IOException;

import com.gk.htc.ahp.brand.jsmpp.InvalidNumberOfDestinationsException;
import com.gk.htc.ahp.brand.jsmpp.PDUStringException;
import com.gk.htc.ahp.brand.jsmpp.SMPPConstant;
import com.gk.htc.ahp.brand.jsmpp.bean.CancelSm;
import com.gk.htc.ahp.brand.jsmpp.bean.Command;
import com.gk.htc.ahp.brand.jsmpp.bean.QuerySm;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceSm;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMulti;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMultiResult;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitSm;
import com.gk.htc.ahp.brand.jsmpp.extra.ProcessRequestException;
import com.gk.htc.ahp.brand.jsmpp.extra.SessionState;
import com.gk.htc.ahp.brand.jsmpp.session.QuerySmResult;
import com.gk.htc.ahp.brand.jsmpp.session.ServerResponseHandler;
import com.gk.htc.ahp.brand.jsmpp.util.MessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author uudashr
 *
 */
class SMPPServerSessionBoundTX extends SMPPServerSessionBound implements
        SMPPServerSessionState {

    private static final Logger logger = LoggerFactory.getLogger(SMPPServerSessionBoundTX.class);

    public SessionState getSessionState() {
        return SessionState.BOUND_TX;
    }

    public void processDeliverSmResp(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        responseHandler.sendNegativeResponse(pduHeader.getCommandId(),
                SMPPConstant.STAT_ESME_RINVBNDSTS, pduHeader
                        .getSequenceNumber());
    }

    public void processSubmitSm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        try {
            SubmitSm submitSm = pduDecomposer.submitSm(pdu);
            MessageId messageId = responseHandler.processSubmitSm(submitSm);
            logger.debug("Sending response with message_id " + messageId + " for request with sequence_number " + pduHeader.getSequenceNumber());
            responseHandler.sendSubmitSmResponse(messageId, pduHeader.getSequenceNumber());
        } catch (PDUStringException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        } catch (ProcessRequestException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        }
    }

    public void processSubmitMulti(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        try {
            SubmitMulti submitMulti = pduDecomposer.submitMulti(pdu);
            SubmitMultiResult result = responseHandler.processSubmitMulti(submitMulti);
            logger.debug("Sending response with message_id " + result.getMessageId() + " for request with sequence_number " + pduHeader.getSequenceNumber());
            responseHandler.sendSubmitMultiResponse(result, pduHeader.getSequenceNumber());
        } catch (PDUStringException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        } catch (InvalidNumberOfDestinationsException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), SMPPConstant.STAT_ESME_RINVNUMDESTS, pduHeader.getSequenceNumber());
        } catch (ProcessRequestException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        }
    }

    public void processQuerySm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        try {
            QuerySm querySm = pduDecomposer.querySm(pdu);
            QuerySmResult result = responseHandler.processQuerySm(querySm);
            responseHandler.sendQuerySmResp(querySm.getMessageId(),
                    result.getFinalDate(), result.getMessageState(),
                    result.getErrorCode(), pduHeader.getSequenceNumber());
        } catch (PDUStringException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        } catch (ProcessRequestException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        }
    }

    public void processCancelSm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        try {
            CancelSm cancelSm = pduDecomposer.cancelSm(pdu);
            responseHandler.processCancelSm(cancelSm);
            responseHandler.sendCancelSmResp(pduHeader.getSequenceNumber());
        } catch (PDUStringException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        } catch (ProcessRequestException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        }
    }

    public void processReplaceSm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        try {
            ReplaceSm replaceSm = pduDecomposer.replaceSm(pdu);
            responseHandler.processReplaceSm(replaceSm);
            responseHandler.sendReplaceSmResp(pduHeader.getSequenceNumber());
        } catch (PDUStringException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        } catch (ProcessRequestException e) {
            responseHandler.sendNegativeResponse(pduHeader.getCommandId(), e.getErrorCode(), pduHeader.getSequenceNumber());
        }
    }
}