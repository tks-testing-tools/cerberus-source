/* Cerberus Copyright (C) 2013 - 2017 cerberustesting
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This file is part of Cerberus.

 Cerberus is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Cerberus is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.*/
package org.cerberus.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Dashboard util for map data treatment. Made to extract statistics of data or
 * other treatment. All function of this class must be compatible with Dashboard
 * indicator data/ Work with specific map formatted like a matrix :
 * {"KEY_1":"Value"},{"VALUE_1_1":"value"},{"VALUE_2_1":"value"}
 * {"KEY_2":"Value"},{"VALUE_1_2":"value"},{"VALUE_2_2":"value"}
 *
 * Target is always format dashboardindicator like it to use this class for
 * treatment.
 *
 * @author cDelage
 */
public class DashboardUtil {

    private static final Logger LOG = LogManager.getLogger(DashboardUtil.class);

    /**
     * reduce map values for chart. Work with formatted map. Read value at
     * regular interval in a line. Example : if i take 100 value and i want 12
     * entry (for 11 nbValueReturned cause index begin 0) function will return
     * [0,10,20,30,40,50,60,70,80,90,100]
     *
     * @param initMap
     * @param nbValueReturned
     * @param nbLineIndexed
     * @return
     */
    public static Map<String, Object> reduceMapForChart(Map<String, Object> initMap, Integer nbValueReturned, Integer nbLineIndexed) {
        Map<String, Object> response = new HashMap();

        if (initMap.size() > nbValueReturned && nbValueReturned > 1) {

            try {
                //Compute real size (including 0 and divise for KEY / VALUE)
                double initialSize = DashboardUtil.computeSize(initMap, nbLineIndexed);

                Integer quantileValue = nbValueReturned - 1;

                //Search x = n/q-1 to compute an regular interval index.
                Double indexQuantile = new Double(initialSize / (quantileValue));

                for (int i = 0; i < nbValueReturned; i++) {
                    //Calcul x_quantile * index of line
                    Double specificQuantile = new Double(indexQuantile * i);
                    Double comparator = new Double(specificQuantile.intValue() + 0.5);
                    int initIndex = specificQuantile.intValue();

                    //If finalIndex decimal is > 0.5 -> final index get next int value
                    if (comparator < specificQuantile) {
                        initIndex++;
                    }

                    //Count response size by number of value list insert to exctract index
                    int responseIndex = response.size() / nbLineIndexed;

                    //If key_x have a value
                    if (DashboardUtil.getStringEntryKey(initMap, initIndex) != null) {
                        //Set it to response
                        response.put("KEY_" + responseIndex, DashboardUtil.getStringEntryKey(initMap, initIndex));

                        //For all values line, set values
                        for (int j = 1; j <= nbLineIndexed - 1; j++) {
                            if (DashboardUtil.getStringEntryValue(initMap, j, initIndex) != null) {
                                response.put("VALUE_" + j + "_" + responseIndex, DashboardUtil.getStringEntryValue(initMap, j, initIndex));
                            } else {
                                response.put("VALUE_1_" + responseIndex, "CONVERSION ERROR");
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                LOG.error("Exception during parse data : ", exception);
            } finally {
                return response;
            }

        }
        return initMap;
    }

    /**
     * Compute advancement for each values of map indexed. work with specific
     * formatted map.
     *
     * @param initMap
     * @param nbLineIndexed
     * @return
     */
    public static Map<String, Object> computeAdvancement(Map<String, Object> initMap, Integer nbLineIndexed, Integer computeLine) {
        if (initMap.size() > nbLineIndexed) {
            Integer size = DashboardUtil.computeSize(initMap, nbLineIndexed);
            for (int i = 0; i <= size; i++) {
                if (i != 0) {
                    //Parse current number of execution in map index i
                    Long nbExe = DashboardUtil.getLongEntryValue(initMap, computeLine, i);
                    LOG.debug("nbExe : " + nbExe);
                    //Get last number of execution in map index i
                    Long nbLastExe = DashboardUtil.getLongEntryValue(initMap, computeLine, (i - 1));
                    LOG.debug("nbLastExe : " + nbLastExe);
                    //Compute advancement
                    Long advancement = nbExe - nbLastExe;
                    LOG.debug("Advancement : " + advancement);
                    //Set values to response
                    initMap.put("VALUE_" + nbLineIndexed + "_" + i, advancement);
                } else {
                    initMap.put("VALUE_" + nbLineIndexed + "_0", DashboardUtil.getLongEntryValue(initMap, computeLine, 0));
                }
            }
        }
        return initMap;
    }

    /**
     * Compute a ladder by an map and an indice. Work with formatted map.
     *
     * @param initMap.
     * @param nbLineIndexed
     * @param targetLine
     * @param scaleMultiplier
     * @return scale formatted like : highest value of line *
     */
    public static long generateScale(Map<String, Object> initMap, int nbLineIndexed, long targetLine, Double scaleMultiplier) {
        long maxValue = 0;
        Double computeLadder = new Double(0);
        int loopSize = DashboardUtil.computeSize(initMap, nbLineIndexed);
        for (int i = 0; i <= loopSize; i++) {
            try {
                Long valueMapped = DashboardUtil.getLongEntryValue(initMap, targetLine, i);
                if (valueMapped > maxValue) {
                    maxValue = valueMapped;
                }
            } catch (NumberFormatException exception) {
                LOG.error("error during read value of map : ", exception);
            }
            computeLadder = maxValue * scaleMultiplier;
        }
        return computeLadder.intValue();
    }

    /**
     * Give number of key / values entry.
     *
     * @param initMap
     * @param nbLineIndexed
     * @return
     */
    public static long computeNbEntry(Map<String, Object> initMap, long nbLineIndexed) {
        return initMap.size() / nbLineIndexed;
    }

    /**
     * Compute average for one line of map indexed.
     *
     * @param initMap
     * @param nbLineIndexed
     * @param targetLine
     * @return
     */
    public static long computeAverage(Map<String, Object> initMap, int nbLineIndexed, long targetLine) {
        int size = DashboardUtil.computeSize(initMap, nbLineIndexed);
        long storeValue = 0;
        long response = 0;

        for (int i = 0; i < size; i++) {
            try {
                Long value = DashboardUtil.getLongEntryValue(initMap, targetLine, i);
                storeValue += value;
            } catch (NumberFormatException exception) {
                LOG.error("Invalid data pass to computeMedian, impossible to compute median cause : ", exception);
                return 0;
            }
        }
        response = storeValue / size;
        return response;
    }

    /**
     * Compute median of map smell indexed.
     *
     * @param initMap
     * @param nbLineIndexed
     * @param targetLine
     * @return
     */
    public static long computeMedian(Map<String, Object> initMap, int nbLineIndexed, long targetLine) {
        long response = 0;
        try {
            int sizeLoop = DashboardUtil.computeSize(initMap, nbLineIndexed);
            List<Long> values = new ArrayList();

            for (int i = 1; i <= sizeLoop; i++) {
                values.add(DashboardUtil.getLongEntryValue(initMap, targetLine, i));
            }
            Collections.sort(values);
            int medianIndex = values.size() / 2;
            response = values.get(medianIndex);

        } catch (Exception exception) {
            LOG.error("Error during median computing, catch exception : ", exception);
        }

        return response;
    }

    /**
     * Rename key value of map line indexed.
     *
     * @param initMap
     * @param nbLineIndexed
     * @param targetLine
     * @param keyName
     * @param type [string, long]
     * @return
     */
    public static Map<String, Object> renameValueKey(Map<String, Object> initMap, Integer nbLineIndexed, Integer targetLine, String keyName, String type) {
        Integer mapSize = DashboardUtil.computeSize(initMap, nbLineIndexed);
        for (int i = 0; i <= mapSize; i++) {
            if (type.equals("long")) {
                initMap.put(keyName + "_" + i, DashboardUtil.getLongEntryValue(initMap, targetLine, i));
            } else {
                initMap.put(keyName + "_" + i, DashboardUtil.getStringEntryValue(initMap, targetLine, i));
            }
            initMap.remove("VALUE_" + targetLine + "_" + i);
        }
        return initMap;
    }

    /**
     * Compute size for map by line indexed. (initMap.size - nbLineIndexed) /
     * nbLineIndexed Compute it for loop sizing for example.
     *
     * @param initMap
     * @param nbLineIndexed
     * @return
     */
    public static int computeSize(Map<String, Object> initMap, int nbLineIndexed) {
        return (initMap.size() - nbLineIndexed) / nbLineIndexed;
    }

    /**
     * Extract int value from map indexed.
     *
     * @param initMap
     * @param targetLine index of line target
     * @param targetIndex index of indice target
     * @return
     */
    public static long getLongEntryValue(Map<String, Object> initMap, long targetLine, long targetIndex) {
        long response = 0;
        try {
            String value = DashboardUtil.getStringEntryValue(initMap, targetLine, targetIndex);
            response = Long.valueOf(value);
        } catch (NumberFormatException exception) {
            LOG.error("Impossible to parse value in integer : ", exception);
            return 0;
        }
        return response;
    }

    /**
     * Get string value from map entry. read string value in initmap indexed
     * like : VALUE_(targetLine)_(targetIndex)
     *
     * @param initMap
     * @param targetLine
     * @param targetIndex
     * @return
     */
    public static String getStringEntryValue(Map<String, Object> initMap, long targetLine, long targetIndex) {
        return String.valueOf(initMap.get("VALUE_" + targetLine + "_" + targetIndex));
    }

    /**
     * Read key entry for indexed map. Read value of KEY_(targetIndex)
     *
     * @param initMap
     * @param targetIndex
     * @return
     */
    public static String getStringEntryKey(Map<String, Object> initMap, long targetIndex) {
        return String.valueOf(initMap.get("KEY_" + targetIndex));
    }

    /**
     * Duplicate a line for indexed map.
     *
     * @param initMap
     * @param nbLineIndexed
     * @param copyLine line who be copied.
     * @param typeOfData Accept string or long
     * @return
     */
    public static Map<String, Object> duplicateLine(Map<String, Object> initMap, int nbLineIndexed, long copyLine, String typeOfData) {
        Integer initSize = DashboardUtil.computeSize(initMap, nbLineIndexed);
        for (int i = 0; i <= initSize; i++) {
            if (typeOfData.equals("long")) {
                initMap.put("VALUE_" + nbLineIndexed + "_" + i, DashboardUtil.getLongEntryValue(initMap, copyLine, i));
            } else {
                initMap.put("VALUE_" + nbLineIndexed + "_" + i, DashboardUtil.getStringEntryValue(initMap, copyLine, i));
            }
        }
        return initMap;
    }

    public static Map<String, Object> convertLineToLong(Map<String, Object> initMap, int nbLineIndexed, int targetLine) {
        Integer index = DashboardUtil.computeSize(initMap, nbLineIndexed);
        for (int i = 0; i <= index; i++) {
            initMap.put("VALUE_" + targetLine + "_" + i, DashboardUtil.getLongEntryValue(initMap, targetLine, i));
        }
        return initMap;
    }
}
