package com.pds.p2p.core.jdbc.meta;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Represents the different categories of jdbc types.
 *
 * @version $Revision: $
 */
public enum JdbcTypeCategoryEnum {

    /**
     * The enum value for numeric jdbc types.
     */
    NUMERIC("numeric", JdbcTypeCategoryEnum.VALUE_NUMERIC),
    /**
     * The enum value for date/time jdbc types.
     */
    DATETIME("datetime", JdbcTypeCategoryEnum.VALUE_DATETIME),
    /**
     * The enum value for textual jdbc types.
     */
    TEXTUAL("textual", JdbcTypeCategoryEnum.VALUE_TEXTUAL),
    /**
     * The enum value for binary jdbc types.
     */
    BINARY("binary", JdbcTypeCategoryEnum.VALUE_BINARY),
    /**
     * The enum value for special jdbc types.
     */
    SPECIAL("special", JdbcTypeCategoryEnum.VALUE_SPECIAL),
    /**
     * The enum value for other jdbc types.
     */
    OTHER("other", JdbcTypeCategoryEnum.VALUE_OTHER);

    private String text;
    private int value;

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }

    /**
     * Creates a new enum object.
     *
     * @param defaultTextRep The textual representation
     * @param value          The corresponding integer value
     */
    private JdbcTypeCategoryEnum(String defaultTextRep, int value) {
        this.text = defaultTextRep;
        this.value = value;
    }

    /**
     * Returns the enum value that corresponds to the given textual
     * representation.
     *
     * @param defaultTextRep The textual representation
     *
     * @return The enum value
     */
    public static JdbcTypeCategoryEnum getEnum(String defaultTextRep) {
        return JdbcTypeCategoryEnum.valueOf(defaultTextRep);
    }

    /**
     * Returns the enum value that corresponds to the given integer
     * representation.
     *
     * @param intValue The integer value
     *
     * @return The enum value
     */
    public static JdbcTypeCategoryEnum getEnum(int intValue) {
        JdbcTypeCategoryEnum[] categoryEnums = JdbcTypeCategoryEnum.values();
        for (int i = 0; i < categoryEnums.length; ++i) {
            if (categoryEnums[i].value == intValue) {
                return categoryEnums[i];
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * The integer value for the enum value for numeric jdbc types.
     */
    public static final int VALUE_NUMERIC = 1;
    /**
     * The integer value for the enum value for date/time jdbc types.
     */
    public static final int VALUE_DATETIME = 2;
    /**
     * The integer value for the enum value for textual jdbc types.
     */
    public static final int VALUE_TEXTUAL = 3;
    /**
     * The integer value for the enum value for binary jdbc types.
     */
    public static final int VALUE_BINARY = 4;
    /**
     * The integer value for the enum value for special jdbc types.
     */
    public static final int VALUE_SPECIAL = 5;
    /**
     * The integer value for the enum value for all other jdbc types.
     */
    public static final int VALUE_OTHER = 6;
}
