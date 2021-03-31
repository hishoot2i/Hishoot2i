@file:Suppress("SpellCheckingInspection")

package template.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

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
@Serializable
@XmlSerialName("DCSMS-Hishoot", "", "")
data class ModelV1(
    @XmlElement(true) val device: String,
    @XmlElement(true) val author: String,
    @XmlElement(true) val topx: Int,
    @XmlElement(true) val topy: Int,
    @XmlElement(true) val botx: Int,
    @XmlElement(true) val boty: Int
)
