package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XmlOperations {

    public static String[][] loadDataFromXmlFile() {
        String[][] data = new String[24][15];

        try {
            File file = new File("src\\main\\java\\com\\company\\katalog.xml");
            if(!file.exists()) {
                file.createNewFile();
            } else {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.parse(file);

                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("laptop");

                for (int itr = 0; itr < nodeList.getLength(); itr++)
                {
                    Node node = nodeList.item(itr);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element element = (Element) node;

                        data[itr][0] = element.getElementsByTagName("manufacturer").item(0).getTextContent();

                        NodeList screenNodeList = element.getElementsByTagName("screen");
                        data[itr][4] = screenNodeList.item(0).getAttributes().getNamedItem("touch").getTextContent();
                        for (int it = 0; it < screenNodeList.getLength(); it++) {
                            Node node1 = screenNodeList.item(it);
                            if (node1.getNodeType() == Node.ELEMENT_NODE) {
                                Element element1 = (Element) node1;
                                data[itr][1] = element1.getElementsByTagName("size").item(0).getTextContent();
                                data[itr][2] = element1.getElementsByTagName("resolution").item(0).getTextContent();
                                data[itr][3] = element1.getElementsByTagName("type").item(0).getTextContent();
                            }
                        }

                        NodeList processorNodeList = element.getElementsByTagName("processor");
                        for(int j = 0; j < processorNodeList.getLength(); j++) {
                            Node procNode = processorNodeList.item(j);
                            if(procNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element procElement = (Element) procNode;
                                data[itr][5] = procElement.getElementsByTagName("name").item(0).getTextContent();
                                data[itr][6] = procElement.getElementsByTagName("physical_cores").item(0).getTextContent();
                                data[itr][7] = procElement.getElementsByTagName("clock_speed").item(0).getTextContent();
                            }
                        }

                        data[itr][8] = element.getElementsByTagName("ram").item(0).getTextContent();

                        NodeList discNodeList = element.getElementsByTagName("disc");
                        data[itr][10] = discNodeList.item(0).getAttributes().getNamedItem("type").getTextContent();
                        Node discNode = discNodeList.item(0);
                        if(discNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element discElement = (Element) discNode;
                            data[itr][9] = discElement.getElementsByTagName("storage").item(0).getTextContent();
                        }

                        NodeList graphicNodeList = element.getElementsByTagName("graphic_card");
                        for(int k = 0; k < graphicNodeList.getLength(); k++) {
                            Node graphicNode = graphicNodeList.item(k);
                            if(graphicNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element graphicElement = (Element) graphicNode;
                                data[itr][11] = graphicElement.getElementsByTagName("name").item(0).getTextContent();
                                data[itr][12] = graphicElement.getElementsByTagName("memory").item(0).getTextContent();
                            }
                        }

                        data[itr][13] = element.getElementsByTagName("os").item(0).getTextContent();
                        data[itr][14] = element.getElementsByTagName("disc_reader").item(0).getTextContent();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveDataFromTableToXmlFile(JTable jTable) {
        try {
            File file = new File("src\\main\\java\\com\\company\\katalog.xml");
            if(!file.exists()) {
                file.createNewFile();
            } else {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
                Document doc = dbBuilder.newDocument();

                Element rootElement = doc.createElement("laptops");
                LocalDateTime myDateObj = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedDate = myDateObj.format(myFormatObj);
                rootElement.setAttribute("moddate", formattedDate);

                for (int row = 0; row < jTable.getRowCount(); row++) {
                    Element laptop = doc.createElement("laptop");
                    laptop.setAttribute("id", String.valueOf(row + 1));

                    Element manufacturer = doc.createElement("manufacturer");
                    manufacturer.setTextContent((String) jTable.getValueAt(row, 0));
                    laptop.appendChild(manufacturer);

                    Element screen = doc.createElement("screen");
                    screen.setAttribute("touch", (String) jTable.getValueAt(row, 4));

                    Element size = doc.createElement("size");
                    size.setTextContent((String) jTable.getValueAt(row, 1));
                    screen.appendChild(size);
                    Element resolution = doc.createElement("resolution");
                    resolution.setTextContent((String) jTable.getValueAt(row, 2));
                    screen.appendChild(resolution);
                    Element type = doc.createElement("type");
                    type.setTextContent((String) jTable.getValueAt(row, 3));
                    screen.appendChild(type);
                    laptop.appendChild(screen);

                    Element processor = doc.createElement("processor");
                    Element name = doc.createElement("name");
                    name.setTextContent((String) jTable.getValueAt(row, 5));
                    processor.appendChild(name);
                    Element physicalCores = doc.createElement("physical_cores");
                    physicalCores.setTextContent((String) jTable.getValueAt(row, 6));
                    processor.appendChild(physicalCores);
                    Element clockSpeed = doc.createElement("clock_speed");
                    clockSpeed.setTextContent((String) jTable.getValueAt(row, 7));
                    processor.appendChild(clockSpeed);
                    laptop.appendChild(processor);

                    Element ram = doc.createElement("ram");
                    ram.setTextContent((String) jTable.getValueAt(row, 8));
                    laptop.appendChild(ram);

                    Element discType = doc.createElement("disc");
                    discType.setAttribute("type", (String) jTable.getValueAt(row, 10));
                    Element storage = doc.createElement("storage");
                    storage.setTextContent((String) jTable.getValueAt(row, 9));
                    discType.appendChild(storage);
                    laptop.appendChild(discType);

                    Element graphicCard = doc.createElement("graphic_card");
                    Element cardName = doc.createElement("name");
                    cardName.setTextContent((String) jTable.getValueAt(row, 11));
                    graphicCard.appendChild(cardName);
                    Element memory = doc.createElement("memory");
                    memory.setTextContent((String) jTable.getValueAt(row, 12));
                    graphicCard.appendChild(memory);
                    laptop.appendChild(graphicCard);

                    Element os = doc.createElement("os");
                    os.setTextContent((String) jTable.getValueAt(row, 13));
                    laptop.appendChild(os);

                    Element discReader = doc.createElement("disc_reader");
                    discReader.setTextContent((String) jTable.getValueAt(row, 14));
                    laptop.appendChild(discReader);

                    rootElement.appendChild(laptop);
                }

                doc.appendChild(rootElement);

                Transformer t = TransformerFactory.newInstance().newTransformer();

                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.setOutputProperty(OutputKeys.METHOD, "xml");

                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);
                t.transform(source, result);
            }

        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
