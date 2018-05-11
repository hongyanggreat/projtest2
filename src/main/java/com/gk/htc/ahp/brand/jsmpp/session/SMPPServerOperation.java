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
package com.gk.htc.ahp.brand.jsmpp.session;

import java.io.IOException;

import com.gk.htc.ahp.brand.jsmpp.InvalidResponseException;
import com.gk.htc.ahp.brand.jsmpp.PDUException;
import com.gk.htc.ahp.brand.jsmpp.bean.DataCoding;
import com.gk.htc.ahp.brand.jsmpp.bean.ESMClass;
import com.gk.htc.ahp.brand.jsmpp.bean.MessageState;
import com.gk.htc.ahp.brand.jsmpp.bean.NumberingPlanIndicator;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter;
import com.gk.htc.ahp.brand.jsmpp.bean.RegisteredDelivery;
import com.gk.htc.ahp.brand.jsmpp.bean.TypeOfNumber;
import com.gk.htc.ahp.brand.jsmpp.bean.UnsuccessDelivery;
import com.gk.htc.ahp.brand.jsmpp.extra.NegativeResponseException;
import com.gk.htc.ahp.brand.jsmpp.extra.ResponseTimeoutException;
import com.gk.htc.ahp.brand.jsmpp.util.MessageId;

/**
 * @author uudashr
 *
 */
public interface SMPPServerOperation extends SMPPOperation {

    void deliverSm(String serviceType, TypeOfNumber sourceAddrTon,
            NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
            TypeOfNumber destAddrTon, NumberingPlanIndicator destAddrNpi,
            String destinationAddr, ESMClass esmClass, byte protocoId,
            byte priorityFlag, RegisteredDelivery registeredDelivery,
            DataCoding dataCoding, byte[] shortMessage,
            OptionalParameter... optionalParameters) throws PDUException,
            ResponseTimeoutException, InvalidResponseException,
            NegativeResponseException, IOException;

    void alertNotification(int sequenceNumber, TypeOfNumber sourceAddrTon,
            NumberingPlanIndicator sourceAddrNpi, String sourceAddr,
            TypeOfNumber esmeAddrTon, NumberingPlanIndicator esmeAddrNpi,
            String esmeAddr, OptionalParameter... optionalParameters)
            throws PDUException, IOException;

    void submitSmResp(MessageId messageId, int sequenceNumber)
            throws PDUException, IOException;

    void submitMultiResp(int sequenceNumber, String messageId,
            UnsuccessDelivery... unsuccessDeliveries) throws PDUException,
            IOException;

    void querySmResp(String messageId, String finalDate,
            MessageState messageState, byte errorCode, int sequenceNumber)
            throws PDUException, IOException;

    void replaceSmResp(int sequenceNumber) throws IOException;
}
