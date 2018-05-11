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

import com.gk.htc.ahp.brand.jsmpp.SMPPConstant;
import com.gk.htc.ahp.brand.jsmpp.bean.Command;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliverSmResp;
import com.gk.htc.ahp.brand.jsmpp.extra.PendingResponse;
import com.gk.htc.ahp.brand.jsmpp.extra.SessionState;
import com.gk.htc.ahp.brand.jsmpp.session.ServerResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author uudashr
 *
 */
class SMPPServerSessionBoundRX extends SMPPServerSessionBound implements
        SMPPServerSessionState {

    private static final Logger logger = LoggerFactory.getLogger(SMPPServerSessionBoundRX.class);

    public SessionState getSessionState() {
        return SessionState.BOUND_RX;
    }

    public void processDeliverSmResp(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        processDeliverSmResp0(pduHeader, pdu, responseHandler);
    }

    public void processQuerySm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        responseHandler.sendNegativeResponse(pduHeader.getCommandId(),
                SMPPConstant.STAT_ESME_RINVBNDSTS, pduHeader
                        .getSequenceNumber());
    }

    public void processSubmitSm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        responseHandler.sendNegativeResponse(pduHeader.getCommandId(),
                SMPPConstant.STAT_ESME_RINVBNDSTS, pduHeader
                        .getSequenceNumber());
    }

    public void processSubmitMulti(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        responseHandler.sendNegativeResponse(pduHeader.getCommandId(),
                SMPPConstant.STAT_ESME_RINVBNDSTS, pduHeader
                        .getSequenceNumber());
    }

    public void processCancelSm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        responseHandler.sendNegativeResponse(pduHeader.getCommandId(),
                SMPPConstant.STAT_ESME_RINVBNDSTS, pduHeader
                        .getSequenceNumber());
    }

    public void processReplaceSm(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        responseHandler.sendNegativeResponse(pduHeader.getCommandId(),
                SMPPConstant.STAT_ESME_RINVBNDSTS, pduHeader
                        .getSequenceNumber());
    }

    static final void processDeliverSmResp0(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        PendingResponse<Command> pendingResp = responseHandler.removeSentItem(pduHeader.getSequenceNumber());
        if (pendingResp != null) {
            DeliverSmResp resp = pduDecomposer.deliverSmResp(pdu);
            pendingResp.done(resp);
        } else {
            logger.warn("No request with sequence number "
                    + pduHeader.getSequenceNumber() + " found");
        }
    }

}
