import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;

import javax.swing.JPanel;
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

/**
 *
 * @author ron
 */
public class DashboardMeter implements  ChangeListener
{

    private ChartPanel chartPanel;

    /**
     *
     */
    protected JFreeChart chart;
    private StandardDialScale standardDialScale;
    private DialPlot dialPlot;
    private StandardDialRange standardDialRangeRed;
    private StandardDialRange standardDialRangeOrange;
    private StandardDialRange standardDialRangeGreen;

    private DefaultValueDataset dataset;
    private DefaultValueDataset vmUsageDataset;

    private int vmUsageScaleRange = 100;

    /**
     *
     * @param title
     * @param annotation
     * @param startValue
     * @param endValue
     * @param majorTickIncrement
     * @param systemLoad
     */
    public DashboardMeter(String title, String annotation, double startValue, double endValue, double majorTickIncrement, boolean systemLoad)
    {
        dataset = new DefaultValueDataset(0);
        if (systemLoad) { vmUsageDataset = new DefaultValueDataset(50.0); }

        dialPlot = new DialPlot();
//        plot.setView(0, 0, 1, 1);
        dialPlot.setDataset(this .dataset);
        if (systemLoad) { dialPlot.setDataset(1, this .vmUsageDataset); }


        StandardDialFrame dialFrame = new StandardDialFrame();
        dialFrame.setRadius(0.97);
        dialFrame.setForegroundPaint(Color.darkGray);
        dialFrame.setStroke(new BasicStroke(1.0f));
        dialPlot.setDialFrame(dialFrame);

        GradientPaint gp = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 170));
        DialBackground sdb = new DialBackground(gp);
        sdb.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
        dialPlot.addLayer(sdb);

        DialValueIndicator dialValueIndicator = new DialValueIndicator(0);
        dialValueIndicator.setFont(new Font("STHeiti", Font.PLAIN, 16));
        dialValueIndicator.setOutlinePaint(Color.BLACK);
        dialValueIndicator.setRadius(0.73);
        dialValueIndicator.setAngle(-90.0);
        dialPlot.addLayer(dialValueIndicator);

        standardDialScale = new StandardDialScale(startValue, endValue, -120, -300, 10, 10);
        standardDialScale.setTickRadius(0.88);
        standardDialScale.setTickLabelOffset(0.20);
        standardDialScale.setMajorTickIncrement((double)majorTickIncrement);
        standardDialScale.setTickLabelPaint(Color.BLACK);
        standardDialScale.setMajorTickPaint(Color.BLUE);
        standardDialScale.setTickLabelFont(new Font("STHeiti", Font.PLAIN, 16));
        dialPlot.addScale(0, standardDialScale);

        if (systemLoad)
        {
            //CPU
            StandardDialScale vmUsageScale = new StandardDialScale(0, vmUsageScaleRange, -120, -300, 10, 10);
            vmUsageScale.setMajorTickPaint(Color.red);
            vmUsageScale.setTickRadius(0.50);
            vmUsageScale.setTickLabelOffset(0.15);
            vmUsageScale.setTickLabelFont(new Font("STHeiti", Font.PLAIN, 11));
            dialPlot.addScale(1, vmUsageScale);
            dialPlot.mapDatasetToScale(1, 1);
        }

        addColorScale(startValue,(endValue * 0.333),(endValue * 0.333),(endValue * 0.666),(endValue * 0.666),endValue);

        DialTextAnnotation annotation1 = new DialTextAnnotation(annotation); annotation1.setFont(new Font("Courier", Font.PLAIN, 18)); annotation1.setRadius(0.40); dialPlot.addLayer(annotation1);

//        DialPointer needle = new DialPointer.Pin(0); needle.setRadius(0.80); plot.addLayer(needle);
        DialPointer needle = new DialPointer.Pointer(0); needle.setRadius(0.80); dialPlot.addLayer(needle);
        DialPointer vmUsageNeedle    = new DialPointer.Pin(1); vmUsageNeedle.setRadius(0.55); dialPlot.addLayer(vmUsageNeedle);

        DialCap cap = new DialCap(); cap.setRadius(0.1); dialPlot.setCap(cap);

        chart = new JFreeChart(dialPlot);
//        chart.setTitle(title);
        chartPanel = new ChartPanel(chart);
	chartPanel.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N

        chartPanel.setPreferredSize(new Dimension(100, 115));
        JPanel content = new JPanel(new BorderLayout());
        content.add(chartPanel);
    }

    /**
     *
     * @return
     */
    public Number  getValue()                           { return dataset.getValue(); }

    /**
     *
     * @return
     */
    public Number  getVMUsageValue()                    { return vmUsageDataset.getValue(); }

    /**
     *
     * @param valueParam
     */
    public void    setValue(Number valueParam)          { dataset.setValue(valueParam); }

    /**
     *
     * @param valueParam
     */
    public void    setVMUsageValue(Number valueParam)   { vmUsageDataset.setValue(valueParam); }

    /**
     *
     * @param startValue
     * @param endValue
     * @param majorTickIncrement
     */
    public void setScale(double startValue, double endValue, double majorTickIncrement)
    {
        standardDialScale.setLowerBound(startValue);
        standardDialScale.setUpperBound(endValue);
        standardDialScale.setMajorTickIncrement(majorTickIncrement);
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
    public void addColorScale( double redStart, double redEnd, double orangeStart, double orangeEnd, double greenStart, double greenEnd)
    {
//        System.out.println("");
//        System.out.println("rangeRed = new StandardDialRange(" + redStart + ", " + redEnd + ", Color.red);");
//        System.out.println("rangeOrange = new StandardDialRange(" + orangeStart + ", " + orangeEnd + ", Color.orange);");
//        System.out.println("rangeGreen = new StandardDialRange(" + greenStart + ", " + greenEnd + ", Color.green);");
//        System.out.println("");

        double innerRadius = 0;
        double outerRadius = 0.5;
        standardDialRangeRed = new StandardDialRange(redStart, redEnd, Color.red);
        standardDialRangeRed.setInnerRadius(innerRadius);
        standardDialRangeRed.setOuterRadius(outerRadius);

        standardDialRangeOrange = new StandardDialRange(orangeStart, orangeEnd, Color.orange);
        standardDialRangeOrange.setInnerRadius(innerRadius);
        standardDialRangeOrange.setOuterRadius(outerRadius);

        standardDialRangeGreen = new StandardDialRange(greenStart, greenEnd, Color.green);
        standardDialRangeGreen.setInnerRadius(innerRadius);
        standardDialRangeGreen.setOuterRadius(outerRadius);

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

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
