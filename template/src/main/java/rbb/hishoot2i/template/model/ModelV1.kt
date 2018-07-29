package rbb.hishoot2i.template.model

/*
// loc: assets/keterangan.xml
<?xml version="1.0" encoding="UTF-8"?>
<DCSMS-Hishoot xmlns:android="http://androidminang.com/armdevteam">
    <device>Galaxy Gio</device>
    <author>http://androidminang.com</author>
	<topx>42</topx>
	<topy>140</topy>
    <botx>39</botx>
     <boty>190</boty>
<!-- DEVICE DPI
		ldpi=-1
		mdpi=1
		hdpi=2
		xhdpi=3
		xxhdpi=4
-->
     <deviceDpi>1</deviceDpi>
</DCSMS-Hishoot>
*/
data class ModelV1 @JvmOverloads constructor(
    var device: String = "",
    var author: String = "",
    var topx: Int = -1,
    var topy: Int = -1,
    var botx: Int = -1,
    var boty: Int = -1
) {
    fun isNotValid(): Boolean = when {
        device == "" -> true
        author == "" -> true
        topx == -1 -> true
        topy == -1 -> true
        botx == -1 -> true
        boty == -1 -> true
        else -> false
    }
}