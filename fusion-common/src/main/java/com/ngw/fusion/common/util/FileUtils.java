package com.ngw.fusion.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.*;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ngw.fusion.common.base.BaseIService;
import lombok.Data;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 文件编码
     */
    private final static String enc = "UTF-8";

    /**
     * 单个sheet页行数
     */
    private static int listSize = 50000;

    public static void exportExcel(String fileName, Class cls, List<?> data, HttpServletResponse response) throws IOException {
        // 设置默认文件名
        try {
            if (data.size() == 0) throw new Exception("导出数据为空！");
            if (StringUtils.isBlank(fileName)) fileName = "查询数据";
            // 导出中文文件名无法显示问题
            fileName = URLEncoder.encode(fileName, enc).replaceAll("\\+", "%20");
            // 文件名拼上时间戳
            String exportFileName = fileName + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            exportExcel(fileName, cls, data, response, exportFileName);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding(enc);
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败：" + e.getMessage());
            response.getWriter().println(JSON.toJSON(map));
        }
    }

    public static void exportFile(String fileType, String fileName, LinkedHashMap<String, String> headMap, List<Map<String, Object>> dataList, HttpServletResponse response) throws IOException {
        // 设置默认文件名
        if (StringUtils.isBlank(fileName)) fileName = "查询数据";
        // 导出中文文件名无法显示问题
        fileName = URLEncoder.encode(fileName, enc).replaceAll("\\+", "%20");
        // 文件名拼上时间戳
        String exportFileName = fileName + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        List<List<String>> headList = getHeadList(headMap);
        List<String> enList = headList.get(0);
        List<String> cnList = headList.get(1);
        try {
            if (dataList.size() == 0) throw new Exception("导出数据为空！");
            switch (fileType) {
                case Constants.FILE_TYPE_EXCEL:
                    exportExcel(fileName, exportFileName, enList, cnList, dataList, response);
                    break;
                case Constants.FILE_TYPE_CSV:
                case Constants.FILE_TYPE_CSV_UTF8:
                    exportCsv(Constants.FILE_TYPE_CSV_UTF8, exportFileName, enList, cnList, dataList, response);
                    break;
                case Constants.FILE_TYPE_CSV_GB2312:
                    exportCsv(Constants.FILE_TYPE_CSV_GB2312, exportFileName, cnList, enList, dataList, response);
                default:
                    logger.warn("不支持的文件类型：{}", fileType);
            }
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding(enc);
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败：" + e.getMessage());
            response.getWriter().println(JSON.toJSON(map));
        }
    }

    /**
     * 保留方法
     *
     * @param fileName
     * @param head
     * @param data
     * @param response
     * @param exportFileName
     * @throws IOException
     */
    private static <T> void exportExcel(String fileName, Class head, List<T> data, HttpServletResponse response, String exportFileName) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + exportFileName + ".xlsx");
        // 输出文件表头
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), head).autoCloseStream(Boolean.FALSE).build()) {
            // 输出文件sheet名称
            String sheetName = URLDecoder.decode(fileName, enc);
            // 限制单个sheet页行数，拆分数据集
            List<List<T>> list = subList(data, listSize);
            for (int i = 0, size = list.size(); i < size; i++) {
                List<?> subList = list.get(i);
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                String suffix = "";
                if (size > 1) suffix = String.valueOf(i + 1);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName + suffix).build();
                excelWriter.write(subList, writeSheet);
            }
        }
    }

    private static void exportExcel(String fileName, String exportFileName, List<String> enHead, List<String> cnHead, List<Map<String, Object>> data, HttpServletResponse response) throws IOException, SQLException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + exportFileName + ".xlsx");
        // 输出文件表头
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).head(headList(cnHead)).autoCloseStream(Boolean.FALSE).build()) {
            // 输出文件sheet名称
            String sheetName = URLDecoder.decode(fileName, enc);
            // 限制单个sheet页行数，拆分数据集
            List<List<Map<String, Object>>> list = subList(data, listSize);
            for (int i = 0, size = list.size(); i < size; i++) {
                List<Map<String, Object>> subList = list.get(i);
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                String suffix = "";
                if (size > 1) suffix = String.valueOf(i + 1);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetName + suffix)
                        .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 25, (short) 20))
                        .registerWriteHandler(new SimpleColumnWidthStyleStrategy(25))
                        .build();
                excelWriter.write(dataList(enHead, subList), writeSheet);
            }
        }
    }

    private static void exportCsv(String csvType, String exportFileName, List<String> enHead, List<String> cnHead, List<Map<String, Object>> data, HttpServletResponse response) throws IOException, SQLException {
        Charset cs = null;
        switch (csvType) {
            case Constants.FILE_TYPE_CSV:
            case Constants.FILE_TYPE_CSV_UTF8:
                cs = CharsetUtil.CHARSET_UTF_8;
                break;
            case Constants.FILE_TYPE_CSV_GB2312:
                cs = CharsetUtil.CHARSET_GBK;
            default:
                logger.warn("不支持的文件类型：{}", csvType);
        }
        CsvWriter writer = CsvUtil.getWriter(exportFileName, cs);
        byte[] uft8bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        cnHead.set(0, new String(uft8bom, "UTF-8") + cnHead.get(0));
        writer.write(Arrays.asList(cnHead));
        List<List<Map<String, Object>>> list = subList(data, 100);
        for (int i = 0, size = list.size(); i < size; i++) {
            List<Map<String, Object>> subList = list.get(i);
            writer.write(dataList(enHead, subList));
        }
        writer.close();
        downloadFileByPath(response, exportFileName);
    }

    public static <T> List<List<T>> subList(List<T> list, int listSize) {
        List<List<T>> resultList = new ArrayList<>();
        int size = list.size();
        for (int i = 1, fromIndex = 0, toIndex = Math.min(i * listSize, size); fromIndex < toIndex && toIndex <= size; ) {
            List<T> subList = list.subList(fromIndex, toIndex);
            resultList.add(subList);
            fromIndex = i++ * listSize;
            toIndex = Math.min(i * listSize, size);
        }
        return resultList;
    }

    /**
     * 文件上传，目前只实现了excel
     * @param fileType     文件类型：EXCEL CSV
     * @param filePath     上传后文件路径
     * @param head         文件对应实体类
     * @param baseIService 文件入库的service
     */
    public static void importFile(String fileType, String filePath, Class head, BaseIService baseIService,Map map) {
        switch (fileType) {
            case Constants.FILE_TYPE_EXCEL:
                importExcel(filePath, head, baseIService,map);
                break;
            case Constants.FILE_TYPE_CSV:
            case Constants.FILE_TYPE_CSV_UTF8:
            case Constants.FILE_TYPE_CSV_GB2312:
//                importCsv(fileType, filePath, fileName, baseIService);
                importCsv(fileType, filePath, head, baseIService);
                break;
            default:
                logger.warn("不支持的文件类型：{}", fileType);
        }
    }

    private static void importExcel(String filePath, Class head, BaseIService baseIService,Map map) {
        String fileName = filePathUpload + File.separator + filePath;
//        fileName = "D:\\ngw\\portal\\upload\\"+filePath ;
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, head, new PageReadListener<>((dataList) -> {
            baseIService.saveEntityBatch(dataList,map);
            logger.info("读取到{}条数据，写入数据库成功！", dataList.size());
        })).sheet().doRead();
    }

    private static void importCsv(String csvType, String filePath, Class head, BaseIService baseIService) {
        Charset cs = null;
        switch (csvType) {
            case Constants.FILE_TYPE_CSV:
            case Constants.FILE_TYPE_CSV_UTF8:
                cs = CharsetUtil.CHARSET_UTF_8;
                break;
            case Constants.FILE_TYPE_CSV_GB2312:
                cs = CharsetUtil.CHARSET_GBK;
            default:
                logger.warn("不支持的文件类型：{}", csvType);
        }
        String fileName = filePathUpload + File.separator + filePath;
        CsvReader reader = CsvUtil.getReader();
        List rows = reader.read(ResourceUtil.getReader(fileName, cs), head);
        baseIService.saveEntityBatch(rows,new HashMap());
    }

    private static void importCsv(String csvType, String filePath, BaseIService baseIService) {
        Charset cs = null;
        switch (csvType) {
            case Constants.FILE_TYPE_CSV:
            case Constants.FILE_TYPE_CSV_UTF8:
                cs = CharsetUtil.CHARSET_UTF_8;
                break;
            case Constants.FILE_TYPE_CSV_GB2312:
                cs = CharsetUtil.CHARSET_GBK;
            default:
                logger.warn("不支持的文件类型：{}", csvType);
        }
        String fileName = filePathUpload + File.separator + filePath;
        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file(fileName), cs);
        List<CsvRow> rows = data.getRows();
        baseIService.saveCsvRowBatch(rows);
    }

