/*
 * Copyright © 2008 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

package datasets;

/**
 *
 * @author ron
 */
public class Coordinate
{
    int row;
    int column;

    /**
     *
     */
    public Coordinate() { } // got to have this default constructor with multiple constructors

    /**
     *
     * @param rowParam
     * @param columnParam
     */
    public Coordinate(int rowParam, int columnParam)
    {
	row = rowParam;
	column = columnParam;
    }

    /**
     *
     * @return
     */
    public int getRow()			    { return row; }

    /**
     *
     * @return
     */
    public int getColumn()		    { return column; }

    /**
     *
     * @param rowParam
     */
    public void setRow(int rowParam)	    { row = rowParam; }

    /**
     *
     * @param columnParam
     */
    public void setColumn(int columnParam)   { column = columnParam; }

}
