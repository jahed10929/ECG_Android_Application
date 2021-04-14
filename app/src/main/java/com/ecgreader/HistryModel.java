package com.ecgreader;

import android.util.Log;

public class HistryModel {
    private String report, reportName, imageLink, age, Trestbp, Chol, Thalach,
            Oldpeak, Ca, Gender, cp,FBS, ecg, exang, slop, thal;

    public HistryModel(String report, String reportName, String imageLink, String age,
                       String trestbp, String chol, String thalach,
                       String oldpeak, String ca, String gender,
                       String cp, String FBS, String ecg,
                       String exang, String slop, String thal) {
        this.report = report;
        this.reportName = reportName;
        this.imageLink = imageLink;
        this.age = age;
        this.Trestbp = trestbp;
        this.Chol = chol;
        this.Thalach = thalach;
        this.Oldpeak = oldpeak;
        this.Ca = ca;
        this.Gender = gender;
        this.cp = cp;
        this.FBS = FBS;
        this.ecg = ecg;
        this.exang = exang;
        this.slop = slop;
        this.thal = thal;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTrestbp() {
        return Trestbp;
    }

    public void setTrestbp(String trestbp) {
        Trestbp = trestbp;
    }

    public String getChol() {
        return Chol;
    }

    public void setChol(String chol) {
        Chol = chol;
    }

    public String getThalach() {
        return Thalach;
    }

    public void setThalach(String thalach) {
        Thalach = thalach;
    }

    public String getOldpeak() {
        return Oldpeak;
    }

    public void setOldpeak(String oldpeak) {
        Oldpeak = oldpeak;
    }

    public String getCa() {
        return Ca;
    }

    public void setCa(String ca) {
        Ca = ca;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getFBS() {
        return FBS;
    }

    public void setFBS(String FBS) {
        this.FBS = FBS;
    }

    public String getEcg() {
        return ecg;
    }

    public void setEcg(String ecg) {
        this.ecg = ecg;
    }

    public String getExang() {
        return exang;
    }

    public void setExang(String exang) {
        this.exang = exang;
    }

    public String getSlop() {
        return slop;
    }

    public void setSlop(String slop) {
        this.slop = slop;
    }

    public String getThal() {
        return thal;
    }

    public void setThal(String thal) {
        this.thal = thal;
    }
}
