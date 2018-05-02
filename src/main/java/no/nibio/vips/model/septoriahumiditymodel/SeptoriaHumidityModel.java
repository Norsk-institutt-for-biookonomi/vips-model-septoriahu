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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import no.nibio.vips.entity.ResultImpl;
import no.nibio.vips.i18n.I18nImpl;
import no.nibio.vips.util.CommonNamespaces;
import no.nibio.vips.util.DateTimeInterval;
import no.nibio.vips.util.ModelUtil;
import no.nibio.vips.util.XDate;

/**
 * Model developed by SEGES, Denmark
 * @copyright 2018 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class SeptoriaHumidityModel extends I18nImpl implements Model {
    
    private final static ModelId MODEL_ID = new ModelId("SEPTORIAHU");
    
    private Date dateGs31, date3rdUpperLeafEmerging, date2ndUpperLeafEmerging, dateUpperLeafEmerging, dateGs75;
    private Date dateSpraying1, dateSpraying2, lastSprayingProtectionEnd;
    private DateTimeInterval protectionPeriod1, protectionPeriod2;
    private List<DateTimeInterval> protectionPeriods;
    private TimeZone timeZone;
    
    private Double thresholdRelativeHumidity, thresholdLeafWetness, thresholdPrecipitation;
    private Integer thresholdHumidPeriodHours, slidingHoursPast, slidingHoursAhead, sprayingProtectionDays;
    
    private DataMatrix dataMatrix;
    
    private final WeatherUtil weatherUtil;
    private final ModelUtil modelUtil;
    
    public SeptoriaHumidityModel(){
        // Setting the file name of the resource bundle
        super("no.nibio.vips.model.septoriahumiditymodel.texts");
        this.weatherUtil = new WeatherUtil();
        this.modelUtil = new ModelUtil();
    }

    @Override
    public List<Result> getResult() throws ModelExcecutionException {
        // Start date for calculation is dateGs31 (mandatory), last date is whatever is first of today + 2 days or dateGs75
        // But inside the model, we just use the weather data as far as they reach, as long as we're not past dateGs75
        XDate currentDate = new XDate(this.dateGs31);
        Date lastDate = this.dataMatrix.getLastDateWithParameterValue("RR");
        Calendar cal = Calendar.getInstance();
        
        // Calculating values
        LinkedList<Integer> humidHours = new LinkedList<>();
        LinkedList<Result> humidHoursConsecutive = new LinkedList<>();

        List<Result> retVal = new ArrayList<>();
        Boolean humidPeriodStarted = false;
        Integer humidPeriodHour = 0;
        // This excludes dates before gs32 and inside protection periods
        Integer humidPeriodHourInSusceptiblePhase = 0;
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
            this.dataMatrix.setParamIntValueForDate(currentDate, DataMatrix.HH, humidHour);
            
            humidPeriodHour = humidHour == 1 ? humidPeriodHour + 1 : 0;
            this.dataMatrix.setParamIntValueForDate(currentDate, DataMatrix.HPH, humidPeriodHour);
            
            humidPeriodHourInSusceptiblePhase = 
                    humidHour == 1 
                    && !DateTimeInterval.isDateInIntervals(currentDate, this.protectionPeriods) 
                    && currentDate.after(this.date3rdUpperLeafEmerging)
                    ? humidPeriodHourInSusceptiblePhase + 1 : 0;
            
            cal.setTime(currentDate);
            cal.add(Calendar.HOUR_OF_DAY, -this.slidingHoursAhead );
            Date middleOfSlidingWindowDate = cal.getTime();
            Integer slidingWindowSum = humidHours.stream().mapToInt(Integer::intValue).sum();
            this.dataMatrix.setParamIntValueForDate(middleOfSlidingWindowDate, DataMatrix.HHS, slidingWindowSum);
            
            
            Result r = new ResultImpl();
            r.setValidTimeStart(new Date(currentDate.getTime()));
            r.setWarningStatus(currentDate.after(this.date3rdUpperLeafEmerging) && currentDate.before(this.dateGs75) ? Result.WARNING_STATUS_NO_RISK: Result.WARNING_STATUS_NO_WARNING);
            r.setValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.HPH, String.valueOf(humidPeriodHour));
            r.setValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.HPHPP, String.valueOf(humidPeriodHourInSusceptiblePhase));
            r.setValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.THPHPP, String.valueOf(this.thresholdHumidPeriodHours));
            r.setValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.HH, String.valueOf(humidHour));
            r.setValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.HHS, String.valueOf(slidingWindowSum));
            if(DateTimeInterval.isDateInIntervals(currentDate, this.protectionPeriods))
            {
                r.setValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.SPP, "1");
            }
            r.setValue(CommonNamespaces.NS_WEATHER, WeatherElements.LEAF_WETNESS, String.valueOf(this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.BT)));
            r.setValue(CommonNamespaces.NS_WEATHER, WeatherElements.RELATIVE_HUMIDITY_MEAN, String.valueOf(this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.UM)));
            r.setValue(CommonNamespaces.NS_WEATHER, WeatherElements.PRECIPITATION, String.valueOf(this.dataMatrix.getParamDoubleValueForDate(currentDate, DataMatrix.RR)));

            if(humidHour == 1){
                humidHoursConsecutive.add(r);
            }
            else
            {
                Integer previousHumidPeriodHourinSusceptiblePhase = retVal.size() > 0 ? Integer.valueOf(retVal.get(retVal.size()-1).getValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.HPHPP)) : null;
                // Set all collected Result objects as part of humid period if above threshold
                if(humidHoursConsecutive.size() > this.thresholdHumidPeriodHours)
                {
                    for(Result re:humidHoursConsecutive)
                    {
                        if(previousHumidPeriodHourinSusceptiblePhase != null &&  previousHumidPeriodHourinSusceptiblePhase > this.thresholdHumidPeriodHours
                                && !DateTimeInterval.isDateInIntervals(re.getValidTimeStart(), this.protectionPeriods) 
                                && re.getValidTimeStart().after(this.date3rdUpperLeafEmerging))
                        {
                            re.setWarningStatus(Result.WARNING_STATUS_HIGH_RISK);
                        }
                    }
                }
                else
                {
                    // False alarm, not enough hours. Clear
                    humidHoursConsecutive.stream().forEach(re->re.setValue(SeptoriaHumidityModel.MODEL_ID.toString(), DataMatrix.HPH, "0"));
                }
                humidHoursConsecutive.clear();
            }
            
            retVal.add(r);
            
            currentDate.add1Hour();
        }
        //System.out.println(this.dataMatrix.toCSV());
        return retVal;
    }
    

    @Override
    public ModelId getModelId() {
        return SeptoriaHumidityModel.MODEL_ID;
    }

    @Override
    public String getModelName() {
        return this.getModelName(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelName(String language) {
        return this.getText("name", language);
    }

    @Override
    public String getLicense() {
        return "Copyright (c) 2018 NIBIO <http://www.nibio.no/>. \n" +
            "\n" +
            "This file is part of SeptoriaHumidityModel. \n" + 
            "SeptoriaHumidityModel is free software: you can redistribute it and/or modify \n" + 
            "it under the terms of the NIBIO Open Source License as published by \n" + 
            "NIBIO, either version 1 of the License, or (at your option) any \n" +
            "later version. \n" +
            "\n" +
            "SeptoriaHumidityModel is distributed in the hope that it will be useful, \n" +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of \n" +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the \n" +
            "NIBIO Open Source License for more details. \n" +
            "" +
            "You should have received a copy of the NIBIO Open Source License \n" +
            "along with AppleScabModel.  If not, see <http://www.nibio.no/licenses/>. \n";
    }

    @Override
    public String getCopyright() {
        return "(c) 2018 NIBIO (http://www.nibio.no/). Contact: post@nibio.no";
    }

    @Override
    public String getModelDescription() {
        return this.getModelDescription(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelDescription(String language) {
        try
        {
            return this.modelUtil.getTextWithBase64EncodedImages(this.getText("description", language), this.getClass());
        }
        catch(IOException ex)
        {
            return this.getText("description", language);
        }
    }

    @Override
    public String getWarningStatusInterpretation() {
        return this.getWarningStatusInterpretation(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getWarningStatusInterpretation(String language) {
        return this.getText("statusInterpretation", language);
    }

    @Override
    public String getModelUsage() {
        return this.getModelUsage(Model.DEFAULT_LANGUAGE);
    }

    @Override
    public String getModelUsage(String language) {
        return this.getText("usage", language);
    }

    @Override
    public String getSampleConfig() {
        return "{\n" +
        "	\"loginInfo\": {\n" +
        "		\"username\": \"example\",\n" +
        "		\"password\": \"example\"\n" +
        "	},\n" +
        "	\"modelId\": \"SEPTORIAHU\",\n" +
        "	\"configParameters\": {\n" +
        "		\"date3rdUpperLeafEmerging\": \"2018-05-03\",\n" +
        "		\"sprayingProtectionDays\": 10,\n" +
        "		\"timeZone\": \"Europe/Copenhagen\",\n" +
        "		\"dateGs31\": \"2018-04-23\",\n" +
        "		\"dateGs75\": \"2018-06-30\",\n" +
        "		\"thresholdPrecipitation\": 0.2,\n" +
        "		\"thresholdLeafWetness\": 30.0,\n" +
        "		\"thresholdRelativeHumidity\": 85.0,\n" +
        "		\"slidingHoursAhead\": 36,\n" +
        "		\"leafLifeTime\": 75,\n" +
        "		\"date2ndUpperLeafEmerging\": \"2018-05-13\",\n" +
        "		\"slidingHoursPast\": 36,\n" +
        "		\"dateSpraying2\": \"\",\n" +
        "		\"thresholdHumidPeriodHours\": 19,\n" +
        "		\"dateSpraying1\": \"\",\n" +
        "		\"dateUpperLeafEmerging\": \"2018-05-23\",\n" +
        "		\"observations\": [{\n" +
        "				\"timeMeasured\": \"2018-03-25T00:00:00+02:00\",\n" +
        "				\"elementMeasurementTypeId\": \"RR\",\n" +
        "				\"logIntervalId\": 1,\n" +
        "				\"value\": 0\n" +
        "			},\n" +
        "                       {\n" +
        "				\"timeMeasured\": \"2018-03-25T00:00:00+02:00\",\n" +
        "				\"elementMeasurementTypeId\": \"UM\",\n" +
        "				\"logIntervalId\": 1,\n" +
        "				\"value\": 88.3\n" +
        "			},\n" +
        "			{\n" +
        "				\"timeMeasured\": \"2018-03-25T00:00:00+02:00\",\n" +
        "				\"elementMeasurementTypeId\": \"BT\",\n" +
        "				\"logIntervalId\": 1,\n" +
        "				\"value\": 0\n" +
        "			}\n" +
        "		]\n" +
        "	}\n" +
        "}";
    }

    @Override
    public void setConfiguration(ModelConfiguration config) throws ConfigValidationException {
        this.dataMatrix = new DataMatrix();
        
        // Object deserialization tool
        ObjectMapper mapper = new ObjectMapper();
        
        
        
        // TODO: Have defaults for these?
        try
        {
            this.thresholdRelativeHumidity = mapper.convertValue(config.getConfigParameter("thresholdRelativeHumidity"), Double.class);
            this.thresholdLeafWetness = mapper.convertValue(config.getConfigParameter("thresholdLeafWetness"), Double.class);
            this.thresholdPrecipitation = mapper.convertValue(config.getConfigParameter("thresholdPrecipitation"), Double.class);
            this.thresholdHumidPeriodHours = mapper.convertValue(config.getConfigParameter("thresholdHumidPeriodHours"), Integer.class);
            this.slidingHoursPast = mapper.convertValue(config.getConfigParameter("slidingHoursPast"), Integer.class);
            this.slidingHoursAhead = mapper.convertValue(config.getConfigParameter("slidingHoursAhead"), Integer.class);
            this.sprayingProtectionDays = mapper.convertValue(config.getConfigParameter("sprayingProtectionDays"), Integer.class);
            
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
        format.setLenient(false); // Make sure it doesn't try to interpret anything, but be strict
        try
        {
            this.dateGs31 = format.parse(config.getConfigParameter("dateGs31").toString());
        }
        catch(ParseException | NullPointerException ex)
        {
            throw new ConfigValidationException("Illegal date format for (or missing completely) dateGs31: " + config.getConfigParameter("dateGs31"));
        }
        
        // Optional spraying info
        Calendar cal = Calendar.getInstance(this.timeZone);
        try
        {
            if(!config.getConfigParameter("dateSpraying1").toString().isEmpty())
            {
                this.dateSpraying1 = format.parse(config.getConfigParameter("dateSpraying1").toString());
            }
        }
        catch(ParseException | NullPointerException ex)
        {
            if(ex instanceof ParseException)
            {
                throw new ConfigValidationException("Illegal date format for dateSpraying1: " + config.getConfigParameter("dateSpraying1"));
            }
        }
        
        try
        {
            if(!config.getConfigParameter("dateSpraying2").toString().isEmpty())
            {
                this.dateSpraying2 = format.parse(config.getConfigParameter("dateSpraying2").toString());
            }
        }
        catch(ParseException | NullPointerException ex)
        {
            if(ex instanceof ParseException)
            {
                throw new ConfigValidationException("Illegal date format for dateSpraying2: " + config.getConfigParameter("dateSpraying2"));
            }
        }
        
        // Setting protection periods, finding last date of spraying
        this.protectionPeriods = new ArrayList<>();
        if(this.dateSpraying1 != null || this.dateSpraying2 != null)
        {
            if(this.dateSpraying1 != null){
                cal.setTime(this.dateSpraying1);
                cal.add(Calendar.DATE, this.sprayingProtectionDays);
                this.protectionPeriod1 = new DateTimeInterval(this.dateSpraying1, cal.getTime());
                this.protectionPeriods.add(protectionPeriod1);
            }
            
            if(this.dateSpraying2 != null){
                cal.setTime(this.dateSpraying2);
                cal.add(Calendar.DATE, this.sprayingProtectionDays);
                this.protectionPeriod2 = new DateTimeInterval(this.dateSpraying2, cal.getTime());
                this.protectionPeriods.add(protectionPeriod2);
            }
 
            this.lastSprayingProtectionEnd = DateTimeInterval.getLastEndDate(Arrays.asList(new DateTimeInterval[]{this.protectionPeriod1,this.protectionPeriod2}));
        }
        
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
