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
public class TestConnectHN {
	public static void main(String[] args) {
		try {
			CWIPRequest request = new CWIPRequest();
			request.setAirline("HN");
			request.setServiceType("AV_SERVICE");
			request.setUserType("1");
			request.setVersion("1.0");
			request.setAppUserId("HNTEST");
			request.setUserId("HNTEST");
			File file = new File("G:\\FileName.xml");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty("encoding", "UTF-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.transform(new DOMSource(doc), new StreamResult(bos));
			String xmlStr = bos.toString();
			request.setRequestXML(xmlStr);
			CWIPServiceProxy cwipServiceProxy = new CWIPServiceProxy();
//			cwipServiceProxy.setEndpoint("http://202.106.139.5/etcwip/services/CWIPService");
			cwipServiceProxy.setEndpoint("http://183.134.5.17:9180/etcwip/services/CWIPService");
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
			//System.out.println(cwipResponse.getResponseXML());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
