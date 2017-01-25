/*
 *  Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Oracle designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Oracle in the LICENSE file that accompanied this code.
 * 
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 * 
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 *  Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 *  or visit www.oracle.com if you need additional information or have any
 *  questions.
 */

package com.sun.tools.visualvm.modules.tracer.impl.timeline;

import com.sun.tools.visualvm.modules.tracer.impl.swing.EnhancedLabelRenderer;
import com.sun.tools.visualvm.modules.tracer.impl.swing.HeaderLabel;
import com.sun.tools.visualvm.modules.tracer.impl.swing.HeaderPanel;
import com.sun.tools.visualvm.modules.tracer.impl.swing.LegendFont;
import com.sun.tools.visualvm.modules.tracer.impl.swing.TimelineMarksPainter;
import com.sun.tools.visualvm.uisupport.UISupport;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.netbeans.lib.profiler.charts.ChartConfigurationListener;
import org.netbeans.lib.profiler.charts.ChartContext;
import org.netbeans.lib.profiler.charts.Timeline;
import org.netbeans.lib.profiler.charts.axis.AxisComponent;
import org.netbeans.lib.profiler.charts.axis.AxisMark;
import org.netbeans.lib.profiler.charts.axis.TimeAxisUtils;
import org.netbeans.lib.profiler.charts.axis.TimelineMarksComputer;
import org.netbeans.lib.profiler.charts.swing.Utils;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYChartContext;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItemsModel;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Jiri Sedlacek
 */
final class TimelineAxis extends JPanel {

    private final HeaderRenderer painter;
    private final AxisComponent axis;
    private final MarksComponent marks;

    private int preferredHeight;
    
    private int pointerX;


    TimelineAxis(final TimelineChart chart, TimelineSupport support) {

        super(null);

        painter = new HeaderRenderer();
        
        Timeline timeline = ((SynchronousXYItemsModel)chart.getItemsModel()).getTimeline();
        axis = new Axis(chart, new MarksComputer(timeline, chart.getChartContext()));

        marks = new MarksComponent(support);

        preferredHeight = HeaderLabel.DEFAULT_HEIGHT;

        add(marks);
        add(axis);
        add(painter);

        chart.addConfigurationListener(new ChartConfigurationListener.Adapter() {

            private final Runnable updater = new Runnable() {
                public void run() {
                    if (!axis.isVisible()) {
                        marks.setupTicks();
                        marks.refreshHoverMark(pointerX);
                        marks.repaint();
                    }
                }
            };

            public void contentsUpdated(long offsetX, long offsetY,
                                        double scaleX, double scaleY,
                                        long lastOffsetX, long lastOffsetY,
                                        double lastScaleX, double lastScaleY,
                                        int shiftX, int shiftY) {
                
                if (lastOffsetX != offsetX || lastScaleX != scaleX)
                    marks.refreshMarks();
                SwingUtilities.invokeLater(updater);
            }

        });

        support.addSelectionListener(new TimelineSupport.SelectionListener() {

            public void rowSelectionChanged(boolean rowsSelected) {}

            public void timeSelectionChanged(boolean timestampsSelected, boolean justHovering) {
                marks.refreshMarks();
                marks.repaint();
            }
        });

        marks.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                pointerX = e.getX();
                marks.setupTicks();
                marks.refreshHoverMark(pointerX);
                axis.setVisible(false);
            }

            public void mouseExited(MouseEvent e) {
                axis.setVisible(true);
                marks.clearTicks();
                marks.refreshHoverMark(-10);
            }

