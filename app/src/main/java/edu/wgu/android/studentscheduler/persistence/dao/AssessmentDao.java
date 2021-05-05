package edu.wgu.android.studentscheduler.persistence.dao;

import java.io.Serializable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssessmentDao implements Serializable {

    private long id;
    private long courseId;
    private String name;
    private String code;
    private long date;
    private String type;
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public AssessmentDao(Parcel source) {
//        id = source.
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(id);
//        dest.writeLong(courseId);
//        dest.writeString(name);
//        dest.writeString(code);
//        dest.writeLong(date);
//        dest.writeString(type);
//    }
//
//    public static final Parcelable.Creator<AssessmentDao> CREATOR = new Parcelable.Creator<AssessmentDao>() {
//
//        @Override
//        public AssessmentDao createFromParcel(Parcel source) {
//            return new AssessmentDao(source);
//        }
//
//        @Override
//        public AssessmentDao[] newArray(int size) {
//            return new AssessmentDao[size];
//        }
//    };
}
