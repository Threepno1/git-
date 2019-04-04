/**
 * CWIPServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.travelsky.et.web.cwip;

public class CWIPServiceServiceLocator extends org.apache.axis.client.Service implements com.travelsky.et.web.cwip.CWIPServiceService {

    public CWIPServiceServiceLocator() {
    }


    public CWIPServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CWIPServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CWIPService
    private java.lang.String CWIPService_address = "http://202.106.139.5:80/etcwip/services/CWIPService";

    public java.lang.String getCWIPServiceAddress() {
        return CWIPService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CWIPServiceWSDDServiceName = "CWIPService";

    public java.lang.String getCWIPServiceWSDDServiceName() {
        return CWIPServiceWSDDServiceName;
    }

    public void setCWIPServiceWSDDServiceName(java.lang.String name) {
        CWIPServiceWSDDServiceName = name;
    }

    public com.travelsky.et.web.cwip.CWIPService getCWIPService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CWIPService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCWIPService(endpoint);
    }

    public com.travelsky.et.web.cwip.CWIPService getCWIPService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.travelsky.et.web.cwip.CWIPServiceSoapBindingStub _stub = new com.travelsky.et.web.cwip.CWIPServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getCWIPServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCWIPServiceEndpointAddress(java.lang.String address) {
        CWIPService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.travelsky.et.web.cwip.CWIPService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.travelsky.et.web.cwip.CWIPServiceSoapBindingStub _stub = new com.travelsky.et.web.cwip.CWIPServiceSoapBindingStub(new java.net.URL(CWIPService_address), this);
                _stub.setPortName(getCWIPServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CWIPService".equals(inputPortName)) {
            return getCWIPService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://cwip.web.et.travelsky.com", "CWIPServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://cwip.web.et.travelsky.com", "CWIPService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CWIPService".equals(portName)) {
            setCWIPServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