//    /**
//     * 文件上传，目前只实现了excel
//     * @param fileType 文件类型：EXCEL CSV
//     * @param filePath 文件路径 不含文件名，
//     * @param fileName 文件名 包含后缀，如果文件名含路径，不需要输入文件路径
//     * @param head 文件对应实体类
//     * @param readListener 文件对应的监听类，需要自己实现。优先级高于service。如果需要对上传文件进行处理，必须实现
//     */
//    public static void importFile(String fileType, String filePath, String fileName, Class head, ReadListener readListener){
//        if (Constants.FILE_TYPE_EXCEL.equals(fileType)) {
//            importExcel (filePath, fileName, head, readListener);
//        }
//    }
//
//    private static void importExcel(String filePath, String fileName, Class head, ReadListener readListener) {
//        fileName = filePath + File.separator + fileName;
//        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
//        EasyExcel.read(fileName, head, readListener).sheet().doRead();
//
//    }

    private static void downloadFileByPath(HttpServletResponse response, String fileName) throws IOException {
        try (BufferedInputStream fis = new FileReader(fileName).getInputStream();
             OutputStream os = new BufferedOutputStream(response.getOutputStream())) {
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8'zh_cn'" + fileName + ".csv");
            byte[] buffer = new byte[fis.available()];
            while (fis.read(buffer) > 0) {
                os.write(buffer);
                os.flush();
            }
        }
    }

    public static List<List<String>> headList(List<String> headList) {
        List<List<String>> heads = new ArrayList<>();
        headList.forEach(head -> heads.add(Arrays.asList(head)));
        return heads;
    }

    public static List<List<String>> getHeadList(LinkedHashMap<String, String> headMap) {
        List<String> enList = new ArrayList<>();
        List<String> cnList = new ArrayList<>();
        for (String key : headMap.keySet()) {
            enList.add(key);
            cnList.add(headMap.get(key));
        }
        return Arrays.asList(enList, cnList);
    }

    public static List<List<Object>> dataList(List<String> enHead, List<Map<String, Object>> dataList) throws SQLException {
        List<List<Object>> resultList = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            List<Object> list = new ArrayList<>();
            for (String key : enHead) {
                Object value = map.get(key);
                if (value instanceof GregorianCalendar)
                    value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((GregorianCalendar) value).getTime());
                if (value instanceof Date) value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                if (value instanceof Clob) {
                    Clob clob = (Clob) value;
                    value = clob.getSubString(1, (int) clob.length());
                }
                if (value instanceof Blob) {
                    Blob blob = (Blob) value;
                    value = new String(blob.getBytes(1, (int) blob.length()));
                }
                if (value instanceof byte[]) value = new String((byte[]) value);
                list.add(value);
            }
            resultList.add(list);
        }
        return resultList;
    }

    private static String filePathUpload;

    private static String filePathSubUpload;

    private static String filePathDownload;

    @Value("${file.path.upload}")
    private String filePathUploadTemp;

    @Value("${file.path.sub-upload:}")
    private String filePathSubUploadTemp;

    @Value("${file.path.download}")
    private String filePathDownloadTemp;

    @PostConstruct
    private void init(){
        filePathUpload = filePathUploadTemp;
        filePathSubUpload = filePathSubUploadTemp;
        filePathDownload = filePathDownloadTemp;
        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().startsWith("win")){
            String userDir = System.getProperty("user.dir").split(":")[0];
            filePathUpload = userDir + ":" + filePathUploadTemp;
            filePathDownload = userDir + ":" + filePathDownloadTemp;
        }
    }

    public static String upload(List<MultipartFile> files, String rootFilePath) {
        if (StringUtils.isBlank(rootFilePath)) {
            rootFilePath = filePathUpload;
        }
        List<Result> list = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return "上传文件不能为空！";
        }else {
            for (MultipartFile file : files) {
                Result result = new Result();
                if (file == null || file.isEmpty()) {
                    result.setMsg("上传文件不能为空！");
                    result.setState("fail");
                }else {
                    // 原文件名
                    String originalFilename = file.getOriginalFilename();
                    result.setFileName(originalFilename);
                    // 原文件后缀
                    String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                    result.setFileSuffix(suffix);
                    // 文件大小
                    result.setFileSize(formatSize(file.getSize()));
                    // 文件类型
                    result.setFileContentType(file.getContentType());
                    // 获取当前日期
                    Calendar calendar  = Calendar.getInstance();
                    String year = String.valueOf(calendar.get(Calendar.YEAR));
                    String month = timeFormat(calendar.get(Calendar.MONTH) + 1);
                    String dayOfMonth = timeFormat(calendar.get(Calendar.DAY_OF_MONTH));
                    String randomNum = UUID.randomUUID().toString().substring(30);
                    // 存储文件名：年+月+日+随机数+”_”+原文件名
                    String fileName = year + month + dayOfMonth + randomNum + "_" + originalFilename;
                    // 文件路径：根目录+年+月+日
                    String filePath = String.join(File.separator, Arrays.asList(rootFilePath, year, month, dayOfMonth));
                    // 相对路径（含文件名）
                    String relativeFilePath = String.join(File.separator, Arrays.asList(year, month, dayOfMonth, fileName));
                    // 路径是否拼子目录
                    if(StringUtils.isNotBlank(filePathSubUpload)){
                        filePath = String.join(File.separator, Arrays.asList(rootFilePath, filePathSubUpload, year, month, dayOfMonth));
                        relativeFilePath = String.join(File.separator, Arrays.asList(filePathSubUpload, year, month, dayOfMonth, fileName));
                    }
                    result.setFilePath(relativeFilePath);
                    //创建一个目录对象
                    File dir = new File(filePath);
                    //判断当前目录是否存在
                    if (!dir.exists()) {
                        //目录不存在，需要创建
                        dir.mkdirs();
                    }  //将临时文件转存到指定位置
                    try {
                        file.transferTo(new File(filePath, fileName));
                        result.setFileName(originalFilename);
                        result.setMsg("上传文件成功！");
                        result.setState("succeed");
                    } catch (IOException e) {
                        result.setMsg("上传文件失败！");
                        result.setState("fail");
                        e.printStackTrace();
                    }
                }
                list.add(result);
            }
        }
        return com.alibaba.fastjson.JSON.toJSONString(list);
    }

    /**
     * 文件下载
     * @param filePath 文件路径，为空时取默认路径：file.path.download
     * @param fileName 文件名称
     * @param isView 是否在线查看
     * @param request
     * @param response
     * @throws IOException
     */
    public static void download(String filePath, String fileName, boolean isView, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("=====filePath==="+filePath);
        logger.info("=====filePathDownload==="+filePathDownload);
        if(StringUtils.isBlank(filePath)) {
            filePath = filePathDownload;
        }else {
            String osName = System.getProperty("os.name");
            if(osName.toLowerCase().startsWith("win")){
                String userDir = System.getProperty("user.dir").split(":")[0];
                filePath = userDir + ":" + filePath;
            }
        }
        File file = new File(filePath, fileName);
        logger.info("=====file==="+fileName);
        logger.info("=====file.getAbsoluteFile==="+file.getAbsoluteFile());
        logger.info("=====file.exists()==="+file.exists());
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            //根据传过来的参数判断是下载，还是在线打开
            if (!isView) {
                //默认以附件形式下载  点击会提供对话框选择另存为：
                response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "utf-8"));
                //点击会提供对话框选择另存为：
//                response.setHeader( "Content-Disposition ", "attachment;fileName= "+fileName);
                //通过IE浏览器直接选择相关应用程序插件打开：
//                 response.setHeader( "Content-Disposition ", "inline;fileName= "+fileName);
                //下载前询问（是打开文件还是保存到计算机）
//                 response.setHeader( "Content-Disposition ", "fileName= "+fileName);
            }
            //获取输出流
            ServletOutputStream os = response.getOutputStream();
            //利用IO流工具类实现流文件的拷贝，（输出显示在浏览器上在线打开方式）
            IOUtils.copy(fis, os);
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(os);
        }else {
            response.reset();
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("文件不存在！");
        }
    }

    public static String timeFormat(int num) {
        return num > 9 ? "" + num : "0" + num;
    }

    private static String formatSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (size == 0) {
            return wrongSize;
        }
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    @Data
    static class Result{

        private String filePath;
        private String fileName;
        private String fileSuffix;
        private String fileSize;
        private String fileContentType;
        private String msg;
        private String state;

    }

}
