package com.bxchongdian.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/08/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class CarCardBean implements Parcelable {
	/*
	 "balance": 9892,
	 "bindId": 2
	"batteryType": "",
	"capacity": "",
	"carType": "宝马 530LE",
	"cardId": "456789",
	"id": 3,
	"license": "京d8888",
	"licenseType": "小型汽车",
	"physicalNumber": "10011706230920432043",
	"voltage": 690
	 */
	public float balance;
	public int bindId;
	public String batteryType;
	public String capacity;
	public String license;
	public String licenseType;
	public String carType;
	public String cardId;
	public String physicalNumber;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(this.balance);
		dest.writeInt(this.bindId);
		dest.writeString(this.batteryType);
		dest.writeString(this.capacity);
		dest.writeString(this.license);
		dest.writeString(this.licenseType);
		dest.writeString(this.carType);
		dest.writeString(this.cardId);
		dest.writeString(this.physicalNumber);
	}

	public CarCardBean() {
	}

	protected CarCardBean(Parcel in) {
		this.balance = in.readFloat();
		this.bindId = in.readInt();
		this.batteryType = in.readString();
		this.capacity = in.readString();
		this.license = in.readString();
		this.licenseType = in.readString();
		this.carType = in.readString();
		this.cardId = in.readString();
		this.physicalNumber = in.readString();
	}

	public static final Parcelable.Creator<CarCardBean> CREATOR = new Parcelable.Creator<CarCardBean>() {
		@Override
		public CarCardBean createFromParcel(Parcel source) {
			return new CarCardBean(source);
		}

		@Override
		public CarCardBean[] newArray(int size) {
			return new CarCardBean[size];
		}
	};
}
