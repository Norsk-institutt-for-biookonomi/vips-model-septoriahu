/*
 * Copyright (c) 2018 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of SeptoriaHumidityModel.
 * SeptoriaHumidityModel is free software: you can redistribute it and/or modify
 * it under the terms of the NIBIO Open Source License as published by 
 * NIBIO, either version 1 of the License, or (at your option) any
 * later version.
 * 
 * SeptoriaHumidityModel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NIBIO Open Source License for more details.
 * 
 * You should have received a copy of the NIBIO Open Source License
 * along with SeptoriaHumidityModel.  If not, see <http://www.nibio.no/licenses/>.
 * 
 */
package no.nibio.vips.model.septoriahumiditymodel;


import java.util.List;
import no.nibio.vips.entity.ModelConfiguration;
import no.nibio.vips.entity.Result;
import no.nibio.vips.model.ConfigValidationException;
import no.nibio.vips.model.ModelId;
import no.nibio.vips.util.test.WeatherDataFileReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author treinar
 */
public class SeptoriaHumidityModelTest {
    
    private WeatherDataFileReader weatherDataFileReader;
    
    public SeptoriaHumidityModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.weatherDataFileReader = new WeatherDataFileReader();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getResult method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetResult() throws Exception {
        System.out.println("getResult");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        ModelConfiguration config = this.weatherDataFileReader.getModelConfigurationWithWeatherData("/2017_aalborg.json", instance.getModelId().toString());
        config.setConfigParameter("timeZone", "Europe/Oslo");
        config.setConfigParameter("thresholdRelativeHumidity", 85.0);
        config.setConfigParameter("thresholdLeafWetness", 30.0);
        config.setConfigParameter("thresholdPrecipitation", 0.2);
        config.setConfigParameter("thresholdHumidPeriodHours", 19);
        config.setConfigParameter("slidingHoursPast", 36);
        config.setConfigParameter("slidingHoursAhead", 36);
        config.setConfigParameter("sprayingProtectionDays", 10);
        config.setConfigParameter("dateGs31", "2017-05-01");
        config.setConfigParameter("dateSpraying1", "2017-05-11");
        config.setConfigParameter("dateSpraying2", "2017-05-19");
        
        
        instance.setConfiguration(config);
        
        List<Result> result = instance.getResult();
        assertNotNull(result);
        //result.stream().forEach(r->System.out.println(r));

    }

    /**
     * Test of getModelId method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetModelId() {
        System.out.println("getModelId");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        ModelId expResult = new ModelId("SEPTORIAHU");
        ModelId result = instance.getModelId();
        assertEquals(expResult.toString(), result.toString());
        
    }

    /**
     * Test of getModelName method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetModelName_0args() {
        System.out.println("getModelName");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getModelName();
        assertNotNull(result);
        assertTrue(!result.isBlank());
    }

    /**
     * Test of getModelName method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetModelName_String() {
        System.out.println("getModelName");
        String language = "";
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getModelName(language);
        assertNotNull(result);
    }

    /**
     * Test of getLicense method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetLicense() {
        System.out.println("getLicense");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getLicense();
        assertNotNull(result);
        
    }

    /**
     * Test of getCopyright method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetCopyright() {
        System.out.println("getCopyright");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getCopyright();
        assertNotNull(result);
    }

    /**
     * Test of getModelDescription method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetModelDescription_0args() {
        System.out.println("getModelDescription");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getModelDescription();
        assertNotNull(result);
    }

    /**
     * Test of getModelDescription method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetModelDescription_String() {
        System.out.println("getModelDescription");
        String language = "";
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getModelDescription(language);
        assertNotNull(result);
    }

    /**
     * Test of getWarningStatusInterpretation method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetWarningStatusInterpretation_0args() {
        System.out.println("getWarningStatusInterpretation");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getWarningStatusInterpretation();
        assertNotNull(result);
    }

    /**
     * Test of getWarningStatusInterpretation method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetWarningStatusInterpretation_String() {
        System.out.println("getWarningStatusInterpretation");
        String language = "";
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getWarningStatusInterpretation(language);
        assertNotNull(result);
    }

    /**
     * Test of getModelUsage method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetModelUsage_0args() {
        System.out.println("getModelUsage");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getModelUsage();
        assertNotNull(result);
    }

    /**
     * Test of getModelUsage method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetModelUsage_String() {
        System.out.println("getModelUsage");
        String language = "";
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getModelUsage(language);
        assertNotNull(result);
    }

    /**
     * Test of getSampleConfig method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testGetSampleConfig() {
        System.out.println("getSampleConfig");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        String result = instance.getSampleConfig();
        assertNotNull(result);
        
    }

    /**
     * Test of setConfiguration method, of class SeptoriaHumidityModel.
     */
    @org.junit.Test
    public void testSetConfiguration() throws Exception {
        System.out.println("setConfiguration");
        SeptoriaHumidityModel instance = new SeptoriaHumidityModel();
        ModelConfiguration config = this.weatherDataFileReader.getModelConfigurationWithWeatherData("/2017_aas.json", instance.getModelId().toString());
        config.setConfigParameter("timeZone", "Europe/Oslo");
        config.setConfigParameter("thresholdRelativeHumidity", 85.0);
        config.setConfigParameter("thresholdLeafWetness", 30.0);
        config.setConfigParameter("thresholdPrecipitation", 0.2);
        config.setConfigParameter("thresholdHumidPeriodHours", 19);
        config.setConfigParameter("slidingHoursPast", 36);
        config.setConfigParameter("slidingHoursAhead", 36);
        config.setConfigParameter("sprayingProtectionDays", 10);
        config.setConfigParameter("dateGs31", "2017-05-01");
        config.setConfigParameter("dateSpraying1", "2017-05-01");
        config.setConfigParameter("dateSpraying2", "2017-05-10");
        
        try
        {
            instance.setConfiguration(config);
        }catch(ConfigValidationException ex){
            fail(ex.getMessage());
        }
        
    }
    
}
