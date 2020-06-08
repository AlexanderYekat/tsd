package ru.ttmf.mark.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Invoice implements Parcelable {


    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("shifr")
    @Expose
    private String cipher;

    @SerializedName("p_name_s")
    @Expose
    private String name;

    @SerializedName("count")
    @Expose
    private String count;

    @SerializedName("ean13")
    @Expose
    private String ean13;

    @SerializedName("scan_count")
    @Expose
    private String scan_count;

    public Invoice(String id, String cipher, String name, String count, String ean13, String scan_count) {
        this.id = id;
        this.cipher = cipher;
        this.name = name;
        this.count = count;
        this.ean13 = ean13;
        this.scan_count = scan_count;
    }

    protected Invoice(Parcel in) {
        id = in.readString();
        cipher = in.readString();
        name = in.readString();
        count = in.readString();
        ean13 = in.readString();
        scan_count = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cipher);
        dest.writeString(name);
        dest.writeString(count);
        dest.writeString(ean13);
        dest.writeString(scan_count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Invoice> CREATOR = new Creator<Invoice>() {
        @Override
        public Invoice createFromParcel(Parcel in) {
            return new Invoice(in);
        }

        @Override
        public Invoice[] newArray(int size) {
            return new Invoice[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }
    public String getScanCount() { return scan_count; }
    public String getEan13() {
        return ean13;
    }

    public void setCount(String count) {
        this.count = count;
    }
    public void setScanCount(String scan_count) {
        this.scan_count = scan_count;
    }
}
