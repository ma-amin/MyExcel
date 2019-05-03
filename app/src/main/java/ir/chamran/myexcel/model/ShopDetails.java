package ir.chamran.myexcel.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity
public class ShopDetails {

    @SerializedName("radif")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "radif")
    private int dbID;

    @SerializedName("mantaghe")
    @ColumnInfo(name = "mantaghe")
    private String region;

    @SerializedName("nahiye")
    @ColumnInfo(name = "nahiye")
    private String district;

    @SerializedName("address")
    @ColumnInfo(name = "address")
    private String address;

    @SerializedName("noa")
    @ColumnInfo(name = "noa")
    private String type;

    @SerializedName("namVaNameKhanevadegi")
    @ColumnInfo(name = "namVaNameKhanevadegi")
    private String fullName;

    @SerializedName("Tel1")
    @ColumnInfo(name = "Tel1")
    private String tel_1;

    @SerializedName("Tel2")
    @ColumnInfo(name = "Tel2")
    private String tel_2;

    @SerializedName("tarikh")
    @ColumnInfo(name = "tarikh")
    private String date;

    @SerializedName("vaziyat")
    @ColumnInfo(name = "vaziyat")
    private boolean state;

    @Ignore
    public ShopDetails() {
    }

    public ShopDetails(int dbID, String region, String district, String address, String type, String fullName,
                       String tel_1, String tel_2, String date, boolean state) {
        this.dbID = dbID;
        this.region = region;
        this.district = district;
        this.address = address;
        this.type = type;
        this.fullName = fullName;
        this.tel_1 = tel_1;
        this.tel_2 = tel_2;
        this.date = date;
        this.state = state;
    }

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTel_1() {
        return tel_1;
    }

    public void setTel_1(String tel_1) {
        this.tel_1 = tel_1;
    }

    public String getTel_2() {
        return tel_2;
    }

    public void setTel_2(String tel_2) {
        this.tel_2 = tel_2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopDetails details = (ShopDetails) o;
        return getDbID() == details.getDbID() &&
                isState() == details.isState() &&
                Objects.equals(getRegion(), details.getRegion()) &&
                Objects.equals(getDistrict(), details.getDistrict()) &&
                Objects.equals(getAddress(), details.getAddress()) &&
                Objects.equals(getType(), details.getType()) &&
                Objects.equals(getFullName(), details.getFullName()) &&
                Objects.equals(getTel_1(), details.getTel_1()) &&
                Objects.equals(getTel_2(), details.getTel_2()) &&
                Objects.equals(getDate(), details.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDbID(), getRegion(), getDistrict(), getAddress(), getType(), getFullName(), getTel_1(), getTel_2(), getDate(), isState());
    }
}
