package ybc.ocr;

import com.baidu.aip.ocr.AipOcr;
import com.baidu.aip.speech.AipSpeech;
import com.sun.xml.internal.ws.util.StringUtils;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AppSpeech {
    //设置APPID/AK/SK
    public static final String APP_ID = "16863174";
    public static final String API_KEY = "ZEzolFaOlsnS3i7MsNtrSSSz";
    public static final String SECRET_KEY = "3XOIHaoVxx0ckLNmP262GELe8qBjN7wx";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
   

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
    
    
  
    	/**
    	 * MP3转换PCM文件方法
    	 * @param mp3filepath 原始文件路径
    	 * @param pcmfilepath 转换文件的保存路径
    	 * @throws Exception 
    	 */
    	public static void mp3Convertpcm(String mp3filepath,String pcmfilepath) throws Exception{
    		File mp3 = new File(mp3filepath);
    		File pcm = new File(pcmfilepath);
    		//原MP3文件转AudioInputStream
    		AudioInputStream mp3audioStream = AudioSystem.getAudioInputStream(mp3);
    		//将AudioInputStream MP3文件 转换为PCM AudioInputStream
    		AudioInputStream pcmaudioStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, mp3audioStream);
    		//准备转换的流输出到OutputStream
    		OutputStream os = new FileOutputStream(pcm);
    		int bytesRead = 0;
    		byte[] buffer = new byte[8192];
    		while ((bytesRead=pcmaudioStream.read(buffer, 0, 8192))!=-1) {
    			os.write(buffer, 0, bytesRead);
    		}
    		os.close();
    		pcmaudioStream.close();
    	}
  

    public static void main(String[] args) throws Exception {
    	 // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(60000);
        client.setSocketTimeoutInMillis(100000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
       // System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        
         HashMap<String,Object> map = new HashMap<String, Object>();
      //  map.put("dev_pid", "1737");
/*        
      //合成的MP3语音文件
      		String path = "f:\\201972310339.mp3";
      		//MP3转pcm要保存的路径和文件名
      		String path2 = "f:\\201972310339.pcm";
      	//	mp3Convertpcm(path, path2);
 
        
        // 调用接口
        JSONObject res = client.asr("f:/16k.pcm", "pcm", 16000,map);
        System.out.println(res.toString(2));*/
     
      //  JSONObject json= client.general("f:\\微信图片_20190626102629.jpg", new HashMap<String, String>());
        JSONObject json= client.general("f:\\微信图片_20190626102629.jpg", new HashMap<String, String>());
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
        System.out.println(wordsB);
      /*  // 读取pdf文件
        String path = "F:\\SAN与NAS的区别.pdf";
        pdfParse(path);*/

    }

}