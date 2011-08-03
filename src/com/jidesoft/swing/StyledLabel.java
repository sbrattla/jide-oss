/*
 * @(#)StyledLabel.java 9/6/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * <code>StyledLabel</code> is a special JLabel which can display text in different styles. It is a component between
 * JLabel and JTextPane. JLabel is simple, fast but has limited features. For example, you can't use different color to
 * draw the text. You may argue JLabel can use HTML tag to display text in different colors. However there are two main
 * reasons to use StyledLabel. First of all, StyledLabel is very fast and almost as fast as JLabel with plain text. HTML
 * JLabel is very slow. You can see StyledLabelPerformanceDemo.java in examples\B15. StyledLabel folder to see a
 * performace test of HTML JLabel and StyledLabel. HTML JLabel is also buggy. See bug report at <a
 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4373575">http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4373575</a>.
 * Sun claimed it is fixed but it is not as another user pointed it out at the end. If you run the test case provided by
 * original submitter, you will immediately notice the tree node disappeared when you click on the tree nodes. This bug
 * is actually one of the main reasons we decided to create StyledLabel. JTextPane is powerful and can display text in
 * different color. But in the cases like cell renderer, JTextPane is obviously an overkill.
 * <p/>
 * StyledLabel sits between JLabel and JTextPane and provides a very simple and fast way to display text in different
 * color and style. It can also support decorations using all kinds of line styles.
 * <p/>
 * All the methods on JLabel still work as before. The methods added in StyledLabel are several methods for StyleRange,
 * such as {@link #addStyleRange(StyleRange)}, {@link #setStyleRanges(StyleRange[])}, {@link
 * #clearStyleRange(StyleRange)}, and {@link #clearStyleRanges()}.
 * <p/>
 * This is one thing about StyleRange that you should be aware of, which could be considered as a future enhancement
 * item, is that the StyleRanges can't overlap with each other. For example, if you defined a StyleRange that covers
 * from index 0 to index 3, you can't define any other StyleRange that overlaps with the first one. If you do so, the
 * second StyleRange will be ignored.
 * <p/>
 * We borrowed some ideas from SWT's StyledText when we designed StyledLabel, especially StyleRange concept. Saying
 * that, the features of the two components are not exactly the same since the purpose of the two components are quite
 * different.
 */
public class StyledLabel extends JLabel {

    private static final String uiClassID = "StyledLabelUI";

    /**
     * The list of StyleRanges.
     */
    private List<StyleRange> _styleRanges;
    private boolean _lineWrap;
    private int _rows;
    private int _columns;
    private int _maxRows;
    private int _maxColumns;
    private int _minRows;
    private int _minColumns;
    private int _rowGap;

    private boolean _ignoreColorSettings;

    public static final String PROPERTY_STYLE_RANGE = "styleRange";
    public static final String PROPERTY_IGNORE_COLOR_SETTINGS = "ignoreColorSettings";

    public StyledLabel() {
        setMaximumSize(null);
    }

    public StyledLabel(Icon image) {
        super(image);
        setMaximumSize(null);
    }

