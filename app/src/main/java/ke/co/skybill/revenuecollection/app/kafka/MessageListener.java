/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.co.skybill.revenuecollection.app.kafka;



import ke.co.skybill.revenuecollection.app.entities.AppCountry;
import ke.co.skybill.revenuecollection.app.kafka.models.AppCountryData;
import ke.co.skybill.revenuecollection.app.kafka.models.CountryEvent;
import ke.co.skybill.revenuecollection.app.repository.AppCountryDao;
import ke.co.skybill.revenuecollection.app.repository.AppDao;
import ke.co.skybill.revenuecollection.app.repository.CountryDao;
import ke.co.skybill.revenuecollection.app.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Optional;


/**
 * @author Vikeln
 */

public class MessageListener {
    Logger log = LoggerFactory.getLogger(MessageListener.class.getName());

    @Autowired
    private CountryDao countryDao;
    @Autowired
    private AppCountryDao appCountryDao;


}