package com.pds.p2p.core.j2ee.xml.dom;

import java.io.PrintWriter;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.pds.p2p.core.utils.StringUtils;

public class DOMPrint {
    //设置输出编码格式
    static final String outputEncoding = "UTF-8";
    private PrintWriter out;
    int indt = 0;

    public DOMPrint(PrintWriter out) {
        this.out = out;
    }

    private void print(Node node) {
        //打印节点名称
        out.println(StringUtils.left(node.getNodeName(), indt++));// + "NodeName=\""+node.getNodeName()+"\"");
        //打印节点URI
            /*String value=node.getNamespaceURI();
            if(value!=null)
	        {
	            out.println("URL=\""+value+"\"");
	        }
	        //打印节点前缀
	        value=node.getPrefix();
	        if(value!=null)
	        {
	            out.println("Prefix=\""+value+"\"");
	        }
	        //打印节点本地名称
	        value=node.getLocalName();
	        if(value!=null)
	        {
	            out.println("LocalName=\""+value+"\"");
	        }*/
        //打印节点值
        String value = node.getNodeValue();
        if (value != null) {
            out.println("NodeValue=");
            if (value.trim().equals("")) {
                out.println("[WS]");
            } else {
                out.println("\"" + node.getNodeValue() + "\"");
            }
        }
    }

    public void echo(Node node) {
        int type = node.getNodeType();
        switch (type) {
            //属性节点
            case Node.ATTRIBUTE_NODE:
                out.println("ATTR");
                print(node);
                break;
            //CDATA
            case Node.CDATA_SECTION_NODE:
                out.println("CDATA");
                print(node);
                break;
            //注释
            case Node.COMMENT_NODE:
                out.println("COMMENT");
                print(node);
                break;
            //段落节点
            case Node.DOCUMENT_FRAGMENT_NODE:
                out.println("DOC_FRAGMENT");
                print(node);
                break;
            //文档节点
            case Node.DOCUMENT_NODE:
                out.println("DOC");
                print(node);
                break;
            //文档类型节点
            case Node.DOCUMENT_TYPE_NODE:
                out.println("DOC_TYPE");
                print(node);
                break;
            //元素节点
            case Node.ELEMENT_NODE:
                out.println("ELEMENT");
                print(node);
                //得到该节点所有属性
                NamedNodeMap atts = node.getAttributes();
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    //递归调用
                    echo(att);
                }
                break;
            //实体节点
            case Node.ENTITY_NODE:
                out.println("ENTITY");
                print(node);
                break;
            //实体引用节点
            case Node.ENTITY_REFERENCE_NODE:
                out.println("ENTITY_REF");
                print(node);
                break;
            //处理说明节点
            case Node.NOTATION_NODE:
                out.println("NOTATION");
                print(node);
                break;
            //预处理节点
            case Node.PROCESSING_INSTRUCTION_NODE:
                out.println("PROC_INST");
                print(node);
                break;
            //文本节点
            case Node.TEXT_NODE:
                out.println("TEXT");
                print(node);
                break;
            //未知节点
            default:
                out.println("UNKNOWN NODE:" + type);
                print(node);
        }
        //遍历该节点子节点
        for (Node child = node.getFirstChild();
             child != null; child = child.getNextSibling()) {
            echo(child);
        }
    }

    // 显示DOM树
             /*OutputStreamWriter outWriter;
	 		TransformerFactory tfactory = TransformerFactory.newInstance();
	 		try {
	 			outWriter = new OutputStreamWriter(System.out);
	 			Transformer transformer = tfactory.newTransformer();
	 			DOMSource source = new DOMSource(nodes[0]);
	 			StreamResult result = new StreamResult(outWriter);
	 			transformer.setOutputProperty("encoding", "GBK");
	 			transformer.transform(source, result);
	 		} catch (TransformerConfigurationException e) {
	 			e.printStackTrace();
	 		} catch (TransformerException e) {
	 			e.printStackTrace();
	 		}
	 		*/
}
