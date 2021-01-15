package ybc.ocr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.ocr.AipOcr;

public class App {
    //设置APPID/AK/SK
    public static final String APP_ID = "16862623";
    public static final String API_KEY = "q6rGMrKMM109O31XBSchde5R";
    public static final String SECRET_KEY = "eekzumdMaS9BDnGPYpso3k65nuVGFzOc";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 解析pdf文档信息
     *
     * @param pdfPath pdf文档路径
     * @throws Exception
     */
    public static void pdfParse(String pdfPath) throws Exception {
        InputStream input = null;
        File pdfFile = new File(pdfPath);
        PDDocument document = null;
        try {
            input = new FileInputStream(pdfFile);
            //加载 pdf 文档
            document = PDDocument.load(input);

            /** 文档属性信息 **/
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println("标题:" + info.getTitle());
            System.out.println("主题:" + info.getSubject());
            System.out.println("作者:" + info.getAuthor());
            System.out.println("关键字:" + info.getKeywords());

            System.out.println("应用程序:" + info.getCreator());
            System.out.println("pdf 制作程序:" + info.getProducer());

            System.out.println("作者:" + info.getTrapped());

            System.out.println("创建时间:" + dateFormat(info.getCreationDate()));
            System.out.println("修改时间:" + dateFormat(info.getModificationDate()));


            //获取内容信息
            PDFTextStripper pts = new PDFTextStripper();
            String content = pts.getText(document);
            System.out.println("内容:" + content);


            /** 文档页面信息 **/
            PDDocumentCatalog cata = document.getDocumentCatalog();
            PDPageTree pages = cata.getPages();
            System.out.println(pages.getCount());
            int count = 1;

            // 初始化一个AipOcr
            AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(20000);
            client.setSocketTimeoutInMillis(60000);

            for (int i = 0; i < pages.getCount(); i++) {
                PDPage page = (PDPage) pages.get(i);
                if (null != page) {
                    PDResources res = page.getResources();
                    Iterable xobjects = res.getXObjectNames();
                    if(xobjects != null){
                        Iterator imageIter = xobjects.iterator();
                        while(imageIter.hasNext()){
                            COSName key = (COSName) imageIter.next();
                            if (res.isImageXObject(key)) {
                                try {
                                    PDImageXObject image = (PDImageXObject) res.getXObject(key);
                                    BufferedImage bimage = image.getImage();
                                     // 将BufferImage转换成字节数组
                                    ByteArrayOutputStream out =new ByteArrayOutputStream();
                                    ImageIO.write(bimage,"png",out);//png 为要保存的图片格式
                                    byte[] barray = out.toByteArray();
                                    out.close();
                                     // 发送图片识别请求 
                                    JSONObject json = client.basicGeneral(barray, new HashMap<String, String>());
                                    System.out.println(json.toString(2));
                                    count++;
                                    System.out.println(count);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != input)
                input.close();
            if (null != document)
                document.close();
        }
    }

    
    /**
     * B方法追加文件：使用FileWriter
     */
    public static void appendFileContent(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件,如果为 true，则将字节写入文件末尾处，而不是写入文件开始处 
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取格式化后的时间信息
     *
     * @param dar 时间信息
     * @return
     * @throws Exception
     */
    public static String dateFormat(Calendar calendar) throws Exception {
        if (null == calendar)
            return null;
        String date = null;
        try {
            String pattern = DATE_FORMAT;
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            date = format.format(calendar.getTime());
        } catch (Exception e) {
            throw e;
        }
        return date == null ? "" : date;
    }
    
    
    

    public static void main(String[] args) throws Exception {

        // 初始化一个AipOcr
    	AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(20000);
        client.setSocketTimeoutInMillis(60000);
     
     /* //  JSONObject json= client.general("f:\\微信图片_20190626102629.jpg", new HashMap<String, String>());
        JSONObject json= client.accurateGeneral("D:\\1.png", new HashMap<String, String>());
        JSONArray jsonArray = json.getJSONArray("words_result");
        List<String> wordsList = new ArrayList<>();
        StringBuilder wordsB = new StringBuilder();
        for (int i=0;i<jsonArray.length();i++){
            JSONObject partDaily = jsonArray.getJSONObject(i);
            String words = partDaily.getString("words");
            wordsList.add(words);
            wordsB.append(words).append("\n\r");
             
        }
        System.out.println(wordsList);
        System.out.println(wordsB);*/
      // 读取pdf文件
        String path = "D:\\第7章 项目质量管理2.0.pdf";
        pdfParse(path); 

    }

}