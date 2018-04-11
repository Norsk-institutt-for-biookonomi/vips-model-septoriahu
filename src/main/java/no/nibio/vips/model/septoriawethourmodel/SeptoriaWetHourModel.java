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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import no.nibio.vips.entity.ModelConfiguration;
import no.nibio.vips.entity.Result;
import no.nibio.vips.entity.WeatherObservation;
import no.nibio.vips.model.ConfigValidationException;
import no.nibio.vips.model.Model;
import no.nibio.vips.model.ModelExcecutionException;
import no.nibio.vips.model.ModelId;
import no.nibio.vips.util.WeatherElements;
import no.nibio.vips.util.WeatherUtil;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Optional;
import no.nibio.vips.entity.ResultImpl;
import no.nibio.vips.util.CommonNamespaces;
import no.nibio.vips.util.XDate;

/**
 * Model developed by SEGES, Denmark
 * @copyright 2018 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class SeptoriaWetHourModel implements Model {
    
    private final static ModelId MODEL_ID = new ModelId("SEPTORIAWH");
    
    private Date dateGs31, date3rdUpperLeafEmerging, date2ndUpperLeafEmerging, dateUpperLeafEmerging, dateGs75;
    private TimeZone timeZone;
    
    private Double thresholdRelativeHumidity, thresholdLeafWetness, thresholdPrecipitation;
    private Integer thresholdHumidPeriodHours, slidingHoursPast, slidingHoursAhead;
    
    private DataMatrix dataMatrix;
    
    private final WeatherUtil weatherUtil;
    
    public SeptoriaWetHourModel(){
        this.weatherUtil = new WeatherUtil();
    }

    @Override
    public List<Result> getResult() throws ModelExcecutionException {
        // Start date for calculation is dateGs31 (mandatory), last date is whatever is first of today + 2 days or dateGs75
        // But inside the model, we just use the weather data as far as they reach
        XDate currentDate = new XDate(this.dateGs31);
        Date lastDate = this.dataMatrix.getLastDateWithParameterValue("RR");
        Calendar cal = Calendar.getInstance();
        
        // Calculating values
        LinkedList<Integer> humidHours = new LinkedList<>();
        List<Result> retVal = new ArrayList<>();
        while(currentDate.compareTo(lastDate) <= 0){
            Double RR = this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.RR);
            Double UM = this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.UM);
            Double BT = this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.BT);

            if(humidHours.size() > this.slidingHoursPast + this.slidingHoursAhead){
                humidHours.removeFirst();
            }
            Integer humidHour = (
                        (BT != null && BT >= this.thresholdLeafWetness)
                        || (UM != null && UM >= this.thresholdRelativeHumidity)
                        || (RR != null && RR >= this.thresholdPrecipitation)
                ) ?
                1:0;
            
            humidHours.addLast(humidHour);
            
            this.dataMatrix.setParamIntValueForDate(currentDate, DataMatrix.WH, humidHour);
            
            cal.setTime(currentDate);
            cal.add(Calendar.HOUR_OF_DAY, -this.slidingHoursAhead );
            Date middleOfSlidingWindowDate = cal.getTime();
            Integer slidingWindowSum = humidHours.stream().mapToInt(Integer::intValue).sum();
            this.dataMatrix.setParamIntValueForDate(middleOfSlidingWindowDate, DataMatrix.WHS, slidingWindowSum);
            
            Result r = new ResultImpl();
            r.setValidTimeStart(new Date(currentDate.getTime()));
            r.setValue(SeptoriaWetHourModel.MODEL_ID.toString(), DataMatrix.WH, String.valueOf(this.dataMatrix.getParamIntValueForDate(currentDate, DataMatrix.WH)));
            r.setValue(CommonNamespaces.NS_WEATHER, WeatherElements.LEAF_WETNESS, String.valueOf(this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.BT)));
            r.setValue(CommonNamespaces.NS_WEATHER, WeatherElements.RELATIVE_HUMIDITY_MEAN, String.valueOf(this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.UM)));
            r.setValue(CommonNamespaces.NS_WEATHER, WeatherElements.PRECIPITATION, String.valueOf(this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.RR)));
            
            Optional<Result> previousR = retVal.stream().filter(res->res.getValidTimeStart().compareTo(middleOfSlidingWindowDate) == 0).findFirst();
            if(previousR.isPresent()){
                previousR.get().setValue(SeptoriaWetHourModel.MODEL_ID.toString(), DataMatrix.WHS, slidingWindowSum.toString());
            }
            retVal.add(r);
            
            currentDate.add1Hour();
        }
        //System.out.println(this.dataMatrix.toCSV());
        return retVal;
    }

    @Override
    public ModelId getModelId() {
        return SeptoriaWetHourModel.MODEL_ID;
    }

    @Override
    public String getModelName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getModelName(String language) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLicense() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCopyright() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getModelDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getModelDescription(String language) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getWarningStatusInterpretation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getWarningStatusInterpretation(String language) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getModelUsage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getModelUsage(String language) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSampleConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setConfiguration(ModelConfiguration config) throws ConfigValidationException {
        System.out.println("JAUDA");
        this.dataMatrix = new DataMatrix();
        
        // Object deserialization tool
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            this.thresholdRelativeHumidity = mapper.convertValue(config.getConfigParameter("thresholdRelativeHumidity"), Double.class);
            this.thresholdLeafWetness = mapper.convertValue(config.getConfigParameter("thresholdLeafWetness"), Double.class);
            this.thresholdPrecipitation = mapper.convertValue(config.getConfigParameter("thresholdPrecipitation"), Double.class);
            this.thresholdHumidPeriodHours = mapper.convertValue(config.getConfigParameter("thresholdHumidPeriodHours"), Integer.class);
            this.slidingHoursPast = mapper.convertValue(config.getConfigParameter("slidingHoursPast"), Integer.class);
            this.slidingHoursAhead = mapper.convertValue(config.getConfigParameter("slidingHoursAhead"), Integer.class);
            
            this.timeZone = TimeZone.getTimeZone((String) config.getConfigParameter("timeZone"));
        }
        catch(NullPointerException npe)
        {
            throw new ConfigValidationException("Missing one or more of these parameters: "
                    + "thresholdRelativeHumidity, thresholdLeafWetness, thresholdPrecipitation, "
                    + "thresholdHumidPeriodHours, slidingHoursPast, slidingHoursAhead, timeZone");
        }
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(timeZone);
        try
        {
            this.dateGs31 = format.parse(config.getConfigParameter("dateGs31").toString());
        }
        catch(ParseException | NullPointerException ex)
        {
            throw new ConfigValidationException("Illegal date format for (or missing completely) dateGs31: " + config.getConfigParameter("dateGs31"));
        }
        
        WeatherUtil wUtil = new WeatherUtil();
        List<WeatherObservation> observations = mapper.convertValue(config.getConfigParameter("observations"), new TypeReference<List<WeatherObservation>>(){});
        for(WeatherObservation o:observations)
        {
            switch(o.getElementMeasurementTypeId())
            {
                case WeatherElements.PRECIPITATION:
                    if(o.getLogIntervalId().equals(WeatherObservation.LOG_INTERVAL_ID_1H))
                    {
                        this.dataMatrix.setParamDoubleValueForDate(o.getTimeMeasured(), DataMatrix.RR, o.getValue());
                    }
                    break;
                case WeatherElements.RELATIVE_HUMIDITY_MEAN:
                    if(o.getLogIntervalId().equals(WeatherObservation.LOG_INTERVAL_ID_1H))
                    {
                        this.dataMatrix.setParamDoubleValueForDate(o.getTimeMeasured(), DataMatrix.UM, o.getValue());
                    }
                    break;
                case WeatherElements.LEAF_WETNESS:
                    if(o.getLogIntervalId().equals(WeatherObservation.LOG_INTERVAL_ID_1H))
                    {
                        this.dataMatrix.setParamDoubleValueForDate(o.getTimeMeasured(), DataMatrix.BT, o.getValue());
                    }
                default:
                    // Keep calm and continue importing data
                    break;
            }
        }
        
        // Make sure we have weather data from dateGs31
        calculateGrowthStages();
        if(this.dataMatrix.getFirstDateWithParameterValue("RR").after(dateGs31))
        {
            throw new ConfigValidationException("ERROR: Missing weather data. First date with precipitation data is " + this.dataMatrix.getFirstDateWithParameterValue("RR") + ", date for GS 31 = " + this.dateGs31);
        }
    }
    
    private void calculateGrowthStages(){
        this.dateGs31 = weatherUtil.normalizeToExactDate(dateGs31, timeZone);
        Calendar cal = Calendar.getInstance(this.timeZone);
        cal.setTime(this.dateGs31);
        cal.add(Calendar.DATE, 10);
        this.date3rdUpperLeafEmerging = cal.getTime();
        cal.add(Calendar.DATE, 10);
        this.date2ndUpperLeafEmerging = cal.getTime();
        cal.add(Calendar.DATE, 10);
        this.dateUpperLeafEmerging = cal.getTime();
        cal.setTime(this.date3rdUpperLeafEmerging);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DATE,30);
        this.dateGs75 = cal.getTime();
    }

}
