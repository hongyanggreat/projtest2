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
package com.gk.htc.ahp.brand.jsmpp.bean;

import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter.Byte;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter.COctetString;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter.Int;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter.Null;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter.OctetString;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter.Short;
import com.gk.htc.ahp.brand.jsmpp.bean.OptionalParameter.Tag;

/**
 * @author uudashr
 *
 */
public class OptionalParameters {

    /**
     * Create SAR_MESSAGE_REF_NUM TLV instance.
     *
     * @param value is the value.
     * @return the optional parameter.
     */
    public static OptionalParameter.Short newSarMsgRefNum(short value) {
        return new OptionalParameter.Short(Tag.SAR_MSG_REF_NUM, value);
    }

    /**
     * Create SAR_MESSAGE_REF_NUM TLV instance. The value will cast
     * automatically into short type.
     *
     * @param value is the value.
     * @return the optional parameter.
     */
    public static OptionalParameter.Short newSarMsgRefNum(int value) {
        return newSarMsgRefNum((byte) value);
    }

    /**
     * Create SAR_SEGMENT_SEQNUM TLV instance.
     *
     * @param value is the value.
     * @return the optional parameter.
     */
    public static OptionalParameter.Byte newSarSegmentSeqnum(byte value) {
        return new OptionalParameter.Byte(Tag.SAR_SEGMENT_SEQNUM, value);
    }

    /**
     * Create SAR_SEGMENT_SEQNUM TLV instance. The value will cast automatically
     * into byte type.
     *
     * @param value is the value.
     * @return the optional parameter.
     */
    public static OptionalParameter.Byte newSarSegmentSeqnum(int value) {
        return newSarSegmentSeqnum((byte) value);
    }

    /**
     * Create SAR_TOTAL_SEGMENTS TLV instance.
     *
     * @param value is the value.
     * @return the optional parameter.
     */
    public static OptionalParameter.Byte newSarTotalSegments(byte value) {
        return new OptionalParameter.Byte(Tag.SAR_TOTAL_SEGMENTS, value);
    }

    /**
     * Create SAR_TOTAL_SEGMENTS TLV instance. The value will cast automatically
     * into byte type.
     *
     * @param value is the value.
     * @return the optional parameter.
     */
    public static OptionalParameter.Byte newSarTotalSegments(int value) {
        return newSarTotalSegments((byte) value);
    }

    public static OptionalParameter deserialize(final short tagCode, byte[] content) {
        Tag tag = null;
        try {
            tag = Tag.valueOf(tagCode);
        } catch (IllegalArgumentException e) {
            return new COctetString(tagCode, content);
        }

        if (Null.class.equals(tag.type)) {
            return new Null(tagCode);
        }
        if (Byte.class.equals(tag.type)) {
            return new Byte(tagCode, content);
        }
        if (Short.class.equals(tag.type)) {
            return new Short(tagCode, content);
        }
        if (Int.class.equals(tag.type)) {
            return new Int(tagCode, content);
        }
        if (OctetString.class.equals(tag.type)) {
            return new OctetString(tagCode, content);
        }
        if (COctetString.class.equals(tag.type)) {
            return new COctetString(tagCode, content);
        }
        throw new IllegalArgumentException("Unsupported tag: " + tagCode);
    }
}
