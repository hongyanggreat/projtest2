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

import com.gk.htc.ahp.brand.jsmpp.PDUSender;
import com.gk.htc.ahp.brand.jsmpp.PDUStringException;
import com.gk.htc.ahp.brand.jsmpp.bean.BindType;
import com.gk.htc.ahp.brand.jsmpp.bean.InterfaceVersion;
import com.gk.htc.ahp.brand.jsmpp.bean.NumberingPlanIndicator;
import com.gk.htc.ahp.brand.jsmpp.bean.TypeOfNumber;

/**
 * @author uudashr
 *
 */
public class BindCommandTask extends AbstractSendCommandTask {

    private final BindType bindType;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final InterfaceVersion interfaceVersion;
    private final TypeOfNumber addrTon;
    private final NumberingPlanIndicator addrNpi;
    private final String addressRange;

    public BindCommandTask(PDUSender pduSender,
            BindType bindType, String systemId, String password,
            String systemType, InterfaceVersion interfaceVersion,
            TypeOfNumber addrTon, NumberingPlanIndicator addrNpi,
            String addressRange) {
        super(pduSender);
        this.bindType = bindType;
        this.systemId = systemId;
        this.password = password;
        this.systemType = systemType;
        this.interfaceVersion = interfaceVersion;
        this.addrTon = addrTon;
        this.addrNpi = addrNpi;
        this.addressRange = addressRange;
    }

    public void executeTask(OutputStream out, int sequenceNumber)
            throws PDUStringException, IOException {
        pduSender.sendBind(out, bindType, sequenceNumber, systemId, password,
                systemType, interfaceVersion, addrTon, addrNpi, addressRange);
    }

    public String getCommandName() {
        return "bind";
    }
}
