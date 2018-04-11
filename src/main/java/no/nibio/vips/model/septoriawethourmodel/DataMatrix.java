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

import no.nibio.vips.util.DateMap;

/**
 * @copyright 2018 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class DataMatrix extends DateMap {
    //  Hourly precipitation
    public final static String RR = "RR";
    // Hourly relative humidity
    public final static String UM = "UM";
    // Hourly leaf wetness
    public final static String BT = "BT";
    // Wet hour sum for sliding middle
    public final static String WHS = "WHS";
    // Current hour is wet hour? 1 : 0
    public final static String WH = "WH";
}
