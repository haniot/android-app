package br.edu.uepb.nutes.haniot.parse;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Parse for device HDP.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class IEEE11073DeviceParser {

    /**
     * Parse for IEEE 11073 device.
     * Supported Models: OMRON BP-792IT, HBF-206IT.
     *
     * FORMAT return Json:
     * { code: int_value,
     *   partition: int_value,
     *   manufacturer: string
     *   modelNumber: string,
     *   systemId: string,
     *   version: int,
     *   type: int,
     *   devConfigurationId: int,
     *   componentId: int,
     *   prodSpec: string,
     *   prodSpecType: int }
     *
     * @param data xml
     * @return JSONObject json
     * @throws JSONException
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static JSONObject parse(String data) throws JSONException, XmlPullParserException, IOException {
        XmlPullParser xmlParser =  XmlPullParserFactory.newInstance().newPullParser();
        xmlParser.setInput(new StringReader(data));

        JSONObject result = new JSONObject();

        boolean end = false;
        String code = null,
                partition = null,
                manufacturer = null,
                modelNumber = null,
                systemId = null,
                version = null,
                type = null,
                devConfigurationId = null,
                componentId = null,
                prodSpec = null,
                prodSpecType = null;

        int eventType = xmlParser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT && !end) {
            String name = null;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = xmlParser.getName();
                    if (name.equalsIgnoreCase("value")) {
                        if (code == null)
                            code = xmlParser.nextText();
                        else if (partition == null)
                            partition = xmlParser.nextText();
                        else if (manufacturer == null)
                            manufacturer = xmlParser.nextText();
                        else if (modelNumber == null)
                            modelNumber = xmlParser.nextText();
                        else if (systemId == null)
                            systemId = xmlParser.nextText();
                        else if (version == null)
                            version = xmlParser.nextText();
                        else if (type == null)
                            type = xmlParser.nextText();
                        else if (devConfigurationId == null)
                            devConfigurationId = xmlParser.nextText();
                        else if (componentId == null)
                            componentId = xmlParser.nextText();
                        else if (prodSpec == null)
                            prodSpec = xmlParser.nextText();
                        else if (prodSpecType == null)
                            prodSpecType = xmlParser.nextText();
                        else
                            end = true;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = xmlParser.next();
        }

        /**
         * Populating the JSON
         */
        result.put("code", Integer.valueOf(code));
        result.put("partition", Integer.valueOf(partition));
        result.put("manufacturer", manufacturer);
        result.put("modelNumber", modelNumber);
        result.put("systemId", systemId);
        result.put("version", Integer.valueOf(version));
        result.put("type", Integer.valueOf(type));
        result.put("devConfigurationId", Integer.valueOf(devConfigurationId));
        result.put("componentId", Integer.valueOf(componentId));
        result.put("prodSpec", prodSpec);
        result.put("prodSpecType", Integer.valueOf(prodSpecType));

        return result;
    }
}
