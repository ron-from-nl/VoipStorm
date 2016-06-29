import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

//public class Dial extends JFrame implements  ChangeListener {

/**
 *
 * @author ron
 */
public class PerformanceMeter implements  ChangeListener {

    /**
     *
     */
    public JFreeChart chart1;

    /**
     *
     */
    public ChartPanel chartPanel;
    private DefaultValueDataset vmUsageDataset;
    private DefaultValueDataset callPerHourDataset;
    private StandardDialScale vmUsageScale;
    private StandardDialScale callsPerHourScale;
    private DialPlot dialPlot;
    private StandardDialRange standardDialRangeRed;
    private StandardDialRange standardDialRangeOrange;
    private StandardDialRange standardDialRangeGreen;
    private int callsPerHourScaleRange;
    private int vmUsageScaleRange = 100;
    private int scaleFactorBetweenBothScalers;

//    JSlider slider1;
//    JSlider slider2;

    /**
     *
     * @param title
     * @param vmUsageDecelerationThreasholdParam
     * @param callsPerHourScaleRangeParam
     */
    
    public PerformanceMeter(String title, int vmUsageDecelerationThreasholdParam, int callsPerHourScaleRangeParam)
    {
        vmUsageDataset                  = new DefaultValueDataset(50.0);
        callPerHourDataset              = new DefaultValueDataset(10.0);
        callsPerHourScaleRange          = callsPerHourScaleRangeParam;
//        scaleFactorBetweenBothScalers = vmUsageScaleRange / callsPerHourScaleRange;
        scaleFactorBetweenBothScalers   = 1;

        // get data for diagrams
        dialPlot = new DialPlot();
//        plot.setView(0.0, 0.0, 1.0, 1.0);
        dialPlot.setDataset(0, vmUsageDataset);
        dialPlot.setDataset(1, callPerHourDataset);

        StandardDialFrame dialFrame = new StandardDialFrame();
        dialFrame.setBackgroundPaint(Color.lightGray); // outerring
        dialFrame.setForegroundPaint(Color.darkGray); // inside of the outer ring
        dialFrame.setStroke(new BasicStroke(1.0f));
        dialPlot.setDialFrame(dialFrame);

        GradientPaint gp = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 170));
        DialBackground db = new DialBackground(gp); db.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
        dialPlot.setBackground(db);

        //CPU
        vmUsageScale = new StandardDialScale(0, vmUsageScaleRange, -120, -300, 10, 10);
        vmUsageScale.setMajorTickPaint(Color.red);
        vmUsageScale.setTickRadius(0.50);
        vmUsageScale.setTickLabelOffset(0.15);
        vmUsageScale.setTickLabelFont(new Font("STHeiti", Font.PLAIN, 11));
        dialPlot.addScale(0, vmUsageScale);
        dialPlot.mapDatasetToScale(0, 0);

        // Calls / Hour
        callsPerHourScale = new StandardDialScale(0, callsPerHourScaleRange, -120, -300, 10, 10);
        callsPerHourScale.setMajorTickIncrement(callsPerHourScaleRange / 10);
        callsPerHourScale.setTickLabelPaint(Color.BLACK);
        callsPerHourScale.setMajorTickPaint(Color.BLUE);
        callsPerHourScale.setMajorTickLength(0.02);
        callsPerHourScale.setMinorTickCount(10);
        callsPerHourScale.setTickRadius(0.88);
        callsPerHourScale.setTickLabelOffset(0.15);
        callsPerHourScale.setTickLabelFont(new Font("STHeiti", Font.PLAIN, 14));
        dialPlot.addScale(1, callsPerHourScale);
        dialPlot.mapDatasetToScale(1, 1);

        // CPU
        DialValueIndicator dialValueIndicator2 = new DialValueIndicator(0);
        dialValueIndicator2.setFont(new Font("STHeiti", Font.PLAIN, 10));
        dialValueIndicator2.setPaint(Color.BLACK);
        dialValueIndicator2.setOutlinePaint(Color.red);
        dialValueIndicator2.setRadius(0.60);
        dialValueIndicator2.setAngle(-77.0);
        dialPlot.addLayer(dialValueIndicator2);

        // Calls / Hour
        DialValueIndicator dialValueIndicator = new DialValueIndicator(1);
        dialValueIndicator.setFont(new Font("STHeiti", Font.PLAIN, 10));
        dialValueIndicator.setPaint(Color.BLACK);
        dialValueIndicator.setOutlinePaint(Color.black);
        dialValueIndicator.setRadius(0.60);
        dialValueIndicator.setAngle(-103.0);
        dialPlot.addLayer(dialValueIndicator);

        addColorScale(Math.round(vmUsageDecelerationThreasholdParam / scaleFactorBetweenBothScalers), vmUsageScaleRange,      0,0,       0,0,   0.49, 0.50); // Red Orange Green Values unfortunately relate to the callsperhour scale (scaleFactorBetweenBothScalers converts from one scale to the other)
//        DialTextAnnotation annotation1 = new DialTextAnnotation("Calls per Hour"); annotation1.setFont(new Font("Courier", Font.PLAIN, 18)); annotation1.setRadius(0.71); plot.addLayer(annotation1);
        DialTextAnnotation annotation2 = new DialTextAnnotation("X100"); annotation2.setFont(new Font("Courier", Font.PLAIN, 18)); annotation2.setRadius(0.71); dialPlot.addLayer(annotation2);

        // Needles
        DialPointer vmUsageNeedle       = new DialPointer.Pin(0); vmUsageNeedle.setRadius(0.55); dialPlot.addLayer(vmUsageNeedle);
        DialPointer callsPerHourNeedle  = new DialPointer.Pointer(1); dialPlot.addLayer(callsPerHourNeedle);
        DialCap cap = new DialCap(); cap.setRadius(0.10); dialPlot.setCap(cap);

        chart1 = new JFreeChart(dialPlot);