    public StyledLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        setMaximumSize(null);
    }

    public StyledLabel(String text) {
        super(text);
        setMaximumSize(null);
    }

    public StyledLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        setMaximumSize(null);
    }

    public StyledLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        setMaximumSize(null);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }


    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "StyledLabelUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Adds a StyleRange into this <code>StyledLabel</code>.
     *
     * @param styleRange the new StyleRange.
     */
    public synchronized void addStyleRange(StyleRange styleRange) {
        if (styleRange == null) {
            throw new IllegalArgumentException("StyleRange cannot be null.");
        }
        List<StyleRange> ranges = internalGetStyleRanges();
        for (int i = ranges.size() - 1; i >= 0; i--) {
            StyleRange range = ranges.get(i);
            if (range.getStart() == styleRange.getStart() && range.getLength() == styleRange.getLength()) {
                ranges.remove(i);
            }
        }
        internalGetStyleRanges().add(styleRange);
        firePropertyChange(PROPERTY_STYLE_RANGE, null, styleRange);
    }

    /**
     * Clears all the old StyleRanges and adds a list of StyleRanges into this <code>StyledLabel</code>.
     *
     * @param styleRanges set the StyleRanges.
     */
    public synchronized void setStyleRanges(StyleRange[] styleRanges) {
        internalGetStyleRanges().clear();
        addStyleRanges(styleRanges);
    }

    /**
     * Adds a list of StyleRanges into this <code>StyledLabel</code>.
     *
     * @param styleRanges an array of StyleRanges.
     */
    public synchronized void addStyleRanges(StyleRange[] styleRanges) {
        if (styleRanges != null) {
            for (StyleRange styleRange : styleRanges) {
                internalGetStyleRanges().add(styleRange);
            }
            firePropertyChange(PROPERTY_STYLE_RANGE, null, styleRanges);
        }
        else {
            firePropertyChange(PROPERTY_STYLE_RANGE, null, null);
        }
    }

    /**
     * Gets the array of StyledText.
     *
     * @return the array of StyledText.
     */
    public synchronized StyleRange[] getStyleRanges() {
        List<StyleRange> list = internalGetStyleRanges();
        return list.toArray(new StyleRange[list.size()]);
    }

    private List<StyleRange> internalGetStyleRanges() {
        if (_styleRanges == null) {
            _styleRanges = new Vector<StyleRange>();
        }
        return _styleRanges;
    }

    /**
     * Removes the StyleRange.
     *
     * @param styleRange the StyleRange to be removed.
     */
    public synchronized void clearStyleRange(StyleRange styleRange) {
        if (internalGetStyleRanges().remove(styleRange)) {
            firePropertyChange(PROPERTY_STYLE_RANGE, styleRange, null);
        }
    }

    /**
     * Clears all the StyleRanges.
     */
    public synchronized void clearStyleRanges() {
        internalGetStyleRanges().clear();
        firePropertyChange(PROPERTY_STYLE_RANGE, null, null);
    }

    /**
     * StyleRange could define color for the text and lines. However when StyledLabel is used in cell renderer, the
     * color could be conflict with selection color. So usually when it is used as cell renderer, the color defined in
     * StyleRange should be ignored when cell is selected. If so, the foreground is used to paint all text and lines.
     *
     * @return true if the color defined by StyleRange should be ignored.
     */
    public boolean isIgnoreColorSettings() {
        return _ignoreColorSettings;
    }

    /**
     * Sets if the color defined by StyleRange should be ignored. This flag is used when StyledLabel is used as a
     * selected cell renderer. Since the selection usually has it own unique selection background and foreground, the
     * color setting set on this StyledLabel could be unreadable on the selection background, it'd better if we don't
     * use any color settings in this case.
     *
     * @param ignoreColorSettings true or false.
     */
    public void setIgnoreColorSettings(boolean ignoreColorSettings) {
        boolean old = _ignoreColorSettings;
        if (old != ignoreColorSettings) {
            _ignoreColorSettings = ignoreColorSettings;
            firePropertyChange(PROPERTY_IGNORE_COLOR_SETTINGS, old, ignoreColorSettings);
        }
    }

    @Override
    public void setMaximumSize(Dimension maximumSize) {
        if (maximumSize == null) {
            super.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
        else {
            super.setMaximumSize(maximumSize);
        }
    }

    /**
     * Gets the flag indicating if the line should be automatically wrapped when the column width is limited.
     *
     * @return true if line wrap is needed. Otherwise false.
     * @since 3.1.2
     */
    public boolean isLineWrap() {
        return _lineWrap;
    }

    /**
     * Sets the flag indicating if the line should be automatically wrapped when the column width is limited.
     *
     * @param lineWrap the flag
     * @since 3.1.2
     */
    public void setLineWrap(boolean lineWrap) {
        _lineWrap = lineWrap;
    }

    /**
     * Gets the default row count to wrap the {@link StyledLabel}.
     *
     * @return the row count.
     * @see #setRows(int)
     */
    public int getRows() {
        return _rows;
    }

    /**
     * Sets the default row count to wrap the {@link StyledLabel}.
     * <p/>
     * By default, the value is 0. Any non-positive value is deemed as not configured.
     * <p/>
     * This has lower priority than {@link #setColumns(int)} and {@link #getMaximumSize()}.
     *
     * @param rows the row count
     */
    public void setRows(int rows) {
        _rows = rows;
    }

    /**
     * Gets the default character count to wrap the {@link StyledLabel}.
     *
     * @return the character count.
     * @see #setColumns(int)
     */
    public int getColumns() {
        return _columns;
    }

    /**
     * Sets the default character count to wrap the {@link StyledLabel}.
     * <p/>
     * By default, the value is 0. Any non-positive value is deemed as not configured.
     * <p/>
     * This has higher priority than {@link #setRows(int)}. However, if the width is wider than {@link #getMaximumSize()},
     * maximum size will be respected.
     *
     * @param columns the character count
     */
    public void setColumns(int columns) {
        _columns = columns;
    }

    /**
     * Gets the gap pixels between rows.
     *
     * @return the gap pixels.
     * @see #setRowGap(int)
     */
    public int getRowGap() {
        return _rowGap;
    }

    /**
     * Sets the gap pixels between rows.
     * <p/>
     * By default, the value is 0.
     *
     * @param rowGap the gap pixels.
     */
    public void setRowGap(int rowGap) {
        _rowGap = rowGap;
    }

    public int getMaxRows() {
        return _maxRows;
    }

    public void setMaxRows(int maxRows) {
        _maxRows = maxRows;
    }

    public int getMaxColumns() {
        return _maxColumns;
    }

    public void setMaxColumns(int maxColumns) {
        _maxColumns = maxColumns;
    }

    public int getMinRows() {
        return _minRows;
    }

    public void setMinRows(int minRows) {
        _minRows = minRows;
    }

    public int getMinColumns() {
        return _minColumns;
    }

    public void setMinColumns(int minColumns) {
        _minColumns = minColumns;
    }
}
