package br.edu.uepb.nutes.haniot.server.historical;

import org.json.JSONObject;

import java.util.List;

/**
 * Interface CallbackHistorical.
 *
 * @param <T>
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public interface CallbackHistorical<T> {
    void onBeforeSend();

    void onError(JSONObject result);

    void onResult(List<T> result);

    void onAfterSend();
}
