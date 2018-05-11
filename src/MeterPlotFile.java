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

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author ron
 */
public class MeterPlotFile extends ApplicationFrame {

    private static DefaultValueDataset dataset;

    /**
     *
     * @param title
     */
    public MeterPlotFile(String title) {
        super(title);
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private static JFreeChart createChart(DefaultValueDataset dataset) {
        MeterPlot plot = new MeterPlot(dataset);
        JFreeChart chart = new JFreeChart("Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        return chart;
    }

    /**
     *
     * @return
     */
    public static JPanel createDemoPanel() {
        dataset = new DefaultValueDataset(50.0);
        JFreeChart chart = createChart(dataset);
        JPanel panel = new JPanel(new BorderLayout());
        JSlider slider = new JSlider(0, 100, 50);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider s = (JSlider) e.getSource();
                dataset.setValue(new Integer(s.getValue()));
            }
        });
        panel.add(new ChartPanel(chart));
        panel.add(BorderLayout.SOUTH, slider);
        return panel;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        MeterPlotFile demo = new MeterPlotFile("Meter Plot");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}