/*
 * Copyright (c) 2018 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of SeptoriaWetHourModel.
 * SeptoriaWetHourModel is free software: you can redistribute it and/or modify
 * it under the terms of the NIBIO Open Source License as published by 
 * NIBIO, either version 1 of the License, or (at your option) any
 * later version.
 * 
 * SeptoriaWetHourModel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NIBIO Open Source License for more details.
 * 
 * You should have received a copy of the NIBIO Open Source License
 * along with SeptoriaWetHourModel.  If not, see <http://www.nibio.no/licenses/>.
 * 
 */
package no.nibio.vips.model.septoriawethourmodel;


import java.util.List;
import no.nibio.vips.entity.ModelConfiguration;
import no.nibio.vips.entity.Result;
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
public class SeptoriaWetHourModelTest {
    
    private WeatherDataFileReader weatherDataFileReader;
    
    public SeptoriaWetHourModelTest() {
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
     * Test of getResult method, of class SeptoriaWetHourModel.
     */
    @org.junit.Test
    public void testGetResult() throws Exception {
        System.out.println("getResult");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        ModelConfiguration config = this.weatherDataFileReader.getModelConfigurationWithWeatherData("/2017_aalborg.json", instance.getModelId().toString());
        config.setConfigParameter("timeZone", "Europe/Oslo");
        config.setConfigParameter("thresholdRelativeHumidity", 85.0);
        config.setConfigParameter("thresholdLeafWetness", 30.0);
        config.setConfigParameter("thresholdPrecipitation", 0.2);
        config.setConfigParameter("thresholdHumidPeriodHours", 19);
        config.setConfigParameter("slidingHoursPast", 36);
        config.setConfigParameter("slidingHoursAhead", 36);
        config.setConfigParameter("dateGs31", "2017-05-01");
        
        
        instance.setConfiguration(config);
        List<Result> expResult = null;
        List<Result> result = instance.getResult();
        assertNotNull(result);
        result.stream().forEach(r->System.out.println(r));
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModelId method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetModelId() {
        System.out.println("getModelId");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        ModelId expResult = null;
        ModelId result = instance.getModelId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModelName method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetModelName_0args() {
        System.out.println("getModelName");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getModelName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModelName method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetModelName_String() {
        System.out.println("getModelName");
        String language = "";
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getModelName(language);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLicense method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetLicense() {
        System.out.println("getLicense");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getLicense();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCopyright method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetCopyright() {
        System.out.println("getCopyright");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getCopyright();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModelDescription method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetModelDescription_0args() {
        System.out.println("getModelDescription");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getModelDescription();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModelDescription method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetModelDescription_String() {
        System.out.println("getModelDescription");
        String language = "";
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getModelDescription(language);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWarningStatusInterpretation method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetWarningStatusInterpretation_0args() {
        System.out.println("getWarningStatusInterpretation");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getWarningStatusInterpretation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWarningStatusInterpretation method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetWarningStatusInterpretation_String() {
        System.out.println("getWarningStatusInterpretation");
        String language = "";
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getWarningStatusInterpretation(language);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModelUsage method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetModelUsage_0args() {
        System.out.println("getModelUsage");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getModelUsage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModelUsage method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetModelUsage_String() {
        System.out.println("getModelUsage");
        String language = "";
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getModelUsage(language);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSampleConfig method, of class SeptoriaWetHourModel.
     */
    //@org.junit.Test
    public void testGetSampleConfig() {
        System.out.println("getSampleConfig");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        String expResult = "";
        String result = instance.getSampleConfig();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setConfiguration method, of class SeptoriaWetHourModel.
     */
    @org.junit.Test
    public void testSetConfiguration() throws Exception {
        System.out.println("setConfiguration");
        SeptoriaWetHourModel instance = new SeptoriaWetHourModel();
        ModelConfiguration config = this.weatherDataFileReader.getModelConfigurationWithWeatherData("/2017_aas.json", instance.getModelId().toString());
        config.setConfigParameter("timeZone", "Europe/Oslo");
        config.setConfigParameter("thresholdRelativeHumidity", 85.0);
        config.setConfigParameter("thresholdLeafWetness", 30.0);
        config.setConfigParameter("thresholdPrecipitation", 0.2);
        config.setConfigParameter("thresholdHumidPeriodHours", 19);
        config.setConfigParameter("slidingHoursPast", 36);
        config.setConfigParameter("slidingHoursAhead", 36);
        config.setConfigParameter("dateGs31", "2017-05-01");
        
        
        instance.setConfiguration(config);
        
    }
    
}
