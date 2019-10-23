package com.sunsharing.eos.uddi.model;

import javax.persistence.*;

/**
 * Created by criss on 14-2-1.
 */
@Entity
@Table(name = "T_METHOD")
public class TMethod {
    private int methodId;
    private TServiceVersion versionObj;
    private String methodName;
    private String mockResult;
    private String params;
    private String methodVersion;

    @Id
    @Column(name = "METHOD_ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    @ManyToOne
    @JoinColumn(name = "VERSION_ID")
    public TServiceVersion getVersionObj() {
        return versionObj;
    }

    public void setVersionObj(TServiceVersion versionObj) {
        this.versionObj = versionObj;
    }

    @Basic
    @Column(name = "VERSION")
    public String getMethodVersion() {
        return methodVersion;
    }

    public void setMethodVersion(String methodVersion) {
        this.methodVersion = methodVersion;
    }

    @Basic
    @Column(name = "PARAMS")
    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Basic
    @Column(name = "METHOD_NAME")
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Basic
    @Column(name = "MOCK_RESULT")
    public String getMockResult() {
        return mockResult;
    }

    public void setMockResult(String mockResult) {
        this.mockResult = mockResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TMethod tMethod = (TMethod) o;

        if (methodId != tMethod.methodId) return false;
        if (methodName != null ? !methodName.equals(tMethod.methodName) : tMethod.methodName != null) return false;
        if (mockResult != null ? !mockResult.equals(tMethod.mockResult) : tMethod.mockResult != null) return false;
        //if (versionId != null ? !versionId.equals(tMethod.versionId) : tMethod.versionId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = methodId;
        //result = 31 * result + (versionId != null ? versionId.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (mockResult != null ? mockResult.hashCode() : 0);
        return result;
    }
}
