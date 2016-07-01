package datasets;

/**
 *
 * @author ron
 */
public class Pricelist
{
    private float b2BDaytimeRatePerSecond;
    private float b2BEveningRatePerSecond;
    private float b2CDaytimeRatePerSecond;
    private float b2CEveningRatePerSecond;
    private float a2SDaytimeRatePerSecond;
    private float a2SEveningRatePerSecond;

    /**
     *
     */
    public Pricelist()
    {
        b2BDaytimeRatePerSecond = (float)0.01;
        b2BEveningRatePerSecond = (float)0.01;
        b2CDaytimeRatePerSecond = (float)0.001;
        b2CEveningRatePerSecond = (float)0.005;
        a2SDaytimeRatePerSecond = (float)0.01;
        a2SEveningRatePerSecond = (float)0.01;
    }

    /**
     *
     * @param b2BDaytimeRatePerSecondParam
     * @param b2BEveningRatePerSecondParam
     * @param b2CDaytimeRatePerSecondParam
     * @param b2CEveningRatePerSecondParam
     * @param a2SDaytimeRatePerSecondParam
     * @param a2SEveningRatePerSecondParam
     */
    public Pricelist(float b2BDaytimeRatePerSecondParam, float b2BEveningRatePerSecondParam, float b2CDaytimeRatePerSecondParam, float b2CEveningRatePerSecondParam, float a2SDaytimeRatePerSecondParam, float a2SEveningRatePerSecondParam)
    {
        b2BDaytimeRatePerSecond = b2BDaytimeRatePerSecondParam;
        b2BEveningRatePerSecond = b2BEveningRatePerSecondParam;
        b2CDaytimeRatePerSecond = b2CDaytimeRatePerSecondParam;
        b2CEveningRatePerSecond = b2CEveningRatePerSecondParam;
        a2SDaytimeRatePerSecond = a2SDaytimeRatePerSecondParam;
        a2SEveningRatePerSecond = a2SEveningRatePerSecondParam;
    }

    /**
     *
     * @return
     */
    public float  getB2BDaytimeRatePerSecond()    { return b2BDaytimeRatePerSecond;}

    /**
     *
     * @return
     */
    public float  getB2BEveningRatePerSecond()    { return b2BEveningRatePerSecond;}

    /**
     *
     * @return
     */
    public float  getB2CDaytimeRatePerSecond()    { return b2CDaytimeRatePerSecond;}

    /**
     *
     * @return
     */
    public float  getB2CEveningRatePerSecond()    { return b2CEveningRatePerSecond;}

    /**
     *
     * @return
     */
    public float  getA2SDaytimeRatePerSecond()    { return a2SDaytimeRatePerSecond;}

    /**
     *
     * @return
     */
    public float  getA2SEveningRatePerSecond()    { return a2SEveningRatePerSecond;}

    /**
     *
     * @param b2BDaytimeRatePerSecondParam
     */
    public void     setB2BDaytimeRatePerSecond(float b2BDaytimeRatePerSecondParam)   { b2BDaytimeRatePerSecond   = b2BDaytimeRatePerSecondParam ;}

    /**
     *
     * @param b2BEveningRatePerSecondParam
     */
    public void     setB2BEveningRatePerSecond(float b2BEveningRatePerSecondParam)   { b2BEveningRatePerSecond   = b2BEveningRatePerSecondParam ;}

    /**
     *
     * @param b2CDaytimeRatePerSecondParam
     */
    public void     setB2CDaytimeRatePerSecond(float b2CDaytimeRatePerSecondParam)   { b2CDaytimeRatePerSecond   = b2CDaytimeRatePerSecondParam ;}

    /**
     *
     * @param b2CEveningRatePerSecondParam
     */
    public void     setB2CEveningRatePerSecond(float b2CEveningRatePerSecondParam)   { b2CEveningRatePerSecond   = b2CEveningRatePerSecondParam ;}

    /**
     *
     * @param a2SDaytimeRatePerSecondParam
     */
    public void     setA2SDaytimeRatePerSecond(float a2SDaytimeRatePerSecondParam)   { a2SDaytimeRatePerSecond   = a2SDaytimeRatePerSecondParam ;}

    /**
     *
     * @param a2SEveningRatePerSecondParam
     */
    public void     setA2SEveningRatePerSecond(float a2SEveningRatePerSecondParam)   { a2SEveningRatePerSecond   = a2SEveningRatePerSecondParam ;}

    @Override
    public String   toString()
    {
        String output = new String("");
        output += "b2BDaytimeRatePerSecond: "           + Float.toString(b2BDaytimeRatePerSecond) + "\n";
        output += "b2BEveningRatePerSecond: "           + Float.toString(b2BEveningRatePerSecond) + "\n";
        output += "b2CDaytimeRatePerSecond: "           + Float.toString(b2CDaytimeRatePerSecond) + "\n";
        output += "b2CEveningRatePerSecond: "           + Float.toString(b2CEveningRatePerSecond) + "\n";
        output += "a2SDaytimeRatePerSecond: "           + Float.toString(a2SDaytimeRatePerSecond) + "\n";
        output += "a2SEveningRatePerSecond: "           + Float.toString(a2SEveningRatePerSecond) + "\n";

        return output;
    }
}
