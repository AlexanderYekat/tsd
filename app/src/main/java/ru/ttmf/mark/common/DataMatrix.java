package ru.ttmf.mark.common;


import android.support.annotation.Nullable;

public class DataMatrix {

    private String SN; //As String 'Серийный номер *\ до 20 символов
    private String GTIN;//As String 'Международный код маркировки *\ 14 символов
    private String SERIAL;//As String 'Номер партии *\ до 20 символов
    private String SHELF_DATE;// As String 'Срок годности *\ до 6 символов
    private String TN_VED;// As String 'Код ТН ВЭД *\ 4 символа
    private String SSCC;//As String 'Код SSCC, третичной (заводской, транспортной) упаковки *\ 18 символов
    private String OPT_TORG; //As String 'Уникальный идентификатор третичной (заводской, транспортной) упаковки
    private String CRYPTO_KEY;
    private String CRYPTO_CIPHER;

    final int getAILenght(String aiCode) {
        switch (aiCode) {
            case "00":
                return 18;
            case "01":
                return 14;
            case "10":
                return 20;
            case "11":
                return 6;
            case "15":
                return 6;
            case "17":
                return 6;
            case "21":
                return 14;
            case "91":
                return 4;
            case "92":
                return 90;
            case "240":
                return 4;
            case "999":
                return 18;
            default:
                return 0;
        }
    }

    public final void setDM(String aiCode, String val) {
        switch (aiCode) {
            case "00":
                this.SSCC = val;
                break;
            case "01":
                this.GTIN = val;
                break;
            case "10":
                this.SERIAL = val;
                break;
            case "17":
                this.SHELF_DATE = val;
                break;
            case "21":
                this.SN = val;
                break;
            case "91":
                this.CRYPTO_KEY = val;
                break;
            case "92":
                this.CRYPTO_CIPHER = val;
                break;
            case "240":
                this.TN_VED = val;
                break;
            case "999":
                this.OPT_TORG = val;
                break;
        }
    }

    public final String SGTIN() {
        return (this.GTIN + this.SN);
    }

    public final String SSCC() {
        return (this.SSCC);
    }
}