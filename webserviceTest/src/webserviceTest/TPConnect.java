package webserviceTest;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.travelsky.et 

.web.cwip.CWIPRequest;
import com.travelsky.et 

.web.cwip.CWIPResponse;
import com.travelsky.et 

.web.cwip.CWIPServiceProxy;
public class TPConnect {
	public static void main(String[] args) {
		try {
			CWIPRequest request = new CWIPRequest();
			request.setAirline("TP");
			request.setServiceType("CONFIRM_SERVICE");//AV_SERVICE
			request.setUserType("2");
			request.setVersion("1.0");
			request.setAppUserId("TEST");
			request.setUserId("CKGTEST");
			File file = new File("C:/Users/msh/Desktop/z.xml");
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
//			cwipServiceProxy.setEndpoint("http://183.134.5.17:9180/etcwip/services/CWIPService ");
			CWIPResponse cwipResponse = cwipServiceProxy.handler(request);
			int resultCode = cwipResponse.getResultCode();
			String errorDesc = cwipResponse.getErrorDesc();
			if (1 != resultCode) {
				// ���ResultCode��Ϊ1����������ִ���
				System.out.println("����-������룺" + resultCode);
				System.out.println("����-����������" + errorDesc);
			} else {
				// ���ResultCodeΪ1����������óɹ������Դ�ResponseXML��ȡ��Ӧֵ
				System.out.println("�ɹ�-����XML:\n" + cwipResponse.getResponseXML());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}