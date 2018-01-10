package br.edu.uepb.nutes.haniot.parse;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import br.edu.uepb.nutes.haniot.utils.DateUtils;

/**
 * Parse for blood pressure.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class IEEE11073BPParser {

    /**
     * Parse for IEEE 11073 device blood pressure.
     * Supported Models: OMRON BP792IT.
     *
     * FORMAT return Json:
     * { systolic: int_value,
     *   diastolic: int_value,
     *   map: double_value
     *   pulse: int_value,
     *   pulse: int_value,
     *   systolicUnit: string_value,
     *   diastolicUnit: string_value,
     *   pulseUnit: string_value,
     *   timestamp: long_value
     *
     * @param data xml
     * @return JSONObject json
     * @throws JSONException
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static JSONObject parse(String data) throws JSONException, XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlParser = factory.newPullParser();
        Calendar timestamp = GregorianCalendar.getInstance();
        JSONObject result = new JSONObject();

        boolean end = false;
        String systolic = null,
                diastolic = null,
                map = null, // Mean Arterial Pressure (MAP)
                pulse = null,
                pressureUnit = null,
                pulseUnit = null,
                century = null,
                year = null,
                month = null,
                day = null,
                hour = null,
                min = null,
                sec = null,
                sec_fractions = null;

        xmlParser.setInput(new StringReader(data));
        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT && !end) {
            String name = null;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = xmlParser.getName();
                    if (name.equalsIgnoreCase("meta")) {
                        String attributeValue = xmlParser.getAttributeValue(null, "name");
                        if (attributeValue.equalsIgnoreCase("unit")) {
                            if (pressureUnit == null)
                                pressureUnit = xmlParser.nextText();
                            else if (pulseUnit == null)
                                pulseUnit = xmlParser.nextText();
                        }
                    } else if (name.equalsIgnoreCase("value")) {
                        if (systolic == null)
                            systolic = xmlParser.nextText();
                        else if (diastolic == null)
                            diastolic = xmlParser.nextText();
                        else if (map == null)
                            map = xmlParser.nextText();
                        else if (century == null)
                            century = xmlParser.nextText();
                        else if (year == null)
                            year = "20".concat(xmlParser.nextText());
                        else if (month == null)
                            month = xmlParser.nextText();
                        else if (day == null)
                            day = xmlParser.nextText();
                        else if (hour == null)
                            hour = xmlParser.nextText();
                        else if (min == null)
                            min = xmlParser.nextText();
                        else if (sec == null)
                            sec = xmlParser.nextText();
                        else if (sec_fractions == null)
                            sec_fractions = xmlParser.nextText();
                        else if (pulse == null)
                            pulse = xmlParser.nextText();
                        else
                            end = true;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = xmlParser.next();
        }

        /**
         * Device timestamp
         */
        timestamp.set(
                Integer.valueOf(year),
                Integer.valueOf(month) - 1,
                Integer.valueOf(day),
                Integer.valueOf(hour),
                Integer.valueOf(min),
                Integer.valueOf(sec)
        );

        /**
         * Populating the JSON
         */
        result.put("systolic", (int) Double.parseDouble(systolic));
        result.put("diastolic", (int) Double.parseDouble(diastolic));
        result.put("map", (int) Double.parseDouble(map));
        result.put("pulse", (int) Double.parseDouble(pulse));
        result.put("systolicUnit", pressureUnit);
        result.put("diastolicUnit", pressureUnit);
        result.put("pulseUnit", pulseUnit);
        result.put("timestamp", timestamp.getTimeInMillis());

        return result;
    }
}
