package com.pds.p2p.core.poi.excel.xlsx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.pds.p2p.core.utils.UTF8OutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pds.p2p.core.utils.UtilType;

public class ZXSSFWorkbook {

    private static final String XL_WORKSHEETS_SHEET1_XML = "xl/worksheets/sheet1.xml";

    public static void main(String[] args) throws Exception {
        FileOutputStream out = new FileOutputStream("C:/my-data/test-poi/tt2.xlsx");
        ZXSSFWorkbook workbook = new ZXSSFWorkbook("C:/my-data/test-poi/tt.xlsx", 9, 100, out);
        workbook.setStartRow(0);
        workbook.write(null);
        workbook.close();
    }

    private static final int BUFFER_SIZE = 1024 * 80;
    private static final String XL_SHARED_STRINGS_XML = "xl/sharedStrings.xml";
    private static final String SST_HEAD = //
            "<?xml version=\"1.0\" encoding=\"UTF-8\" " + //
                    " standalone=\"yes\" ?>\n" + //
                    "<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" " + //
                    "count=\"@count@\" uniqueCount=\"@uniqueCount@\">\n  ";//

    private SST sst;
    private File sstTemtFile;
    private ZipOutputStream zsaveTo;
    private String sheetDataStartPart;
    private String sheetDataEndPart;
    private Map<String, CellStyle> cellStyles;
    private int startRow;
    private int currRow;

