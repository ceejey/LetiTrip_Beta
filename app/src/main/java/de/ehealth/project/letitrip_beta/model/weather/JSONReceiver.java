package de.ehealth.project.letitrip_beta.model.weather;

import org.json.JSONObject;

public interface JSONReceiver {
    void receive(JSONObject data);
}
