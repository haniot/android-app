package br.edu.uepb.nutes.haniot.model;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.signove.health.service.HealthAgentAPI;
import com.signove.health.service.HealthServiceAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by lucas on 28/09/17.
 */

public abstract class Deviceshdp {

    int [] specs = {0x1004,0x1007,0x1029,0x100f};
    Handler tm;
    HealthServiceAPI api;

    private ArrayList<String> auxTimeStamp = new ArrayList<String>();
    private ArrayList<String> auxMeasurement = new ArrayList<String>();
//    private ArrayList<String> infoDevice = new ArrayList<String>();

    private JSONObject measurement;
    private JSONObject infoDevice;

    public JSONObject getInfoDevice() {
        return infoDevice;
    }

    public void setInfoDevice(JSONObject infoDevice) {
        this.infoDevice = infoDevice;
    }

    public JSONObject getMeasurement() {
        return measurement;
    }

    public void setMeasurement(JSONObject measurement) {
        this.measurement = measurement;
    }

    public Deviceshdp(){
        tm = new Handler();
    }

    private void findData(Object json) throws JSONException {
        if (isJSON(json.toString())){
            JSONObject jsonObj = (JSONObject) json;
            Iterator itr= jsonObj.keys();
            while(itr.hasNext()) {
                String key = itr.next().toString();
                String value = jsonObj.get(key).toString();
                if(value.equals("unit")){
                    if(jsonObj.has("content")){
                        if(auxMeasurement.size()>0)
                            auxMeasurement.add("@");
                        auxMeasurement.add(jsonObj.get("content").toString());
                        if(!measurement.has(jsonObj.get("content").toString()))
                            measurement.put(jsonObj.get("content").toString(), new ArrayList<String>());
                        else{
                            Object aux = measurement.get(jsonObj.get("content").toString());
                            measurement.remove(jsonObj.get("content").toString());
                            measurement.put(jsonObj.get("content").toString(),aux);
                        }
                    }else
                        measurement.put("undefined", new ArrayList<String>());
                }else if (key.equals("value") && jsonObj.get("type").toString().equals("float")) {
                    JSONArray names = measurement.names();

                    ArrayList<String> measurementArray = (ArrayList<String>) measurement.get(names.get(names.length()-1).toString());
                    measurementArray.add(jsonObj.get("value").toString());

                    measurement.put(names.get(names.length()-1).toString(),measurementArray);
                    auxMeasurement.add(jsonObj.get("value").toString());
                }else if(value.equals("intu8")){
                    measurement.put(jsonObj.get("name").toString(), jsonObj.get("value").toString());
                    auxTimeStamp.add(jsonObj.get("name").toString());
                    auxTimeStamp.add(jsonObj.get("value").toString());
                }else if (value.equals("type") || value.equals("manufacturer") || value.equals("model-number"))
                    infoDevice.put(value, jsonObj.get("value").toString());
                else
                    findData(jsonObj.get(key));
            }
        }

        if (isJSONArray(json.toString())){
            JSONArray jsonObj = (JSONArray) json;
            for (int i = 0; i < jsonObj.length();i++)
                findData(jsonObj.get(i));
        }
    }

