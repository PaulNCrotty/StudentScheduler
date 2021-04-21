package edu.wgu.android.studentscheduler.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Entity(tableName = "assessment", foreignKeys = @ForeignKey(entity = AssessmentAttempt.class, parentColumns = "id", childColumns = "attempts"))
public class Assessment {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String name;

    private String code;

    private AssessmentType type;

    private List<AssessmentAttempt> attempts;

}
