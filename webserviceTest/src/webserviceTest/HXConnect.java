package webserviceTest;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.travelsky.et.web.cwip.CWIPRequest;
import com.travelsky.et.web.cwip.CWIPResponse;
import com.travelsky.et.web.cwip.CWIPServiceProxy;

public class HXConnect {
	public static void main(String[] args) {
		try {
			CWIPRequest request = new CWIPRequest();
			request.setAirline("3U");
			request.setServiceType("AV_SERVICE");//AV_SERVICE 国内查询 INTER_AV_SERVICE 国际查询  CONFIRM_SERVICE 航班确认 BOOK_SERVICE 预定座位 PAYMENT_APPLY_SERVICE 授权支付   PAYMENT_CONFIRM_SERVICE 确认支付   CHANGE_AV_SERVICE 改期升舱查询航班  CHANGE_ORDER_SERVICE 改期升舱确认价格   REFUND_APPLY_SERVICE 退票  ORDER_QUERY_SERVICE 订单查询  USER_LOGIN_SERVICE 用户登录跟用户查询  USER_REGISTER_SERVICE 用户信息注册   ORDER_CANCEL_SERVICE 取消预定订单
			request.setUserType("2");
			request.setVersion("1.0");
			request.setAppUserId("TAOBAO");
			request.setUserId("TAOBAO");
			File file = new File("D:/project/webservice/request/1.查询航班/国内单程请求.xml");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty("encoding", "UTF-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			String xmlStr = bos.toString();
			request.setRequestXML(xmlStr);
//			request.setRequestXML("<av_request xmlns=\"http://xml.cwip.web.et.travelsky.com\"><orgCity>PEK</orgCity><dstCity>HAK</dstCity><tripType>OW</tripType><takeoffDate>2017-08-30</takeoffDate><returnDate>2017-09-02</returnDate></av_request>");
			CWIPServiceProxy cwipServiceProxy = new CWIPServiceProxy();
			cwipServiceProxy.setEndpoint("http://10.221.167.55:8180/etcwip/services/CWIPService");
//			cwipServiceProxy.setEndpoint("http://183.134.5.17:9180/etcwip/services/CWIPService");
			CWIPResponse cwipResponse = cwipServiceProxy.handler(request);
			int resultCode = cwipResponse.getResultCode();
			String errorDesc = cwipResponse.getErrorDesc();
			if (1 != resultCode) {
				// 如果ResultCode不为1，则表明出现错误
				System.out.println("错误-错误代码：" + resultCode);
				System.out.println("错误-错误描述：" + errorDesc);
			} else {
				// 如果ResultCode为1，则表明调用成功，可以从ResponseXML获取相应值
				System.out.println("成功-返回XML:\n" + cwipResponse.getResponseXML());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}