    protected boolean isJSON(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    protected boolean isJSONArray(String json) {
        try {
            new JSONArray(json);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    private JSONObject xmlToJson(String xml){
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(xml);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }
        return jsonObj;
    }

    private void RequestDeviceAttributes(String dev) {
        try {
            Log.w("HST", "Requested device attributes");
            api.RequestDeviceAttributes(dev);
        } catch (RemoteException e) {
            Log.w("HST", "Exception (RequestDeviceAttributes)");
        }
    }

    public HealthAgentAPI.Stub agent = new HealthAgentAPI.Stub() {
        @Override
        public void Connected(String dev, String addr) {
            Log.w("HST", "Connected " + dev);
            Log.w("HST", "..." + addr);

            connect(addr);

//            measurement = new JSONObject();
//
//            String xmldata = "<data-list><entry><meta-data><meta name=\"personal-id\">65</meta><meta name=\"HANDLE\">1</meta></meta-data><compound><name>Numeric</name><entries><entry><meta-data><meta name=\"partition\">2</meta><meta name=\"metric-id\">18948</meta><meta name=\"unit-code\">3872</meta><meta name=\"unit\">mmHg</meta></meta-data><compound><name>Compound-Basic-Nu-Observed-Value</name><entries><entry><meta-data><meta name=\"partition\">2</meta><meta name=\"metric-id\">18949</meta></meta-data><simple><name>0</name><type>float</type><value>111.000000</value></simple></entry><entry><meta-data><meta name=\"partition\">2</meta><meta name=\"metric-id\">18950</meta></meta-data><simple><name>1</name><type>float</type><value>65.000000</value></simple></entry><entry><meta-data><meta name=\"partition\">2</meta><meta name=\"metric-id\">18951</meta></meta-data><simple><name>2</name><type>float</type><value>80.000000</value></simple></entry></entries></compound></entry><entry><compound><name>Absolute-Time-Stamp</name><entries><entry><simple><name>century</name><type>intu8</type><value>20</value></simple></entry><entry><simple><name>year</name><type>intu8</type><value>16</value></simple></entry><entry><simple><name>month</name><type>intu8</type><value>3</value></simple></entry><entry><simple><name>day</name><type>intu8</type><value>9</value></simple></entry><entry><simple><name>hour</name><type>intu8</type><value>7</value></simple></entry><entry><simple><name>minute</name><type>intu8</type><value>25</value></simple></entry><entry><simple><name>second</name><type>intu8</type><value>30</value></simple></entry><entry><simple><name>sec_fractions</name><type>intu8</type><value>0</value></simple></entry></entries></compound></entry></entries></compound></entry><entry><meta-data><meta name=\"personal-id\">65</meta><meta name=\"HANDLE\">2</meta></meta-data><compound><name>Numeric</name><entries><entry><meta-data><meta name=\"partition\">2</meta><meta name=\"metric-id\">18474</meta><meta name=\"unit-code\">2720</meta><meta name=\"unit\">bpm</meta></meta-data><simple><name>Basic-Nu-Observed-Value</name><type>float</type><value>60.000000</value></simple></entry><entry><compound><name>Absolute-Time-Stamp</name><entries><entry><simple><name>century</name><type>intu8</type><value>20</value></simple></entry><entry><simple><name>year</name><type>intu8</type><value>16</value></simple></entry><entry><simple><name>month</name><type>intu8</type><value>3</value></simple></entry><entry><simple><name>day</name><type>intu8</type><value>9</value></simple></entry><entry><simple><name>hour</name><type>intu8</type><value>7</value></simple></entry><entry><simple><name>minute</name><type>intu8</type><value>25</value></simple></entry><entry><simple><name>second</name><type>intu8</type><value>30</value></simple></entry><entry><simple><name>sec_fractions</name><type>intu8</type><value>0</value></simple></entry></entries></compound></entry></entries></compound></entry><entry><meta-data><meta name=\"personal-id\">65</meta><meta name=\"HANDLE\">3</meta></meta-data><compound><name>Numeric</name><entries><entry><meta-data><meta name=\"partition\">2</meta><meta name=\"metric-id\">61458</meta><meta name=\"unit-code\">512</meta><meta name=\"unit\"></meta></meta-data><simple><name>Basic-Nu-Observed-Value</name><type>float</type><value>0.000000</value></simple></entry><entry><compound><name>Absolute-Time-Stamp</name><entries><entry><simple><name>century</name><type>intu8</type><value>20</value></simple></entry><entry><simple><name>year</name><type>intu8</type><value>16</value></simple></entry><entry><simple><name>month</name><type>intu8</type><value>3</value></simple></entry><entry><simple><name>day</name><type>intu8</type><value>9</value></simple></entry><entry><simple><name>hour</name><type>intu8</type><value>7</value></simple></entry><entry><simple><name>minute</name><type>intu8</type><value>25</value></simple></entry><entry><simple><name>second</name><type>intu8</type><value>30</value></simple></entry><entry><simple><name>sec_fractions</name><type>intu8</type><value>0</value></simple></entry></entries></compound></entry></entries></compound></entry></data-list>";
//
//            try {
//                findData(xmlToJson(xmldata));
//            } catch (JSONException e) {
//                Log.w("JSON","Erro no JSON");
//                e.printStackTrace();
//            }
//
//            receiveData();
//
//            xmldata = "<data-list><entry><compound><name>MDS</name><entries><entry><meta-data><meta name=\"attribute-id\">2438</meta></meta-data><compound><name>System-Type</name><entries><entry><simple><name>code</name><type>intu16</type><value>0</value></simple></entry><entry><simple><name>partition</name><type>intu16</type><value>0</value></simple></entry></entries></compound></entry><entry><meta-data><meta name=\"attribute-id\">2344</meta></meta-data><compound><name>System-Model</name><entries><entry><simple><name>manufacturer</name><type>string</type><value>OMRON HEALTHCARE</value></simple></entry><entry><simple><name>model-number</name><type>string</type><value>HBF-206IT</value></simple></entry></entries></compound></entry><entry><meta-data><meta name=\"attribute-id\">2436</meta></meta-data><simple><name>System-Id</name><type>hex</type><value>002209225807E86B</value></simple></entry><entry><meta-data><meta name=\"attribute-id\">2650</meta></meta-data><compound><name>System-Type-Spec-List</name><entries><entry><compound><name>0</name><entries><entry><simple><name>version</name><type>intu16</type><value>1</value></simple></entry><entry><simple><name>type</name><type>intu16</type><value>4111</value></simple></entry></entries></compound></entry></entries></compound></entry><entry><meta-data><meta name=\"attribute-id\">2628</meta></meta-data><simple><name>Dev-Configuration-Id</name><type>intu16</type><value>16384</value></simple></entry><entry><meta-data><meta name=\"attribute-id\">2349</meta></meta-data><compound><name>Production-Specification</name><entries><entry><compound><name>0</name><entries><entry><simple><name>component-id</name><type>intu16</type><value>1</value></simple></entry><entry><simple><name>prod-spec</name><type>string</type><value>SERIAL_NUMBER: HJ710XXXXXXXXX</value></simple></entry><entry><simple><name>spec-type</name><type>intu16</type><value>1</value></simple></entry></entries></compound></entry><entry><compound><name>1</name><entries><entry><simple><name>component-id</name><type>intu16</type><value>1</value></simple></entry><entry><simple><name>prod-spec</name><type>string</type><value>FW_REVISION: 100</value></simple></entry><entry><simple><name>spec-type</name><type>intu16</type><value>5</value></simple></entry></entries></compound></entry></entries></compound></entry></entries></compound></entry></data-list>";
//
//            DeviceAttributes("XXXX",xmldata);

            auxTimeStamp.clear();
            auxMeasurement.clear();
//            infoDevice.clear();
        }

        @Override
        public void Associated(String dev, String xmldata) {
            final String idev = dev;
            Log.w("HST", "Associated " + dev);
            Log.w("HST", "...." + xmldata);

            Runnable req1 = new Runnable() {
                public void run() {
                    RequestDeviceAttributes(idev);
                }
            };
            tm.postDelayed(req1, 50);
        }
        @Override
        public void MeasurementData(String dev, String xmldata) {

            Log.w("ASDLUCAS", "" + xmldata);
            Log.w("HST", "MeasurementData " + dev);
            Log.w("HST", "....." + xmldata);
            measurement = new JSONObject();

            try {
                findData(xmlToJson(xmldata));
            } catch (JSONException e) {
                Log.w("JSON","Erro no JSON");
                e.printStackTrace();
            }

            receiveData();
        }
        @Override
        public void DeviceAttributes(String dev, String xmldata) {
            Log.w("HST", "DeviceAttributes " + dev);
            Log.w("HST", ".." + xmldata);

            infoDevice = new JSONObject();

            try {
                findData(xmlToJson(xmldata));
            } catch (JSONException e) {
                Log.w("JSON","Erro no JSON");
                e.printStackTrace();
            }

//            disconnect();
        }

        @Override
        public void Disassociated(String dev) {
            Log.w("HST", "Disassociated " + dev);
        }

        @Override
        public void Disconnected(String dev) {
            Log.w("HST", "Disconnected " + dev);
            disconnect();
        }
    };

    public ArrayList<String> getAuxTimeStamp() {
        return auxTimeStamp;
    }

    public ArrayList<String> getAuxMeasurement() {
        return auxMeasurement;
    }

//    public ArrayList<String> getInfoDevice() {
//        return infoDevice;
//    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w("HST", "Service connection established");

            // that's how we get the client side of the IPC connection
            api = HealthServiceAPI.Stub.asInterface(service);
            try {
                Log.w("HST", "Configuring...");
                api.ConfigurePassive(agent, specs);
            } catch (RemoteException e) {
                Log.e("HST", "Failed to add listener", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("HST", "Service connection closed");
        }
    };

    public void finalize(){
        try {
            Log.w("HST", "Unconfiguring...");
            api.Unconfigure(agent);
        } catch (Throwable t) {
            Log.w("HST", "Erro tentando desconectar");
        }
        Log.w("HST", "Activity destroyed");

    }

    public abstract void connect(String addr);

    public abstract void disconnect();

    public abstract void receiveData();
}
