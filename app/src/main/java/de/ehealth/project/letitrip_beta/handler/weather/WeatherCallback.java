package de.ehealth.project.letitrip_beta.handler.weather;

import de.ehealth.project.letitrip_beta.model.weather.Channel;

public interface WeatherCallback {
    void success(Channel channel);
    void failure(Exception exc);
}