    /***
     * @param path
     * @param dataRows
     * @param dataCols
     * @param saveTo
     *
     * @throws IOException
     * @throws InvalidFormatException
     */
    public ZXSSFWorkbook(String path, int dataRows, int dataCols, OutputStream saveTo)
            throws IOException, InvalidFormatException {
        // poi-bugs?模板文件总保存strings和styles的内容，所以做个复制，以保持原始文件的面貌
        File tmprawXlsx = this.createTempFile("tmp", "xlsx");

        FileUtils.copyFile(new File(path), tmprawXlsx);
        OPCPackage pkg = OPCPackage.open(tmprawXlsx);
        XSSFWorkbook wb = new XSSFWorkbook(pkg);
        sstTemtFile = this.createTempFile("sst", "xml");

        cellStyles = this.createStyles(wb);

        UTF8OutputStream sstOut = new UTF8OutputStream(new BufferedOutputStream(new FileOutputStream(sstTemtFile)));

        this.writeSstEmptyHead(sstOut);
        this.sst = new SST(sstOut, wb.getSharedStringSource());

        this.zsaveTo = new ZipOutputStream(new BufferedOutputStream(saveTo));

        // 保持styles的内容
        File tmpXlsx = TempFile.createTempFile("poi-zxssfworkbook", ".xlsx");
        FileOutputStream out = new FileOutputStream(tmpXlsx);
        wb.write(out);
        out.close();
        wb.close();
        tmprawXlsx.delete();

        ZipFile zip = ZipHelper.openZipFile(tmpXlsx);

        Enumeration<? extends ZipEntry> en = zip.entries();
        while (en.hasMoreElements()) {
            ZipEntry ze = en.nextElement();
            String zename = ze.getName();
            System.out.println(zename);
            if (XL_SHARED_STRINGS_XML.equals(zename)) {

            } else if (XL_WORKSHEETS_SHEET1_XML.equals(zename)) {
                InputStream is = zip.getInputStream(ze);
                String sheetData = IOUtils.toString(is);
                int idx = StringUtils.indexOf(sheetData, "</sheetData>");
                this.sheetDataStartPart = sheetData.substring(0, idx);
                this.sheetDataEndPart = sheetData.substring(idx);
                IOUtils.closeQuietly(is);
            } else {
                this.zsaveTo.putNextEntry(new ZipEntry(zename));
                InputStream is = zip.getInputStream(ze);
                this.copy(is, zsaveTo);
                this.zsaveTo.closeEntry();
                IOUtils.closeQuietly(is);
            }
        }
        zip.close();
        tmpXlsx.delete();

        this.zsaveTo.putNextEntry(new ZipEntry(XL_WORKSHEETS_SHEET1_XML));
        this.zsaveTo.write(this.sheetDataStartPart.getBytes(), 0, this.sheetDataStartPart.length());
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    private StringBuilder sb = new StringBuilder(1024);

    public void write(Map<String, Object> rowDat, String... keys) throws IOException {
        if (this.currRow < this.startRow) {
            this.currRow = this.startRow;
        }
        sb.setLength(0);
        int columnIndex = 0;
        sb.append("<row r=\"").append(this.currRow + 1).append("\"").append(">");
        for (String key : keys) {
            Object obj = rowDat.get(key);
            if (UtilType.isEmpty(obj)) {

            } else {
                String ref = new CellReference(currRow, columnIndex).formatAsString();
                sb.append("<c r=\"").append(ref).append("\"");
                // sb.append(" s=\"").append(cellStyle.getIndex() &
                // 0xffff).append("\"");
                String s = obj.toString();
                if (UtilType.isJavaStringType(obj.getClass())) {
                    setStringValue(s);
                } else if (TypeUtils.isInstance(obj, Number.class) && s.length() > 11) {
                    setStringValue(s);
                } else {
                    sb.append(">");
                    sb.append("<v>");
                    sb.append(s);
                    sb.append("</v>");
                }
                sb.append("</c>");
            }
            ++columnIndex;
        }
        sb.append("</row>");
        this.zsaveTo.write(sb.toString().getBytes(), 0, sb.length());
        ++currRow;
    }

    /**
     * @param s
     *
     * @throws IOException
     */
    private void setStringValue(String s) throws IOException {
        long idx = this.sst.add(s);
        sb.append(" t=\"s\">");
        sb.append("<v>");
        sb.append(idx);
        sb.append("</v>");
    }

    /***
     * 提交sst内容
     *
     * @throws IOException
     */
    public void close() throws IOException {
        // 结束数据内容
        this.zsaveTo.write(this.sheetDataEndPart.getBytes(), 0, this.sheetDataEndPart.length());
        this.zsaveTo.closeEntry();

        // 处理sst内容
        sst.write("</sst>");
        sst.close();

        // 更新sst文件
        this.updateSstHead();

        // 拷贝sst到目标
        this.copyStrings();

        this.zsaveTo.close();

        if (!this.sstTemtFile.delete()) {
            throw new IOException("Could not delete temporary file after processing: " + sstTemtFile);
        }

    }

    /***
     * 将缓存在临时文件中sst内容复制excel的sst节中
     *
     * @throws Exception
     */
    private void copyStrings() throws IOException {
        InputStream strings = new BufferedInputStream(new FileInputStream(this.sstTemtFile));
        try {
            this.zsaveTo.putNextEntry(new ZipEntry(XL_SHARED_STRINGS_XML));
            this.copy(strings, this.zsaveTo);
            this.zsaveTo.closeEntry();
        } finally {
            strings.close();
        }
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = in.read(buffer);
        while (len > 0) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
    }

    private void updateSstHead() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.sstTemtFile, "rw");
        try {
            raf.seek(0);
            String head = SST_HEAD.replaceFirst("@count@", Long.toString(sst.getCount()));
            head = head.replaceFirst("@uniqueCount@", Long.toString(sst.getUnique()));
            raf.write(head.getBytes());
        } finally {
            raf.close();
        }
    }

    /***
     * 写sst头，占位sst节内容，当sst完毕后对字符串统计数进行更新
     *
     * @param sstOut
     *
     * @throws IOException
     */
    private void writeSstEmptyHead(OutputStream sstOut) throws IOException {
        for (int i = 0; i < SST_HEAD.length() + 20; i++) {
            sstOut.write((byte) 9);
        }
    }

    /***
     * 创建一个临时文件存储sst字符串
     *
     * @return
     *
     * @throws IOException
     */
    private File createTempFile(String tag, String ext) throws IOException {
        return TempFile.createTempFile(String.format("poi-zxssfworkbook-%s.%s", tag), "." + ext);
    }

    protected Map<String, CellStyle> createStyles(XSSFWorkbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        XSSFDataFormat fmt = wb.createDataFormat();

        XSSFCellStyle style1 = wb.createCellStyle();
        style1.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        style1.setDataFormat(fmt.getFormat("0.0%"));
        styles.put("percent", style1);

        XSSFCellStyle style2 = wb.createCellStyle();
        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style2.setDataFormat(fmt.getFormat("0.0X"));
        styles.put("coeff", style2);

        XSSFCellStyle style3 = wb.createCellStyle();
        style3.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        style3.setDataFormat(fmt.getFormat("$#,##0.00"));
        styles.put("currency", style3);

        XSSFCellStyle style4 = wb.createCellStyle();
        style4.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        style4.setDataFormat(fmt.getFormat("mmm dd"));
        styles.put("date", style4);

        XSSFCellStyle style5 = wb.createCellStyle();
        XSSFFont headerFont = wb.createFont();
        headerFont.setBold(true);
        style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style5.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style5.setFont(headerFont);
        styles.put("header", style5);

        XSSFCellStyle style6 = wb.createCellStyle();
        style3.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        styles.put("number", style6);

        return styles;
    }

}
