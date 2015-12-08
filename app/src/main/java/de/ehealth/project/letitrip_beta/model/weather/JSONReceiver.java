package de.ehealth.project.letitrip_beta.model.weather;

import org.json.JSONObject;

/**
 * Created by Lars on 11.10.2015.
 */
public interface JSONReceiver {
    void receive(JSONObject data);
}
