package org.karora.cooee.ng;

import org.karora.cooee.app.Color;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.Style;
import org.karora.cooee.app.event.ChangeListener;
import org.karora.cooee.ng.model.ColorSelectionModel;
import org.karora.cooee.ng.model.ColorSwatchModel;
import org.karora.cooee.ng.model.DefaultColorSelectionModel;
import org.karora.cooee.ng.model.WebSafeColorSwatchModel;
import org.karora.cooee.ng.util.ColorKit;

/**
 * ColorChooser allows the user to select a color from a color swatch model.
 */
public class ColorChooser extends AbleComponent {

	public static final Style DEFAULT_STYLE;

	public static final String PROPERTY_SELECTION_MODEL = "selectionModel";

	public static final String PROPERTY_SELECTION_TEXT = "selectionText";

	public static final String PROPERTY_SWATCH_HEIGHT = "swatchHeight";

	public static final String PROPERTY_SWATCH_MODEL = "swatchModel";

	public static final String PROPERTY_SWATCH_WIDTH = "swatchWidth";

	public static final String PROPERTY_SWATCHES_PER_ROW = "swatchesPerRow";
	static {
		MutableStyleEx style = new MutableStyleEx();

		style.setProperty(PROPERTY_BORDER,
				new BorderEx(ColorKit.clr("#317082")));
		style.setProperty(PROPERTY_INSETS, new org.karora.cooee.app.Insets(
				new ExtentEx(1)));

		style.setProperty(PROPERTY_SELECTION_TEXT, "Selected : ");

		style.setProperty(PROPERTY_SWATCHES_PER_ROW, 17);
		style.setProperty(PROPERTY_SWATCH_HEIGHT, new ExtentEx(10));
		style.setProperty(PROPERTY_SWATCH_WIDTH, new ExtentEx(10));
		style.setProperty(PROPERTY_SWATCH_MODEL,new WebSafeColorSwatchModel());		
		DEFAULT_STYLE = style;
	}

	private ChangeListener internalListener = new ChangeListener() {
		public void stateChanged(org.karora.cooee.app.event.ChangeEvent e) {
			firePropertyChange("selectedColor", null, getSelectedColor());
		};
	};

	/**
	 * Creates a ColorChooser
	 */
	public ColorChooser() {
		setSelectionModel(new DefaultColorSelectionModel());
	}

	/**
	 * A short-cut method for
	 * <code>getSelectionModel().getSelectedColor()</code>
	 */
	public Color getSelectedColor() {
		if (getSelectionModel() != null) {
			return getSelectionModel().getSelectedColor();
		}
		return null;
	}

	public ColorSelectionModel getSelectionModel() {
		return (ColorSelectionModel) getProperty(PROPERTY_SELECTION_MODEL);
	}

	public String getSelectionText() {
		return (String) getProperty(PROPERTY_SELECTION_TEXT);
	}

	/**
	 * @return the number for color swatches that will be displayed per row of
	 *         color data.
	 */
	public int getSwatchesPerRow() {
		return getProperty(PROPERTY_SWATCHES_PER_ROW, 17);
	}

	/**
	 * @return - the height of the color swatches
	 */
	public Extent getSwatchHeight() {
		return (Extent) getProperty(PROPERTY_SWATCH_HEIGHT);
	}

	public ColorSwatchModel getSwatchModel() {
		return (ColorSwatchModel) getProperty(PROPERTY_SWATCH_MODEL);
	}

	/**
	 * @return - the width of the color swatches
	 */
	public Extent getSwatchWidth() {
		return (Extent) getProperty(PROPERTY_SWATCH_WIDTH);
	}

	/**
	 * @see org.karora.cooee.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
		if ("click".equals(inputName)) {
			setSelectedColor(ColorKit.makeColor((String) inputValue));
		}
	}

	/**
	 * A short-cut method for
	 * <code>getSelectionModel().setSelectedColor()</code>
	 */
	public void setSelectedColor(Color newValue) {
		if (getSelectionModel() != null) {
			getSelectionModel().setSelectedColor(newValue);
		}
	}

	public void setSelectionModel(ColorSelectionModel newValue) {
		ColorSelectionModel oldValue = getSelectionModel();
		if (oldValue != null) {
			oldValue.removeListener(internalListener);
		}
		if (newValue != null) {
			newValue.addListener(internalListener);
		}
		setProperty(PROPERTY_SELECTION_MODEL, newValue);
	}

	public void setSelectionText(String newValue) {
		setProperty(PROPERTY_SELECTION_TEXT, newValue);
	}

	/**
	 * Sets the number for color swatches that will be displayed per row of
	 * color data.
	 * 
	 * @param newValue -
	 *            the number for color swatches that will be displayed per row
	 *            of color data.
	 */
	public void setSwatchesPerRow(int newValue) {
		setProperty(PROPERTY_SWATCHES_PER_ROW, newValue);
	}

	/**
	 * Sets the height of the color swatches
	 * 
	 * @param newValue -
	 *            the height of the color swatches
	 */
	public void setSwatchHeight(Extent newValue) {
		setProperty(PROPERTY_SWATCH_HEIGHT, newValue);
	}

	public void setSwatchModel(ColorSwatchModel newValue) {
		setProperty(PROPERTY_SWATCH_MODEL, newValue);
	}

	/**
	 * Sets the width of the color swatches
	 * 
	 * @param newValue -
	 *            the width of the color swatches
	 */
	public void setSwatchWidth(Extent newValue) {
		setProperty(PROPERTY_SWATCH_WIDTH, newValue);
	}
}
