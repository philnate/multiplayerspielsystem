package org.mss.windows.svg;

import java.awt.Color;
import java.io.IOException;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*
 * 
 */
public class SVGPanel extends JSVGCanvas{
	
	private static final long serialVersionUID = 1405056131073275382L;
	public static String CIRCLE = "/Circle.svg";
	public static String CROSS = "/Cross.svg";
	public static String FULL ="/Full.svg";
	
	public String specialInfo = "";
	Color color = null;
	Document document;
	Element svg;

	public SVGPanel(String picture) {
		this(picture, new Color(0));
	}
	
	public SVGPanel(String picture, Color color) {
		//Nötigen Klassen zum Laden der SVG erzeugen
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		try {
			//File Laden aus dem Package
			document = f.createDocument(getClass().getResource("/" + getClass().getPackage().getName().replace(".", "/") + picture).toString());
			if (color.getRGB() != 0) {
				//Farbe ändern falls diese sich vom Defaultwert unterscheidet
				svg = document.getDocumentElement();
	            Element style = document.getElementById("color");
	            Node node =style.getFirstChild();
	            node.setNodeValue(".coloring{stroke:#"+Integer.toHexString(color.getRGB()).substring(2)+";}");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Dokument zuweisen
		this.setDocument(document);
	}
}
