/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.entity;

import com.gk.htc.ahp.brand.common.Tool;
import java.util.UUID;

/**
 *
 * @author tuanp
 */
public class UniqueID {

    public static String getId(String phone) {
        try {
            if (!Tool.checkNull(phone)) {
                return phone + "-" + Long.toString(System.nanoTime(), 16);
            } else {
                return Tool.generateRandomPassword(11) + "-" + Long.toString(System.nanoTime(), 16);
            }
        } catch (Exception e) {
            UUID idOne = UUID.randomUUID();
            return idOne.toString();
        }
    }
}
