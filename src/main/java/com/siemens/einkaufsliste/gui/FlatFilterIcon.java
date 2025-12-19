package com.siemens.einkaufsliste.gui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

public final class FlatFilterIcon extends FlatSearchIcon {

	private Area area;

	public FlatFilterIcon() {
		super();
	}

	public FlatFilterIcon(boolean ignoreButtonState) {
		super(ignoreButtonState);
	}

	@Override
	protected void paintIcon(Component c, Graphics2D g) {
		g.setColor(FlatButtonUI.buttonStateColor(c, this.searchIconColor, this.searchIconColor, null,
				this.searchIconHoverColor, this.searchIconPressedColor));

		if (area == null) {
			Area polygon = new Area(
					FlatUIUtils.createPath(2f, 2f, 14f, 2f, 14f, 2.857f, 9.5f, 8f, 6.5f, 8f, 2f, 2.857f));

			Area rect = new Area(new Rectangle2D.Float(6.5f, 8f, 3f, 6f));

			polygon.add(rect);
			area = polygon;
		}

		g.fill(area);
	}
}
