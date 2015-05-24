package org.illegaller.ratabb.hishoot2i.skin;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SkinDescription {

	private String device = null;
	private String author = null;
	private int tx, ty, bx, by, densType;
	private String value = null;

	public SkinDescription(InputStream inputStream) {

		XmlPullParserFactory factory;
		XmlPullParser xpp;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();

			xpp.setInput(inputStream, null);

			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = xpp.getName();

				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					// XXX null
					break;
				case XmlPullParser.TEXT:
					value = xpp.getText();
					break;
				case XmlPullParser.END_TAG:
					if (tagname.equalsIgnoreCase("device")) {
						this.device = value;
					} else if (tagname.equalsIgnoreCase("author")) {
						this.author = value;
					} else if (tagname.equalsIgnoreCase("topx")) {
						this.tx = Integer.parseInt(value);
					} else if (tagname.equalsIgnoreCase("topy")) {
						this.ty = Integer.parseInt(value);
					} else if (tagname.equalsIgnoreCase("botx")) {
						this.bx = Integer.parseInt(value);
					} else if (tagname.equalsIgnoreCase("boty")) {
						this.by = Integer.parseInt(value);
					} else if (tagname.equalsIgnoreCase("deviceDpi")) {
						this.densType = Integer.parseInt(value);
					}
					break;
				default:
					break;
				}
				eventType = xpp.nextToken();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getDevice() {
		return this.device;
	}

	public String getAuthor() {
		return this.author;
	}

	public int getDensType() {
		return this.densType;
	}

	public int getTx() {
		return this.tx;
	}

	public int getTy() {
		return this.ty;
	}

	public int getBx() {
		return this.bx;
	}

	public int getBy() {
		return this.by;
	}
}
