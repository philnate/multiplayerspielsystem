package org.mss.windows;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/*
 * Renderer für Listenzellen. Farbe des Eintrages richtet sich nach dessen String Hashwert
 */
class HashColorCellRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = 3951805205071348113L;

	public HashColorCellRenderer() {
        setOpaque(true);
    }

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        setForeground(new Color(value.toString().hashCode(), false));
        return this;
    }
}