@file:Suppress("SpellCheckingInspection")

package template.model

/**
 * // loc: assets/keterangan.xml
 *```
 *<?xml version="1.0" encoding="UTF-8"?>
 *<DCSMS-Hishoot xmlns:android="http://androidminang.com/armdevteam">
 *  <device>Galaxy Gio</device>
 *  <author>http://androidminang.com</author>
 *  <topx>42</topx>
 *  <topy>140</topy>
 *  <botx>39</botx>
 *  <boty>190</boty>
 *  <!-- DEVICE DPI
 *    ldpi  =-1
 *    mdpi  =1
 *    hdpi  =2
 *    xhdpi =3
 *    xxhdpi=4 -->
 *  <deviceDpi>1</deviceDpi>
 *</DCSMS-Hishoot>
 *```
 **/
data class ModelV1 @JvmOverloads constructor(
    var device: String = "",
    var author: String = "",
    var topx: Int = -1,
    var topy: Int = -1,
    var botx: Int = -1,
    var boty: Int = -1
) {
    fun isNotValid(): Boolean = device == "" || author == "" || topx == -1 || topy == -1 ||
        botx == -1 || boty == -1
}
