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
	public static String CIRCLE = "https://www.wuala.com/en/api/preview/philnate/public/Circle.svg?key=f2palQYSvKXX";
	public static String CROSS = "https://www.wuala.com/en/api/preview/philnate/public/Cross.svg?key=f2palQYSvKXX";
	public static String FULL ="https://www.wuala.com/en/api/preview/philnate/public/Full.svg?key=f2palQYSvKXX";
	
	public String specialInfo = "";
	Color color = null;
	Document document;
	Element svg;

	public SVGPanel(String picture) {
		setDocument(picture, new Color(0));
	}
	
	public SVGPanel(String picture, Color color) {
		setDocument(picture, color);
	}
	
	private void setDocument(String picture, Color color) {
		//Nötigen Klassen zum Laden der SVG erzeugen
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
//	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//Document document = documentBuilder.newDocument();	
//	document.setDocumentURI(picture);
		try {
			//File Laden aus dem Package
			document = f.createDocument(/*getClass().getResource("/" + getClass().getPackage().getName().replace(".", "/") + picture).toString()*/picture);
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

	public void setPicture(String picture, Color color) {
		setDocument(picture, color);
	}
}
