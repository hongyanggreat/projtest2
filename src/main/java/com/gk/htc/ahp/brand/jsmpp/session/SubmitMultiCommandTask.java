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
import java.io.OutputStream;

import com.gk.htc.ahp.brand.jsmpp.PDUException;
import com.gk.htc.ahp.brand.jsmpp.PDUSender;
import com.gk.htc.ahp.brand.jsmpp.bean.DataCoding;
import com.gk.htc.ahp.brand.jsmpp.bean.DestinationAddress;
import com.gk.htc.ahp.brand.jsmpp.bean.ESMClass;
import com.gk.htc.ahp.brand.jsmpp.bean.NumberingPlanIndicator;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter;
import com.gk.htc.ahp.brand.jsmpp.bean.RegisteredDelivery;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceIfPresentFlag;
import com.gk.htc.ahp.brand.jsmpp.bean.TypeOfNumber;

/**
 * @author uudashr
 *
 */
public class SubmitMultiCommandTask extends AbstractSendCommandTask {

    private String serviceType;
    private TypeOfNumber sourceAddrTon;
    private NumberingPlanIndicator sourceAddrNpi;
    private String sourceAddr;
    private DestinationAddress[] destinationAddresses;
    private ESMClass esmClass;
    private byte protocolId;
    private byte priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private RegisteredDelivery registeredDelivery;
    private ReplaceIfPresentFlag replaceIfPresentFlag;
    private DataCoding dataCoding;
    private byte smDefaultMsgId;
    private byte[] shortMessage;
    private OptionalParameter[] optionalParameters;

    public SubmitMultiCommandTask(PDUSender pduSender, String serviceType,
            TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi,
            String sourceAddr, DestinationAddress[] destinationAddresses,
            ESMClass esmClass, byte protocolId, byte priorityFlag,
            String scheduleDeliveryTime, String validityPeriod,
            RegisteredDelivery registeredDelivery,
            ReplaceIfPresentFlag replaceIfPresentFlag, DataCoding dataCoding,
            byte smDefaultMsgId, byte[] shortMessage,
            OptionalParameter[] optionalParameters) {
        super(pduSender);
        this.serviceType = serviceType;
        this.sourceAddrTon = sourceAddrTon;
        this.sourceAddrNpi = sourceAddrNpi;
        this.sourceAddr = sourceAddr;
        this.destinationAddresses = destinationAddresses;
        this.esmClass = esmClass;
        this.protocolId = protocolId;
        this.priorityFlag = priorityFlag;
        this.scheduleDeliveryTime = scheduleDeliveryTime;
        this.validityPeriod = validityPeriod;
        this.registeredDelivery = registeredDelivery;
        this.replaceIfPresentFlag = replaceIfPresentFlag;
        this.dataCoding = dataCoding;
        this.smDefaultMsgId = smDefaultMsgId;
        this.shortMessage = shortMessage;
        this.optionalParameters = optionalParameters;
    }

    public void executeTask(OutputStream out, int sequenceNumber)
            throws PDUException, IOException {
        pduSender.sendSubmiMulti(out, sequenceNumber, serviceType,
                sourceAddrTon, sourceAddrNpi, sourceAddr, destinationAddresses,
                esmClass, protocolId, priorityFlag, scheduleDeliveryTime,
                validityPeriod, registeredDelivery, replaceIfPresentFlag,
                dataCoding, smDefaultMsgId, shortMessage, optionalParameters);
    }

    public String getCommandName() {
        return "submit_multi";
    }

}
