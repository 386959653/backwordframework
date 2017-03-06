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
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import com.pds.p2p.core.poi.excel.utils.CellStyleBuilder;
import com.pds.p2p.core.utils.UTF8OutputStream;

public class Copy_2_of_ZXSSFWorkbook {

    private static final String XL_WORKSHEETS_SHEET1_XML = "xl/worksheets/sheet1.xml";

    public static void main(String[] args) throws Exception {
        FileOutputStream out = new FileOutputStream("C:/my-data/test-poi/tt2.xlsx");
        Copy_2_of_ZXSSFWorkbook workbook = new Copy_2_of_ZXSSFWorkbook("C:/my-data/test-poi/tt.xlsx", 900000, 100, out);
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
    private int dataRows;
    private int dataCols;
    private int startRow;

    public Copy_2_of_ZXSSFWorkbook(String path, int dataRows, int dataCols, OutputStream saveTo)
            throws IOException, InvalidFormatException {
        // poi-bugs?模板文件总保存strings和styles的内容，所以做个复制，以保持原始文件的面貌
        File tmprawXlsx = TempFile.createTempFile("poi-zxssfworkbook-tmp", ".xlsx");
        FileUtils.copyFile(new File(path), tmprawXlsx);
        OPCPackage pkg = OPCPackage.open(tmprawXlsx);
        XSSFWorkbook wb = new XSSFWorkbook(pkg);
        sstTemtFile = this.createTempFile("sst");
        cellStyles = CellStyleBuilder.createNormalStyles(wb);

        this.dataRows = dataRows;
        this.dataCols = dataCols;

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
                ClassPathResource datHeader = new ClassPathResource("sheetheader.txt", this.getClass());
                ClassPathResource datFooter = new ClassPathResource("sheetfooter.txt", this.getClass());
                InputStream his = datHeader.getInputStream();
                InputStream fis = datFooter.getInputStream();
                this.sheetDataStartPart = IOUtils.toString(his);
                this.sheetDataEndPart = IOUtils.toString(fis);
                his.close();
                fis.close();
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

    public void write(List<Object[]> dat) throws IOException {
        StringBuilder sb = new StringBuilder(1024 * 4);
        CellStyle cellStyle = CellStyleBuilder.getNormalLeftStyle(this.cellStyles);
        int rownum = this.getStartRow();
        String spans = "";//String.format(" spans=\"1:%d\" x14ac:dyDescent=\"0.15\"", this.dataCols);
        for (int i = 0; i < this.dataRows; ++i) {
            sb.setLength(0);
            sb.append("<row r=\"").append(rownum + 1).append("\"").append(spans).append(">");
            for (int columnIndex = 0; columnIndex < this.dataCols; ++columnIndex) {
                String ref = new CellReference(rownum, columnIndex).formatAsString();
                sb.append("<c r=\"").append(ref).append("\"");
                sb.append(" s=\"").append(cellStyle.getIndex() & 0xffff).append("\"");
                long idx = this.sst.add("大家好");
                sb.append(" t=\"s\">");
                sb.append("<v>");
                sb.append(idx);
                sb.append("</v>");
                sb.append("</c>");
            }
            sb.append("</row>");
            this.zsaveTo.write(sb.toString().getBytes(), 0, sb.length());
            ++rownum;
            System.out.println(rownum);
        }
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
    private File createTempFile(String tag) throws IOException {
        return TempFile.createTempFile(String.format("poi-zxssfworkbook-%s", tag), ".xml");
    }

}
