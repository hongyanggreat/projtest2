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
package com.gk.htc.ahp.brand.jsmpp.util;

import com.gk.htc.ahp.brand.jsmpp.InvalidNumberOfDestinationsException;
import com.gk.htc.ahp.brand.jsmpp.PDUStringException;
import com.gk.htc.ahp.brand.jsmpp.bean.AlertNotification;
import com.gk.htc.ahp.brand.jsmpp.bean.Bind;
import com.gk.htc.ahp.brand.jsmpp.bean.BindResp;
import com.gk.htc.ahp.brand.jsmpp.bean.CancelSm;
import com.gk.htc.ahp.brand.jsmpp.bean.CancelSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.Command;
import com.gk.htc.ahp.brand.jsmpp.bean.DataSm;
import com.gk.htc.ahp.brand.jsmpp.bean.DataSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliverSm;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliverSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.DeliveryReceipt;
import com.gk.htc.ahp.brand.jsmpp.bean.EnquireLink;
import com.gk.htc.ahp.brand.jsmpp.bean.EnquireLinkResp;
import com.gk.htc.ahp.brand.jsmpp.bean.GenericNack;
import com.gk.htc.ahp.brand.jsmpp.bean.Outbind;
import com.gk.htc.ahp.brand.jsmpp.bean.QuerySm;
import com.gk.htc.ahp.brand.jsmpp.bean.QuerySmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceSm;
import com.gk.htc.ahp.brand.jsmpp.bean.ReplaceSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMulti;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitMultiResp;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitSm;
import com.gk.htc.ahp.brand.jsmpp.bean.SubmitSmResp;
import com.gk.htc.ahp.brand.jsmpp.bean.Unbind;
import com.gk.htc.ahp.brand.jsmpp.bean.UnbindResp;

/**
 * This class is provide interface to decompose SMPP PDU bytes form into SMPP
 * command object.
 *
 * @author uudashr
 * @version 1.0
 * @since 1.0
 *
 */
public interface PDUDecomposer {

    /**
     * Decompose the header only SMPP PDU command.
     *
     * @param b is the PDU.
     * @return the header ( {@link Command} ) object.
     */
    Command header(byte[] b);

    // BIND OPERATION
    /**
     * Decompose the SMPP PDU bind command.
     *
     * @param b is the PDU.
     * @return the bind command object. PDUStringException if there is an
     * invalid constraint of string parameter found.
     */
    Bind bind(byte[] b) throws PDUStringException;

    /**
     * Decompose the SMPP PDU bind response command.
     *
     * @param b is the PDU.
     * @return the bind response command object.
     * @PDUStringException if there is an invalid constraint of string parameter
     * found.
     */
    BindResp bindResp(byte[] b) throws PDUStringException;

    /**
     * Decompose the SMPP PDU unbind command.
     *
     * @param b is the PDU.
     * @return the unbind command object.
     */
    Unbind unbind(byte[] b);

    /**
     * Decompose the SMPP PDU unbind response command.
     *
     * @param b is the PDU.
     * @return the unbind response command object.
     */
    UnbindResp unbindResp(byte[] b);

    /**
     * Decompose the SMPP PDU outbind command.
     *
     * @param b is the PDU.
     * @return the outbind command object. PDUStringException if there is an
     * invalid constraint of string parameter found.
     */
    Outbind outbind(byte[] b) throws PDUStringException;

    // ENQUIRE_LINK OPERATION
    /**
     * Decompose the SMPP PDU enquire link command.
     *
     * @param b is the PDU.
     * @return the enquire link command object.
     */
    EnquireLink enquireLink(byte[] b);

    /**
     * Decompose the SMPP PDU enquire link response command.
     *
     * @param b is the PDU.
     * @return the enquire link response command object.
     */
    EnquireLinkResp enquireLinkResp(byte[] b);

    // GENERICK_NACK OPERATION
    /**
     * Decompose the SMPP PDU generic nack command.
     *
     * @param b is the PDU.
     * @return the generic nack command object.
     */
    GenericNack genericNack(byte[] b);

