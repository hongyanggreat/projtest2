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

import com.gk.htc.ahp.brand.jsmpp.bean.Bind;
import com.gk.htc.ahp.brand.jsmpp.bean.BindType;
import com.gk.htc.ahp.brand.jsmpp.bean.CancelSm;
import com.gk.htc.ahp.brand.jsmpp.bean.MessageState;
import com.gk.htc.ahp.brand.jsmpp.bean.QuerySm;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceSm;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMulti;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMultiResult;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitSm;
import com.gk.htc.ahp.brand.jsmpp.extra.ProcessRequestException;
import com.gk.htc.ahp.brand.jsmpp.util.MessageId;

/**
 * @author uudashr
 *
 */
public interface ServerResponseHandler extends BaseResponseHandler {

    void sendBindResp(String systemId, BindType bindType, int sequenceNumber)
            throws IOException;

    void sendSubmitSmResponse(MessageId messageId, int sequenceNumber)
            throws IOException;

    void processBind(Bind bind);

    MessageId processSubmitSm(SubmitSm submitSm) throws ProcessRequestException;

    SubmitMultiResult processSubmitMulti(SubmitMulti submitMulti) throws ProcessRequestException;

    void sendSubmitMultiResponse(SubmitMultiResult submiitMultiResult,
            int sequenceNumber) throws IOException;

    QuerySmResult processQuerySm(QuerySm querySm)
            throws ProcessRequestException;

    void sendQuerySmResp(String messageId, String finalDate,
            MessageState messageState, byte errorCode, int sequenceNumber)
            throws IOException;

    void processCancelSm(CancelSm cancelSm) throws ProcessRequestException;

    void sendCancelSmResp(int sequenceNumber) throws IOException;

    void processReplaceSm(ReplaceSm replaceSm) throws ProcessRequestException;

    void sendReplaceSmResp(int sequenceNumber) throws IOException;
}
