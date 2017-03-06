package com.pds.p2p.core.poi.excel.event;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

//import org.apache.poi.ss.usermodel.DataFormatter;

/**

 */
public class Excel2007Reader {
    enum xssfDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
    }

    class MyXSSFSheetHandler extends DefaultHandler {
        private StylesTable stylesTable;
        private ReadOnlySharedStringsTable sharedStringsTable;
        private boolean vIsOpen;
        private xssfDataType nextDataType;
        private short formatIndex;
        private String formatString;
        private final DataFormatter formatter;
        private int thisColumn = -1;
        private int lastColumnNumber = -1;
        private StringBuilder value;
        private String thisColumnLable = "";
        private Map<String, String> rowVals = new java.util.LinkedHashMap<String, String>();

        public MyXSSFSheetHandler(StylesTable styles, ReadOnlySharedStringsTable strings) {
            this.stylesTable = styles;
            this.sharedStringsTable = strings;
            this.value = new StringBuilder();
            this.nextDataType = xssfDataType.NUMBER;
            this.formatter = new DataFormatter();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("inlineStr".equals(name) || "v".equals(name)) {
                vIsOpen = true;
                value.setLength(0);
            }
            // c => cell
            else if ("c".equals(name)) {
                // Get the cell reference
                String r = attributes.getValue("r");
                int firstDigit = -1;
                for (int c = 0; c < r.length(); ++c) {
                    if (Character.isDigit(r.charAt(c))) {
                        firstDigit = c;
                        break;
                    }
                }
                thisColumnLable = r.substring(0, firstDigit);
                thisColumn = nameToColumn(r.substring(0, firstDigit));
                // Set up defaults.
                this.nextDataType = xssfDataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleStr = attributes.getValue("s");
                if ("b".equals(cellType)) {
                    nextDataType = xssfDataType.BOOL;
                } else if ("e".equals(cellType)) {
                    nextDataType = xssfDataType.ERROR;
                } else if ("inlineStr".equals(cellType)) {
                    nextDataType = xssfDataType.INLINESTR;
                } else if ("s".equals(cellType)) {
                    nextDataType = xssfDataType.SSTINDEX;
                } else if ("str".equals(cellType)) {
                    nextDataType = xssfDataType.FORMULA;
                } else if (cellStyleStr != null) {
                    // It's a number, but almost certainly one
                    // with a special style or format
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null) {
                        this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                    }
                }
            }
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            String thisStr = null;
            if ("v".equals(name)) {
                switch (nextDataType) {
                    case BOOL:
                        char first = value.charAt(0);
                        thisStr = first == '0' ? "FALSE" : "TRUE";
                        break;
                    case ERROR:
                        thisStr = "ERROR:" + value.toString();
                        break;
                    case FORMULA:
                        // A formula could result in a string value,
                        // so always add double-quote characters.
                        thisStr = value.toString();
                        break;
                    case INLINESTR:
                        XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                        thisStr = rtsi.toString();
                        break;
                    case SSTINDEX:
                        String sstIndex = value.toString();
                        try {
                            int idx = Integer.parseInt(sstIndex);
                            XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                            thisStr = rtss.toString().trim();
                        } catch (NumberFormatException ex) {
                            thisStr = "";
                        }
                        break;
                    case NUMBER:
                        thisStr = value.toString();
                        try {
                            if (this.formatString != null) {
                                String fmtStr = formatter
                                        .formatRawCellContents(Double.parseDouble(thisStr), this.formatIndex,
                                                this.formatString);
                                rowVals.put("$" + thisColumnLable, fmtStr);
                            } else {

                            }
                        } catch (Exception ex) {
                        }
                        break;
                    default:
                        thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                        break;
                }
                if (lastColumnNumber == -1) {
                    lastColumnNumber = 0;
                }
                rowVals.put(thisColumnLable, thisStr);
                if (thisColumn > -1) {
                    lastColumnNumber = thisColumn;
                }
            } else if ("row".equals(name)) {
                lastColumnNumber = -1;
                excelReader.rowRead(sheetIndex, sheetName, curRow, rowVals);
                curRow++;
                this.rowVals.clear();
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            if (vIsOpen) {
                value.append(ch, start, length);
            }
        }

        private int nameToColumn(String name) {
            int column = -1;
            for (int i = 0; i < name.length(); ++i) {
                int c = name.charAt(i);
                column = (column + 1) * 26 + c - 'A';
            }
            return column;
        }

    }

    // /////////////////////////////////////
    private OPCPackage xlsxPackage;
    private String sheetName;
    private int sheetIndex;
    private IExcelReader excelReader;
    // 当前行
    private int curRow = 0;

    public void setExcelReader(IExcelReader excelReader) {
        this.excelReader = excelReader;
    }

    public Excel2007Reader(OPCPackage pkg) {
        this.xlsxPackage = pkg;
    }

    public Excel2007Reader(String filename) throws InvalidFormatException {
        OPCPackage pkg = OPCPackage.open(filename, PackageAccess.READ);
        this.xlsxPackage = pkg;
    }

    public Excel2007Reader(String filename, IExcelReader excelReader) throws InvalidFormatException {
        this(filename);
        this.setExcelReader(excelReader);
    }

    public Excel2007Reader(InputStream input) throws IOException, InvalidFormatException {
        OPCPackage pkg = OPCPackage.open(input);
        this.xlsxPackage = pkg;
    }

    public Excel2007Reader(InputStream input, IExcelReader excelReader) throws IOException, InvalidFormatException {
        this(input);
        this.setExcelReader(excelReader);
    }

    public void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetInputStream)
            throws IOException, ParserConfigurationException, SAXException {
        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        ContentHandler handler = new MyXSSFSheetHandler(styles, strings);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);

    }

    public void process() throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
        try {
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
            XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
            StylesTable styles = xssfReader.getStylesTable();
            XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            sheetIndex = 0;
            if (!this.excelReader.startRead()) {
                return;
            }
            while (iter.hasNext()) {
                InputStream stream = null;
                try {
                    stream = iter.next();
                    curRow = 0;
                    sheetName = iter.getSheetName();
                    if (this.excelReader.beforeReadSheet(sheetIndex, sheetName)) {
                        processSheet(styles, strings, stream);
                    }
                    this.excelReader.afterReadSheet(sheetIndex, sheetName);
                } finally {
                    stream.close();
                }
                ++sheetIndex;
            }
            this.excelReader.endRead();
        } finally {
            this.xlsxPackage.close();
        }

    }
}