            public void mouseClicked(MouseEvent e) {
                marks.handleAction();
                marks.repaint();
            }
        });

        marks.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                pointerX = e.getX();
            }

            public void mouseMoved(MouseEvent e) {
                pointerX = e.getX();
                if (!axis.isVisible()) marks.refreshHoverMark(pointerX);
            }
        });

        marks.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                e.setSource(chart);
                chart.processMouseWheelEvent(e);
            }
        });

    }


    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }


    public void validate() {}
    public void doLayout() {}

    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        painter.reshape(0, 0, width, height);
        axis.reshape(1, 1, width - 2, height - 2);
        marks.reshape(0, 0, width, height);
    }


    private static class HeaderRenderer extends HeaderPanel {

        private Image offscreen;

        public void reshape(int x, int y, int width, int height) {
            if (getWidth() != width || getHeight() != height) offscreen = null;
            super.reshape(x, y, width, height);
        }

        long total = 0;
        int count = 0;

        public void validate() {}

        public void paint(Graphics g) {
            if (offscreen == null) {
                offscreen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                super.paint(offscreen.getGraphics());
            }
            g.drawImage(offscreen, 0, 0, null);
        }

    }


    private static class MarksComponent extends JComponent {

        private static final Image MARK = ImageUtilities.loadImage(
                "com/sun/tools/visualvm/modules/tracer/impl/resources/mark.png");  // NOI18N
        private static final Image MARK_HIGHL = ImageUtilities.loadImage(
                "com/sun/tools/visualvm/modules/tracer/impl/resources/markHighl.png");  // NOI18N
        private static final int MARK_EXTENT = MARK.getWidth(null) / 2;
        private static final int MARK_HEIGHT = MARK.getHeight(null);

        private final TimelineSupport support;
        private final Timeline timeline;
        private final SynchronousXYChartContext context;

        private final EnhancedLabelRenderer timeRenderer;
        private final Format timeFormat;

        private int[] ticks;
        private int hoverIndex = -1;
        private int hoverX = -10;
        private boolean wasSelected;
        private long hoverTime;

        private final List<Integer> selections = new ArrayList();
        private final int markExtent = 2;


        MarksComponent(TimelineSupport support) {
            this.support = support;

            TimelineChart chart = support.getChart();
            SynchronousXYItemsModel model = (SynchronousXYItemsModel)chart.getItemsModel();
            context = (SynchronousXYChartContext)chart.getChartContext();
            timeline = model.getTimeline();

            timeRenderer = new EnhancedLabelRenderer();
            if (UISupport.isAquaLookAndFeel()) {
                Font f = new LegendFont();
                timeRenderer.setFont(f.deriveFont(f.getSize2D() - 1));
                timeRenderer.setMargin(new Insets(0, 2, 0, 2));
            } else {
                timeRenderer.setFont(new LegendFont());
                timeRenderer.setMargin(new Insets(1, 2, 1, 2));
            }
            timeRenderer.setBackground(Color.WHITE);
            timeRenderer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            timeFormat = new SimpleDateFormat(TimeAxisUtils.getFormatString(1, 1, 1));

            setToolTipText("Click a mark to select or unselect it");

            setOpaque(false);
        }
        
        public void validate() {}
        public void doLayout() {}


        void refreshMarks() {
            Set<Integer> selectedIndexes = support.getSelectedTimestamps();
            if (selectedIndexes.size() == 0 && selections.isEmpty()) return;

            selections.clear();

            for (int selectedIndex : selectedIndexes) {
                long time = timeline.getTimestamp(selectedIndex);
                int x = Utils.checkedInt(context.getViewX(time));
                if (x > -markExtent && x < getWidth() + markExtent)
                    selections.add(x + 1);
            }
        }

        void setupTicks() {
            int[][] idxs = support.getPointsComputer().getVisible(getBounds(),
                           timeline.getTimestampsCount(), context, 1, 0);
            ticks = idxs == null ? null : idxs[0];
            if (ticks != null) for (int i = 0; i < idxs[1][0]; i++)
                    ticks[i] = Utils.checkedInt(context.getViewX(timeline.
                                                getTimestamp(ticks[i]))) + 1;
        }

        void refreshHoverMark(int pointerX) {
            int lastHoverIndex = hoverIndex;

            hoverIndex = context.getNearestTimestampIndex(pointerX - 1, 0);
            hoverX = hoverIndex == -1 ? -10 : Utils.checkedInt(context.getViewX(
                                        timeline.getTimestamp(hoverIndex))) + 1;
            if (Math.abs(hoverX - pointerX + 1) > MARK_EXTENT) {
                hoverIndex = -1;
                hoverX = -10;
            }

            if (lastHoverIndex != hoverIndex) {
                if (!wasSelected) support.unselectTimestamp(lastHoverIndex);
                wasSelected = hoverIndex != -1 && support.isTimestampSelected(hoverIndex);
                support.setTimestampHovering(hoverIndex != -1, wasSelected);
                if (hoverIndex != -1) {
                    support.selectTimestamp(hoverIndex, false);
                    hoverTime = timeline.getTimestamp(hoverIndex);
                    if (wasSelected) repaint();
                } else {
                    if (!wasSelected) repaint();
                }
                if (hoverIndex == -1) setCursor(Cursor.getDefaultCursor());
                else setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

        }

        void clearTicks() {
            ticks = null;
        }

        void handleAction() {
            wasSelected = !wasSelected;
            support.setTimestampHovering(hoverIndex != -1, wasSelected);
        }


        public void paint(Graphics g) {
            int top = getHeight() / 2 - 1;
            int bottom = top + 2;

            if (ticks != null)
                for (int i = 0; i < ticks.length; i++)
                    g.drawLine(ticks[i], top, ticks[i], bottom);

            if (selections == null || selections.isEmpty()) return;

            int y = getHeight() - 5 - MARK_HEIGHT;
            for (int x : selections)
                g.drawImage((x == hoverX && wasSelected) ? MARK_HIGHL :
                            MARK, x - MARK_EXTENT + 1, y, null);

            if (hoverIndex != -1) {
                timeRenderer.setText(timeFormat.format(hoverTime));
                Dimension timeSize = timeRenderer.getPreferredSize();
                int timeWidth = timeSize.width;
                int extraWidth = MARK_EXTENT + TimelineTooltipOverlay.TOOLTIP_OFFSET;
                int timeX = hoverX + extraWidth;
                if (timeX > getWidth() - timeWidth - TimelineTooltipOverlay.TOOLTIP_MARGIN)
                    timeX = hoverX - timeWidth - extraWidth;
                timeRenderer.setLocation(timeX, top - timeSize.height / 2);
                timeRenderer.paint(g);
            }
        }

    }


    private static class MarksComputer extends TimelineMarksComputer {

        MarksComputer(Timeline timeline, ChartContext context) {
            super(timeline, context, SwingConstants.HORIZONTAL);
        }

        void refresh() {
            refreshConfiguration();
        }

    }


    private static class Axis extends AxisComponent {

        private static final int LAF_OFFSET = resolveOffset();

        private final Paint meshPaint = Utils.checkedColor(new Color(180, 180, 180, 50));
        private final Stroke meshStroke = new BasicStroke(1);

        private final TimelineChart chart;
        private final MarksComputer marksComputer;
        private final TimelineMarksPainter marksPainter;
        private boolean hadTicks = false;

        private final Runnable repainter;


        Axis(TimelineChart chart, MarksComputer marksComputer) {

            super(chart, marksComputer, null, SwingConstants.NORTH,
                  AxisComponent.MESH_FOREGROUND);

            this.chart = chart;
            this.marksComputer = marksComputer;
            this.marksPainter = new TimelineMarksPainter();

            repainter = new Runnable() {
                public void run() { Axis.this.chart.invalidateRepaint(); }
            };
        }


        public void validate() {}
        public void doLayout() {}


        public void paint(Graphics g) {
            Rectangle clip = g.getClipBounds();
            if (clip == null) clip = new Rectangle(0, 0, getWidth(), getHeight());

            marksComputer.refresh();

            paintHorizontalAxis(g, clip);
        }

        protected void paintHorizontalMesh(Graphics2D g, Rectangle clip, Rectangle chartMask) {
            Iterator<AxisMark> marks =
                    marksComputer.marksIterator(chartMask.x, chartMask.x + chartMask.width);

            boolean hasTicks = false;

            while (marks.hasNext()) {
                hasTicks = true;

                AxisMark mark = marks.next();
                int x = mark.getPosition();

                g.setPaint(meshPaint);
                g.setStroke(meshStroke);
                g.drawLine(x, chartMask.y, x, chartMask.y + chartMask.height);
            }

            if (!hadTicks && hasTicks) SwingUtilities.invokeLater(repainter);
            hadTicks = hasTicks;
        }

        protected void paintHorizontalAxis(Graphics g, Rectangle clip) {
            int viewStart = -1; // -1: extra 1px for axis
            int viewEnd = viewStart + chart.getWidth() + 2; // +2 extra 1px + 1px for axis

            Iterator<AxisMark> marks = marksComputer.marksIterator(viewStart, viewEnd);

            int lZeroOffset = chart.isRightBased() ? 0 : 1;
            int rZeroOffset = chart.isRightBased() ? 1 : 0;

            while (marks.hasNext()) {
                AxisMark mark = marks.next();

                int x = mark.getPosition() - 1;

                if (x < -1 - lZeroOffset ||
                    x >= -1 + chart.getWidth() + rZeroOffset) continue;

                TimelineMarksPainter painter =
                        (TimelineMarksPainter)marksPainter.getPainter(mark);
                Dimension painterSize = painter.getPreferredSize();
                int markOffsetX = painterSize.width / 2;

                if (x + markOffsetX < clip.x ||
                    x - markOffsetX >= clip.x + clip.width) continue;

                g.setColor(getForeground());
                g.drawLine(x, 1, x, 3);
                
                int markOffsetY = (getHeight() - painterSize.height) / 2 + LAF_OFFSET;
                painter.setLocation(x - markOffsetX, markOffsetY);
                painter.paint(g);
            }
        }

        private static int resolveOffset() {
            if (UISupport.isWindowsLookAndFeel() ||
                UISupport.isMetalLookAndFeel() ||
                UISupport.isGTKLookAndFeel()) return 1;
            return 0;
        }

    }

}
