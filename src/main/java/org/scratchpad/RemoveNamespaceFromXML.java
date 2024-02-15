package org.scratchpad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RemoveNamespaceFromXML {
    public static void main(String[] args) throws Exception {
        // Sample XML with namespaces
        String xml = "\n" +
                "<root>\n" +
                "\n" +
                "<h:table xmlns:h=\"http://www.w3.org/TR/html4/\">\n" +
                "  <h:tr>\n" +
                "    <h:td>Apples</h:td>\n" +
                "    <h:td>Bananas</h:td>\n" +
                "  </h:tr>\n" +
                "</h:table>\n" +
                "\n" +
                "<f:table xmlns:f=\"https://www.w3schools.com/furniture\">\n" +
                "  <f:name>African Coffee Table</f:name>\n" +
                "  <f:width>80</f:width>\n" +
                "  <f:length>120</f:length>\n" +
                "</f:table>\n" +
                "\n" +
                "</root> ";

        // Parse the XML document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Document document = builder.parse(inputStream);

        // Remove namespaces
        removeNamespaceRecursively(document.getDocumentElement());

        // Write the modified document back to a string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(output));
        String modifiedXml = output.toString("UTF-8");

        // Output the modified XML
        System.out.println(modifiedXml);
    }

    private static void removeNamespaceRecursively(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Document ownerDocument = node.getOwnerDocument();
            Element element = (Element) node;
            String nodeName = element.getNodeName();
            String localName = nodeName.contains(":") ? nodeName.split(":")[1] : nodeName;
            Element newElement = ownerDocument.createElement(localName);
            NodeList children = node.getChildNodes();
            while (children.getLength() > 0) {
                Node child = children.item(0);
                element.removeChild(child);
                newElement.appendChild(child);
            }
            Node parent = node.getParentNode();
            parent.replaceChild(newElement, node);
            node = newElement;
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            removeNamespaceRecursively(children.item(i));
        }
    }
}
