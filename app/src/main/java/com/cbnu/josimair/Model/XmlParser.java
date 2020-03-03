package com.cbnu.josimair.Model;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    private XmlPullParser parser;
    private String city;
    private String gu;

    public XmlParser(String city, String gu, String xml) {
        this.city = city;
        this.gu = gu;
        try {
            StringReader in = new StringReader(xml);
            parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public OutdoorAir parse() throws XmlPullParserException, IOException {
        List<OutdoorAir> airs = readBody();
        for(OutdoorAir air : airs){
            if(air.getGu().equals(this.gu))
                return air;
        }
        return null;
    }

    private List<OutdoorAir> readBody() throws XmlPullParserException, IOException {
        List<OutdoorAir> items = new ArrayList();
        parser.require(XmlPullParser.START_TAG, null, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String name = parser.getName();

            if (name.equals("body")) {
                parser.require(XmlPullParser.START_TAG, null, "body");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                    name = parser.getName();
                    if (name.equals("items")) {
                        items = readItems(parser);
                        return items;
                    }
                    else{
                        skip(parser);
                    }
                }
            }
            else {
                skip(parser);
            }
        }
        return items;
    }

    private List<OutdoorAir> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<OutdoorAir> li = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "items");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
               OutdoorAir item = readItem();
                li.add(item);
            }
            else {
                skip(parser);
            }
        }
        return li;
    }

    private OutdoorAir readItem() throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String city = null;
        String gu = null;
        float value = 0;
        int quality = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("stationName")) {
                gu = readUsingName("stationName");
            }
            else if (name.equals("coValue")) {
                try {
                    value = Float.parseFloat(readUsingName("coValue"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (name.equals("coGrade")) {
                try {
                    value = Integer.parseInt(readUsingName("coGrade"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                skip(parser);
            }
        }

        return new OutdoorAir(city,gu, value, quality);
    }

    private String readUsingName(String name) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null,name);
        String stationName = readText();
        parser.require(XmlPullParser.END_TAG, null, name);
        return stationName;
    }

    private String readText() throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
