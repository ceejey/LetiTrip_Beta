package de.ehealth.project.letitrip_beta.model.weather;

import de.ehealth.project.letitrip_beta.model.weather.model.Channel;

public interface WeatherCallback {
    void success(Channel channel);
    void failure(Exception exc);
}
