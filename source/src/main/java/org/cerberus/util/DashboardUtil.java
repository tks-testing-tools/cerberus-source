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

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Dashboard util for map data treatment. Made to extract statistics of data or
 * other treatment. All function of this class must be compatible with Dashboard
 * indicator data/ Work with specific map formatted like a matrix.
 * {"KEY_1":"Value"},{"VALUE_1_1":"value"},{"VALUE_2_1":"value"}
 * {"KEY_2":"Value"},{"VALUE_1_2":"value"},{"VALUE_2_2":"value"}
 *
 * Function of this class is called in DashboardEntryDataService.
 *
 * @author cDelage
 */
public class DashboardUtil {

    private static final Logger LOG = LogManager.getLogger(DashboardUtil.class);

    /**
     * Extract value of each approximate quantile for a map (reduce data for
     * linear chart). Work for specific map formatted with key like :
     * {"KEY_1":"Value"},{"VALUE_1_1":"value"},{"VALUE2_1":"Value"} It made to
     * reduce map to specific number of value for chart logic. Work with
     * quantile > 1.
     *
     * @param initialMap map formatted for reduce by quantile
     * @param nbEntry n-1 of value returned
     * @param value2 if you add {"VALUE_2_X" : "values"} to your map
     * @param value3 if you add {"VALUE_3_X" : "values"} to your map
     * @param value4 if you add {"VALUE_4_X" : "values"} to your map
     * @return
     */
    public static Map<String, Object> reduceMapForChart(Map<String, Object> initialMap, Integer nbEntry, boolean value2, boolean value3, boolean value4) {
        Map<String, Object> response = new HashMap();

        //Count number of value sent by map (for example KEY + VALUE1 + VALUE2 = 3)
        int numberValues = 2;

        if (value2) {
            numberValues++;
        }
        if (value3) {
            numberValues++;
        }
        if (value4) {
            numberValues++;
        }

        if (initialMap.size() > nbEntry && nbEntry > 1) {

            try {
                //Compute real size (including 0 and divise for KEY / VALUE)
                double initialSize = (initialMap.size() - numberValues) / numberValues;

                //Search x = n/q-1 to calcul each value in for
                Double indexQuantile = new Double(initialSize / (nbEntry - 1));

                for (int i = 0; i <= nbEntry; i++) {
                    //Calcul x_quantile * index of list
                    Double specificQuantile = new Double(indexQuantile * i);
                    Double comparator = new Double(specificQuantile.intValue() + 0.5);
                    int finalIndex = specificQuantile.intValue();
                    //If finalIndex decimal is > 0.5 -> final index get next int value
                    if (comparator < specificQuantile) {
                        finalIndex++;
                    }

                    //Count response size by number of value list insert to exctract index
                    int responseIndex = response.size() / numberValues;

                    if (initialMap.get("KEY_" + finalIndex) != null) {
                        response.put("KEY_" + responseIndex, initialMap.get("KEY_" + finalIndex));
                        if (initialMap.get("VALUE_1_" + finalIndex) != null) {
                            response.put("VALUE_1_" + responseIndex, initialMap.get("VALUE_1_" + finalIndex));
                        } else {
                            response.put("VALUE_1_" + responseIndex, "CONVERSION ERROR");
                        }

                        if (value2 && initialMap.get("VALUE_2_" + finalIndex) != null) {
                            response.put("VALUE_2_" + responseIndex, initialMap.get("VALUE_2_" + finalIndex));
                        } else if (value2) {
                            response.put("VALUE_2_" + responseIndex, "CONVERSION ERROR");
                        }

                        if (value3 && initialMap.get("VALUE_3_" + finalIndex) != null) {
                            response.put("VALUE_3_" + responseIndex, initialMap.get("VALUE_3_" + finalIndex));
                        } else if (value3) {
                            response.put("VALUE_3_" + responseIndex, "CONVERSION ERROR");
                        }

                        if (value4 && initialMap.get("VALUE_4_" + finalIndex) != null) {
                            response.put("VALUE_4_" + responseIndex, initialMap.get("VALUE_4_" + finalIndex));
                        } else if (value4) {
                            response.put("VALUE_4_" + responseIndex, "CONVERSION ERROR");
                        }
                    }
                }
            } catch (Exception exception) {
                LOG.error("Exception during parse data : ", exception);
            } finally {
                return response;
            }

        }
        return initialMap;
    }

    /**
     * Compute advancement for each value. work with specific formatted map.
     *
     * @param initialMap
     * @param nbOfLineIndexed
     * @return
     */
    public static Map<String, Object> computeAdvancement(Map<String, Object> initialMap, Integer nbOfLineIndexed) {
        Map<String, Object> response = new HashMap();
        if (initialMap.size() > nbOfLineIndexed) {
            int size = (initialMap.size() - nbOfLineIndexed) / nbOfLineIndexed;
            for (int i = 0; i <= size; i++) {
                if (i != 0) {
                    //Parse current number of execution in map index i
                    String objectValue = String.valueOf(initialMap.get("VALUE_1_" + i));
                    Integer nbExe = Integer.valueOf(objectValue);

                    //Get last number of execution in map index i
                    String lastValue = String.valueOf(initialMap.get("VALUE_1_" + (i - 1)));
                    Integer nbLastExe = Integer.valueOf(lastValue);

                    //Compute advancement
                    Integer advancement = nbExe - nbLastExe;

                    //Set values to response
                    response.put("KEY_" + i, initialMap.get("KEY_" + i));
                    response.put("VALUE_1_" + i, advancement);
                } else {
                    response.put("KEY_0", initialMap.get("KEY_0"));
                    response.put("VALUE_1_0", initialMap.get("VALUE_1_0"));
                }
            }
            return response;
        }
        return initialMap;
    }

    /**
     * Compute a ladder by an map and an indice.
     *
     * @param initialMap. Map formatted for DashboardUtil
     * @param nbOfLineIndexed. Number of line values of map (example INDEX_X;
     * VALUE_1_X; VALUE_2_X = 3)
     * @param lineTarget. Line target to compute index (for example 2 :
     * VALUE_2_X)
     * @param statisticIndex
     * @return
     */
    public static long generateLadder(Map<String, Object> initialMap, long nbOfLineIndexed, long lineTarget, Double statisticIndex) {
        long maxValue = 0;
        Double computeLadder = new Double(0);
        long loopSize = (initialMap.size() - nbOfLineIndexed) / nbOfLineIndexed;
        for (int i = 0; i < loopSize; i++) {
            try {
                String value = String.valueOf(initialMap.get("VALUE_" + lineTarget + "_" + i));
                Long valueMapped = Long.valueOf(value);
                if (valueMapped > maxValue) {
                    maxValue = valueMapped;
                }
            } catch (NumberFormatException exception) {
                LOG.error("error during read value of map : ", exception);
            }
            computeLadder = maxValue * statisticIndex;
        }
        return computeLadder.intValue();
    }

    /**
     * Give number of key / values entry.
     * @param initialMap
     * @param nbOfLineIndexed nb of line indexed in map (example : {"KEY_1" : "value"},{"VALUE_1_1" : "value"},{"VALUE_2_1" : "value"} = 3)
     * @return 
     */
    public static long computeNbOfEntry(Map<String, Object> initialMap, Integer nbOfLineIndexed) {
        return initialMap.size() / nbOfLineIndexed;
    }
}
