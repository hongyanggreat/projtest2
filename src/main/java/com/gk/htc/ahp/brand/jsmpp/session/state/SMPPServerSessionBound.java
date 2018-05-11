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
import com.gk.htc.ahp.brand.jsmpp.session.ServerResponseHandler;

/**
 * @author uudashr
 *
 */
abstract class SMPPServerSessionBound extends
        AbstractGenericSMPPSessionBound implements SMPPServerSessionState {

    public void processBind(Command pduHeader, byte[] pdu,
            ServerResponseHandler responseHandler) throws IOException {
        responseHandler.sendNegativeResponse(pduHeader.getCommandId(),
                SMPPConstant.STAT_ESME_RALYBND, pduHeader.getSequenceNumber());
    }
}