    // SUBMIT_SM OPERATION
    /**
     * Decompose the SMPP PDU submit short message command.
     *
     * @param b is the PDU.
     * @return the submit short message command object. PDUStringException if
     * there is an invalid constraint of string parameter found.
     */
    SubmitSm submitSm(byte[] b) throws PDUStringException;

    /**
     * Decompose the SMPP PDU submit short message response command.
     *
     * @param b is the PDU.
     * @return the submit short message response command object.
     * PDUStringException if there is an invalid constraint of string parameter
     * found.
     */
    SubmitSmResp submitSmResp(byte[] b) throws PDUStringException;

    // QUERY_SM OPERATION
    /**
     * Decompose the SMPP PDU query short message command.
     *
     * @param b is the PDU.
     * @return the query short message command object. PDUStringException if
     * there is an invalid constraint of string parameter found.
     */
    QuerySm querySm(byte[] b) throws PDUStringException;

    /**
     * Decompose the SMPP PDU query short message reponse command.
     *
     * @param b is the PDU.
     * @return the query short message response command object.
     * PDUStringException if there is an invalid constraint of string parameter
     * found.
     */
    QuerySmResp querySmResp(byte[] b) throws PDUStringException;

    // DELIVER_SM OPERATION
    /**
     * Decompose the SMPP PDU deliver short message command.
     *
     * @param b is the PDU.
     * @return the deliver short message command object.
     * @throws PDUStringException if there is an invalid constraint of string
     * parameter found.
     */
    DeliverSm deliverSm(byte[] b) throws PDUStringException;

    /**
     * Decompose the SMPP PDU deliver short message response command.
     *
     * @param b is the PDU.
     * @return the deliver short message response command object.
     */
    DeliverSmResp deliverSmResp(byte[] b);

    /**
     * Decompose the SMPP PDU delivery receipt content.
     *
     * @param data is the content.
     * @return the delivery receipt object.
     * @throws InvalidDeliveryReceiptException throw if the data is invalid, so
     * it can be parsed.
     */
    DeliveryReceipt deliveryReceipt(String data)
            throws InvalidDeliveryReceiptException;

    /**
     * Decompose the SMPP PDU delivery receipt content.
     *
     * @param data is the content.
     * @return the delivery receipt object.
     * @throws InvalidDeliveryReceiptException throw if the data is invalid, so
     * it can be parsed.
     */
    DeliveryReceipt deliveryReceipt(byte[] data)
            throws InvalidDeliveryReceiptException;

    // DATA_SM OPERATION
    /**
     * Decompose the SMPP PDU data short message command.
     *
     * @param data is the PDU.
     * @return the data short message command object.
     * @throws PDUStringException if there is an invalid constraint of string
     * parameter found.
     */
    DataSm dataSm(byte[] data) throws PDUStringException;

    /**
     * Decompose the SMPP PDU data short message response command.
     *
     * @param data is the PDU.
     * @return the data short message command object.
     * @PDUStringException if there is an invalid constraint of string parameter
     * found.
     */
    DataSmResp dataSmResp(byte[] data) throws PDUStringException;

    /**
     * Decompose the SMPP PDU cancel short message command.
     *
     * @param data is the PDU.
     * @return the cancel short message command object.
     * @throws PDUStringException if there is an invalid constraint of string
     * parameter found.
     */
    CancelSm cancelSm(byte[] data) throws PDUStringException;

    /**
     * Decompose the SMPP PDU cancel short message response command.
     *
     * @param data is the PDU.
     * @return the cancel short message command object.
     */
    CancelSmResp cancelSmResp(byte[] data);

    SubmitMulti submitMulti(byte[] data) throws PDUStringException,
            InvalidNumberOfDestinationsException;

    SubmitMultiResp submitMultiResp(byte[] data) throws PDUStringException;

    ReplaceSm replaceSm(byte[] data) throws PDUStringException;

    ReplaceSmResp replaceSmResp(byte[] data);

    AlertNotification alertNotification(byte[] data) throws PDUStringException;
}
