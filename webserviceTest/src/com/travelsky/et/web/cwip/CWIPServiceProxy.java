package com.travelsky.et.web.cwip;

public class CWIPServiceProxy implements com.travelsky.et.web.cwip.CWIPService {
  private String _endpoint = null;
  private com.travelsky.et.web.cwip.CWIPService cWIPService = null;
  
  public CWIPServiceProxy() {
    _initCWIPServiceProxy();
  }
  
  public CWIPServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initCWIPServiceProxy();
  }
  
  private void _initCWIPServiceProxy() {
    try {
      cWIPService = (new com.travelsky.et.web.cwip.CWIPServiceServiceLocator()).getCWIPService();
      if (cWIPService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cWIPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cWIPService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cWIPService != null)
      ((javax.xml.rpc.Stub)cWIPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.travelsky.et.web.cwip.CWIPService getCWIPService() {
    if (cWIPService == null)
      _initCWIPServiceProxy();
    return cWIPService;
  }
  
  public com.travelsky.et.web.cwip.CWIPResponse handler(com.travelsky.et.web.cwip.CWIPRequest rq) throws java.rmi.RemoteException{
    if (cWIPService == null)
      _initCWIPServiceProxy();
    return cWIPService.handler(rq);
  }
  
  
}