/*
 * Copyright (c) 2018 NIBIO <http://www.nibio.no/>. 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package no.nibio.vips.model.septoriahumiditymodel;

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
    // Humid hour sum for sliding middle
    public final static String HHS = "HHS";
    
    // Current hour is humid hour? 1 : 0
    public final static String HH = "HH";
    // Humid period hour (consecutive humid hours)
    public final static String HPH = "HPH";
    // Humid period hour (consecutive humid hours) corrected for protection period
    public final static String HPHPP = "HPHPP";
    // Threshold for consecutive humid hours
    public final static String THPHPP = "THPHPP";
    // Is this in the Spraying Protection Period? (1/0)
    public final static String SPP = "SPP";
}
