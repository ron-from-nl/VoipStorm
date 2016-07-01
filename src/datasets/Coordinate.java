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
