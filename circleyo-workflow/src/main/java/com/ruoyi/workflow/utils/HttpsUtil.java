//package com.ruoyi.workflow.utils;
//
////import com.ruoyi.common.core.utils.StringUtils;
//import com.ruoyi.common.utils.StringUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;
//
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 普通的接口调用
// */
//public class HttpsUtil {
//
//	public static String httpsRequest(String requestUrl,String requestMethod,String outputStr){
//		StringBuffer buffer=null;
//		try{
//		//创建SSLContext
//		SSLContext sslContext=SSLContext.getInstance("SSL");
//		TrustManager[] tm={new MyX509TrustManager()};
//		//初始化
//		sslContext.init(null, tm, new java.security.SecureRandom());
//		//获取SSLSocketFactory对象
//		SSLSocketFactory ssf=sslContext.getSocketFactory();
//		URL url=new URL(requestUrl);
//		HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
//		conn.setDoOutput(true);
//		conn.setDoInput(true);
//		conn.setUseCaches(false);
//		conn.setRequestMethod(requestMethod);
//		//设置当前实例使用的SSLSoctetFactory
//		conn.setSSLSocketFactory(ssf);
//		conn.connect();
//		//往服务器端写内容
//		if(null!=outputStr){
//			OutputStream os=conn.getOutputStream();
//			os.write(outputStr.getBytes("utf-8"));
//			os.close();
//		}
//
//		//读取服务器端返回的内容
//		InputStream is=conn.getInputStream();
//		InputStreamReader isr=new InputStreamReader(is,"utf-8");
//		BufferedReader br=new BufferedReader(isr);
//		buffer=new StringBuffer();
//		String line=null;
//		while((line=br.readLine())!=null){
//			buffer.append(line);
//		}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return buffer.toString();
//	}
//
//	/**
//	 * 向指定 URL 发送POST方法的请求
//	 * @param url 发送请求的 URL
//	 * @param params 请求的参数集合
//	 * @return 远程资源的响应结果
//	 */
//	@SuppressWarnings("unused")
//	public static String sendPost(String url, Map<String, String> params) {
//		OutputStreamWriter out = null;
//		BufferedReader in = null;
//		StringBuilder result = new StringBuilder();
//		try {
//			URL realUrl = new URL(url);
//			HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
//			// 发送POST请求必须设置如下两行
//			conn.setDoOutput(true);
//			conn.setDoInput(true);
//			// POST方法
//			conn.setRequestMethod("POST");
//			// 设置通用的请求属性
//			conn.setRequestProperty("accept", "*/*");
//			conn.setRequestProperty("connection", "Keep-Alive");
//			conn.setRequestProperty("user-agent",
//					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			conn.connect();
//			// 获取URLConnection对象对应的输出流
//			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
//			// 发送请求参数
//			if (params != null) {
//				StringBuilder param = new StringBuilder();
//				for (Map.Entry<String, String> entry : params.entrySet()) {
//					if(param.length()>0){
//						param.append("&");
//					}
//					param.append(entry.getKey());
//					param.append("=");
//					param.append(entry.getValue());
//					//System.out.println(entry.getKey()+":"+entry.getValue());
//				}
//				//System.out.println("param:"+param.toString());
//				out.write(param.toString());
//			}
//			// flush输出流的缓冲
//			out.flush();
//			// 定义BufferedReader输入流来读取URL的响应
//			in = new BufferedReader(
//					new InputStreamReader(conn.getInputStream(), "UTF-8"));
//			String line;
//			while ((line = in.readLine()) != null) {
//				result.append(line);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		//使用finally块来关闭输出流、输入流
//		finally{
//			try{
//				if(out!=null){
//					out.close();
//				}
//				if(in!=null){
//					in.close();
//				}
//			}
//			catch(Exception ex){
//				ex.printStackTrace();
//			}
//		}
//		return result.toString();
//	}
//
//	/**
//	 * json Post请求
//	 * @param url
//	 * @param jsonObject
//	 * @param encoding
//	 * @return
//	 * @throws Exception
//	 */
//	public static String jsonPost(String url, String jsonObject,
//				String encoding,String appcode) throws Exception{
//		if(StringUtils.isBlank(encoding)){
//			encoding = "UTF-8";
//		}
//		String body = "";
//		//创建httpclient对象
//		CloseableHttpClient client = HttpClients.createDefault();
//		//创建post方式请求对象
//		HttpPost httpPost = new HttpPost(url);
//		//装填参数
//		StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
//		s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
//				"application/json"));
//		//设置参数到请求对象中
//		httpPost.setEntity(s);
//		//设置header信息
//		//指定报文头【Content-type】、【User-Agent】
////        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
//		httpPost.setHeader("Content-type", "application/json");
//		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//		//httpPost.setHeader("Authorization", "APPCODE" + appcode);
//		//执行请求操作，并拿到结果（同步阻塞）
//		CloseableHttpResponse response = client.execute(httpPost);
//		//获取结果实体
//		HttpEntity entity = response.getEntity();
//		if (entity != null) {
//			//按指定编码转换结果实体为String类型
//			body = EntityUtils.toString(entity, encoding);
//		}
//		EntityUtils.consume(entity);
//		//释放链接
//		response.close();
//		return body;
//	}
//
////	public static void main(String[] args) {
////		try {
////			Map<String, String> params = new HashMap<String, String>();
////			params.put("resourceId", "CLT");
////			params.put("sceneId","GZH");
////			params.put("produceNum","1");
////			params.put("agent","abcdffg");
////			String url ="http://127.0.0.1:8080/a/nbserver/kpb/index";
////			String appId = "xiaoma";
////			String appsecret = "123456";
////			String appcode = "xiaoma";
////			JSONObject querysJson = JSONObject.fromObject(params);
////			String result = jsonPost(url, querysJson.toString(),"", appcode);
////			System.out.println(result);
////
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
//}