//        chart1.setTitle("Dial Demo 2");
        chartPanel = new ChartPanel(chart1);
        chartPanel.setPreferredSize(new Dimension(150, 150));
        Color windowGrey = new Color(216,216,222);
        chart1.setBackgroundPaint(windowGrey);
    }

    /**
     *
     * @param startValue
     * @param endValue
     * @param majorTickIncrement
     */
    public void setCallPerHourScale(double startValue, double endValue, double majorTickIncrement)
    {
        callsPerHourScale.setLowerBound(startValue);
        callsPerHourScale.setUpperBound(endValue);
        callsPerHourScale.setMajorTickIncrement(majorTickIncrement);
    }

    /**
     *
     * @param redStart
     * @param redEnd
     * @param orangeStart
     * @param orangeEnd
     * @param greenStart
     * @param greenEnd
     * @param innerRadiusParam
     * @param outerRadiusParam
     */
    public void addColorScale( double redStart, double redEnd, double orangeStart, double orangeEnd, double greenStart, double greenEnd,double innerRadiusParam,double outerRadiusParam)
    {
//        System.out.println("");
//        System.out.println("rangeRed = new StandardDialRange(" + redStart + ", " + redEnd + ", Color.red);");
//        System.out.println("rangeOrange = new StandardDialRange(" + orangeStart + ", " + orangeEnd + ", Color.orange);");
//        System.out.println("rangeGreen = new StandardDialRange(" + greenStart + ", " + greenEnd + ", Color.green);");
//        System.out.println("");

        standardDialRangeRed = new StandardDialRange(redStart, redEnd, Color.red);
        standardDialRangeRed.setInnerRadius(innerRadiusParam);
        standardDialRangeRed.setOuterRadius(outerRadiusParam);

        standardDialRangeOrange = new StandardDialRange(orangeStart, orangeEnd, Color.orange);
        standardDialRangeOrange.setInnerRadius(innerRadiusParam);
        standardDialRangeOrange.setOuterRadius(outerRadiusParam);

        standardDialRangeGreen = new StandardDialRange(greenStart, greenEnd, Color.green);
        standardDialRangeGreen.setInnerRadius(innerRadiusParam);
        standardDialRangeGreen.setOuterRadius(outerRadiusParam);

        dialPlot.addLayer(standardDialRangeRed);
        dialPlot.addLayer(standardDialRangeOrange);
        dialPlot.addLayer(standardDialRangeGreen);
    }

    /**
     *
     * @param redStart
     * @param redEnd
     * @param orangeStart
     * @param orangeEnd
     * @param greenStart
     * @param greenEnd
     */
    public void setColorScale( double redStart, double redEnd, double orangeStart, double orangeEnd, double greenStart, double greenEnd)
    {
        double innerRadius = 0;
        double outerRadius = 0.5;

        standardDialRangeRed.setBounds(redStart, redEnd);
        standardDialRangeOrange.setBounds(orangeStart, orangeEnd);
        standardDialRangeGreen.setBounds(greenStart, greenEnd);

//        if (redStart    < rangeRed.getUpperBound())     { rangeRed.setLowerBound(redStart); rangeRed.setUpperBound(redEnd); }               else { rangeRed.setUpperBound(redEnd); rangeRed.setLowerBound(redStart); }
//        if (orangeStart < rangeOrange.getUpperBound())  { rangeOrange.setLowerBound(orangeStart); rangeOrange.setUpperBound(orangeEnd); }   else { rangeOrange.setUpperBound(orangeEnd); rangeOrange.setLowerBound(orangeStart); }
//        if (greenStart  < rangeGreen.getUpperBound())   { rangeGreen.setLowerBound(greenStart); rangeGreen.setUpperBound(greenEnd); }       else { rangeGreen.setUpperBound(greenEnd); rangeGreen.setLowerBound(greenStart); }

        standardDialRangeGreen.setInnerRadius(innerRadius); standardDialRangeRed.setOuterRadius(outerRadius);
        standardDialRangeOrange.setInnerRadius(innerRadius); standardDialRangeRed.setOuterRadius(outerRadius);
        standardDialRangeRed.setInnerRadius(innerRadius); standardDialRangeRed.setOuterRadius(outerRadius);
    }

    /**
     *
     */
    public void removeColorScale()
    {
        dialPlot.removeLayer(standardDialRangeRed);
        dialPlot.removeLayer(standardDialRangeOrange);
        dialPlot.removeLayer(standardDialRangeGreen);
    }

    /**
     *
     * @return
     */
    public Number   getCallPerHourNeedle()                          { return callPerHourDataset.getValue(); }

    /**
     *
     * @return
     */
    public Number   getVMUsageNeedle()                        { return vmUsageDataset.getValue(); }

    /**
     *
     * @param valueParam
     */
    public void     setCallPerHourNeedle(Number valueParam) { callPerHourDataset.setValue(valueParam); }

    /**
     *
     * @param valueParam
     */
    public void     setVMUsageNeedle(Number valueParam)     { vmUsageDataset.setValue(valueParam); }

    @Override
    public void stateChanged(ChangeEvent e) {
//        dataset1.setValue(new Integer(slider1.getValue()));
//        dataset2.setValue(new Integer(slider2.getValue()));
    }
}