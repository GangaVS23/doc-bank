package com.example.docbank.User;

public class DocModel {
    String docId,docName,docPassword,docType,encDoc,uID,encodedUrl,Fname;

    public DocModel(String docId, String docName, String docPassword, String docType, String encDoc, String uID, String encodedUrl, String fname) {
        this.docId = docId;
        this.docName = docName;
        this.docPassword = docPassword;
        this.docType = docType;
        this.encDoc = encDoc;
        this.uID = uID;
        this.encodedUrl = encodedUrl;
        Fname = fname;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getEncodedUrl() {
        return encodedUrl;
    }

    public void setEncodedUrl(String encodedUrl) {
        this.encodedUrl = encodedUrl;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocPassword() {
        return docPassword;
    }

    public void setDocPassword(String docPassword) {
        this.docPassword = docPassword;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getEncDoc() {
        return encDoc;
    }

    public void setEncDoc(String encDoc) {
        this.encDoc = encDoc;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }


}
